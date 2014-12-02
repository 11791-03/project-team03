package edu.cmu.lti.f14.project.pipeline;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import edu.cmu.lti.f14.project.service.UmlsService;
import edu.cmu.lti.f14.project.similarity.CosineSimilarity;
import edu.cmu.lti.f14.project.similarity.Similarity;
import edu.cmu.lti.f14.project.util.Normalizer;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Document retrieval component in pipeline.
 *
 * @author junjiah
 */
public class DocumentRetrieval extends JCasAnnotator_ImplBase {

  private static final int TOP_K = 150;

  private static final String PUBMED_URI_PREFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";

  private static final String FULLTEXT_URI_PREFIX = "http://metal.lti.cs.cmu.edu:30002/pmc/";

  private static final CloseableHttpClient httpClient = HttpClients.createDefault();

  private static Class similarityClass = CosineSimilarity.class;

  private GoPubMedService goPubMedService;

  private Similarity similarity;

  private JsonParser jsonParser = new JsonParser();

  /**
   * @inheritDoc
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    try {
      goPubMedService = new GoPubMedService("project.properties");
      similarity = (Similarity) similarityClass.getConstructors()[0].newInstance();
    } catch (Exception e) {
      System.err.println("ERROR: Initialize PubMed goPubMedService error in Document Retrieval.");
      System.exit(1);
    }

  }

  /**
   * Input the preprocessed texts to PubMed and retrieve the documents.
   *
   * @param aJCas CAS structure
   * @throws AnalysisEngineProcessException
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println("RUNNING DOCUMENT RETRIEVAL");
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String preprocessedQuery = question.getPreprocessedText(),
              originalQuery = question.getText();
      if (preprocessedQuery == null || preprocessedQuery.isEmpty())
        return;

      List<PubMedSearchServiceResponse.Document> pubMedDocuments;

      try {
        pubMedDocuments = goPubMedService
                .findPubMedCitations(
                        queryExpand(originalQuery), 0, TOP_K).getDocuments();
        if (pubMedDocuments.isEmpty()) {
          // re-formulate the query and search again
          pubMedDocuments = goPubMedService.findPubMedCitations(queryFormulate(originalQuery), 0, TOP_K)
                  .getDocuments();
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("ERROR: Search PubMed in Document Retrieval Failed.");
        return;
      }

      // compare similarity between text and query
      Map<Document, Double> documentScores = new HashMap<>();
      for (PubMedSearchServiceResponse.Document pubMedDocument : pubMedDocuments) {
        String pmid = pubMedDocument.getPmid();
        String jsonText = retrieveDocumentJsonText(pubMedDocument);
        Document document = TypeFactory
                .createDocument(aJCas, PUBMED_URI_PREFIX + pmid, jsonText, -1, preprocessedQuery,
                        retrieveDocumentJsonText(pubMedDocument), pmid);
        String documentAbstract = pubMedDocument.getDocumentAbstract();
        if (documentAbstract == null || documentAbstract.isEmpty()) {
          continue;
        }
        double score = similarity.computeSimilarity(Normalizer.normalize(
                documentAbstract), preprocessedQuery);
        documentScores.put(document, score);
      }

      // sort document by its score
      List<Map.Entry<Document, Double>> scoreList = new ArrayList<>(documentScores.entrySet());
      Collections.sort(scoreList, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));
      int rank = 1;
      for (Map.Entry<Document, Double> entry : scoreList) {
        Document d = entry.getKey();
        d.setRank(rank++);
        d.addToIndexes();
      }

      System.out.println("Retrieved Document: " + scoreList.size());
    }
  }

  /**
   * @inheritDoc
   */
  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    try {
      httpClient.close();
    } catch (IOException ignored) {
    }

    super.collectionProcessComplete();
  }

  /**
   * Formulating the document as a json file containing section information and texts.
   *
   * @param pubMedDocument Retrieved PubMed document
   * @return A json string containing document title, text and section
   */
  public String retrieveDocumentJsonText(PubMedSearchServiceResponse.Document pubMedDocument) {
    // retrieve the document from the seb server
    String pmid = pubMedDocument.getPmid();
    String fullTextString = null;

    /*
          !!!!!!!!! Getting full text is very slow!     !!!!!!!!!
          !!!!!!!!! So we give up and didn't do it here !!!!!!!!!

    // try to get full text
    HttpGet httpGet = new HttpGet(FULLTEXT_URI_PREFIX + pmid);
    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity entity = response.getEntity();
      final InputStreamReader reader = new InputStreamReader(entity.getContent());
      fullTextString = CharStreams.toString(reader);
    } catch (Exception ignored) {
    }
    */

    JsonObject documentJson = new JsonObject();
    JsonArray sections = new JsonArray();
    if (pubMedDocument.getDocumentAbstract() != null) {
      sections.add(new JsonPrimitive(pubMedDocument.getDocumentAbstract()));
    }
    documentJson.addProperty("pmid", pmid);

    if (fullTextString != null && !fullTextString.isEmpty()) {
      // add abstract to the existing json object
      JsonObject fullTextObject = jsonParser.parse(fullTextString).getAsJsonObject();
      JsonArray oldSections = fullTextObject.getAsJsonArray("sections");
      sections.addAll(oldSections);
    }
    documentJson.add("sections", sections);
    documentJson.addProperty("title", pubMedDocument.getTitle());

    return documentJson.toString();
  }

  /**
   * Query formulation simply use n-gram nouns.
   *
   * @param query Original query string
   * @return Formulated query string
   */
  private String queryFormulate(String query) {
    String grams = Normalizer.retrieveNGrams(query)
            .stream()
            .map(gramList -> '"' + String.join(" ", gramList) + '"')
            .collect(Collectors.joining(" OR "));
    return grams;
  }

  /**
   * Expand original query by concatenating nouns with corresponding synonyms on UMLS.
   *
   * @param query Query text
   * @return Expanded query text
   */
  public String queryExpand(String query) {
    UmlsService umlsService = UmlsService.getInstance();
    List<String> grams = Normalizer.retrieveNGrams(query)
            .stream()
            .map(gramList -> String.join(" ", gramList))
            .collect(toList());
    return grams
            .stream()
            .map(s1 -> umlsService
                    .getSynonyms(s1)
                    .stream()
                    .limit(5)
                    .map(i -> '"' + i + '"')
                    .collect(Collectors.joining(" OR ")))
            .map(s2 -> '(' + s2 + ')')
            .collect(Collectors.joining(" AND "));
  }
}

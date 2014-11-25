package edu.cmu.lti.f14.project;

import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import edu.cmu.lti.f14.project.util.CosineSimilarity;
import edu.cmu.lti.f14.project.util.Similarity;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import java.io.InputStreamReader;
import java.util.*;

/**
 * Document retrieval component in pipeline.
 *
 * @author junjiah
 */

public class AnswerExtraction extends JCasAnnotator_ImplBase {

  private static final String URI_PREFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";

  private static final String FULLTEXT_URI_PREFIX = "http://metal.lti.cs.cmu.edu:30002/pmc/";

  private static final CloseableHttpClient httpClient = HttpClients.createDefault();

  private static Class similarityClass = CosineSimilarity.class;

  private GoPubMedService service;

  private Similarity similarity;

  private JsonParser jsonParser = new JsonParser();

  /**
   * Initialize the PubMed service.
   */

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    try {
      service = new GoPubMedService("project.properties");
      similarity = (Similarity) similarityClass.getConstructors()[0].newInstance();
    } catch (Exception e) {
      System.err.println("ERROR: Initialize PubMed service error in Document Retrieval.");
      System.exit(1);
    }

  }

  /**
   * Input the preprocessed texts to PubMed and retrieve the documents.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String query = question.getPreprocessedText();
      if (query == null || query.isEmpty())
        return;

      PubMedSearchServiceResponse.Result pubMedResult = null;

      try {
        pubMedResult = service.findPubMedCitations(query, 0);
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("ERROR: Search PubMed in Document Retrieval Failed.");
        return;
      }

      // compare similarity between text and query
      Map<Document, Double> documentScores = new HashMap<>();
      for (PubMedSearchServiceResponse.Document pubMedDocument : pubMedResult.getDocuments()) {
        String pmid = pubMedDocument.getPmid();
        // String text = pubMedDocument.getDocumentAbstract();
        String text = retrieveDocumentJsonText(pubMedDocument);
        Document document = TypeFactory.createDocument(aJCas, URI_PREFIX + pmid, text, -1, query,
                retrieveDocumentJsonText(pubMedDocument), pmid);
        double score = similarity.computeSimilarity(text, query);
        documentScores.put(document, score);
      }

      // sort document by its score
      List<Map.Entry<Document, Double>> scoreList = new ArrayList<>(documentScores.entrySet());
      Collections.sort(scoreList, (e1, e2) -> e1.getValue().compareTo(e2.getValue()));
      scoreList.stream().map(Map.Entry::getKey).forEach(Document::addToIndexes);
    }

  }

  private String retrieveDocumentJsonText(PubMedSearchServiceResponse.Document pubMedDocument) {
    // retrieve the document from the seb server
    String pmid = pubMedDocument.getPmid();
    HttpGet httpGet = new HttpGet(FULLTEXT_URI_PREFIX + pmid);
    String fullTextString = null;
    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity entity = response.getEntity();
      final InputStreamReader reader = new InputStreamReader(entity.getContent());
      fullTextString = CharStreams.toString(reader);
    } catch (Exception ignored) {
    }

    JsonObject documentJson = new JsonObject();
    JsonArray sections = new JsonArray();
    sections.add(new JsonPrimitive(pubMedDocument.getDocumentAbstract()));
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

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    try {
      httpClient.close();
    } catch (IOException ignored) {
    }

    super.collectionProcessComplete();
  }
}

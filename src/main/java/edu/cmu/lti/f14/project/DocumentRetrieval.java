package edu.cmu.lti.f14.project;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import edu.cmu.lti.f14.project.util.*;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Document retrieval component in pipeline.
 *
 * @author junjiah
 */

public class DocumentRetrieval extends JCasAnnotator_ImplBase {

  private static final String URI_PREFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";

  private static final String FULLTEXT_URI_PREFIX = "http://metal.lti.cs.cmu.edu:30002/pmc/";

  private static final CloseableHttpClient httpClient = HttpClients.createDefault();

  private static Class similarityClass = CosineSimilarity.class;

  private GoPubMedService goPubMedService;

  private Similarity similarity;

  private JsonParser jsonParser = new JsonParser();

  /**
   * Initialize services and resources.
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
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println("RUNNING DOCUMENT RETRIEVAL");
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String query = question.getPreprocessedText();
      if (query == null || query.isEmpty())
        return;

      PubMedSearchServiceResponse.Result pubMedResult;

      try {
        pubMedResult = goPubMedService
//                .findPubMedCitations(queryExpand(question.getText(), JCasUtil.select(aJCas, Concept.class)), 0);
                .findPubMedCitations(query, 0);
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("ERROR: Search PubMed in Document Retrieval Failed.");
        return;
      }

      // compare similarity between text and query
      Map<Document, Double> documentScores = new HashMap<>();
      for (PubMedSearchServiceResponse.Document pubMedDocument : pubMedResult.getDocuments()) {
        String pmid = pubMedDocument.getPmid();
        String jsonText = retrieveDocumentJsonText(pubMedDocument);
        Document document = TypeFactory
                .createDocument(aJCas, URI_PREFIX + pmid, jsonText, -1, query,
                        retrieveDocumentJsonText(pubMedDocument), pmid);
        double score = similarity.computeSimilarity(Normalizer.normalize(
                pubMedDocument.getDocumentAbstract()), query);
        documentScores.put(document, score);
      }

      // sort document by its score
      List<Map.Entry<Document, Double>> scoreList = new ArrayList<>(documentScores.entrySet());
      Collections.sort(scoreList, (e1, e2) -> e1.getValue().compareTo(e2.getValue()));
      scoreList
              .stream()
              .map(Map.Entry::getKey)
              .forEach(Document::addToIndexes);
    }

  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    try {
      httpClient.close();
    } catch (IOException ignored) {
    }

    super.collectionProcessComplete();
  }

  private String retrieveDocumentJsonText(PubMedSearchServiceResponse.Document pubMedDocument) {
    // retrieve the document from the seb server
    String pmid = pubMedDocument.getPmid();
    String fullTextString = null;

    /*
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
   * Query formulation using simple query operators
   *
   * @param preprocessedQuery Original query string
   * @return expanded query string
   */
  private String queryFormulate(String preprocessedQuery, Collection<Concept> concepts) {
    NamedEntityChunker chunker = NamedEntityChunker.getInstance();
    String namedEntities = chunker
            .chunk(preprocessedQuery)
            .stream()
            .map(s -> "\"" + s + "\"")
            .limit(5)
            .collect(Collectors.joining(" AND "));

    String conceptsString = concepts
            .stream()
            .map(Concept::getName)
            .map(s -> s.replace("_", " "))
            .filter(s -> !s.isEmpty() && Pattern.matches("\\p{Punct}", s))
            .limit(5)
            .map(s -> "\"" + s + "\"")
            .collect(Collectors.joining(" AND "));

    List<String> bigrams = buildGrams(preprocessedQuery, 2);
    String bigramsString = bigrams
            .stream()
            .map(s -> "\"" + s + "\"")
            .collect(Collectors.joining(" AND "));

    return String.format("(\"%s\") OR (%s) OR (%s) OR (%s)", preprocessedQuery, namedEntities,
            conceptsString, bigramsString);
  }

  private String queryExpand(String originalQuery, Collection<Concept> concepts) {
    UmlsService umlsService = UmlsService.getInstance();
    List<String> nouns = Normalizer.retrieveImportantWords(originalQuery);
    return nouns
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

  /**
   * Build NGrams from Unigram string.
   *
   * @param s Original text
   * @param N Number of consecutive words in a gram
   * @return A list of N-Grams
   */
  private List<String> buildGrams(String s, int N) {
    List<String> ngrams = Lists.newArrayList();
    String grams[] = s.split(" ");
    for (int n = 0; n <= N - 1; ++n) {
      for (int i = 0; i < grams.length - n; ++i) {
        StringBuilder gramBuilder = new StringBuilder(grams[i]);
        for (int j = i; j < i + n; ++j) {
          gramBuilder.append(" ").append(grams[j + 1]);
        }
        String gram = gramBuilder.toString();
        if (gram.isEmpty() || gram.charAt(0) == ' ')
          continue;
        ngrams.add(gram);
      }
    }
    return ngrams;
  }
}

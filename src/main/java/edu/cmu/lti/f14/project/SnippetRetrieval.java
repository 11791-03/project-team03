package edu.cmu.lti.f14.project;

import edu.cmu.lti.oaqa.type.retrieval.Document;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Snippet retrieval component in pipeline.
 *
 * @author junjiah
 */
public class SnippetRetrieval extends JCasAnnotator_ImplBase {

  private static final String URI_PREFIX = "http://gold.lti.cs.cmu.edu:30002/pmc/";

  private static final CloseableHttpClient httpClient = HttpClients.createDefault();

  /**
   * Input the preprocessed texts to PubMed and retrieve the documents.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    Collection<Document> documents = JCasUtil.select(aJCas, Document.class);
    List<String> pmids = documents
            .stream()
            .map(Document::getDocId)
            .filter(Objects::nonNull)
            .collect(toList());

    for (String pmid : pmids) {
      String url = URI_PREFIX + pmid;
      HttpGet httpGet = new HttpGet(url);
      try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
        HttpEntity entity = response.getEntity();
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(entity.getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
          System.out.println(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

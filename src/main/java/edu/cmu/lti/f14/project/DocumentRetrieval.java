package edu.cmu.lti.f14.project;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.io.IOException;

/**
 * Document retrieval component in pipeline.
 *
 * @author junjiah
 */
public class DocumentRetrieval extends JCasAnnotator_ImplBase {

  private static final String URI_PREFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";

  private GoPubMedService service;

  /**
   * Initialize the PubMed service.
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    try {
      service = new GoPubMedService("project.properties");
    } catch (ConfigurationException e) {
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
        System.err.println("ERROR: Search PubMed in Document Retrieval Failed.");
        System.exit(1);
      }

      for (PubMedSearchServiceResponse.Document pubMedDocument : pubMedResult.getDocuments()) {
        String pmid = pubMedDocument.getPmid();
        Document document = TypeFactory
                .createDocument(aJCas, URI_PREFIX + pmid,
                        pubMedDocument.isFulltextAvailable() ? pmid : null);
        document.addToIndexes();
      }
    }
  }
}

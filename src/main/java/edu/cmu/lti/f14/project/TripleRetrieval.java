package edu.cmu.lti.f14.project;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Triple;
import org.apache.http.client.ClientProtocolException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author hanz
 */
public class TripleRetrieval extends JCasAnnotator_ImplBase {

  private GoPubMedService service;

  /**
   *
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    try {
      service = new GoPubMedService("project.properties");
    } catch (org.apache.commons.configuration.ConfigurationException e) {
      System.err.println("ERROR: Initialize PubMed service error in Document Retrieval.");
      System.exit(1);
    }
  }

  public ArrayList<HashMap<String, String>> getTriples(String text) throws ClientProtocolException,
          IOException {
    ArrayList<HashMap<String, String>> triples = new ArrayList<HashMap<String, String>>();
    LinkedLifeDataServiceResponse.Result linkedLifeDataResult = service
            .findLinkedLifeDataEntitiesPaged(text, 0);
    for (LinkedLifeDataServiceResponse.Entity entity : linkedLifeDataResult.getEntities()) {
      Double score = entity.getScore();
      LinkedLifeDataServiceResponse.Relation relation = entity.getRelations().get(0);
      HashMap<String, String> t = new HashMap<String, String>();
      t.put("PRED", relation.getPred());
      t.put("SUB", relation.getSubj());
      t.put("OBJ", relation.getObj());
      t.put("SCORE", score.toString());
      triples.add(t);
    }
    return triples;
  }

  /**
   *
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      try {
        ArrayList<HashMap<String, String>> triples = getTriples(question.getText());
        for (HashMap<String, String> t : triples) {

          Triple triple = TypeFactory
                  .createTriple(aJCas, t.get("SUB"), t.get("PRED"), t.get("OBJ"));

          triple.addToIndexes();
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("ERROR: " + e.getMessage());
      }
    }
  }
}

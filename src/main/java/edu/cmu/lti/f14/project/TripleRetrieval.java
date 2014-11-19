package edu.cmu.lti.f14.project;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

import org.apache.http.client.ClientProtocolException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import util.TypeConstants;
import util.TypeFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author hanz
 */
public class TripleRetrieval extends JCasAnnotator_ImplBase {
  public static Comparator<HashMap<String, String>> TripleComparator = new Comparator<HashMap<String, String>>() {

    @Override
    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
      if (Double.parseDouble(o1.get("SCORE"))<Double.parseDouble(o2.get("SCORE"))){
        return 1;
        
      }
      else if (Double.parseDouble(o1.get("SCORE"))==Double.parseDouble(o2.get("SCORE"))){
      return 0;
      }
      else
        return -1;
      
    }

  };

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
   triples.sort(TripleComparator);
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
        int rank = 1;
        for (HashMap<String, String> t : triples) {

          Triple triple = TypeFactory
                  .createTriple(aJCas, t.get("SUB"), t.get("PRED"), t.get("OBJ"));

          TripleSearchResult tr = TypeFactory.createTripleSearchResult(aJCas, triple,
                  TypeConstants.URI_UNKNOWN, Double.parseDouble(t.get("SCORE")),
                  TypeConstants.TEXT_UNKNOWN, rank, question.getText(),
                  TypeConstants.SEARCH_ID_UNKNOWN, new ArrayList<>());

          tr.addToIndexes();
          rank++;
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("ERROR: " + e.getMessage());
      }
    }
  }
}

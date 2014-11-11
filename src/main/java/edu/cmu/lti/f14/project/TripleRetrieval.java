package edu.cmu.lti.f14.project;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.type.input.*;

/**
 * @author junjiah
 *
 */
public class TripleRetrieval extends JCasAnnotator_ImplBase {

  /**
   *
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
  };

  /**
   *
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      // do something here to extract the concepts
    }

  }
}

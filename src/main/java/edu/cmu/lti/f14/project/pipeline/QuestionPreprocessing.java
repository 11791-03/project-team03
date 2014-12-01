package edu.cmu.lti.f14.project.pipeline;

import edu.cmu.lti.f14.project.util.Normalizer;
import edu.cmu.lti.oaqa.type.input.Question;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.util.List;

/**
 * Process the original question texts with tokenization, stemming and stop-word filtering.
 *
 * @author junjiah
 */
public class QuestionPreprocessing extends JCasAnnotator_ImplBase {

  /**
   * Set up a stop-word dictionary.
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    Normalizer.initialize();
  }

  /**
   * Simply add tokenized text to the CAS.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String text = question.getText();
      List<String> tokenizedText = Normalizer.tokenize(text);
      question.setPreprocessedText(String.join(" ", tokenizedText));
    }
  }
}

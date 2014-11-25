package edu.cmu.lti.f14.project;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import util.TypeFactory;

import com.google.common.collect.Lists;

import edu.cmu.lti.f14.project.util.NEChunker;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Passage;

/**
 * Answer Extraction
 *
 * @author junjiah
 */

public class AnswerExtraction extends JCasAnnotator_ImplBase {
  NEChunker chunker;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    chunker = new NEChunker();
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
      // we have here a list of strings (snippets)
      // extract NEs
      List<String> nes = Lists.newArrayList();
      for (FeatureStructure fs : aJCas.getAnnotationIndex(Passage.type)) {
        Passage passage = (Passage) fs;
        String text = passage.getText();
        // TODO Cheng extract NEs here
        nes.addAll(chunker.chunk(text));
      }
      // compute TF for each NE and create an answer with the NE name as text and the frequency as
      // ranks
      List<String> selectedNEs = selectEntities(nes, question);
      // perform error analysis after this baseline
      // use ontology to enhance the results
      TypeFactory.createAnswer(aJCas, selectedNEs);
    }

  }

  private List<String> selectEntities(List<String> nes, Question question) {
    // TODO Gowayyed Auto-generated method stub
    return null;
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {

    super.collectionProcessComplete();
  }
}

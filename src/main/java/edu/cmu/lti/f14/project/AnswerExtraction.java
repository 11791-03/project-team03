package edu.cmu.lti.f14.project;

import com.google.common.collect.Lists;
import edu.cmu.lti.f14.project.util.NEChunker;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
      for (Passage passage : JCasUtil.select(aJCas, Passage.class)) {
        String text = passage.getText();
        // TODO Cheng extract NEs here
        nes.addAll(chunker.chunk(text));
      }
      // compute TF for each NE and create an answer with the NE name as text and the frequency as
      // ranks
      List<String> selectedNEs = selectEntities(aJCas, nes);
      // perform error analysis after this baseline
      // use ontology to enhance the results
      for(String ans : selectedNEs)
        TypeFactory.createAnswer(aJCas, ans);
    }

  }

  private List<String> selectEntities(JCas aJCas, List<String> nes) {
    List<NamedEntity> nesWithFreq = new ArrayList<NamedEntity>();
    for (String ne : nes) {
      int freq = 0;
      for (Passage passage : JCasUtil.select(aJCas, Passage.class)) {
        freq += StringUtils.countMatches(passage.getText(), ne);
      }
      nesWithFreq.add(new NamedEntity(ne, freq));
    }
    Collections.sort(nesWithFreq);
    List<String> res = new ArrayList<>();
    for (int i = 0; i < Math.min(nesWithFreq.size(), 5); i++) { // TODO how to decide the number !!!
      res.add(nesWithFreq.get(i).ne);
    }
    return res;
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {

    super.collectionProcessComplete();
  }

  private class NamedEntity implements Comparable<NamedEntity> {
    String ne;
    int freq;

    public NamedEntity(String n, int f) {
      this.ne = n;
      this.freq = f;
    }

    @Override
    public int compareTo(NamedEntity o) {
      return this.freq > o.freq ? 1 : -1;
    }
  }
}

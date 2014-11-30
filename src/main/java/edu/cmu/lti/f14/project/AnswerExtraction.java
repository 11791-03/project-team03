package edu.cmu.lti.f14.project;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.cmu.lti.f14.project.util.CosineSimilarity;
import edu.cmu.lti.f14.project.util.NamedEntityChunker;
import edu.cmu.lti.f14.project.util.Normalizer;
import edu.cmu.lti.f14.project.util.Word2VecService;
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

import java.util.*;

/**
 * Answer Extraction
 *
 * @author junjiah
 */

public class AnswerExtraction extends JCasAnnotator_ImplBase {
  NamedEntityChunker chunker;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    chunker = NamedEntityChunker.getInstance();
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
      Set<String> nes = new HashSet<>();
      for (Passage passage : JCasUtil.select(aJCas, Passage.class)) {
        String text = passage.getText();
        // TODO Cheng extract NEs here
        nes.addAll(chunker.chunk(text));
        nes.addAll(Normalizer.retrieveImportantWords(text));
      }
      // compute TF for each NE and create an answer with the NE name as text and the frequency as
      // ranks
      // List<String> selectedNEs = selectEntities(aJCas, Lists.newArrayList(nes));
      List<String> selectedNEs = selectEntitiesWithEmbeddings(aJCas, Lists.newArrayList(nes), query);
      // perform error analysis after this baseline
      // use ontology to enhance the results
      for (String ans : selectedNEs)
        TypeFactory.createAnswer(aJCas, ans);

    }

  }

  private List<String> selectEntitiesWithEmbeddings(JCas aJCas, List<String> nes, String query) {
    Word2VecService service = Word2VecService.getInstance();
    TreeMap<String, Double> mapping = Maps.newTreeMap();
    List<Double> queryEmbeddings = service.getVector(query);
    for (String ne : nes) {
      mapping.put(ne,
              CosineSimilarity.computeCosineSimilarity(queryEmbeddings, service.getVector(ne)));
    }
    return Lists.newArrayList(mapping.descendingKeySet());
  }

  private List<String> selectEntities(JCas aJCas, List<String> nes) {
    List<NamedEntity> nesWithFreq = new ArrayList<>();
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

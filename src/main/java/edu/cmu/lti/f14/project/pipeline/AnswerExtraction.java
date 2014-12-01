package edu.cmu.lti.f14.project.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import util.TypeFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.cmu.lti.f14.project.similarity.Similarity;
import edu.cmu.lti.f14.project.similarity.Word2VecSimilarity;
import edu.cmu.lti.f14.project.util.NamedEntityChunker;
import edu.cmu.lti.f14.project.util.Normalizer;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Passage;

/**
 * Answer extraction part.
 *
 * @author junjiah
 */

public class AnswerExtraction extends JCasAnnotator_ImplBase {
  private static Class similarityClass = Word2VecSimilarity.class;

  NamedEntityChunker chunker;

  private Similarity similarity;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    chunker = NamedEntityChunker.getInstance();
    try {
      similarity = (Similarity) similarityClass.getConstructors()[0].newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Extract NEs and nouns to answer candidates.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String preprocessedQuery = question.getPreprocessedText();
      Set<String> wordsInQuery = Sets.newHashSet(Splitter.on(" ").split(preprocessedQuery));

      // extract NEs
      Set<String> nes = new HashSet<>();
      for (Passage passage : JCasUtil.select(aJCas, Passage.class)) {
        String text = passage.getText();
        nes.addAll(chunker.chunk(text));
        List<List<String>> names = Normalizer.retrieveConsecutiveNouns(text);
        long ones = names.stream().filter(l -> l.size() == 1).count();
        long twos = names.stream().filter(l -> l.size() == 2).count();
        long threes = names.stream().filter(l -> l.size() == 3).count();
        System.out.println("Ones: " + ones + " twos: " + twos + " threes: " + threes);
        for (List<String> name : names) {
          nes.add(Joiner.on(" ").join(name));
        }
      }
      Set<String> cleanNEs = new HashSet<>();
      for (String s : nes) {
        Set<String> words = Sets.newHashSet(Splitter.on(" ").split(s));
        if (!wordsInQuery.containsAll(words)) {
          cleanNEs.add(s);
        }
      }
      // compute TF for each NE and create an answer with the NE
      // name as text and the frequency as ranks
      List<String> selectedNEs = selectEntitiesWithEmbeddings(Lists.newArrayList(cleanNEs),
              preprocessedQuery);
      // perform error analysis after this baseline
      // use ontology to enhance the results
      for (String ans : selectedNEs) {
        TypeFactory.createAnswer(aJCas, ans).addToIndexes();
      }

    }
  }

  private List<String> selectEntitiesWithEmbeddings(List<String> nes, String query) {
    TreeMap<String, Double> mapping = Maps.newTreeMap();
    for (String ne : nes) {
      mapping.put(ne, similarity.computeSimilarity(query, ne));
    }
    return Lists.newArrayList(mapping.descendingKeySet());
  }

  private List<String> selectEntities(JCas aJCas, List<String> nes) {
    // UmlsService.getInstance().getSynonyms();
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

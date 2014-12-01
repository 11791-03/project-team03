package edu.cmu.lti.f14.project.pipeline;

import com.google.common.collect.Lists;
import edu.cmu.lti.f14.project.similarity.Similarity;
import edu.cmu.lti.f14.project.similarity.Word2VecSimilarity;
import edu.cmu.lti.f14.project.util.NamedEntityChunker;
import edu.cmu.lti.f14.project.util.Normalizer;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

/**
 * Answer extraction analysis engine.
 *
 */
public class AnswerExtraction extends JCasAnnotator_ImplBase {
  private static final int TOK_K = 100;

  private static Class similarityClass = Word2VecSimilarity.class;

  private NamedEntityChunker chunker;

  private Similarity similarity;

  /**
   * @{inheritDoc}
   */
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
   * For every question, retrieve its snippets, recognize candidate answers
   * and sort them by their similarities with the question text.
   *
   * @param aJCas CAS structure
   * @throws AnalysisEngineProcessException
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String query = question.getText(); // use original question
      Set<String> wordsInQuery = Arrays.asList(query.split(" "))
              .stream()
              .collect(toSet());

      Set<String> candidates = new HashSet<>();
      for (Passage passage : JCasUtil.select(aJCas, Passage.class)) {
        String text = passage.getText();
        candidates.addAll(chunker.chunk(text));
        // TODO: consective nouns are not good
        List<List<String>> nouns = Normalizer.retrieveConsecutiveNouns(text);
        // TODO: debugging information, to-be deleted
        long ones = nouns.stream().filter(l -> l.size() == 1).count();
        long twos = nouns.stream().filter(l -> l.size() == 2).count();
        long threes = nouns.stream().filter(l -> l.size() == 3).count();
        System.out.println("Ones: " + ones + " twos: " + twos + " threes: " + threes);

        // add to candidates
        nouns
                .stream()
                .filter(nameList -> !wordsInQuery.containsAll(nameList))
                .map(nameList -> String.join(" ", nameList))
                .forEach(candidates::add);

      }

      List<String> sortedCandidates = selectEntitiesWithEmbeddings(Lists.newArrayList(candidates),
              query);
      // perform error analysis after this baseline
      sortedCandidates
              .forEach(c -> TypeFactory.createAnswer(aJCas, c).addToIndexes());
    }
  }

  /**
   * Select candidate answers by the order of their similarities with the query.
   *
   * @param candidates A list of noun candidate answers
   * @param query      The query text contained in the question
   * @return Sorted candidates by the word2vec similarity with the query
   */
  public List<String> selectEntitiesWithEmbeddings(List<String> candidates, String query) {
    Map<String, Double> candidateScoreMap = candidates
            .stream()
            .collect(toMap(Function.identity(),
                    candidate -> similarity.computeSimilarity(query, candidate)));
    List<Map.Entry<String, Double>> candidateScoreList = new ArrayList<>(
            candidateScoreMap.entrySet());
    Collections.sort(candidateScoreList, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));
    return candidateScoreList
            .stream()
            .map(Map.Entry::getKey)
            .limit(TOK_K)
            .collect(toList());
  }
}

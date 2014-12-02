package edu.cmu.lti.f14.project.pipeline;

import com.google.common.collect.Maps;
import edu.cmu.lti.f14.project.similarity.Similarity;
import edu.cmu.lti.f14.project.similarity.Word2VecSimilarity;
import edu.cmu.lti.f14.project.util.AbnerChunker;
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

import static java.util.stream.Collectors.*;

/**
 * Answer extraction analysis engine.
 */
public class AnswerExtraction extends JCasAnnotator_ImplBase {
  private static final int TOK_K = 100;

  private static Class similarityClass = Word2VecSimilarity.class;

  private Similarity similarity;

  /**
   * @inheritDoc
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    try {
      similarity = (Similarity) similarityClass.getConstructors()[0].newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * For every question, retrieve its snippets, recognize candidate answers and sort them by their
   * similarities with the question text.
   *
   * @param aJCas
   *          CAS structure
   * @throws AnalysisEngineProcessException
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println("RUNNING ANSWER EXTRACTION");
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String query = question.getPreprocessedText();
      Set<String> wordsInQuery = Arrays.asList(query.split(" ")).stream().collect(toSet());

      Map<String, Integer> candidateRank = Maps.newHashMap();
      for (Passage passage : JCasUtil.select(aJCas, Passage.class)) {
        String text = passage.getText();
        int rank = passage.getRank();
        List<List<String>> nouns = Normalizer.retrieveNGrams(text);
        // add n-grams to candidates
        nouns.stream().filter(nameList -> !wordsInQuery.containsAll(nameList))
                .map(nameList -> String.join(" ", nameList))
                .forEach(noun -> candidateRank.put(noun, rank));
        // add NEs to candidates
        AbnerChunker.getInstance().chunk(text).stream().filter(n -> !wordsInQuery.contains(n))
                .forEach(n -> candidateRank.put(n, rank));
      }

      List<String> sortedCandidates = selectEntitiesWithEmbeddings(candidateRank, query);

      for (int i = 0; i < sortedCandidates.size(); i++) {
        TypeFactory.createAnswer(aJCas, sortedCandidates.get(i), null, i + 1).addToIndexes();
      }
    }
  }

  /**
   * Select candidate answers by the order of their scores calculated based on similarity and
   * corresponding snippet rank.
   *
   * @param candidateRank
   *          A list of noun candidate answers
   * @param query
   *          The query text contained in the question
   * @return Sorted candidates by the word2vec similarity with the query
   */
  public List<String> selectEntitiesWithEmbeddings(Map<String, Integer> candidateRank, String query) {
    Map<String, Double> candidateScoreMap = candidateRank
            .entrySet()
            .stream()
            .collect(
                    toMap(Map.Entry::getKey,
                            entry -> discountedScore(entry.getKey(), query, entry.getValue())));
    List<Map.Entry<String, Double>> candidateScoreList = new ArrayList<>(
            candidateScoreMap.entrySet());
    Collections.sort(candidateScoreList, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

    int rank = 1;
    for (Map.Entry<String, Double> entry : candidateScoreList) {
      System.out.println(String.format("\t%d: %s -\t%f", rank++, entry.getKey(), entry.getValue()));
    }

    return candidateScoreList
            .stream()
            .map(Map.Entry::getKey)
            .limit(TOK_K)
            .collect(toList());
  }

  /**
   * Calculated score for answer candidate discounted on the corresponding snippet rank.
   *
   * @param candidate
   *          Candidate answer, usually are nouns or named entities
   * @param toCompare
   *          The string to compare, usually is the original query
   * @param rank
   *          Corresponding rank for candidate answer
   * @return A discounted score based on candidate's similarity and rank of source snippet
   */
  public double discountedScore(String candidate, String toCompare, int rank) {
    double similarityValue = similarity.computeSimilarity(toCompare, candidate);
    return (Math.pow(2, similarityValue) - 1) / (Math.log(rank + 1) / Math.log(2));
  }
}

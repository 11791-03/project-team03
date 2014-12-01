package edu.cmu.lti.f14.project.similarity;

import edu.cmu.lti.f14.project.service.Word2VecService;

import java.nio.channels.NonReadableChannelException;

/**
 * Similarity using bioasq word2vec web service.
 */
public class Word2VecSimilarity extends Similarity {

  private Word2VecService service = Word2VecService.getInstance();

  /**
   * Use the word2vec web service to calculate the similarity.
   *
   * @param s1 The first string to compare
   * @param s2 The second string to compare
   * @return
   */
  @Override
  public double computeSimilarity(String s1, String s2) {
    if (s1.isEmpty() || s2.isEmpty())
      return 0;

    try {
      return service.getSimilarity(s1, s2);
    } catch (NonReadableChannelException e) {
      System.out.println("ERROR: server not running.");
      System.exit(1);
      return 0;
    }
  }
}

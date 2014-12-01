package edu.cmu.lti.f14.project.similarity;

/**
 * Interface for similarity calculation.
 */
public abstract class Similarity {
  /**
   * Return the similarity between 2 strings.
   *
   * @param s1 The first string to compare
   * @param s2 The second string to compare
   * @return The similarity between 2 strings
   */
  public abstract double computeSimilarity(String s1, String s2);
}

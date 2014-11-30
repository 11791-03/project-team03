package edu.cmu.lti.f14.project.util;

import java.nio.channels.NonReadableChannelException;

public class Word2VecSimilarity extends Similarity {

  private Word2VecService service = Word2VecService.getInstance();

  @Override
  public double computeSimilarity(String s1, String s2) {
    try {
      return service.getSimilarity(s1, s2);
    } catch (NonReadableChannelException e) {
      System.out.println("ERROR: server not running.");
      System.exit(1);
      return 0;
    }
  }
}

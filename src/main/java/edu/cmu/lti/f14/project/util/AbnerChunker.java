package edu.cmu.lti.f14.project.util;

import abner.Tagger;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Utility class using ABNER to do named entity chunking.
 */
public class AbnerChunker {
  private static AbnerChunker abnerChunker = null;

  private Tagger tagger;

  private AbnerChunker() {
    tagger = new Tagger(Tagger.NLPBA);
  }

  /**
   * Get the singleton chunker.
   * @return The singleton chunker
   */
  public static AbnerChunker getInstance() {
    if (abnerChunker == null) {
      abnerChunker = new AbnerChunker();
    }
    return abnerChunker;
  }

  /**
   * Recognize named entities in the sentence.
   * @param toChunk The sentence to be chunked
   * @return List of recognized named entities
   */
  public List<String> chunk(String toChunk) {
    if (tagger == null)
      return null;
    String[][] entities = tagger.getEntities(toChunk);

    return Arrays.asList(entities[0])
            .stream()
            .filter(s -> s.length() <= 2)
            .collect(toList());
  }
}

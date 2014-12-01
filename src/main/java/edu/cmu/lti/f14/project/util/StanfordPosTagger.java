package edu.cmu.lti.f14.project.util;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * A wrapper class for Stanford POS tagger.
 */
public class StanfordPosTagger {

  private MaxentTagger tagger;

  /**
   * Initialize POS tagger with corresponding model file.
   *
   * @param modelfile
   */
  public StanfordPosTagger(String modelfile) {
    tagger = new MaxentTagger(modelfile);
  }

  /**
   * Get the POS tags for a string of text.
   * @param input Input text strings
   * @return A tagged text containing both words and tags.
   */
  public String doPOSTagging(String input) {
    return tagger.tagString(input);
  }

}

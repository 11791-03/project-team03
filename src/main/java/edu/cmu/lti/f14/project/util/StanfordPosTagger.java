package edu.cmu.lti.f14.project.util;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class StanfordPosTagger {

  private MaxentTagger tagger;

  public StanfordPosTagger(String modelfile) {
    tagger = new MaxentTagger(modelfile);
  }

  public static void main(String[] args) {
    String text = "I love you !";
    StanfordPosTagger pos = new StanfordPosTagger(
            "/Users/hanz/git/project-team03/src/main/java/edu/cmu/lti/f14/project/util/model");
    System.out.println(pos.doPOSTagging(text));
  }

  public String doPOSTagging(String input) {
    return tagger.tagString(input);
  }

}

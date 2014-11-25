package edu.cmu.lti.f14.project.util;

import edu.stanford.nlp.tagger.maxent.*;

;
public class StanfordPosTagger {

  private String modelfile;

  private String input;

  private MaxentTagger tagger;

  public StanfordPosTagger(String modelfile, String input) {
    this.modelfile = modelfile;
    this.input = input;
    tagger = new MaxentTagger(this.modelfile);

  }

  public String doPOSTagging() {
    return tagger.tagString(input);
  }
  
  public static void main(String []args){
    String text="I love you !";
    StanfordPosTagger pos=new StanfordPosTagger("/Users/hanz/git/project-team03/src/main/java/edu/cmu/lti/f14/project/util/model",text);
    System.out.println(pos.doPOSTagging());
  }

}

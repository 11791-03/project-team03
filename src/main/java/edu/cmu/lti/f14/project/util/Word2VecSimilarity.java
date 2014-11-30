package edu.cmu.lti.f14.project.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Word2VecSimilarity extends Similarity {

  private static final int vec_length = 200;

  private WordVectorService service;

  public Word2VecSimilarity() {
    service = new WordVectorService();

  }

  @Override
  public double computeSimilarity(String s1, String s2) {
    // TODO Auto-generated method stub
    ArrayList<Double> v1 = getWordVec(s1);
    ArrayList<Double> v2 = getWordVec(s2);
    Double sum = 0.0;
    Double sum1=0.0;
    Double sum2=0.0;
    for (int i = 0; i < vec_length; i++) {
      sum += v1.get(i) *v2.get(i);
      sum1+=v1.get(i)*v1.get(i);
      sum2+=v2.get(i)*v2.get(i);
    }
    return sum/Math.sqrt(sum1*sum2);

  }

  // return the sents vec
  private ArrayList<Double> getWordVec(String s) {
    ArrayList<Double> vectorList = new ArrayList<Double>();
    vectorList = (ArrayList<Double>) service.getVector(s);
    return vectorList;

  }

}

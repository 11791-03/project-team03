package edu.cmu.lti.f14.project.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosineSimilarity extends Similarity {

  public static void main(String[] args) {
    System.out.println(new CosineSimilarity().computeSimilarity("A B C D", "C D E F ")); // should be 0.5
  }
  
  @Override
  public double computeSimilarity(String s1, String s2) {
    Map<String, Integer> m1 = tokenize(s1);
    Map<String, Integer> m2 = tokenize(s2);
    ArrayList<String> tokensUnion = getTokensUnion(m1, m2);
    return computeCosineSimilarity(getVector(m1, tokensUnion), getVector(m2, tokensUnion));
  }
  private Map<String, Integer> tokenize(String str)
  {
    Map<String, Integer> res = new HashMap<>();
    List<String> tokens1 = Arrays.asList(str.split(" "));
    tokens1.stream().forEach(s -> res.put(s, Collections.frequency(tokens1, s)));
    return res;
  }
  /**
   * This function is used in computing the cosine similarities. It computes the union of two tokens
   * list.
   * 
   * @param map
   * @param map2
   * @return
   */
  private ArrayList<String> getTokensUnion(Map<String, Integer> map, Map<String, Integer> map2) {
    ArrayList<String> res = new ArrayList<String>();
    for (String ts : map.keySet()) {
      if (!res.contains(ts)) // This if is redundant because tokens should be unique in the
                             // first place.
      {
        res.add(ts);
      }
    }
    for (String ts : map2.keySet()) {
      if (!res.contains(ts)) {
        res.add(ts);
      }
    }
    return res;
  }

  /**
   * This method create the vector as a {@link HashMap} by taking an existing vector and a union of
   * tokens.
   * 
   * @param map
   * @param tokensUnion
   * @return
   */
  private Map<String, Integer> getVector(Map<String, Integer> token, ArrayList<String> tokensUnion) {
    Map<String, Integer> vector = new HashMap<String, Integer>();

    boolean found = false;
    for (String s : tokensUnion) {
      found = false;
      for (String t : token.keySet()) {
        if (s.equals(t)) {
          vector.put(s, token.get(t));
          found = true;
        }
      }
      if (!found)
        vector.put(s, 0);
    }
    return vector;
  }

  /**
   * 
   * This method computes the cosine similarity between two vectors. Each vector is represented as a
   * {@link Map}
   * 
   * @return cosine_similarity
   */
  private double computeCosineSimilarity(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double ab = 0;
    double a2 = 0;
    double b2 = 0;
    double ai = 0;
    double bi = 0;
    for (String s : queryVector.keySet()) {
      ai = queryVector.get(s);
      bi = docVector.get(s);
      ab += ai * bi;
      a2 += Math.pow(ai, 2);
      b2 += Math.pow(bi, 2);
    }
    return ab / (Math.sqrt(a2) * Math.sqrt(b2));
  }

}

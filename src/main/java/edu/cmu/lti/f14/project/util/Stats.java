package edu.cmu.lti.f14.project.util;

import java.util.List;
import java.util.Set;

import lombok.Data;

import com.google.common.collect.Sets;

/**
 * Statistics helper class
 * 
 * @author chucheng
 *
 */
@Data
public class Stats {
  private double truePositive, falsePositive, falseNegative, ap;

  /**
   * constructs a new Stats object. items must match exactly to be considered the same
   * 
   * @param type
   *          indicator string of the type e.g. "document", "answer"
   * @param golden
   *          list of string representations of the ground truth
   * @param results
   *          list of string representations of the retrieved results
   */
  public Stats(String type, List<String> golden, List<String> results) {
    this(type, golden, results, false);
  }

  /**
   * constructs a new Stats object. if partialMatch is set to true, results that contains ground
   * truth as substrings are also considered as matches.
   * 
   * @param type
   *          indicator string of the type e.g. "document", "answer"
   * @param golden
   *          list of string representations of the ground truth
   * @param results
   *          list of string representations of the retrieved results
   * @param partialMatch
   *          whether we use partial match or not
   */
  public Stats(String type, List<String> golden, List<String> results, boolean partialMatch) {
    if (type == "snippets") {
      // System.out.println("for " + type);
      System.out.println(" GOLDEN ARE ");
      for (String s : golden) {
        System.out.println(s);
      }
      System.out.println(" RETRIEVED ARE ");
      for (String s : results) {
        System.out.println(s);
      }
    }
    Set<String> intersection = null;
    Set<String> goldenSet = Sets.newHashSet(golden);
    if (!partialMatch) {
      intersection = Sets.newLinkedHashSet(results);
      intersection.retainAll(goldenSet);
    } else {
      Set<String> matchedGolden = Sets.newHashSet();
      intersection = Sets.newLinkedHashSet();
      for (String r : results) {
        if (goldenSet.contains(r) && !matchedGolden.contains(r)) {
          intersection.add(r);
          matchedGolden.add(r);
        } else { // partial match
          for (String g : goldenSet) {

            // do not match matched ground truth again
            if (matchedGolden.contains(g)) {
              continue;
            }

            // compare in lower case
            if (r.toLowerCase().contains(g.toLowerCase())) {
              intersection.add(r);
              matchedGolden.add(g);
              break;
            }
          }
        }
      }
    }

    truePositive = intersection.size();
    System.out.println("INTERSECTION OF " + type + " IS: " + truePositive);
    falsePositive = results.size() - truePositive;
    falseNegative = golden.size() - truePositive;
    int trueCount = 0;
    for (int r = 0; r < results.size(); r++) {
      if (goldenSet.contains(results.get(r))) {
        trueCount++;
        ap += ((double) trueCount) / (r + 1);
      }
    }
    ap /= golden.size();
  }

  /**
   * computes recall from a list of Stats
   * 
   * @param l
   *          list of Stats
   * @return recall
   */
  public static double calculateRecall(List<Stats> l) {
    return l.stream().mapToDouble(s -> s.getTruePositive()).sum()
            / l.stream().mapToDouble(s -> s.getTruePositive() + s.getFalseNegative()).sum();
  }

  /**
   * computes precision from a list of Stats
   * 
   * @param l
   *          list of Stats
   * @return precision
   */
  public static double calculatePrecision(List<Stats> l) {
    return l.stream().mapToDouble(s -> s.getTruePositive()).sum()
            / l.stream().mapToDouble(s -> s.getTruePositive() + s.getFalsePositive()).sum();
  }

  /**
   * computes MAP from a list of Stats
   * 
   * @param l
   *          list of Stats
   * @return MAP
   */
  public static double calculateMAP(List<Stats> l) {
    return l.stream().mapToDouble(s -> s.getAp()).average().orElse(Double.NaN);
  }

  /**
   * computes GMAP from a list of Stats
   * 
   * @param l
   *          list of Stats
   * @param EPSILON
   *          minimum value even if AP equals 0
   * @return
   */
  public static double calculateGMAP(List<Stats> l, final double EPSILON) {
    return Math.exp(l.stream().mapToDouble(s -> Math.log(s.getAp() + EPSILON)).average()
            .orElse(Double.NaN));
  }

  /**
   * prints precision, recall, F1, MAP, GMAP
   * 
   * @param s
   *          list of Stats
   * @param type
   *          type of items
   * @param EPSILON
   *          minimum value even if AP equals 0
   */
  public static void printStats(List<Stats> s, String type, final double EPSILON) {
    double precision = Stats.calculatePrecision(s);
    double recall = Stats.calculateRecall(s);
    double f1 = 2 * precision * recall / (precision + recall);
    System.out.println(String.format(
            "%s - precision: %.4f, recall: %.4f, F1: %.4f, MAP: %.4f, GMAP: %.4f", type, precision,
            recall, f1, Stats.calculateMAP(s), Stats.calculateGMAP(s, EPSILON)));
  }
}

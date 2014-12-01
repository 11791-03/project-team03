package edu.cmu.lti.f14.project.util;

import java.util.List;
import java.util.Set;

import lombok.Data;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

@Data
public class Stats {
  private double truePositive, falsePositive, falseNegative, ap;

  public Stats(String type, List<String> golden, List<String> results) {
    if (true) {
      System.out.println("for " + type);
      System.out.println(" GOLDEN ARE ");
      for (String s : golden) {
        System.out.println(s);
//        System.out.println("NEs in the golden: "
//                + Joiner.on(" ").join(NamedEntityChunker.getInstance().chunk(s)));
      }
      System.out.println(" RETRIEVED ARE ");
      for (String s : results) {
        System.out.println(s);
//        System.out.println("NEs in the retrieved: "
//                + Joiner.on(" ").join(NamedEntityChunker.getInstance().chunk(s)));
      }
    }
    Set<String> intersection = Sets.newLinkedHashSet(results);
    intersection.retainAll(golden);
    Set<String> goldenSet = Sets.newHashSet(golden);
    truePositive = intersection.size();
    System.out.println("INTERSECTION IS: " + truePositive);
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

  public static double calculateRecall(List<Stats> l) {
    return l.stream().mapToDouble(s -> s.getTruePositive()).sum()
            / l.stream().mapToDouble(s -> s.getTruePositive() + s.getFalseNegative()).sum();
  }

  public static double calculatePrecision(List<Stats> l) {
    return l.stream().mapToDouble(s -> s.getTruePositive()).sum()
            / l.stream().mapToDouble(s -> s.getTruePositive() + s.getFalsePositive()).sum();
  }

  public static double calculateMAP(List<Stats> l) {
    return l.stream().mapToDouble(s -> s.getAp()).average().orElse(Double.NaN);
  }

  public static double calculateGMAP(List<Stats> l, final double EPSILON) {
    return Math.exp(l.stream().mapToDouble(s -> Math.log(s.getAp() + EPSILON)).average()
            .orElse(Double.NaN));
  }

  public static void printStats(List<Stats> s, String type, final double EPSILON) {
    double precision = Stats.calculatePrecision(s);
    double recall = Stats.calculateRecall(s);
    double f1 = 2 * precision * recall / (precision + recall);
    System.out.println(String.format(
            "%s - precision: %.4f, recall: %.4f, F1: %.4f, MAP: %.4f, GMAP: %.4f", type, precision,
            recall, f1, Stats.calculateMAP(s), Stats.calculateGMAP(s, EPSILON)));
  }
}

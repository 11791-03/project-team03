package edu.cmu.lti.f14.project;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import json.gson.TestQuestion;
import json.gson.TestSet;
import lombok.Data;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.Document;

/**
 * Evaluator for intermediate results - Document, Concept and Triple
 */
public class InformationRetrievalEvaluator extends JCasAnnotator_ImplBase {

  private static final double EPSILON = 0.01;

  private Map<String, json.gson.Question> goldenStandards;

  private List<Stats> docStats = Lists.newArrayList();

  private List<Stats> conceptStats = Lists.newArrayList();

  private List<Stats> tripStats = Lists.newArrayList();

  /**
   * Initialize the golden results.
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    String filePath = "/BioASQ-SampleData1B.json";
    goldenStandards = Maps.newHashMap();
    List<json.gson.Question> questions = Lists.newArrayList();

    try {
      List<? extends TestQuestion> gsonQuestions = TestSet.load(getClass().getResourceAsStream(
              String.class.cast(filePath)));
      for (json.gson.Question q : gsonQuestions)
        questions.add(q);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("ERROR: Reading golden standard fails.");
      System.exit(1);
    }
    // trim question texts and add to golden standards
    questions.stream().filter(input -> input.getBody() != null)
            .forEach(input -> goldenStandards.put(input.getId(), input));
  }

  /**
   *
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();
    json.gson.Question goldenResult = null;
    if (iter.hasNext()) {
      String questionId = ((Question) (iter.next())).getId();
      goldenResult = goldenStandards.get(questionId);
    }

    if (goldenResult == null) {
      // cannot evaluate current question
      return;
    }

    Collection<Document> documents = JCasUtil.select(aJCas, Document.class);
    Collection<Concept> concepts = JCasUtil.select(aJCas, Concept.class);
    Collection<Triple> triples = JCasUtil.select(aJCas, Triple.class);

    List<String> goldenDocuments = goldenResult.getDocuments();
    List<String> goldenConcepts = goldenResult.getConcepts();
    List<json.gson.Triple> goldenTriples = goldenResult.getTriples();

    if (goldenDocuments != null) {
      Stats docStat = new Stats(goldenDocuments, documents.stream().map(Document::getUri)
              .collect(toList()));
      docStats.add(docStat);
    }
    if (goldenConcepts != null) {
      Stats conceptStat = new Stats(goldenConcepts, concepts.stream().map(Concept::getUris)
              .map(i -> i.getNthElement(0)).collect(toList()));
      conceptStats.add(conceptStat);
    }
    if (goldenTriples != null) {
      Stats tripStat = new Stats(goldenTriples.stream().map(Object::toString).collect(toList()),
              triples.stream().map(t -> new json.gson.Triple(t).toString()).collect(toList()));
      tripStats.add(tripStat);
    }
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
    printStats(docStats, "Document");
    printStats(conceptStats, "Concept");
    printStats(tripStats, "Triple");
  }

  private void printStats(List<Stats> s, String type) {
    double precision = calculatePrecision(s);
    double recall = calculateRecall(s);
    double f1 = 2 * precision * recall / (precision + recall);
    System.out.println(String.format(
            "%s - precision: %.4f, recall: %.4f, F1: %.4f, MAP: %.4f, GMAP: %.4f", type, precision,
            recall, f1, calculateMAP(s), calculateGMAP(s)));
  }

  @Data
  class Stats {
    private double truePositive, falsePositive, falseNegative, ap;

    Stats(List<String> golden, List<String> results) {
      Set<String> intersection = Sets.newLinkedHashSet(results);
      intersection.retainAll(golden);
      Set<String> goldenSet = Sets.newHashSet(golden);
      truePositive = intersection.size();
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
  }

  static double calculateRecall(List<Stats> l) {
    return l.stream().mapToDouble(s -> s.getTruePositive()).sum()
            / l.stream().mapToDouble(s -> s.getTruePositive() + s.getFalseNegative()).sum();
  }

  static double calculatePrecision(List<Stats> l) {
    return l.stream().mapToDouble(s -> s.getTruePositive()).sum()
            / l.stream().mapToDouble(s -> s.getTruePositive() + s.getFalsePositive()).sum();
  }

  static double calculateMAP(List<Stats> l) {
    return l.stream().mapToDouble(s -> s.getAp()).average().orElse(Double.NaN);
  }

  static double calculateGMAP(List<Stats> l) {
    return Math.exp(l.stream().mapToDouble(s -> Math.log(s.getAp() + EPSILON)).average()
            .orElse(Double.NaN));
  }

  private String convertTripleToString(Triple t) {
    return String.format("o: %s, p: %s, s: %s", t.getObject(), t.getPredicate(), t.getSubject());
  }
}
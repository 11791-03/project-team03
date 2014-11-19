package edu.cmu.lti.f14.project;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;
import json.gson.TestQuestion;
import json.gson.TestSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Evaluator for intermediate results - Document, Concept and Triple
 */
public class InformationRetrievalEvaluator extends JCasAnnotator_ImplBase {

  private static final double EPSILON = 0.01;

  private Map<String, json.gson.Question> goldenStandards;

  private double[] documentsCounts = new double[6];

  private double[] conceptsCounts = new double[6];

  private double[] triplesCounts = new double[6];

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
    //Collection<TripleSearchResult> triples=JCasUtil.select(aJCas,TripleSearchResult.class);

      
    List<String> goldenDocuments = goldenResult.getDocuments();
    List<String> goldenConcepts = goldenResult.getConcepts();
    List<json.gson.Triple> goldenTriples = goldenResult.getTriples();

    if (goldenDocuments != null) {
      compareToGroundTruth(
              documentsCounts,
              goldenDocuments,
              documents
                      .stream()
                      .map(Document::getUri)
                      .collect(toList()));
    }
    if (goldenConcepts != null) {
      compareToGroundTruth(
              conceptsCounts,
              goldenConcepts,
              concepts
                      .stream()
                      .map(Concept::getUris)
                      .map(i -> i.getNthElement(0))
                      .collect(toList()));
    }
    if (goldenTriples != null) {
      compareToGroundTruth(
              triplesCounts,
              goldenTriples
                      .stream()
                      .map(Object::toString)
                      .collect(toList()),
              triples.stream().map(this::convertTripleToString)
                      .collect(toList()));
      if (triples
      .stream()
      .map(this::convertTripleToString)
      .collect(toList()).size()!=0){
        System.out.print("fuck");
      }
    }
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
    // calculate precision & recall
    double documentPrecision = documentsCounts[0]
            / (documentsCounts[0] + documentsCounts[1]);
    double conceptPrecision = conceptsCounts[0]
            / (conceptsCounts[0] + conceptsCounts[1]);
    double triplePrecision = triplesCounts[0] / (triplesCounts[0] + triplesCounts[1]);

    double documentRecall = documentsCounts[0]
            / (documentsCounts[0] + documentsCounts[2]);
    double conceptRecall = conceptsCounts[0] / (conceptsCounts[0] + conceptsCounts[2]);
    double tripleRecall = triplesCounts[0] / (triplesCounts[0] + triplesCounts[2]);

    double documentMAP = documentsCounts[3] / documentsCounts[5];
    double conceptMAP = conceptsCounts[3] / conceptsCounts[5];
    double tripleMAP = triplesCounts[3] / triplesCounts[5];

    double documentGMAP = Math.pow(documentsCounts[4], 1 / documentsCounts[5]);
    double conceptGMAP = Math.pow(conceptsCounts[4], 1 / conceptsCounts[5]);
    double tripleGMAP = Math.pow(triplesCounts[4], 1 / triplesCounts[5]);

    System.out.println(String.format(
            "Document - precision: %.4f, recall: %.4f, F1: %.4f, MAP: %.4f, GMAP: %.4f",
            documentPrecision, documentRecall, 2 * documentPrecision * documentRecall
                    / (documentPrecision + documentRecall), documentMAP, documentGMAP));
    System.out.println(String.format(
            "Concept - precision: %.4f, recall: %.4f, F1: %.4f, MAP: %.4f, GMAP: %.4f",
            conceptPrecision, conceptRecall, 2 * conceptPrecision * conceptRecall
                    / (conceptPrecision + conceptRecall), conceptMAP, conceptGMAP));
    System.out.println(String.format(
            "Triple - precision: %.4f, recall: %.4f, F1: %.4f, MAP: %.4f, GMAP: %.4f",
            triplePrecision, tripleRecall, 2 * triplePrecision * tripleRecall
                    / (triplePrecision + tripleRecall), tripleMAP, tripleGMAP));
  }

  private void compareToGroundTruth(double[] counts, List<String> golden,
          List<String> results) {
    Set<String> intersection = Sets.newLinkedHashSet(results);
    // remove the duplicates and reserve the order
    intersection.retainAll(golden);
    Set<String> goldenSet = Sets.newHashSet(golden);

    double intersectionSize = intersection.size();
    // index for true positive = 0
    counts[0] += intersectionSize;
    // index for false positive = 1
    counts[1] += results.size() - intersectionSize;
    // index for false negative = 2
    counts[2] += golden.size() - intersectionSize;

    int trueCount = 0;
    double ap = 0;
    for (int r = 0; r < results.size(); r++) {
      if (goldenSet.contains(results.get(r))) {
        trueCount++;
        ap += ((double)trueCount) / (r + 1);
      }
    }
    ap /= golden.size();

    // sumAPrecision
    counts[3] += ap;
    // productAPrecision
    counts[4] *= ap == 0 ? EPSILON : ap;
    counts[5]++;
  }

  private String convertTripleToString(Triple t) {
    return String.format("o: %s, p: %s, s: %s", t.getObject(), t.getPredicate(), t.getSubject());
  }
}
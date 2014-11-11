package edu.cmu.lti.f14.project;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import json.gson.TrainingQuestion;
import json.gson.TrainingSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.cmu.lti.oaqa.type.input.Question;

/**
 * Evaluator for intermediate results - Document, Concept and Triple
 */
public class InformationRetrievalEvaluator extends JCasAnnotator_ImplBase {

  private Map<String, TrainingQuestion> goldenStandards;

  private Integer[] documentsCounts = new Integer[3];
  private Integer[] conceptsCounts = new Integer[3];
  private Integer[] triplesCounts = new Integer[3];

  /**
   * Initialize the golden results.
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    String filePath = "/BioASQ-SampleData1B.json";
    goldenStandards = Maps.newHashMap();
    List<TrainingQuestion> questions = Lists.newArrayList();

    try {
      List<? extends TrainingQuestion> gsonQuestions = TrainingSet
              .load(getClass().getResourceAsStream(
                      String.class.cast(filePath)));
      questions.addAll(gsonQuestions.stream().collect(Collectors.toList()));
    } catch (Exception e) {
      System.err.println("ERROR: Reading golden standard fails.");
    }
    // trim question texts and add to golden standards
    questions.stream()
            .filter(input -> input.getBody() != null)
            .forEach(
                    input -> goldenStandards.put(input.getId(), input));

  }

  /**
   *
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();
    TrainingQuestion goldenResult = null;
    int i = 0;
    try {
      FSIterator<FeatureStructure> documents = (FSIterator<FeatureStructure>) aJCas.getIndexRepository().getAllIndexedFS(aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.Document"));
      FSIterator<FeatureStructure> concepts = (FSIterator<FeatureStructure>) aJCas.getIndexRepository().getAllIndexedFS(aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.Concept"));
      FSIterator<FeatureStructure> triples = (FSIterator<FeatureStructure>) aJCas.getIndexRepository().getAllIndexedFS(aJCas.getRequiredType("edu.cmu.lti.oaqa.type.retrieval.Triple"));
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
//    compareToGroundTruth(documentsCounts, goldenResult.getDocuments(), documents);
//    compareToGroundTruth(conceptsCounts, goldenResult.getConcepts(), concepts);
//    compareToGroundTruth(triplesCounts, goldenResult.getTriples(), triples);
  }

  private void compareToGroundTruth(Integer[] counts, List<String> results,
          List<String> golden) {
    Set<String> intersection = Sets.newHashSet(results);
    intersection.retainAll(golden);
    int intersectionSize = intersection.size();
    // index for true positive = 0
    counts[0] += intersectionSize;
    // index for false positive = 1
    counts[1] += results.size() - intersectionSize;
    // index for false negative = 2
    counts[2] += golden.size() - intersectionSize;
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
    // make sure data is consistent
//    double f = (2 * )/();
//    System.err.println("Documents F-Measure", f);
  }
}

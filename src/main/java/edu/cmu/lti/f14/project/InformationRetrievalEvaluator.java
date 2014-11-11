package edu.cmu.lti.f14.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import json.gson.TrainingQuestion;
import json.gson.TrainingSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.Document;

/**
 * @author junjiah
 */
public class InformationRetrievalEvaluator extends JCasAnnotator_ImplBase {

  private Map<String, TrainingQuestion> goldenStandards;
  
  private ArrayList<Integer> documentsCounts = new ArrayList<Integer>(4);
  private ArrayList<Integer> conceptsCounts = new ArrayList<Integer>(4);
  private ArrayList<Integer> triplesCounts = new ArrayList<Integer>(4);
  
  
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
    Iterator<TOP> fs = aJCas.getIndexRepository().getAllIndexedFS(Concept.class).iterator();
    int i = 0;
    List<String> concepts = Lists.newArrayList();
    List<String> triples = Lists.newArrayList();
    List<String> documents = Lists.newArrayList();
    while (fs.hasNext()) {
      TOP ann = fs.next();
      if (ann.getClass() == Question.class) {
        String questionId = ((Question) (ann)).getId();
        goldenResult = goldenStandards.get(questionId);
      }
      if (ann.getClass() == Document.class) {
//        documents.add((Document) ann);
      }
      if (ann.getClass() == Concept.class) {
//        concepts.add((Concept) ann);
      }
      if (ann.getClass() == Triple.class) {
//        concepts.add((Triple) ann);
      }
    }
    // compare
    compareToGroundTruth(documentsCounts, goldenResult.getDocuments(), documents);
    compareToGroundTruth(conceptsCounts, goldenResult.getConcepts(), concepts);
//    compareToGroundTruth(triplesCounts, goldenResult.getTriples(), triples);
    
  }

  private void compareToGroundTruth(List<Integer> counts, List<String> concepts, List<String> concepts2) {
    
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException{
    super.collectionProcessComplete();
    // make sure data is consistent
//    double f = (2 * )/();
//    System.err.println("Documents F-Measure", f);
  }
}

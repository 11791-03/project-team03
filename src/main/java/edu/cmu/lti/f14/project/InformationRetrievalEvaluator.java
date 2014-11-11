package edu.cmu.lti.f14.project;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import json.gson.TrainingQuestion;
import json.gson.TrainingSet;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author junjiah
 */
public class InformationRetrievalEvaluator extends JCasAnnotator_ImplBase {

  private Map<String, TrainingQuestion> goldenStandards;

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
    if (iter.hasNext()) {
      String questionId = ((Question) (iter.next())).getId();
      goldenResult = goldenStandards.get(questionId);
    }

    // extract concepts, documents, triples
    List<Concept> concepts = Lists.newArrayList();
    List<Triple> triples = Lists.newArrayList();
    List<Document> documents = Lists.newArrayList();
    for (FeatureStructure f : aJCas.getAnnotationIndex(Concept.type)) {
      concepts.add((Concept) f);
    }
    for (FeatureStructure f : aJCas.getAnnotationIndex(Triple.type)) {
      concepts.add((Concept) f);
    }
    for (FeatureStructure f : aJCas.getAnnotationIndex(Document.type)) {
      documents.add((Document) f);
    }

    // compare

  }

  private void evaluateOrderedMeasure() {

  }

  private void evaluateUnorderedMeasure() {

  }
}

package edu.cmu.lti.f14.project.evaluator;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import json.gson.TestListQuestion;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cmu.lti.f14.project.util.Stats;
import edu.cmu.lti.oaqa.type.answer.Answer;
import edu.cmu.lti.oaqa.type.input.Question;

/**
 * Evaluator for the final answer
 */
public class FinalAnswerEvaluator extends JCasAnnotator_ImplBase {

  // constant for GMAP
  private static final double EPSILON = 0.01;

  private Map<String, json.gson.Question> goldenStandards;

  private List<Stats> ansStats = Lists.newArrayList();

  /**
   * initialization of the AE loads questions from the configuration
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    // TODO move to config file
    String filePath = "/BioASQ-trainingDataset2b-b.json";
    goldenStandards = Maps.newHashMap();
    List<TestQuestion> questions = Lists.newArrayList();

    try {
      List<? extends TestQuestion> gsonQuestions = TestSet.load(getClass().getResourceAsStream(
              String.class.cast(filePath)));
      for (TestQuestion q : gsonQuestions)
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
   * processes each question and outputs exact answers
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println("RUNNING FINAL ANSWER EVALUATOR");
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();
    TestListQuestion goldenResult = null;
    if (iter.hasNext()) {
      Question q = ((Question) (iter.next()));
      String questionId = q.getId();
      goldenResult = (TestListQuestion) goldenStandards.get(questionId);

      System.out.println("Query: " + q.getPreprocessedText());
    }

    if (goldenResult == null) {
      // cannot evaluate current question
      return;
    }

    // select answers associated with this question
    Collection<Answer> answers = JCasUtil.select(aJCas, Answer.class);

    // we don't care from which answer the exact answer comes from
    List<List<String>> nestedAnswers = goldenResult.getExactAnswer();
    List<String> goldenAnswers = Lists.newArrayList();
    for (List<String> ls : nestedAnswers) {
      goldenAnswers.add(ls.get(0));
    }

    System.out.println("golden:");
    for (String ans : goldenAnswers) {
      System.out.println("\t" + ans);
    }

    // directly evaluate the answers against the ground truth
    Stats ansStat = new Stats("answers", goldenAnswers, answers.stream().map(Answer::getText)
            .collect(toList()), true);

    // add this question
    ansStats.add(ansStat);

    System.out.println("answers:");
    int rank = 1;
    for (Answer ans : answers) {
      System.out.println("\t" + (rank++) + ":\t" + ans.getText());
    }

  }

  /**
   * when processing is complete, print stats.
   */
  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
    Stats.printStats(ansStats, "Answers", EPSILON);
  }
}

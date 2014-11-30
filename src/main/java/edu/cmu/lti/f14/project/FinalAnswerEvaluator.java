package edu.cmu.lti.f14.project;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import json.gson.TestListQuestion;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.cmu.lti.f14.project.util.NamedEntityChunker;
import edu.cmu.lti.f14.project.util.Stats;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
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

import static java.util.stream.Collectors.toList;

/**
 * Evaluator for intermediate results - Document, Concept and Triple
 */
public class FinalAnswerEvaluator extends JCasAnnotator_ImplBase {

  private static final double EPSILON = 0.01;

  private Map<String, json.gson.Question> goldenStandards;

  private List<Stats> docStats = Lists.newArrayList();

  private List<Stats> ansStats = Lists.newArrayList();

  private List<Stats> conceptStats = Lists.newArrayList();

  private List<Stats> tripStats = Lists.newArrayList();

  private List<Stats> snippetStats = Lists.newArrayList();

  /**
   * Initialize the golden results.
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
   *
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
      System.out.println("NEs in the query: "
              + Joiner.on(" ")
                      .join(NamedEntityChunker.getInstance().chunk(q.getPreprocessedText())));

    }

    if (goldenResult == null) {
      // cannot evaluate current question
      return;
    }

    Collection<Answer> answers = JCasUtil.select(aJCas, Answer.class);
    List<List<String>> goldenAnswers2 = goldenResult.getExactAnswer();
    List<String> goldenAnswers = Lists.newArrayList();
    for (List<String> ls : goldenAnswers2) {
      goldenAnswers.add(ls.get(0));
    }

    Collection<Document> documents = JCasUtil.select(aJCas, Document.class);
    Collection<ConceptSearchResult> concepts = JCasUtil.select(aJCas, ConceptSearchResult.class);

    Collection<Triple> triples = JCasUtil.select(aJCas, Triple.class);
    Collection<Passage> snippets = JCasUtil.select(aJCas, Passage.class);

    List<String> goldenDocuments = goldenResult.getDocuments();
    List<String> goldenConcepts = goldenResult.getConcepts();
    List<json.gson.Triple> goldenTriples = goldenResult.getTriples();
    List<json.gson.Snippet> goldenSnippets = goldenResult.getSnippets();

    if (goldenAnswers != null) {
      System.out.println("golden:");
      for(String ans: goldenAnswers) {
        System.out.println(ans);
      }
      
      Stats ansStat = new Stats("answers", goldenAnswers, answers.stream().map(Answer::getText)
              .collect(toList()));
      ansStats.add(ansStat);
    }
    
    System.out.println("answers:");
    for(Answer ans:answers) {
      System.out.println(ans.getText());
    }

    if (goldenDocuments != null) {
      Stats docStat = new Stats("documents", goldenDocuments, documents.stream()
              .map(Document::getUri).collect(toList()));
      docStats.add(docStat);
    }
    if (goldenConcepts != null) {
      Stats conceptStat = new Stats("concepts", goldenConcepts, concepts.stream()
              .map(ConceptSearchResult::getUri).map(i -> i).collect(toList()));
      conceptStats.add(conceptStat);
    }
    if (goldenTriples != null) {
      Stats tripStat = new Stats("triples", goldenTriples.stream().map(Object::toString)
              .collect(toList()), triples.stream().map(t -> new json.gson.Triple(t).toString())
              .collect(toList()));
      tripStats.add(tripStat);
    }
    if (goldenSnippets != null) {
      Stats snippetStat = new Stats("snippets", goldenSnippets.stream().map(Object::toString)
              .collect(toList()), snippets.stream().map(t -> new json.gson.Snippet(t).toString())
              .collect(toList()));
      snippetStats.add(snippetStat);
    }
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
    Stats.printStats(docStats, "Document", EPSILON);
    Stats.printStats(conceptStats, "Concept", EPSILON);
    Stats.printStats(tripStats, "Triple", EPSILON);
    Stats.printStats(snippetStats, "Snippet", EPSILON);
    Stats.printStats(ansStats, "Answers", EPSILON);
  }
}

package edu.cmu.lti.f14.project;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cmu.lti.f14.project.util.NamedEntityChunker;
import edu.cmu.lti.f14.project.util.Stats;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.Passage;

/**
 * Evaluator for intermediate results - Document, Concept and Triple
 */
public class InformationRetrievalEvaluator extends JCasAnnotator_ImplBase {

  private static final double EPSILON = 0.01;

  private Map<String, json.gson.Question> goldenStandards;

  private List<Stats> docStats = Lists.newArrayList();

  private List<Stats> conceptStats = Lists.newArrayList();

  private List<Stats> tripStats = Lists.newArrayList();

  private List<Stats> snippetStats = Lists.newArrayList();

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
    System.out.println("RUNNING INFORMATION RETRIEVAL EVALUATOR");
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();
    json.gson.Question goldenResult = null;
    if (iter.hasNext()) {
      Question q = ((Question) (iter.next()));
      String questionId = q.getId();
      goldenResult = goldenStandards.get(questionId);

      System.out.println("Query: " + q.getPreprocessedText());
      System.out.println("NEs in the query: "
              + Joiner.on(" ").join(NamedEntityChunker.getInstance().chunk(q.getPreprocessedText())));

    }

    if (goldenResult == null) {
      // cannot evaluate current question
      return;
    }

    Collection<Document> documents = JCasUtil.select(aJCas, Document.class);
    Collection<Concept> concepts = JCasUtil.select(aJCas, Concept.class);

    Collection<Triple> triples = JCasUtil.select(aJCas, Triple.class);
    Collection<Passage> snippets = JCasUtil.select(aJCas, Passage.class);

    List<String> goldenDocuments = goldenResult.getDocuments();
    List<String> goldenConcepts = goldenResult.getConcepts();
    List<json.gson.Triple> goldenTriples = goldenResult.getTriples();
    List<json.gson.Snippet> goldenSnippets = goldenResult.getSnippets();

    if (goldenDocuments != null) {
      Stats docStat = new Stats("documents", goldenDocuments, documents.stream()
              .map(Document::getUri).collect(toList()));
      docStats.add(docStat);
    }
    if (goldenConcepts != null) {
      Stats conceptStat = new Stats("concepts", goldenConcepts, concepts.stream()
              .map(Concept::getUris).map(i -> i.getNthElement(0)).collect(toList()));
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
  }

}
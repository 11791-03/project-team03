package edu.cmu.lti.f14.project;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.cmu.lti.f14.project.util.CosineSimilarity;
import edu.cmu.lti.f14.project.util.NamedEntityChunker;
import edu.cmu.lti.f14.project.util.Normalizer;
import edu.cmu.lti.f14.project.util.Similarity;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Snippet retrieval component in pipeline.
 *
 * @author junjiah
 */
public class SnippetRetrieval extends JCasAnnotator_ImplBase {

  private static final int TOP_K = 5;

  private static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;

  private static final SentenceModel SENTENCE_MODEL = new MedlineSentenceModel();

  private static final SentenceChunker SENTENCE_CHUNKER = new SentenceChunker(TOKENIZER_FACTORY,
          SENTENCE_MODEL);

  private static final double simWithQuestionWeight = 0.;

  private static final double simWithConceptsWeight = 1.5;

  private static final double simWithEntityWeight = 1.;

  private static Class similarityClass = CosineSimilarity.class;

  private JsonParser jsonParser = new JsonParser();

  private Similarity similarity;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    try {
      similarity = (Similarity) similarityClass.getConstructors()[0].newInstance();
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | SecurityException e) {
      e.printStackTrace();
    }
  }

  /**
   * Extract sentence snippets from document text.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println("RUNNING SNIPPET RETRIEVAL");
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String query = question.getPreprocessedText();

      List<String> nesInQuery = NamedEntityChunker.getInstance().chunk(query);

      Collection<Document> documents = JCasUtil.select(aJCas, Document.class);
      Collection<Concept> concepts = JCasUtil.select(aJCas, Concept.class);

      List<Sentence> sentences = new ArrayList<>();

      String concatenatedConcepts = concatenateConcepts(concepts);

      for (Document d : documents) {
        JsonObject textJson = jsonParser.parse(d.getText()).getAsJsonObject();
        JsonArray sections = textJson.getAsJsonArray("sections");

        for (int i = 0; i < sections.size(); ++i) {
          String text = sections.get(i).getAsString();
          if (text == null)
            continue;

          Chunking chunking = SENTENCE_CHUNKER.chunk(text.toCharArray(), 0, text.length());
          Set<Chunk> sentenceBoundaries = chunking.chunkSet();
          String slice = chunking.charSequence().toString();

          List<String> tokenList = new ArrayList<>();
          List<String> whiteList = new ArrayList<>();
          Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
          tokenizer.tokenize(tokenList, whiteList);

          String st;
          int start, end;
          double simWithQuestion, simWithConcepts, simWithEntities, score;
          for (Chunk sentenceBoundary : sentenceBoundaries) {
            start = sentenceBoundary.start();
            end = sentenceBoundary.end();
            st = slice.substring(start, end);
            String normalizedSentence = Normalizer.normalize(st);
            List<String> nesInSentence = NamedEntityChunker.getInstance().chunk(normalizedSentence);
            simWithEntities = similarity.computeSimilarity(Joiner.on(" ").join(nesInSentence),
                    Joiner.on(" ").join(nesInQuery));
            simWithQuestion = similarity.computeSimilarity(normalizedSentence, query);
            simWithConcepts = similarity
                    .computeSimilarity(normalizedSentence, concatenatedConcepts);
            score = simWithQuestionWeight * simWithQuestion + simWithConceptsWeight
                    * simWithConcepts + simWithEntityWeight * simWithEntities;
            sentences.add(new Sentence(sentenceBoundary, d, i, st, score));
          }
        }
      }
      Collections.sort(sentences);
      Sentence s;
      for (int i = 0; i < Math.min(TOP_K, sentences.size()); i++) {
        s = sentences.get(i);
        String sectionNumber = "sections." + s.sectionNumber;
        // TODO: consider Title!
        TypeFactory.createPassage(aJCas, s.referencedDocument.getUri(), s.text,
                s.referencedDocument.getDocId(), s.boundary.start(), s.boundary.end(),
                sectionNumber, sectionNumber).addToIndexes();
      }
    }
  }

  String concatenateConcepts(Collection<Concept> concepts) {
    return concepts
            .stream()
            .map(Concept::getName)
            .collect(Collectors.joining(" "));
  }

  private class Sentence implements Comparable<Sentence> {
    public Chunk boundary;

    public Document referencedDocument;

    public String text;

    public double score;

    public int sectionNumber;

    public Sentence(Chunk boundary, Document doc, int secNum, String t, double sim) {
      this.boundary = boundary;
      this.referencedDocument = doc;
      this.text = t;
      this.score = sim;
    }

    @Override
    public int compareTo(Sentence o) {
      return Double.compare(o.score, this.score);
    }
  }
}

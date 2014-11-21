package edu.cmu.lti.f14.project;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import util.TypeFactory;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.cmu.lti.f14.project.util.CosineSimilarity;
import edu.cmu.lti.f14.project.util.Similarity;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.retrieval.Document;

/**
 * Snippet retrieval component in pipeline.
 *
 * @author junjiah
 */
public class SnippetRetrieval extends JCasAnnotator_ImplBase {

  private static final String FULLTEXT_URI_PREFIX = "http://metal.lti.cs.cmu.edu:30002/pmc/";

  private static final int TOP_K = 5;

  private static final CloseableHttpClient httpClient = HttpClients.createDefault();

  private static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;

  private static final SentenceModel SENTENCE_MODEL = new MedlineSentenceModel();

  private static final SentenceChunker SENTENCE_CHUNKER = new SentenceChunker(TOKENIZER_FACTORY,
          SENTENCE_MODEL);

  private static final double simWithQuestionWeight = 0.5;
  
  private static final double simWithConceptsWeight = 0.5;
  
  private static Class similarityClass = CosineSimilarity.class;

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
   * simi Input the preprocessed texts to PubMed and retrieve the documents.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String query = question.getPreprocessedText();
      Collection<Document> documents = JCasUtil.select(aJCas, Document.class);
      Collection<Concept> concepts = JCasUtil.select(aJCas, Concept.class);

      List<Sentence> sentences = new ArrayList<>();

      String concatenatedConcepts = concatenateConcepts(concepts);
      
      for (Document d : documents) {
        String text = d.getText();
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
        double simWithQuestion, simWithConcepts, score;
        for (Chunk sentenceBoundary : sentenceBoundaries) {
          start = sentenceBoundary.start();
          end = sentenceBoundary.end();
          st = slice.substring(start, end);
          simWithQuestion = similarity.computeSimilarity(st, query);
//          simWithConcepts = similarity.computeSimilarity(st, concatenatedConcepts);
          simWithConcepts = 0;
          score = simWithQuestionWeight * simWithQuestion + simWithConceptsWeight * simWithConcepts;
          sentences.add(new Sentence(sentenceBoundary, d, st, score));
        }
      }
      Collections.sort(sentences);
      Sentence s;
      for (int i = 0; i < Math.min(TOP_K, sentences.size()); i++) {
        s = sentences.get(i);
        TypeFactory.createPassage(aJCas, s.referencedDocument.getUri(), s.text,
                s.referencedDocument.getDocId(),
                s.boundary.start(), s.boundary.end(), "ABSTRACT", "ABSTRACT").addToIndexes();
      }
    }
  }
  String concatenateConcepts(Collection<Concept> concepts) {
    Iterator<Concept> it = concepts.iterator();
    String res = "";
    while(it.hasNext())
      res += it.next().getName() + " ";
    return res;
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    try {
      httpClient.close();
    } catch (IOException ignored) {
    }

    super.collectionProcessComplete();
  }

  private class Sentence implements Comparable<Sentence> {
    public Chunk boundary;

    public Document referencedDocument;

    public String text;

    public double score;

    public Sentence(Chunk boundary, Document doc, String t, double sim) {
      this.boundary = boundary;
      this.referencedDocument = doc;
      this.text = t;
      this.score = sim;
    }

    @Override
    public int compareTo(Sentence o) {
      //TODO check this
      return this.score > o.score ? 1 : -1;
    }
  }
}

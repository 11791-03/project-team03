package edu.cmu.lti.f14.project;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.stanford.nlp.util.Maps;

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

      List<SentenceScore> sentenceScores = new ArrayList<>();

      for (Document d : documents) {
        String text = d.getText();
        if (text == null)
          continue;

        Chunking chunking = SENTENCE_CHUNKER.chunk(text.toCharArray(), 0, text.length());
        Set<Chunk> sentences = chunking.chunkSet();
        String slice = chunking.charSequence().toString();

        List<String> tokenList = new ArrayList<>();
        List<String> whiteList = new ArrayList<>();
        Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
        tokenizer.tokenize(tokenList, whiteList);

        String st = "";
        for (Chunk sentence : sentences) {
          int start = sentence.start();
          int end = sentence.end();
          // TODO: store into cache
          // System.out.println("SENTENCE -----:");
          // System.out.println(slice.substring(start, end));
          st = slice.substring(start, end);
          sentenceScores.add(new SentenceScore(sentence, d, st, similarity.computeSimilarity(st,
                  query)));
        }
      }
      Collections.sort(sentenceScores);
      SentenceScore s;
      for (int i = 0; i < Math.min(TOP_K, sentenceScores.size()); i++) {
        s = sentenceScores.get(i);
        TypeFactory.createPassage(aJCas, s.document.getUri(), s.text, s.document.getDocId(),
                s.sentence.start(), s.sentence.end(), "ABSTRACT", "ABSTRACT").addToIndexes();
      }
    }
  }

  private class SentenceScore implements Comparable<SentenceScore> {
    public Chunk sentence;

    public Document document;

    public String text;

    public double sim;

    public SentenceScore(Chunk sen, Document doc, String t, double sim) {
      this.sentence = sen;
      this.document = doc;
      this.text = t;
      this.sim = sim;
    }

    @Override
    public int compareTo(SentenceScore o) {
      return this.sim > o.sim ? 1 : -1;
    }
  }

  // for full texts, currently not useful

  /*
   * List<String> pmids = documents .stream() .map(Document::getDocId) .filter(Objects::nonNull)
   * .collect(toList());
   * 
   * for (String pmid : pmids) { String url = FULLTEXT_URI_PREFIX + pmid; HttpGet httpGet = new
   * HttpGet(url); try (CloseableHttpResponse response = httpClient.execute(httpGet)) { HttpEntity
   * entity = response.getEntity(); System.out.println(EntityUtils.toString(entity)); } catch
   * (IOException e) { e.printStackTrace(); } }
   */

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    try {
      httpClient.close();
    } catch (IOException ignored) {
    }

    super.collectionProcessComplete();
  }
}

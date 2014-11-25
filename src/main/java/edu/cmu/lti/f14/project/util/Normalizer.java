package edu.cmu.lti.f14.project.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import util.StanfordLemmatizer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.cmu.lti.oaqa.type.input.Question;

public class Normalizer extends JCasAnnotator_ImplBase {

  /**
   * Simply add tokenized text to the CAS.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      String text = question.getText();
      List<String> tokenizedText = tokenize0(text);
      question.setPreprocessedText(String.join(" ", tokenizedText));
    }
  }

  private Set<String> stopwords;

  /**
   * Set up a stop-word dictionary.
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    stopwords = Sets.newHashSet();

    // read stop-words from file
    InputStream stopwordsStream = getClass().getResourceAsStream("/stopwords.txt");
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(stopwordsStream));
      String line;
      while ((line = br.readLine()) != null) {
        stopwords.add(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (br != null)
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }

  /**
   * A simple tokenizer doing stemming and stop-word filtering.
   *
   * @param text
   *          Original text
   * @return Tokenized text
   */
  private List<String> tokenize0(String text) {
    List<String> res = Lists.newArrayList();
    StringTokenizer tokenizer = new StringTokenizer(text, " .,?!:;()<>[]\b\t\n\f\r\"\'\"");
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken().trim();
      if (!stopwords.contains(token))
        res.add(StanfordLemmatizer.stemWord(token));
    }
    return res;
  }

  private List<String> postagger(String text) {
    List<String> res = Lists.newArrayList();
    StanfordPosTagger pos = new StanfordPosTagger(
            "/Users/hanz/git/project-team03/src/main/java/edu/cmu/lti/f14/project/util/model", text);
    String temp = pos.doPOSTagging();
    StringTokenizer tokenizer = new StringTokenizer(temp);
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken().trim();
      int index = 0;
      for (int i = 0; i < token.length(); i++) {
        if (token.charAt(i) == '_') {
          index = i;
          break;
        }
      }
      res.add(token.substring(index, token.length()));
    }

    return res;
  }

}

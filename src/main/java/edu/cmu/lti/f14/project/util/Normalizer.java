package edu.cmu.lti.f14.project.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Normalizer {

  private static Set<String> stopwords = Sets.newHashSet();

  private static StanfordPosTagger posTagger;

  /**
   * Set up a stop-word dictionary.
   */
  public static void initialize() {
    // read stop-words from file
    InputStream stopwordsStream = Normalizer.class.getResourceAsStream("/stopwords.txt");
    try (BufferedReader br = new BufferedReader(new InputStreamReader(stopwordsStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        stopwords.add(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // initialize POS tagger
    posTagger = new StanfordPosTagger("postag-model");
  }

  /**
   * A simple tokenizer doing stemming and stop-word filtering.
   *
   * @param text Original text
   * @return Tokenized text
   */
  public static List<String> tokenize(String text) {
    List<String> res = Lists.newArrayList();
    StringTokenizer tokenizer = new StringTokenizer(text, " .,?!:;()<>[]\b\t\n\f\r\"\'\"");
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken().trim();
      if (!stopwords.contains(token))
        res.add(StanfordLemmatizer.stemWord(token));
    }
    return res;
  }

  /**
   * Retrieve nouns in a sentence.
   *
   * @param text Original text (without normalization)
   * @return A list of nouns
   */
  public static List<String> retrieveImportantWords(String text) {
    List<String> res = Lists.newArrayList();
    String temp = posTagger.doPOSTagging(text);
    StringTokenizer tokenizer = new StringTokenizer(temp);
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken().trim();
      int splitIndex = 0;
      for (int i = token.length() - 1; i > 0; --i) {
        if (token.charAt(i) == '_') {
          splitIndex = i + 1;
          break;
        }
      }

      String tag = token.substring(splitIndex, token.length());
      if (tag.startsWith("NN")) {
        res.add(token.substring(0, splitIndex - 1));
      }
    }
    return res;
  }

  /**
   * Do tokenization.
   *
   * @param text Original text
   * @return Concatenated tokenized words
   */
  public static String normalize(String text) {
    return String.join(" ", tokenize(text));
  }

}

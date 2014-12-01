package edu.cmu.lti.f14.project.service;

import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.NonReadableChannelException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Call our word2vec server to retrieve vectors or similarities.
 */
public class Word2VecService {
  /**
   * Web url to retrieve vectors.
   */
  private static final String VECTOR_QUERY_URL = "http://128.2.190.10:3000/?w=%s";

  /**
   * Web url to retrieve similarities.
   */
  private static final String SIMILARITY_QUERY_URL = "http://128.2.190.10:3000/sim?m=%s&n=%s";

  /**
   * A single HTTP client.
   */
  private static final CloseableHttpClient httpClient = HttpClients.createDefault();

  /**
   * A single service.
   */
  private static Word2VecService word2VecService = null;

  /**
   * Private constructor.
   */
  private Word2VecService() {
  }

  /**
   * Get the singleton class.
   *
   * @return Return the single instance
   */
  public static Word2VecService getInstance() {
    if (word2VecService == null) {
      word2VecService = new Word2VecService();
    }
    return word2VecService;
  }

  /**
   * Retrieve the word(s) vector.
   * @param words If only one then retrieve its vector, otherwise retrieve the mean
   *              average of all vectors
   * @return Word vector for requsted word(s). Null if doesn't exist.
   * @throws NonReadableChannelException Thrown when server is not running
   */
  public List<Double> getVector(String words) throws NonReadableChannelException {
    HttpGet httpGet = null;
    try {
      httpGet = new HttpGet(
              String.format(VECTOR_QUERY_URL, URLEncoder.encode(words.toLowerCase(), "UTF-8")));
    } catch (UnsupportedEncodingException ignored) {
    }
    String vectorString = "";
    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity entity = response.getEntity();
      final InputStreamReader reader = new InputStreamReader(entity.getContent());
      vectorString = CharStreams.toString(reader);
    } catch (Exception ignored) {
    }

    if (vectorString.isEmpty()) {
      throw new NonReadableChannelException();
    } else if (vectorString.startsWith("NOTHING")) {
      return null;
    } else {
      return Arrays.asList(vectorString.split(" "))
              .stream()
              .map(Double::parseDouble)
              .collect(toList());
    }
  }

  /**
   * Retrieve similarity between two strings (words or phrases or sentences).
   *
   * @param words1 First requested string
   * @param words2 Second requested string
   * @return Similarity between 2 strings
   * @throws NonReadableChannelException Thrown when server is not running
   */
  public double getSimilarity(String words1, String words2) throws NonReadableChannelException {
    HttpGet httpGet = null;
    try {
      httpGet = new HttpGet(
              String.format(SIMILARITY_QUERY_URL,
                      URLEncoder.encode(words1.toLowerCase(), "UTF-8"),
                      URLEncoder.encode(words2.toLowerCase(), "UTF-8")));
    } catch (UnsupportedEncodingException ignored) {
    }
    String similarityString = "";
    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity entity = response.getEntity();
      final InputStreamReader reader = new InputStreamReader(entity.getContent());
      similarityString = CharStreams.toString(reader);
    } catch (Exception e) {
      throw new NonReadableChannelException();
    }

    try {
      return Double.parseDouble(similarityString);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}

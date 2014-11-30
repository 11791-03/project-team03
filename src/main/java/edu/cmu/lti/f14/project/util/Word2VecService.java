package edu.cmu.lti.f14.project.util;

import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Word2VecService {
  private static final String QUERY_URL = "http://128.2.190.10:3000/?query=%s&n=%d";

  private static final CloseableHttpClient httpClient = HttpClients.createDefault();

  private static Word2VecService word2VecService = null;

  public static Word2VecService getInstance() {
    if (word2VecService == null) {
      word2VecService = new Word2VecService();
    }
    return word2VecService;
  }

  public List<Double> getVector(String words, int n) {
    HttpGet httpGet = null;
    try {
      httpGet = new HttpGet(
              String.format(QUERY_URL, URLEncoder.encode(words.toLowerCase(), "UTF-8"), n));
    } catch (UnsupportedEncodingException ignored) {
    }
    String vectorString = "";
    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity entity = response.getEntity();
      final InputStreamReader reader = new InputStreamReader(entity.getContent());
      vectorString = CharStreams.toString(reader);
    } catch (Exception ignored) {
    }

    if (!vectorString.equals("NO WORD")) {
      return Arrays.asList(vectorString.split(" "))
              .stream()
              .map(Double::parseDouble)
              .collect(toList());
    } else {
      return null;
    }
  }
}
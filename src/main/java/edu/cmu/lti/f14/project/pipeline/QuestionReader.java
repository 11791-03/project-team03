package edu.cmu.lti.f14.project.pipeline;

import com.google.common.collect.Lists;

import json.JsonCollectionReaderHelper;
import json.gson.QuestionType;
import json.gson.TestQuestion;
import json.gson.TestSet;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Extended CollectionReader to process the source document line by line, and separated the ID from
 * the text. Note the ID is passed to the CAS as an annotation.
 *
 * @author junjiah
 */
public class QuestionReader extends CollectionReader_ImplBase {

  /**
   * Indicates the location of input file. Mandatory.
   */
  public static final String PARAM_INPUTFILE = "InputFile";

  /**
   * Indicates the source of questions. Optional.
   */
  public static final String PARAM_SOURCE = "Source";

  /**
   * Questions source read from descriptor.
   */
  private static String questionSource;

  /**
   * Stores the questions read from the input json. Process them one by one during getNext().
   */
  private List<TestQuestion> questions;

  /**
   * Indicates the current index of questions.
   */
  private int currentIndex;

  /**
   * Read and store the source questions.
   *
   * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
   */
  @Override
  public void initialize() throws ResourceInitializationException {
    String[] filePath = ((String) getConfigParameterValue(PARAM_INPUTFILE)).split("\\s*,\\s*");
    questionSource = (String) getConfigParameterValue(PARAM_SOURCE);
    if (questionSource == null) // this is optional
      questionSource = "";

    currentIndex = 0;
    questions = Lists.newArrayList();

    try {
      questions = Arrays
              .stream(String[].class.cast(filePath))
              .flatMap(
                      path -> TestSet.load(
                              getClass().getResourceAsStream(path))
                              .stream())
                              .filter(input -> input.getType().equals(QuestionType.list))
                              .collect(toList());

      // trim question texts
      questions.stream()
              .filter(input -> input.getBody() != null)
              .forEach(
                      input -> input.setBody(input.getBody().trim()
                              .replaceAll("\\s+", " ")));
    } catch (Exception e) {
      throw new ResourceInitializationException(
              ResourceConfigurationException.RESOURCE_DATA_NOT_VALID, new Object[] {
              PARAM_INPUTFILE, this.getMetaData().getName(), filePath });
    }

  }

  /**
   * Extract the question then add to the indexes.
   *
   * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
   */
  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    JCas jCas;
    try {
      jCas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }

    TestQuestion testQuestion = questions.get(currentIndex++);

    JsonCollectionReaderHelper.addQuestionToIndex(testQuestion, questionSource, jCas);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return currentIndex < questions.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(currentIndex, questions.size(), Progress.ENTITIES) };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws IOException {

  }

}

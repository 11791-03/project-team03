package edu.cmu.lti.f14.project.pipeline;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Concept;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.io.IOException;
import java.util.*;

/**
 * Concept retrieval component.
 */
public class ConceptRetrieval extends JCasAnnotator_ImplBase {

  private GoPubMedService service;

  private Set<String> conceptsSoFar;

  private String processedQuestion;

  /**
   * @inheritDoc
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    try {
      service = new GoPubMedService("project.properties");
    } catch (ConfigurationException e) {
      System.err.println("ERROR: Initialize PubMed service error in Document Retrieval.");
      System.exit(1);
    }
  }

  /**
   * Used APIs to retrieve concepts.
   * @param aJCas CAS structure
   * @throws AnalysisEngineProcessException
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println("RUNNING CONCEPT RETRIEVAL");
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      int cPerPage = 100;

      conceptsSoFar = new HashSet<>();
      processedQuestion = question.getPreprocessedText();
      List<ScoredConcept> scoredConcepts = new ArrayList<>();
      try {
        OntologyServiceResponse.Result diseaseOntologyResult = service
                .findDiseaseOntologyEntitiesPaged(processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : diseaseOntologyResult.getFindings()) {
          scoredConcepts.add(new ScoredConcept(finding));
        }

        OntologyServiceResponse.Result geneOntologyResult = service.findGeneOntologyEntitiesPaged(
                processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : geneOntologyResult.getFindings()) {
          scoredConcepts.add(new ScoredConcept(finding));
        }
        OntologyServiceResponse.Result jochemResult = service.findJochemEntitiesPaged(
                processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : jochemResult.getFindings()) {
          scoredConcepts.add(new ScoredConcept(finding));
        }

        OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(
                processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : meshResult.getFindings()) {
          scoredConcepts.add(new ScoredConcept(finding));
        }

        OntologyServiceResponse.Result uniprotResult = service.findUniprotEntitiesPaged(
                processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : uniprotResult.getFindings()) {
          scoredConcepts.add(new ScoredConcept(finding));
        }
        Collections.sort(scoredConcepts);
        int rank = 1;
        for (ScoredConcept c : scoredConcepts) {
          createConcept(aJCas, c.finding.getConcept(), c.score, rank++);
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("ERROR: " + e.getMessage());
      }
    }
  }

  private void createConcept(JCas jcas, Concept c, double score, int rank) {
    if (score > 0.15) {
      conceptsSoFar.add(c.getLabel().toLowerCase());
      String uri = c.getUri().replace("2014", "2012");
      ConceptSearchResult conceptSearchResult = TypeFactory.createConceptSearchResult(jcas,
              TypeFactory.createConcept(jcas, c.getLabel(), uri), uri);
      conceptSearchResult.setRank(rank);
      conceptSearchResult.addToIndexes();
    }
  }

  private class ScoredConcept implements Comparable<ScoredConcept> {
    OntologyServiceResponse.Finding finding;

    double score;

    public ScoredConcept(Finding f) {
      this.finding = f;
      this.score = f.getScore();
    }

    @Override
    public int compareTo(ScoredConcept o) {
      return this.score > o.score ? -1 : 1;
    }
  }
}

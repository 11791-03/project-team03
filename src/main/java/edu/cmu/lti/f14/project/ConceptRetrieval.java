package edu.cmu.lti.f14.project;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Concept;
import edu.cmu.lti.oaqa.type.input.Question;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.io.IOException;

/**
 *
 */

public class ConceptRetrieval extends JCasAnnotator_ImplBase {

  private GoPubMedService service;

  /**
   *
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    /*
    try {
      service = new GoPubMedService("project.properties");
    } catch (ConfigurationException e) {
      System.err.println("ERROR: Initialize PubMed service error in Document Retrieval.");
      System.exit(1);
    }
    */
  }

  /**
   *
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    /*
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      try {
        OntologyServiceResponse.Result diseaseOntologyResult = service
                .findDiseaseOntologyEntitiesPaged(question.getPreprocessedText(), 0);
        for (OntologyServiceResponse.Finding finding : diseaseOntologyResult.getFindings()) {
          createConcept(aJCas, finding.getConcept());
        }

        OntologyServiceResponse.Result geneOntologyResult = service.findGeneOntologyEntitiesPaged(
                question.getPreprocessedText(), 0, 10);
        for (OntologyServiceResponse.Finding finding : geneOntologyResult.getFindings()) {
          createConcept(aJCas, finding.getConcept());
        }
        OntologyServiceResponse.Result jochemResult = service.findJochemEntitiesPaged(
                question.getPreprocessedText(), 0);
        for (OntologyServiceResponse.Finding finding : jochemResult.getFindings()) {
          createConcept(aJCas, finding.getConcept());
        }

        OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(
                question.getPreprocessedText(), 0);
        for (OntologyServiceResponse.Finding finding : meshResult.getFindings()) {
          createConcept(aJCas, finding.getConcept());
        }

        OntologyServiceResponse.Result uniprotResult = service.findUniprotEntitiesPaged(
                question.getPreprocessedText(), 0);
        for (OntologyServiceResponse.Finding finding : uniprotResult.getFindings()) {
          createConcept(aJCas, finding.getConcept());
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("ERROR: " + e.getMessage());
      }
    }
    */
  }

  private void createConcept(JCas jcas, Concept c) {
    /*
    TypeFactory.createConcept(jcas, c.getUri()).addToIndexes();
    */
  }
}

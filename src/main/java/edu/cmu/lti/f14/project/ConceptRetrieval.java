package edu.cmu.lti.f14.project;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import util.TypeFactory;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Concept;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Document;

/**
 *
 */
public class ConceptRetrieval extends JCasAnnotator_ImplBase {

  private GoPubMedService service;

  private Set<String> conceptsSoFar;

  private String processedQuestion;

  /**
   *
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
   *
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      Collection<Document> documents = JCasUtil.select(aJCas, Document.class);
      int cPerPage = 10;
      // for(Document d : documents)
      // {
      // System.out.println(d.toString());
      // }
      conceptsSoFar = new HashSet<String>();
      processedQuestion = question.getPreprocessedText();
      try {
        OntologyServiceResponse.Result diseaseOntologyResult = service
                .findDiseaseOntologyEntitiesPaged(processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : diseaseOntologyResult.getFindings()) {
          createConcept(aJCas, finding.getConcept(), finding.getScore());
        }

        OntologyServiceResponse.Result geneOntologyResult = service.findGeneOntologyEntitiesPaged(
                processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : geneOntologyResult.getFindings()) {
          createConcept(aJCas, finding.getConcept(), finding.getScore());
        }
        OntologyServiceResponse.Result jochemResult = service.findJochemEntitiesPaged(
                processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : jochemResult.getFindings()) {
          createConcept(aJCas, finding.getConcept(), finding.getScore());
        }

        OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(
                processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : meshResult.getFindings()) {
          createConcept(aJCas, finding.getConcept(), finding.getScore());
        }

        OntologyServiceResponse.Result uniprotResult = service.findUniprotEntitiesPaged(
                processedQuestion, 0, cPerPage);
        for (OntologyServiceResponse.Finding finding : uniprotResult.getFindings()) {
          createConcept(aJCas, finding.getConcept(), finding.getScore());
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("ERROR: " + e.getMessage());
      }
    }
  }

  private void createConcept(JCas jcas, Concept c, double score) {
     System.out.print(">" + c.getLabel());
     System.out.println("-->\t\t\t" + c.getUri());
    if (score > 0.5)
      if (validConcept(jcas, c.getLabel().toLowerCase())) {
        conceptsSoFar.add(c.getLabel().toLowerCase());
        TypeFactory.createConcept(jcas, c.getUri()).addToIndexes();
      }
  }

  private boolean validConcept(JCas jcas, String c) {
    // return processedQuestion.contains(c.toLowerCase());

    return true;

    // this enhances the precision but harm the recall
    // for(String t : processedQuestion.split(" "))
    // if(c.toLowerCase().contains(t))
    // return true;
    // return false;

    // Iterator<String> it = conceptsSoFar.iterator();
    // while (it.hasNext())
    // if (it.next().startsWith(c))
    // return false;
    // return true;
  }
}

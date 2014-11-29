package edu.cmu.lti.f14.project;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Concept;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.io.IOException;
import java.util.*;

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
    System.out.println("RUNNING CONCEPT RETRIEVAL");
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      Collection<Document> documents = JCasUtil.select(aJCas, Document.class);
      int cPerPage = 100;
      // for(Document d : documents)
      // {
      // System.out.println(d.toString());
      // }
      conceptsSoFar = new HashSet<String>();
      processedQuestion = question.getPreprocessedText();
      List<ScoredConcept> scoredConcepts = new ArrayList<ScoredConcept>();
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
        for (ScoredConcept c : scoredConcepts) {
          createConcept(aJCas, c.finding.getConcept(), c.score);
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("ERROR: " + e.getMessage());
      }
    }
  }

  private void createConcept(JCas jcas, Concept c, double score) {
    // System.out.println(">" + c.getLabel());
    // System.out.println("-->\t\t\t" + c.getUri());
    // score 0.1 - Concept - precision: 0.0178, recall: 0.1357, F1: 0.0314, MAP: 0.0655, GMAP:
    // 0.0311
    // score 0.15 - Concept - precision: 0.0364, recall: 0.0929, F1: 0.0523, MAP: 0.0841, GMAP:
    // 0.0321
    // System.out.println(score);
    if (score > 0.15) {
      // System.out.println("selected:\t" + score);
      if (validConcept(jcas, c.getLabel().toLowerCase())) {
        conceptsSoFar.add(c.getLabel().toLowerCase());
        String uri = c.getUri().replace("2014", "2012");
        TypeFactory.createConceptSearchResult(jcas,
                TypeFactory.createConcept(jcas, c.getLabel(), uri), uri).addToIndexes();
      }
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

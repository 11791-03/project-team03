package edu.cmu.lti.f14.project;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Concept;
import edu.cmu.lti.oaqa.type.input.Question;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import util.TypeFactory;

import java.io.IOException;

/**
 * @author junjiah
 */
public class ConceptRetrieval extends JCasAnnotator_ImplBase {

  /**
   *
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
  }

  ;

  /**
   *
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FeatureStructure featureStructure : aJCas.getAnnotationIndex(Question.type)) {
      Question question = (Question) featureStructure;
      GoPubMedService service;
      try {
        service = new GoPubMedService("project.properties");
        OntologyServiceResponse.Result diseaseOntologyResult = service
                .findDiseaseOntologyEntitiesPaged(question.getPreprocessedText(), 0);
        System.out.println("Disease ontology: " + diseaseOntologyResult.getFindings().size());
        for (OntologyServiceResponse.Finding finding : diseaseOntologyResult.getFindings()) {
          createConcept(aJCas, finding.getConcept());
        }
//        OntologyServiceResponse.Result geneOntologyResult = service.findGeneOntologyEntitiesPaged(
//                question.getPreprocessedText(), 0, 10);
//        System.out.println("Gene ontology: " + geneOntologyResult.getFindings().size());
//        for (OntologyServiceResponse.Finding finding : geneOntologyResult.getFindings()) {
//          createConcept(aJCas, finding.getConcept());
//        }
//        OntologyServiceResponse.Result jochemResult = service.findJochemEntitiesPaged(
//                question.getPreprocessedText(), 0);
//        System.out.println("Jochem: " + jochemResult.getFindings().size());
//        for (OntologyServiceResponse.Finding finding : jochemResult.getFindings()) {
//          createConcept(aJCas, finding.getConcept());
//        }
//        OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(
//                question.getPreprocessedText(), 0);
//        System.out.println("MeSH: " + meshResult.getFindings().size());
//        for (OntologyServiceResponse.Finding finding : meshResult.getFindings()) {
//          createConcept(aJCas, finding.getConcept());
//        }
//        OntologyServiceResponse.Result uniprotResult = service.findUniprotEntitiesPaged(
//                question.getPreprocessedText(), 0);
//        System.out.println("UniProt: " + uniprotResult.getFindings().size());
//        for (OntologyServiceResponse.Finding finding : uniprotResult.getFindings()) {
//          createConcept(aJCas, finding.getConcept());
//        }
      } catch (ConfigurationException e) {
        e.printStackTrace();
      } catch (ClientProtocolException e) {
        e.printStackTrace();
      } catch (IOException e) {
        // TODyO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  private void createConcept(JCas jcas, Concept c) {
    TypeFactory.createConcept(jcas, c.getUri()).addToIndexes();
    System.out.println(" > " + c.getLabel() + " " + c.getUri());
  }
}

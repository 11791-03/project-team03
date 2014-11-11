package edu.cmu.lti.f14.project;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;

/**
 * @author junjiah
 *
 */
public class QuestionPreprocessing extends JCasAnnotator_ImplBase {

  /**
   *
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
  };

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
                .findDiseaseOntologyEntitiesPaged(question.getText(), 0);
        System.out.println("Disease ontology: " + diseaseOntologyResult.getFindings().size());
        for (OntologyServiceResponse.Finding finding : diseaseOntologyResult.getFindings()) {
          
          System.out.println(" > " + finding.getConcept().getLabel() + " "
                  + finding.getConcept().getUri());
        }
      } catch (ConfigurationException e) {
        e.printStackTrace();
      } catch (ClientProtocolException e) {
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }
}

package edu.cmu.lti.f14.project.util;

import gov.nih.nlm.uts.webservice.finder.Psf;
import gov.nih.nlm.uts.webservice.finder.UiLabel;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderController;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsFault_Exception;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class UmlsService {

  private static final String PASSWORD;

  private static final String UMLS_RELEASE = "2014AB";

  private static final String SERVICE_NAME = "http://umlsks.nlm.nih.gov";

  private static final UtsWsFinderController utsFinderService = (new UtsWsFinderControllerImplService())
          .getUtsWsFinderControllerImplPort();

  private static final UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService())
          .getUtsWsSecurityControllerImplPort();

  private static String ticketGrantingTicket;

  private static UmlsService umlsService = null;

  static {
    String pw;
    try {
      pw = new String(Base64.decodeBase64("YWJjZGVmZw=="), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      pw = "";
    }
    PASSWORD = pw;
  }

  private UmlsService() {
    try {
      ticketGrantingTicket = securityService.getProxyGrantTicket("junjiah", PASSWORD);
    } catch (UtsFault_Exception e) {
      e.printStackTrace();
    }
  }

  private static UmlsService getInstance() {
    if (umlsService == null)
      umlsService = new UmlsService();
    return umlsService;
  }

  public List<String> getSynonyms(String searchWord)
          throws gov.nih.nlm.uts.webservice.finder.UtsFault_Exception, UtsFault_Exception {
    String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, SERVICE_NAME);
    Psf myPsf = new Psf();
    myPsf.setPageLn(50);
    List<UiLabel> uiLabels = utsFinderService.findAtoms(singleUseTicket, UMLS_RELEASE, "atom",
            searchWord, "words", myPsf);
    return uiLabels.stream().map(UiLabel::getLabel).collect(toList());
  }
}

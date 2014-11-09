/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.retrieval;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * A passage search result.
 * Updated by JCasGen Sat Oct 18 19:40:19 EDT 2014
 *
 * @generated
 */
public class Passage_Type extends SearchResult_Type {
  /**
   * @return the generator for this type
   * @generated
   */
  @Override
  protected FSGenerator getFSGenerator() {
    return fsGenerator;
  }

  /**
   * @generated
   */
  private final FSGenerator fsGenerator =
          new FSGenerator() {
            public FeatureStructure createFS(int addr, CASImpl cas) {
              if (Passage_Type.this.useExistingInstance) {
                // Return eq fs instance if already created
                FeatureStructure fs = Passage_Type.this.jcas.getJfsFromCaddr(addr);
                if (null == fs) {
                  fs = new Passage(addr, Passage_Type.this);
                  Passage_Type.this.jcas.putJfsFromCaddr(addr, fs);
                  return fs;
                }
                return fs;
              } else
                return new Passage(addr, Passage_Type.this);
            }
          };

  /**
   * @generated
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = Passage.typeIndexID;

  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public final static boolean featOkTst = JCasRegistry
          .getFeatOkTst("edu.cmu.lti.oaqa.type.retrieval.Passage");

  /**
   * @generated
   */
  final Feature casFeat_title;

  /**
   * @generated
   */
  final int casFeatCode_title;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public String getTitle(int addr) {
    if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_title);
  }

  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setTitle(int addr, String v) {
    if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    ll_cas.ll_setStringValue(addr, casFeatCode_title, v);
  }

  /**
   * @generated
   */
  final Feature casFeat_docId;

  /**
   * @generated
   */
  final int casFeatCode_docId;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public String getDocId(int addr) {
    if (featOkTst && casFeat_docId == null)
      jcas.throwFeatMissing("docId", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_docId);
  }

  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setDocId(int addr, String v) {
    if (featOkTst && casFeat_docId == null)
      jcas.throwFeatMissing("docId", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    ll_cas.ll_setStringValue(addr, casFeatCode_docId, v);
  }

  /**
   * @generated
   */
  final Feature casFeat_offsetInBeginSection;

  /**
   * @generated
   */
  final int casFeatCode_offsetInBeginSection;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public int getOffsetInBeginSection(int addr) {
    if (featOkTst && casFeat_offsetInBeginSection == null)
      jcas.throwFeatMissing("offsetInBeginSection", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    return ll_cas.ll_getIntValue(addr, casFeatCode_offsetInBeginSection);
  }

  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setOffsetInBeginSection(int addr, int v) {
    if (featOkTst && casFeat_offsetInBeginSection == null)
      jcas.throwFeatMissing("offsetInBeginSection", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    ll_cas.ll_setIntValue(addr, casFeatCode_offsetInBeginSection, v);
  }

  /**
   * @generated
   */
  final Feature casFeat_offsetInEndSection;

  /**
   * @generated
   */
  final int casFeatCode_offsetInEndSection;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public int getOffsetInEndSection(int addr) {
    if (featOkTst && casFeat_offsetInEndSection == null)
      jcas.throwFeatMissing("offsetInEndSection", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    return ll_cas.ll_getIntValue(addr, casFeatCode_offsetInEndSection);
  }

  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setOffsetInEndSection(int addr, int v) {
    if (featOkTst && casFeat_offsetInEndSection == null)
      jcas.throwFeatMissing("offsetInEndSection", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    ll_cas.ll_setIntValue(addr, casFeatCode_offsetInEndSection, v);
  }

  /**
   * @generated
   */
  final Feature casFeat_beginSection;

  /**
   * @generated
   */
  final int casFeatCode_beginSection;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public String getBeginSection(int addr) {
    if (featOkTst && casFeat_beginSection == null)
      jcas.throwFeatMissing("beginSection", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_beginSection);
  }

  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setBeginSection(int addr, String v) {
    if (featOkTst && casFeat_beginSection == null)
      jcas.throwFeatMissing("beginSection", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    ll_cas.ll_setStringValue(addr, casFeatCode_beginSection, v);
  }

  /**
   * @generated
   */
  final Feature casFeat_endSection;

  /**
   * @generated
   */
  final int casFeatCode_endSection;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public String getEndSection(int addr) {
    if (featOkTst && casFeat_endSection == null)
      jcas.throwFeatMissing("endSection", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_endSection);
  }

  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setEndSection(int addr, String v) {
    if (featOkTst && casFeat_endSection == null)
      jcas.throwFeatMissing("endSection", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    ll_cas.ll_setStringValue(addr, casFeatCode_endSection, v);
  }

  /**
   * @generated
   */
  final Feature casFeat_aspects;

  /**
   * @generated
   */
  final int casFeatCode_aspects;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public String getAspects(int addr) {
    if (featOkTst && casFeat_aspects == null)
      jcas.throwFeatMissing("aspects", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_aspects);
  }

  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setAspects(int addr, String v) {
    if (featOkTst && casFeat_aspects == null)
      jcas.throwFeatMissing("aspects", "edu.cmu.lti.oaqa.type.retrieval.Passage");
    ll_cas.ll_setStringValue(addr, casFeatCode_aspects, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @param jcas    JCas
   * @param casType Type
   * @generated
   */
  public Passage_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "uima.cas.String", featOkTst);
    casFeatCode_title = (null == casFeat_title) ?
            JCas.INVALID_FEATURE_CODE :
            ((FeatureImpl) casFeat_title).getCode();

    casFeat_docId = jcas.getRequiredFeatureDE(casType, "docId", "uima.cas.String", featOkTst);
    casFeatCode_docId = (null == casFeat_docId) ?
            JCas.INVALID_FEATURE_CODE :
            ((FeatureImpl) casFeat_docId).getCode();

    casFeat_offsetInBeginSection = jcas
            .getRequiredFeatureDE(casType, "offsetInBeginSection", "uima.cas.Integer", featOkTst);
    casFeatCode_offsetInBeginSection = (null == casFeat_offsetInBeginSection) ?
            JCas.INVALID_FEATURE_CODE :
            ((FeatureImpl) casFeat_offsetInBeginSection).getCode();

    casFeat_offsetInEndSection = jcas
            .getRequiredFeatureDE(casType, "offsetInEndSection", "uima.cas.Integer", featOkTst);
    casFeatCode_offsetInEndSection = (null == casFeat_offsetInEndSection) ?
            JCas.INVALID_FEATURE_CODE :
            ((FeatureImpl) casFeat_offsetInEndSection).getCode();

    casFeat_beginSection = jcas
            .getRequiredFeatureDE(casType, "beginSection", "uima.cas.String", featOkTst);
    casFeatCode_beginSection = (null == casFeat_beginSection) ?
            JCas.INVALID_FEATURE_CODE :
            ((FeatureImpl) casFeat_beginSection).getCode();

    casFeat_endSection = jcas
            .getRequiredFeatureDE(casType, "endSection", "uima.cas.String", featOkTst);
    casFeatCode_endSection = (null == casFeat_endSection) ?
            JCas.INVALID_FEATURE_CODE :
            ((FeatureImpl) casFeat_endSection).getCode();

    casFeat_aspects = jcas.getRequiredFeatureDE(casType, "aspects", "uima.cas.String", featOkTst);
    casFeatCode_aspects = (null == casFeat_aspects) ?
            JCas.INVALID_FEATURE_CODE :
            ((FeatureImpl) casFeat_aspects).getCode();

  }
}



    
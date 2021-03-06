/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.input;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** The Question and any associated meta-data.
 * Updated by JCasGen Mon Nov 10 20:27:49 EST 2014
 * @generated */
public class Question_Type extends Annotation_Type {
  /**
   * @return the generator for this type
   * @generated
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /**
   * @generated
   */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Question_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Question_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Question(addr, Question_Type.this);
  			   Question_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Question(addr, Question_Type.this);
  	  }
    };

  /**
   * @generated
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = Question.typeIndexID;

  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.type.input.Question");

  /**
   * @generated
   */
  final Feature casFeat_id;

  /**
   * @generated
   */
  final int casFeatCode_id;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public String getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "edu.cmu.lti.oaqa.type.input.Question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }
  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "edu.cmu.lti.oaqa.type.input.Question");
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);}
    
  
 
  /**
   * @generated
   */
  final Feature casFeat_source;

  /**
   * @generated
   */
  final int casFeatCode_source;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public String getSource(int addr) {
        if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "edu.cmu.lti.oaqa.type.input.Question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_source);
  }
  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setSource(int addr, String v) {
        if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "edu.cmu.lti.oaqa.type.input.Question");
    ll_cas.ll_setStringValue(addr, casFeatCode_source, v);}
    
  
 
  /**
   * @generated
   */
  final Feature casFeat_questionType;

  /**
   * @generated
   */
  final int casFeatCode_questionType;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public String getQuestionType(int addr) {
        if (featOkTst && casFeat_questionType == null)
      jcas.throwFeatMissing("questionType", "edu.cmu.lti.oaqa.type.input.Question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_questionType);
  }
  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setQuestionType(int addr, String v) {
        if (featOkTst && casFeat_questionType == null)
      jcas.throwFeatMissing("questionType", "edu.cmu.lti.oaqa.type.input.Question");
    ll_cas.ll_setStringValue(addr, casFeatCode_questionType, v);}
    
  
 
  /**
   * @generated
   */
  final Feature casFeat_text;

  /**
   * @generated
   */
  final int casFeatCode_text;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public String getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "edu.cmu.lti.oaqa.type.input.Question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "edu.cmu.lti.oaqa.type.input.Question");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  
 
  /** @generated */
  final Feature casFeat_preprocessedText;
  /** @generated */
  final int     casFeatCode_preprocessedText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPreprocessedText(int addr) {
        if (featOkTst && casFeat_preprocessedText == null)
      jcas.throwFeatMissing("preprocessedText", "edu.cmu.lti.oaqa.type.input.Question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_preprocessedText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPreprocessedText(int addr, String v) {
        if (featOkTst && casFeat_preprocessedText == null)
      jcas.throwFeatMissing("preprocessedText", "edu.cmu.lti.oaqa.type.input.Question");
    ll_cas.ll_setStringValue(addr, casFeatCode_preprocessedText, v);}
    
  



  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @param jcas    JCas
   * @param casType Type
   * @generated
   */
  public Question_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

 
    casFeat_source = jcas.getRequiredFeatureDE(casType, "source", "uima.cas.String", featOkTst);
    casFeatCode_source  = (null == casFeat_source) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_source).getCode();

 
    casFeat_questionType = jcas.getRequiredFeatureDE(casType, "questionType", "edu.cmu.lti.oaqa.type.input.QuestionType", featOkTst);
    casFeatCode_questionType  = (null == casFeat_questionType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_questionType).getCode();

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

 
    casFeat_preprocessedText = jcas.getRequiredFeatureDE(casType, "preprocessedText", "uima.cas.String", featOkTst);
    casFeatCode_preprocessedText  = (null == casFeat_preprocessedText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_preprocessedText).getCode();

  }
}



    
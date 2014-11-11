/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.answer;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/** An exact answer text generated by the QA system.
 * Updated by JCasGen Mon Nov 10 20:27:49 EST 2014
 * @generated */
public class Answer_Type extends TOP_Type {
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
  			 if (Answer_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Answer_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Answer(addr, Answer_Type.this);
  			   Answer_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Answer(addr, Answer_Type.this);
  	  }
    };

  /**
   * @generated
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = Answer.typeIndexID;

  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.type.answer.Answer");

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
      jcas.throwFeatMissing("text", "edu.cmu.lti.oaqa.type.answer.Answer");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "edu.cmu.lti.oaqa.type.answer.Answer");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  
 
  /**
   * @generated
   */
  final Feature casFeat_variants;

  /**
   * @generated
   */
  final int casFeatCode_variants;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public int getVariants(int addr) {
        if (featOkTst && casFeat_variants == null)
      jcas.throwFeatMissing("variants", "edu.cmu.lti.oaqa.type.answer.Answer");
    return ll_cas.ll_getRefValue(addr, casFeatCode_variants);
  }
  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setVariants(int addr, int v) {
        if (featOkTst && casFeat_variants == null)
      jcas.throwFeatMissing("variants", "edu.cmu.lti.oaqa.type.answer.Answer");
    ll_cas.ll_setRefValue(addr, casFeatCode_variants, v);}
    
  
 
  /**
   * @generated
   */
  final Feature casFeat_rank;

  /**
   * @generated
   */
  final int casFeatCode_rank;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public int getRank(int addr) {
        if (featOkTst && casFeat_rank == null)
      jcas.throwFeatMissing("rank", "edu.cmu.lti.oaqa.type.answer.Answer");
    return ll_cas.ll_getIntValue(addr, casFeatCode_rank);
  }
  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setRank(int addr, int v) {
        if (featOkTst && casFeat_rank == null)
      jcas.throwFeatMissing("rank", "edu.cmu.lti.oaqa.type.answer.Answer");
    ll_cas.ll_setIntValue(addr, casFeatCode_rank, v);}
    
  



  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @param jcas    JCas
   * @param casType Type
   * @generated
   */
  public Answer_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

 
    casFeat_variants = jcas.getRequiredFeatureDE(casType, "variants", "uima.cas.StringList", featOkTst);
    casFeatCode_variants  = (null == casFeat_variants) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_variants).getCode();

 
    casFeat_rank = jcas.getRequiredFeatureDE(casType, "rank", "uima.cas.Integer", featOkTst);
    casFeatCode_rank  = (null == casFeat_rank) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_rank).getCode();

  }
}



    
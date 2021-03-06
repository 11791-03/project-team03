/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.kb;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.cas.TOP_Type;

/** A container that integrates Concepts and ConceptMentions.
 * Updated by JCasGen Mon Nov 10 20:27:49 EST 2014
 * XML source: /home/gowayyed/workspace/11791/project-team03/src/main/resources/type/OAQATypes.xml
 * @generated */
public class Interpretation extends TOP {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = JCasRegistry.register(Interpretation.class);

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public final static int type = typeIndexID;

  /**
   * @return index of the type
   * @generated
   */
  @Override
  public int getTypeIndexID() {return typeIndexID;}
 
  /**
   * Never called.  Disable default constructor
   *
   * @generated
   */
  protected Interpretation() {/* intentionally empty block */}
    
  /**
   * Internal - constructor used by generator
   *
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   * @generated
   */
  public Interpretation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /**
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public Interpretation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}

  //*--------------*
  //* Feature: concepts

  /**
   * getter for concepts - gets A list of all concepts mentioned in the corresponding text.
   *
   * @return value of the feature
   * @generated
   */
  public FSList getConcepts() {
    if (Interpretation_Type.featOkTst && ((Interpretation_Type)jcasType).casFeat_concepts == null)
      jcasType.jcas.throwFeatMissing("concepts", "edu.cmu.lti.oaqa.type.kb.Interpretation");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Interpretation_Type)jcasType).casFeatCode_concepts)));}
    
  /**
   * setter for concepts - sets A list of all concepts mentioned in the corresponding text.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setConcepts(FSList v) {
    if (Interpretation_Type.featOkTst && ((Interpretation_Type)jcasType).casFeat_concepts == null)
      jcasType.jcas.throwFeatMissing("concepts", "edu.cmu.lti.oaqa.type.kb.Interpretation");
    jcasType.ll_cas.ll_setRefValue(addr, ((Interpretation_Type)jcasType).casFeatCode_concepts, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: mentions

  /**
   * getter for mentions - gets A list of all mentions in the text.
   *
   * @return value of the feature
   * @generated
   */
  public FSList getMentions() {
    if (Interpretation_Type.featOkTst && ((Interpretation_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "edu.cmu.lti.oaqa.type.kb.Interpretation");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Interpretation_Type)jcasType).casFeatCode_mentions)));}
    
  /**
   * setter for mentions - sets A list of all mentions in the text.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setMentions(FSList v) {
    if (Interpretation_Type.featOkTst && ((Interpretation_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "edu.cmu.lti.oaqa.type.kb.Interpretation");
    jcasType.ll_cas.ll_setRefValue(addr, ((Interpretation_Type)jcasType).casFeatCode_mentions, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    
/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.nlp;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/** The phrase in the question that indicates the answer variable.
 * Updated by JCasGen Mon Nov 10 20:27:49 EST 2014
 * XML source: /home/gowayyed/workspace/11791/project-team03/src/main/resources/type/OAQATypes.xml
 * @generated */
public class Focus extends Annotation {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = JCasRegistry.register(Focus.class);

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
  protected Focus() {/* intentionally empty block */}
    
  /**
   * Internal - constructor used by generator
   *
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   * @generated
   */
  public Focus(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /**
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public Focus(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /**
   * @param jcas  JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end   offset to the end spot in the SofA
   * @generated
   */
  public Focus(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
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
  //* Feature: token

  /**
   * getter for token - gets The corresponding token for the focus.
   *
   * @return value of the feature
   * @generated
   */
  public Token getToken() {
    if (Focus_Type.featOkTst && ((Focus_Type)jcasType).casFeat_token == null)
      jcasType.jcas.throwFeatMissing("token", "edu.cmu.lti.oaqa.type.nlp.Focus");
    return (Token)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Focus_Type)jcasType).casFeatCode_token)));}
    
  /**
   * setter for token - sets The corresponding token for the focus.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setToken(Token v) {
    if (Focus_Type.featOkTst && ((Focus_Type)jcasType).casFeat_token == null)
      jcasType.jcas.throwFeatMissing("token", "edu.cmu.lti.oaqa.type.nlp.Focus");
    jcasType.ll_cas.ll_setRefValue(addr, ((Focus_Type)jcasType).casFeatCode_token, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: label

  /**
   * getter for label - gets String representation of the Question Focus.
   *
   * @return value of the feature
   * @generated
   */
  public String getLabel() {
    if (Focus_Type.featOkTst && ((Focus_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "edu.cmu.lti.oaqa.type.nlp.Focus");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Focus_Type)jcasType).casFeatCode_label);}
    
  /**
   * setter for label - sets String representation of the Question Focus.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setLabel(String v) {
    if (Focus_Type.featOkTst && ((Focus_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "edu.cmu.lti.oaqa.type.nlp.Focus");
    jcasType.ll_cas.ll_setStringValue(addr, ((Focus_Type)jcasType).casFeatCode_label, v);}    
  }

    
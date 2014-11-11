/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.input;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/** The Question and any associated meta-data.
 * Updated by JCasGen Mon Nov 10 20:23:29 EST 2014
 * XML source: /home/gowayyed/workspace/11791/project-team03/src/main/resources/type/OAQATypes.xml
 * @generated */
public class Question extends Annotation {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = JCasRegistry.register(Question.class);

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
  protected Question() {/* intentionally empty block */}
    
  /**
   * Internal - constructor used by generator
   *
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   * @generated
   */
  public Question(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /**
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public Question(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /**
   * @param jcas  JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end   offset to the end spot in the SofA
   * @generated
   */
  public Question(JCas jcas, int begin, int end) {
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
  //* Feature: id

  /**
   * getter for id - gets A unique id for the question.
   *
   * @return value of the feature
   * @generated
   */
  public String getId() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "edu.cmu.lti.oaqa.type.input.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_id);}
    
  /**
   * setter for id - sets A unique id for the question.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setId(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "edu.cmu.lti.oaqa.type.input.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: source

  /**
   * getter for source - gets The source of the question, e.g., TREC11, Jeopardy, etc.
   *
   * @return value of the feature
   * @generated
   */
  public String getSource() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "edu.cmu.lti.oaqa.type.input.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_source);}
    
  /**
   * setter for source - sets The source of the question, e.g., TREC11, Jeopardy, etc.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setSource(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "edu.cmu.lti.oaqa.type.input.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_source, v);}    
   
    
  //*--------------*
  //* Feature: questionType

  /**
   * getter for questionType - gets The class of the question, determined by either an automatic question classification process or human judgment.
   *
   * @return value of the feature
   * @generated
   */
  public String getQuestionType() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_questionType == null)
      jcasType.jcas.throwFeatMissing("questionType", "edu.cmu.lti.oaqa.type.input.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_questionType);}
    
  /**
   * setter for questionType - sets The class of the question, determined by either an automatic question classification process or human judgment.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setQuestionType(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_questionType == null)
      jcasType.jcas.throwFeatMissing("questionType", "edu.cmu.lti.oaqa.type.input.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_questionType, v);}    
   
    
  //*--------------*
  //* Feature: text

  /**
   * getter for text - gets The question content.
   *
   * @return value of the feature
   * @generated
   */
  public String getText() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.oaqa.type.input.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_text);}
    
  /**
   * setter for text - sets The question content.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setText(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.oaqa.type.input.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: keywords

  /** getter for keywords - gets 
   * @generated
   * @return value of the feature 
   */
  public String getKeywords() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_keywords == null)
      jcasType.jcas.throwFeatMissing("keywords", "edu.cmu.lti.oaqa.type.input.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_keywords);}
    
  /** setter for keywords - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setKeywords(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_keywords == null)
      jcasType.jcas.throwFeatMissing("keywords", "edu.cmu.lti.oaqa.type.input.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_keywords, v);}    
  }

    
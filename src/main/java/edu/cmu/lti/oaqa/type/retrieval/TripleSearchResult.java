/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.retrieval;

import edu.cmu.lti.oaqa.type.kb.Triple;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/** A search result from a triple store, e.g., an RDF store.
 * Updated by JCasGen Mon Nov 10 20:27:49 EST 2014
 * XML source: /home/gowayyed/workspace/11791/project-team03/src/main/resources/type/OAQATypes.xml
 * @generated */
public class TripleSearchResult extends AnswerSearchResult {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = JCasRegistry.register(TripleSearchResult.class);

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
  protected TripleSearchResult() {/* intentionally empty block */}
    
  /**
   * Internal - constructor used by generator
   *
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   * @generated
   */
  public TripleSearchResult(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /**
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public TripleSearchResult(JCas jcas) {
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
  //* Feature: triple

  /**
   * getter for triple - gets The relevant triple searched in the RDF store.
   *
   * @return value of the feature
   * @generated
   */
  public Triple getTriple() {
    if (TripleSearchResult_Type.featOkTst && ((TripleSearchResult_Type)jcasType).casFeat_triple == null)
      jcasType.jcas.throwFeatMissing("triple", "edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult");
    return (Triple)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((TripleSearchResult_Type)jcasType).casFeatCode_triple)));}
    
  /**
   * setter for triple - sets The relevant triple searched in the RDF store.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setTriple(Triple v) {
    if (TripleSearchResult_Type.featOkTst && ((TripleSearchResult_Type)jcasType).casFeat_triple == null)
      jcasType.jcas.throwFeatMissing("triple", "edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult");
    jcasType.ll_cas.ll_setRefValue(addr, ((TripleSearchResult_Type)jcasType).casFeatCode_triple, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    
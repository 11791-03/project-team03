/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.kb;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/** A named entity mention that identify or define the named entity concept.
 * Updated by JCasGen Mon Nov 10 20:23:30 EST 2014
 * XML source: /home/gowayyed/workspace/11791/project-team03/src/main/resources/type/OAQATypes.xml
 * @generated */
public class EntityMention extends ConceptMention {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = JCasRegistry.register(EntityMention.class);

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
  protected EntityMention() {/* intentionally empty block */}
    
  /**
   * Internal - constructor used by generator
   *
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   * @generated
   */
  public EntityMention(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /**
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public EntityMention(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /**
   * @param jcas  JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end   offset to the end spot in the SofA
   * @generated
   */
  public EntityMention(JCas jcas, int begin, int end) {
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

}

    
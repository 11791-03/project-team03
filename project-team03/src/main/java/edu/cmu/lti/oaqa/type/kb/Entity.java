/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.kb;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * An abstract normalized entity, which may have various surface forms (alternative representations).
 * Updated by JCasGen Sat Oct 18 19:40:19 EDT 2014
 * XML source: /home/mog/dev/11791/project/project-team03-archetype/src/main/resources/type/OAQATypes.xml
 *
 * @generated
 */
public class Entity extends Concept {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = JCasRegistry.register(Entity.class);

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
  public int getTypeIndexID() {
    return typeIndexID;
  }

  /**
   * Never called.  Disable default constructor
   *
   * @generated
   */
  protected Entity() {/* intentionally empty block */}

  /**
   * Internal - constructor used by generator
   *
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   * @generated
   */
  public Entity(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public Entity(JCas jcas) {
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

}

    
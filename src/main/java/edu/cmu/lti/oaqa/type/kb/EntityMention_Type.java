/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.kb;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * A named entity mention that identify or define the named entity concept.
 * Updated by JCasGen Sat Oct 18 19:40:19 EDT 2014
 *
 * @generated
 */
public class EntityMention_Type extends ConceptMention_Type {
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
              if (EntityMention_Type.this.useExistingInstance) {
                // Return eq fs instance if already created
                FeatureStructure fs = EntityMention_Type.this.jcas.getJfsFromCaddr(addr);
                if (null == fs) {
                  fs = new EntityMention(addr, EntityMention_Type.this);
                  EntityMention_Type.this.jcas.putJfsFromCaddr(addr, fs);
                  return fs;
                }
                return fs;
              } else
                return new EntityMention(addr, EntityMention_Type.this);
            }
          };

  /**
   * @generated
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = EntityMention.typeIndexID;

  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public final static boolean featOkTst = JCasRegistry
          .getFeatOkTst("edu.cmu.lti.oaqa.type.kb.EntityMention");

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @param jcas    JCas
   * @param casType Type
   * @generated
   */
  public EntityMention_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

  }
}



    
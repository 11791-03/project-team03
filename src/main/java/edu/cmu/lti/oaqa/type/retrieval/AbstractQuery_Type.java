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
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * An abstract search query for the question that represents query concepts and query operators over those concepts.
 * Updated by JCasGen Sat Oct 18 19:40:19 EDT 2014
 *
 * @generated
 */
public class AbstractQuery_Type extends TOP_Type {
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
              if (AbstractQuery_Type.this.useExistingInstance) {
                // Return eq fs instance if already created
                FeatureStructure fs = AbstractQuery_Type.this.jcas.getJfsFromCaddr(addr);
                if (null == fs) {
                  fs = new AbstractQuery(addr, AbstractQuery_Type.this);
                  AbstractQuery_Type.this.jcas.putJfsFromCaddr(addr, fs);
                  return fs;
                }
                return fs;
              } else
                return new AbstractQuery(addr, AbstractQuery_Type.this);
            }
          };

  /**
   * @generated
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = AbstractQuery.typeIndexID;

  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public final static boolean featOkTst = JCasRegistry
          .getFeatOkTst("edu.cmu.lti.oaqa.type.retrieval.AbstractQuery");

  /**
   * @generated
   */
  final Feature casFeat_concepts;

  /**
   * @generated
   */
  final int casFeatCode_concepts;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public int getConcepts(int addr) {
    if (featOkTst && casFeat_concepts == null)
      jcas.throwFeatMissing("concepts", "edu.cmu.lti.oaqa.type.retrieval.AbstractQuery");
    return ll_cas.ll_getRefValue(addr, casFeatCode_concepts);
  }

  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setConcepts(int addr, int v) {
    if (featOkTst && casFeat_concepts == null)
      jcas.throwFeatMissing("concepts", "edu.cmu.lti.oaqa.type.retrieval.AbstractQuery");
    ll_cas.ll_setRefValue(addr, casFeatCode_concepts, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @param jcas    JCas
   * @param casType Type
   * @generated
   */
  public AbstractQuery_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_concepts = jcas.getRequiredFeatureDE(casType, "concepts", "uima.cas.FSList", featOkTst);
    casFeatCode_concepts = (null == casFeat_concepts) ?
            JCas.INVALID_FEATURE_CODE :
            ((FeatureImpl) casFeat_concepts).getCode();

  }
}



    
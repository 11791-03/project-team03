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

/** A search result from a triple store, e.g., an RDF store.
 * Updated by JCasGen Mon Nov 10 20:27:49 EST 2014
 * @generated */
public class TripleSearchResult_Type extends AnswerSearchResult_Type {
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
  			 if (TripleSearchResult_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TripleSearchResult_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TripleSearchResult(addr, TripleSearchResult_Type.this);
  			   TripleSearchResult_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TripleSearchResult(addr, TripleSearchResult_Type.this);
  	  }
    };

  /**
   * @generated
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = TripleSearchResult.typeIndexID;

  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult");

  /**
   * @generated
   */
  final Feature casFeat_triple;

  /**
   * @generated
   */
  final int casFeatCode_triple;

  /**
   * @param addr low level Feature Structure reference
   * @return the feature value
   * @generated
   */
  public int getTriple(int addr) {
        if (featOkTst && casFeat_triple == null)
      jcas.throwFeatMissing("triple", "edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult");
    return ll_cas.ll_getRefValue(addr, casFeatCode_triple);
  }
  /**
   * @param addr low level Feature Structure reference
   * @param v    value to set
   * @generated
   */
  public void setTriple(int addr, int v) {
        if (featOkTst && casFeat_triple == null)
      jcas.throwFeatMissing("triple", "edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult");
    ll_cas.ll_setRefValue(addr, casFeatCode_triple, v);}
    
  



  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @param jcas    JCas
   * @param casType Type
   * @generated
   */
  public TripleSearchResult_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_triple = jcas.getRequiredFeatureDE(casType, "triple", "edu.cmu.lti.oaqa.type.kb.Triple", featOkTst);
    casFeatCode_triple  = (null == casFeat_triple) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_triple).getCode();

  }
}



    
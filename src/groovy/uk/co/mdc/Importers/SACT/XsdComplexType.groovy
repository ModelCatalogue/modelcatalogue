package uk.co.mdc.Importers.SACT

import java.lang.reflect.Array

/**
 * Created by sus_avi on 17/06/2014.
 */
class XsdComplexType {
    String name
    String description
    String abstractAttr
//    String minOccurs
//    String maxOccurs
    String mixed
    XsdRestriction restriction
    XsdComplexContent complexContent
    XsdSequence sequence
    ArrayList<XsdAttribute> attributes
}
package uk.co.mdc.Importers.SACT

/**
 * Created by sus_avi on 17/06/2014.
 */
class XsdSequence{
    String minOccurs
    String maxOccurs
    ArrayList <XsdElement> elements
    ArrayList <XsdSequence> sequenceElements
    ArrayList <XsdGroup> groupElements
    ArrayList <XsdChoice> choiceElements
    XsdAny any
}
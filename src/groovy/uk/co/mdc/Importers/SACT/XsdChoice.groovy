package uk.co.mdc.Importers.SACT

/**
 * Created by sus_avi on 17/06/2014.
 */


class XsdChoice{
    String description
    ArrayList <XsdElement> elements
    ArrayList <XsdChoice> choiceElements
    ArrayList <XsdSequence> sequenceElements
    ArrayList <XsdGroup> groupElements
}
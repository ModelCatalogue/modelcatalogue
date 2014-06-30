package uk.co.mdc.Importers.SACT

/**
 * Created by sus_avi on 05/06/2014.
 */
//class SACTXsdLoader {
//}


class XsdLoader {




    String logErrors =""


    def parse(String xmlFileName, sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements ){
        XmlParser parser = new XmlParser()
        def sact = parser.parse (xmlFileName)
        sact.eachWithIndex{ Node sactNode, int nodeIndex ->
            switch (sactNode.name().localPart)
            {
                case "schema":
                    break
                case "include":
                    break
                case "element":
                    XsdElement element =  readSACTElement(sactNode)
                    sactDataElements << element
                    allElements << element
                    break
                case "complexType":
                    XsdComplexDataType complexDataType = readComplexType (sactNode)
                    sactComplexDataTypes << complexDataType
                    break
                case "simpleType":
                    XsdSimpleType sactSimpleType =  readSACTSimpleType(sactNode)
                    sactSimpleDataTypes << sactSimpleType
                    break
                case "group":
                    XsdComplexDataType complexDataType = readComplexType(sactNode)
                    XsdGroup sactGroup = new XsdGroup(dataTypeName: complexDataType.dataTypeName, sequenceElements: complexDataType.sequenceElements, minOccurs: complexDataType.minOccurs, maxOccurs: complexDataType.maxOccurs )
                    sactGroups << sactGroup
                default:
                    logErrors += sactNode.name().localPart  // minExclusiveGroup
                    break
            }
        }
        [sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups,  logErrors]
    }


    def readSACTElement(Node node){
//        create element row
//        ["Data Item Name","Data Item Description",
//        "Parent Model",
//        "List content", "Metadata","Data item No.", "Format", "Data Dictionary Element",
//        "Current Collection", "Schema Specification"]

        // Read attributes
        def element = []
        String dataItemName=""
        String dataItemType = ""
        String dataItemMinOccurs = ""
        String dataItemMaxOccurs = ""
        String dataItemDescription = ""


        def attributes = node.attributes()
        attributes.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "name": dataItemName = attribute.value
                    break
                case "type": dataItemType = attribute.value
                    break
                case "minOccurs": dataItemMinOccurs  = attribute.value
                    break
                case "maxOccurs": dataItemMaxOccurs  = attribute.value
                    break
                default:
                    break
            }
        }

        NodeList values = node.value()

        String dataElementAnnotation
        XsdRestriction sactRestriction
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "annotation": dataItemDescription = readAnnotation(valueNode)
                    break

                default:
                    break
            }
        }
        XsdElement result = new XsdElement()Element(dataItemName: dataItemName, dataItemDescription: dataItemDescription, dataItemType: dataItemType, minOccurs: dataItemMinOccurs, maxOccurs: dataItemMaxOccurs )
        return result
    }

// sactNode.children()
//    def values = sactNode.value()
//    values.eachWithIndex{ Node value, int valueIndex ->
//        def name = value.attributes().name
//        def type = value.attributes().type
//
//
//    }

//    sactNode.attributes()

//    def sactSize = sact.children().size()
//    def sactIt = sact.children().iterator()
//
//    Node sact3 = sact.children().get(3)
//    def sact3Name = sact3.'@name'
//    def sact3Attr = sact3.attributes()
//    NodeList valuesSact3 = sact3.value()
//    Node sact31 =  valuesSact3.get(0)
//    def sact31Name = sact31.name()
//
//    sact3.children()[0].value()[0].attributes().name
//    sact3.children()[0].value()[0].attributes().type
//
//    sact.children()[3].children()[0].value()[0].attributes().type
    def readSACTSimpleType(Node simpleTypeNode){
        // data type can be enumeration
        // have restriction
        // minInclusive
        // restriction base => look into CommonTypes_20110810.xsd


        String dataTypeName = ""
        def attributes = simpleTypeNode.attributes()
        attributes.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "name": dataTypeName = attribute.value
                    break

                default:
                    break
            }
        }

        String sactElementAnnotation
        XsdRestriction sactRestriction
        def values = simpleTypeNode.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "annotation":
                    sactElementAnnotation =  readAnnotation(valueNode)
                    break
                case "restriction": sactRestriction = readRestriction(valueNode)
                    break
                default:
                    break
                    }
            }

        XsdSimpleType result = new XsdSimpleType(dataTypeName: dataTypeName,dataTypeDescription: sactElementAnnotation, dataTypeRestriction: sactRestriction )
    }




    def readComplexType(Node complexNode) {
//        XsdSequenceComplexDataType sequenceElements =[]
        String dataTypeName = ""
        String minOccurs = ""
        String maxOccurs = ""
        XsdSequence  sequence
        def attributes = complexNode.attributes()
        attributes.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "name":
                    dataTypeName = attribute.value
                    break
                case "minOccurs":
                    minOccurs = attribute.value
                    break
                case "maxOccurs":
                    maxOccurs  = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }

        def values = complexNode.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "sequence": sequence= readSequence(valueNode)
                    break

                default:
                    logErrors += valueNode.name().localPart
                    break

            }
        }


        XsdComplexDataType result = new XsdComplexDataType(dataTypeName: dataTypeName, sequenceElements: sequenceElements)

    }

    def readSequence (Node node){
        ArrayList<XsdElement> elements =[]
        ArrayList<XsdChoice> choices = []
        ArrayList<XsdGroup> groups = []
        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "element":
                    XsdElement elementlement= readSACTElement(valueNode)
                    elements << element
                    allElements << element
                    break

                case "choice" :
                    XsdChoice choice = readChoice (valueNode)
                    choices << choice
                    break

                case "group" :
                    XsdGroup group = readGroup (valueNode)
                    groups << group
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break
            }

        }

        XsdSequence result = new XsdSequence (elements:  elements, choiceElements: choices, groupElements: groups)

        return result
    }

    def readChoice(Node node){
        def values = node.value()
        ArrayList<XsdChoice> choices = []
        ArrayList <XsdSequence> sequences = []
        ArrayList <XsdElement> elements = []


        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "element":
                    XsdElement element= readSACTElement(valueNode)
                    elements << element
                    allElements << element
                    break
                case "sequence":
                    XsdSequence sequence = readSequence(valueNode)
                    sequences << sequence
                    break
                case "choice" :
                    XsdChoice choice = readChoice (valueNode)
                    choices << choice
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break
            }
        }

        XsdChoice result = new XsdChoice( elements: elements, sequenceElements: sequences, choiceElements: choices )
        return result
    }

    def readGroup(Node node){
        def values = node.value()
        ArrayList<XsdChoice> choices = []
        ArrayList <XsdSequence> sequences = []
        ArrayList <XsdElement> elements = []


        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "element":
                    XsdElement element= readSACTElement(valueNode)
                    elements << element
                    allElements << element
                    break
                case "sequence":
                    XsdSequence sequence = readSequence(valueNode)
                    sequences << sequence
                    break
                case "choice" :
                    XsdChoice choice = readChoice (valueNode)
                    choices << choice
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break
            }
        }

        XsdGroup result = new XsdGroup( elements: elements, sequenceElements: sequences, choiceElements: choices )
        return result
    }



    String readAnnotation(Node annotationNode){
        String dataTypeDescription = ""
      //  String dataItemDescription = ""
        NodeList values = annotationNode.value()
        values.each { Node valueNode ->
            switch (valueNode.name().localPart) {
                case "appinfo":
                    dataTypeDescription = valueNode.value()[0]
                    break
                case "documentation":
                    dataTypeDescription = valueNode.value()[0]
            }
        }
        //SactElementAnnotation result = new SactElementAnnotation(dataItemDescription: dataItemDescription, dataTypeDescription: dataTypeDescription)
        return dataTypeDescription
    }

    def readRestriction (Node restrictionNode){
        String restrictionBase = ""
        String restrictionPattern = []
        String restrictionMinLength = ""
        String restrictionMaxLength = ""
        String restrictionLength = ""
        String restrictionMinInclusive = ""
        String restrictionMaxInclusive = ""
        String restrictionMinExclusive = ""
        String restrictionMaxExclusive = ""
        ArrayList <String> restrictionEnumeration = []

        def attributes = restrictionNode.attributes()
        attributes.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "base": restrictionBase = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }
        NodeList values = restrictionNode.value()
        values.each { Node valueNode ->
            switch (valueNode.name().localPart) {
                case "pattern":
                    String pattern = valueNode.attributes().get("value")
                    restrictionPattern << pattern
                    break
                case "minLength":
                    restrictionMinLength = valueNode.attributes().get("value")
                    break
                case "length":
                    restrictionLength = valueNode.attributes().get("value")
                    break
                case "maxLength":
                    restrictionMaxLength = valueNode.attributes().get("value")
                    break
                case "minInclusive":
                    restrictionMinInclusive = valueNode.attributes().get("value")
                    break
                case "maxInclusive":
                    restrictionMaxInclusive = valueNode.attributes().get("value")
                    break
                case "minExclusive":
                    restrictionMinExclusive = valueNode.attributes().get("value")
                    break
                case "maxExclusive":
                    restrictionMaxExclusive = valueNode.attributes().get("value")
                    break
                case "enumeration":
                    restrictionEnumeration << valueNode.attributes().get("value")
                    break
                default:
                    logErrors += (valueNode.name().localPart  + ": " + valueNode.attributes().get("value") +  "\r\n")   // minExclusive
                    break
            }
        }

        XsdRestriction result = new XsdRestriction(restrictionBase: restrictionBase,
                                                    restrictionPattern: restrictionPattern,
                                                    restrictionLength: restrictionLength,
                                                    restrictionMinLength: restrictionMinLength,
                                                    restrictionMaxLength: restrictionMaxLength,
                                                    restrictionMinInclusive: restrictionMinInclusive,
                                                    restrictionMaxInclusive: restrictionMaxInclusive,
                                                    restrictionMinExclusive: restrictionMinExclusive,
                                                    restrictionMaxExclusive: restrictionMaxExclusive,
                                                    restrictionEnumeration: restrictionEnumeration)

        return  result
    }



}

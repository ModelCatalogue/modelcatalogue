package uk.co.mdc.Importers.SACT

/**
 * Created by sus_avi on 17/06/2014.
 */

class SactXsdLoader {

    ArrayList< SactXsdElement> allElements = []
    String logErrors =""

    protected static fileInputStream

    public SactXsdLoader (String path){
        fileInputStream = new FileInputStream(path)
    }

    public SactXsdLoader(InputStream inputStream){
        fileInputStream  = inputStream
    }

    def parse( ArrayList<SactXsdElement> sactDataElements,ArrayList<XsdSimpleType> sactSimpleDataTypes,ArrayList<XsdComplexDataType>  sactComplexDataTypes,ArrayList<XsdGroup> sactGroups, ArrayList<SactXsdElement> sactAllDataElements ){
        XmlParser parser = new XmlParser()
        def sact = parser.parse (fileInputStream)
        sact.eachWithIndex{ Node sactNode, int nodeIndex ->
            switch (sactNode.name().localPart)
            {
                case "schema":
                    break
                case "include":
                    break
                case "element":
                    SactXsdElement element =  readSACTElement(sactNode, "root")
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
                    XsdGroup sactGroup = readGroup(sactNode, "")
                    sactGroups << sactGroup
                    break
                default:
                    logErrors += sactNode.name().localPart  // minExclusiveGroup
                    break
            }
        }
//        sactAllDataElements<<allElements

        sactAllDataElements.addAll(allElements)
        [sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements, logErrors]
    }


    def readSACTElement(Node node, String section){
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
        SactXsdElement result = new SactXsdElement(name: dataItemName, description: dataItemDescription, type: dataItemType, minOccurs: dataItemMinOccurs, maxOccurs: dataItemMaxOccurs, section: section )
        return result
    }
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

        XsdSimpleType result = new XsdSimpleType(name: dataTypeName,description: sactElementAnnotation, restriction: sactRestriction )
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
                case "sequence": sequence= readSequence(valueNode,dataTypeName)
                    break

                default:
                    logErrors += valueNode.name().localPart
                    break

            }
        }


        XsdComplexDataType result = new XsdComplexDataType(name: dataTypeName, sequence: sequence)

    }
    def readSequence (Node node, String section){
        ArrayList<SactXsdElement> elements =[]
        ArrayList<XsdChoice> choices = []
        ArrayList<XsdGroup> groups = []
        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "element":
                    SactXsdElement element= readSACTElement(valueNode, section)
                    elements << element
                    allElements << element
                    break

                case "choice" :
                    XsdChoice choice = readChoice (valueNode, section)
                    choices << choice
                    break

                case "group" :
                    XsdGroup group = readGroup (valueNode, section)
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

    def readChoice(Node node, String section){
        def values = node.value()
        ArrayList<XsdChoice> choices = []
        ArrayList <XsdSequence> sequences = []
        ArrayList <SactXsdElement> elements = []


        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "element":
                    SactXsdElement element= readSACTElement(valueNode, section)
                    elements << element
                    allElements << element
                    break
                case "sequence":
                    XsdSequence sequence = readSequence(valueNode, section)
                    sequences << sequence
                    break
                case "choice" :
                    XsdChoice choice = readChoice (valueNode, section)
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

    def readGroup(Node node, String section){
        def values = node.value()
        XsdChoice choice
        XsdSequence sequence
        String sectionName = section
        String name = ""

        def attributes = node.attributes()
        attributes.eachWithIndex { def attribute, int attributeIndex ->
            switch (attribute.key) {
                case "name":
                    name = attribute.value
                    break
            }

        }

        if (sectionName== "") {
            sectionName = name
        }

        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "sequence":
                    sequence = readSequence(valueNode, sectionName)
                    break
                case "choice" :
                    choice = readChoice (valueNode, sectionName)
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break
            }
        }

        XsdGroup result = new XsdGroup( name:name, sequence: sequence, choice: choice )
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
        String restrictionPattern = ""
        String restrictionMinLength = ""
        String restrictionMaxLength = ""
        String restrictionLength = ""
        String restrictionMinInclusive = ""
        String restrictionMaxInclusive = ""
        String restrictionMinExclusive = ""
        String restrictionMaxExclusive = ""
        String restrictionEnumeration = ""

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
                    if (restrictionPattern=="")
                    {
                        restrictionPattern = pattern
                    }
                    else
                    {
                        restrictionPattern += ("|" + pattern)
                    }
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
                    String key = valueNode.attributes().get("value")
                    restrictionEnumeration += ((key + ":" + key + "\r\n"))
                    break
                default:
                    logErrors += (valueNode.name().localPart  + ": " + valueNode.attributes().get("value") +  "\r\n")   // minExclusive
                    break
            }
        }

        XsdRestriction result = new XsdRestriction(base: restrictionBase,
                pattern: restrictionPattern,
                length: restrictionLength,
                minLength: restrictionMinLength,
                maxLength: restrictionMaxLength,
                minInclusive: restrictionMinInclusive,
                maxInclusive: restrictionMaxInclusive,
                minExclusive: restrictionMinExclusive,
                maxExclusive: restrictionMaxExclusive,
                enumeration: restrictionEnumeration)

        return  result
    }



}

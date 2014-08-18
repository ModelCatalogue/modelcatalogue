package uk.co.mdc.Importers.SACT

/**
 * Created by sus_avi on 17/06/2014.
 */

class SactXsdLoader {

    ArrayList< XsdElement> allElements = []
    String logErrors =""
    ArrayList<XsdElement> sactDataElements =[]
    ArrayList<XsdSimpleType> sactSimpleDataTypes =[]
    ArrayList<XsdComplexType>  sactComplexDataTypes =[]
    ArrayList<XsdGroup> sactGroups =[]
    ArrayList<XsdElement> sactAllDataElements =[]
    XmlParser parser
    def sact

    protected static fileInputStream

    public SactXsdLoader (String path){
        fileInputStream = new FileInputStream(path)
        parser = new XmlParser()
        sact = parser.parse (fileInputStream)
    }

    public SactXsdLoader(InputStream inputStream){
        fileInputStream  = inputStream
        parser = new XmlParser()
        sact = parser.parse (fileInputStream)
    }

    public SactXsdLoader(String xsdText, Boolean test)
    {
        parser = new XmlParser()
        sact = parser.parse (xsdText)
    }

    def parse(){
        sact.eachWithIndex{ Node valueNode, int nodeIndex ->
            switch (valueNode.name().localPart)
            {
                case "schema":
                    break
                case "include":
                    break
                case "element":
                    XsdElement element =  readSACTElement(valueNode, "root")
                    sactDataElements << element
                    allElements << element
                    break
                case "complexType":
                    XsdComplexType complexDataType = readComplexType (valueNode, "")
                    sactComplexDataTypes << complexDataType
                    break
                case "simpleType":
                    XsdSimpleType sactSimpleType =  readSACTSimpleType(valueNode, "")
                    sactSimpleDataTypes << sactSimpleType
                    break
                case "group":
                    XsdGroup sactGroup = readGroup(valueNode, "")
                    sactGroups << sactGroup
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break
            }
        }
        sactAllDataElements.addAll(allElements)
        [sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements, logErrors]
    }


    def readSACTElement(Node node, String section){
        String dataItemName=""
        String dataItemType = ""
        String dataItemMinOccurs = ""
        String dataItemMaxOccurs = ""
        String dataItemDescription = ""


        def attributes = node.attributes()
        attributes.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "name":
                    dataItemName = attribute.value
                    break
                case "type":
                    dataItemType = attribute.value
                    break
                case "minOccurs":
                    dataItemMinOccurs  = attribute.value
                    break
                case "maxOccurs":
                    dataItemMaxOccurs  = attribute.value
                    break
                default:
                    logErrors += ("Loader does not loads this attribute: " + attribute.key + "\r\n")
                    break
            }
        }

        NodeList values = node.value()

        XsdSimpleType simpleType
        XsdComplexType complexDataType

        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "annotation":
                    dataItemDescription = readAnnotation(valueNode)
                    break
                case "simpleType":
                    simpleType =  readSACTSimpleType(valueNode, dataItemName)
                    dataItemType = simpleType.name
                    sactSimpleDataTypes << simpleType
                    break
                case "complexType":
                    complexDataType = readComplexType (valueNode, dataItemName)
                    dataItemType = complexDataType.name
                    sactComplexDataTypes << complexDataType
                    break
                default:
                    logErrors += ("Loader does not loads this valueNode: " + valueNode.name().localPart + "\r\n")
                    break
            }
        }
        XsdElement result = new XsdElement(name: dataItemName, description: dataItemDescription, type: dataItemType, minOccurs: dataItemMinOccurs, maxOccurs: dataItemMaxOccurs, section: section, simpleType: simpleType, complexType: complexDataType )
        return result
    }
    def readSACTSimpleType(Node node, String elementName){
        // data type can be enumeration
        // have restriction
        // minInclusive
        // restriction base => look into CommonTypes_20110810.xsd

        String sactElementAnnotation
        XsdRestriction sactRestriction
        XsdList list
        XsdUnion union
        String dataTypeName = ""
        def attributes = node.attributes()
        attributes.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "name": dataTypeName = attribute.value
                    break

                default:
                    logErrors += attribute.key
                    break
            }
        }


        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "annotation":
                    sactElementAnnotation =  readAnnotation(valueNode)
                    break
                case "restriction":
                    sactRestriction = readRestriction(valueNode, elementName)
                    break
                case "list":
                    list = readList(valueNode,elementName)
                    break
                case "union":
                    union = readUnion(valueNode,elementName)
                    break
                default:
                    logErrors +=  valueNode.name().localPart + " : " + dataTypeName
                    break
            }
        }
        if (dataTypeName=="") dataTypeName = elementName

        XsdSimpleType result = new XsdSimpleType(name: dataTypeName,description: sactElementAnnotation, restriction: sactRestriction, list: list, union: union )
    }

    def readUnion (Node node, String elementName){
        String id
        String memberTypes
        String description //annotation
        ArrayList<XsdSimpleType> simpleTypes

        def attributeList = node.attributes()
        attributeList.eachWithIndex { def attribute, int attributeIndex ->
            switch (attribute.key) {
                case "id":
                    id = attribute.value
                    break
                case "memberTypes":
                    memberTypes = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }
        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "annotation":
                    description =  readAnnotation(valueNode)
                    break
                case "simpleType":
                    XsdSimpleType simpleType = readSACTSimpleType(valueNode, dataItemName)
                    simpleTypes<<simpleType
                    sactSimpleDataTypes << simpleType
                    break
                default:
                    logErrors +=  valueNode.name().localPart + " : " + dataTypeName
                    break
            }
        }
        XsdUnion result = new XsdUnion(id: id, memberTypes: memberTypes, description: description, simpleTypes: simpleTypes)
        return result

    }

    def readList(Node node, String elementName){
        String id
        String itemType
        String description
        XsdSimpleType simpleType
        def attributeList = node.attributes()
        attributeList.eachWithIndex { def attribute, int attributeIndex ->
            switch (attribute.key) {
                case "id":
                    id = attribute.value
                    break
                case "itemType":
                    itemType = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }
        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "annotation":
                    description =  readAnnotation(valueNode)
                    break
                case "simpleType":
                    simpleType = readSACTSimpleType(valueNode, elementName)
                    break
                default:
                    logErrors +=  valueNode.name().localPart + " : "
                    break
            }
        }
        XsdList result = new XsdList(id: id, itemType: itemType, description: description, simpleType: simpleType)
        return result
    }


    def readComplexType(Node node, String elementName) {
//        XsdSequenceComplexDataType sequenceElements =[]
        String dataTypeName = ""
        String minOccurs = ""
        String maxOccurs = ""
        String description =""
        XsdSequence  sequence
        XsdComplexContent complexContent
        XsdRestriction restriction
        String abstractAttr
        String mixed
        ArrayList<XsdAttribute> attributes =[]

        def attributeList = node.attributes()
        attributeList.eachWithIndex{ def attribute, int attributeIndex ->
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
                case "abstract":
                    abstractAttr = attribute.value
                    break
                case "mixed":
                    mixed = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }


        if (dataTypeName=="") dataTypeName = elementName

        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "sequence": sequence = readSequence(valueNode,dataTypeName)
                    break
                case "annotation":
                    description =  readAnnotation(valueNode)
                    break
                case "complexContent": complexContent= readComplexContent(valueNode,dataTypeName, dataTypeName)
                    break
                case "restriction": restriction = readRestriction(valueNode, dataTypeName)
                    break
                case "attribute":
                    XsdAttribute attribute = readAttribute(valueNode, dataTypeName)
                    attributes << attribute
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break

            }
        }

        XsdComplexType result = new XsdComplexType(name: dataTypeName, description: description, abstractAttr: abstractAttr, restriction: restriction, sequence: sequence, complexContent: complexContent, mixed: mixed, attributes:attributes)

    }


    def readExtension (Node node, String section, String elementName){
        String name
        String description
        String base
        String id
        XsdRestriction restriction
        XsdSequence sequence
        ArrayList<XsdAttribute> attributes =[]
        XsdChoice choice
        XsdGroup group

        def attributesList = node.attributes()
        attributesList.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "name":
                    name = attribute.value
                    break
                case "base":
                    base = attribute.value
                    break
                case "id":
                    id= attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }

        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){

                case "annotation":
                    description =  readAnnotation(valueNode)
                    break
                case "restriction": restriction = readRestriction(valueNode, elementName)
                    break
                case "attribute":
                    XsdAttribute attribute = readAttribute(valueNode,elementName)
                    attributes << attribute
                    break
                case "choice" :
                    choice = readChoice (valueNode, section)
                    break
                case "group" :
                    group = readGroup (valueNode, section)
                    break
                case "sequence":
                    sequence = readSequence(valueNode, section)
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break

            }
        }

        XsdExtension result = new XsdExtension(name:name, description: description, base:base, id: id, restriction: restriction, choice: choice, sequence: sequence, group: group, attributes: attributes )
        return result

    }

    def readComplexContent (Node node, String section, String elementName){

        String name= ""
        String description = ""
        XsdRestriction restriction
        ArrayList <XsdAttribute> attributes = []
        XsdExtension extension


        def attributesList = node.attributes()
        attributesList.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "name":
                    name = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }

        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){

                case "annotation":
                    description =  readAnnotation(valueNode)
                    break
                case "restriction": restriction = readRestriction(valueNode,elementName)
                    break
                case "attribute":
                    XsdAttribute attribute = readAttribute(valueNode, elementName)
                    attributes << attribute
                    break
                case "extension": extension = readExtension(valueNode, section, elementName)
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break

            }
        }

        XsdComplexContent result = new XsdComplexContent (name:name, description: description, restriction: restriction, attributes: attributes, extension: extension)

        return result
    }

    def readAttribute(Node node, String elementName){
        String name
        String defaultValue
        String fixed
        String form
        String id
        String ref
        String type
        String use
        String description
        XsdSimpleType simpleType

        def attributesList = node.attributes()
        attributesList.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "name":
                    name = attribute.value
                    break
                case "default":
                    defaultValue = attribute.value
                    break
                case "fixed":
                    fixed = attribute.value
                    break
                case "form":
                    form = attribute.value
                    break
                case "id":
                    id = attribute.value
                    break
                case "ref":
                    ref = attribute.value
                    break
                case "type":
                    type = attribute.value
                    break
                case "use":
                    use = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }

        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "annotation":
                    description =  readAnnotation(valueNode)
                    break
                case "simpleType":
                    simpleType =  readSACTSimpleType(valueNode, elementName)
                    sactSimpleDataTypes << simpleType
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break
            }
        }
        XsdAttribute result = new XsdAttribute (name:name, defaultValue: defaultValue, fixed: fixed, form: form, id: id, ref: ref, type: type, use:use, description: description, simpleType: simpleType )
        return result
    }


    def readPattern (Node node) {
        String value
        String description

        def attributesList = node.attributes()
        attributesList.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "value":
                    value = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }

        def values = node.value()
        values.eachWithIndex { Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart) {
                case "annotation":
                    description = readAnnotation(valueNode)
                    break
                default:
                    logErrors += attribute.key
                    break

            }
        }
        XsdPattern result = new XsdPattern (value: value, description: description)
        return result
    }

    def readSequence (Node node, String section){
        ArrayList<XsdElement> elements =[]
        ArrayList<XsdChoice> choices = []
        ArrayList<XsdGroup> groups = []
        XsdAny any
        def values = node.value()
        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "element":
                    XsdElement element= readSACTElement(valueNode, section)
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
                case "any":
                    any = readAny(valueNode)
                    break
                default:
                    logErrors += valueNode.name().localPart
                    break
            }

        }

        XsdSequence result = new XsdSequence (elements:  elements, choiceElements: choices, groupElements: groups, any:any)

        return result
    }

    def readAny(Node node){
        String namespace
        String processContents
        String minOccurs
        String maxOccurs
        def attributes = node.attributes()
        attributes.eachWithIndex { def attribute, int attributeIndex ->
            switch (attribute.key) {
                case "namespace":
                    namespace = attribute.value
                    break
                case "processContents":
                    processContents = attribute.value
                    break
                case "minOccurs":
                    minOccurs = attribute.value
                    break
                case "maxOccurs":
                    maxOccurs = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }

        }

    }

    def readChoice(Node node, String section){
        def values = node.value()
        ArrayList<XsdChoice> choices = []
        ArrayList <XsdSequence> sequences = []
        ArrayList <XsdElement> elements = []


        String minOccurs = ""
        String maxOccurs = ""
        def attributes = node.attributes()
        attributes.eachWithIndex { def attribute, int attributeIndex ->
            switch (attribute.key) {
                case "minOccurs":
                    minOccurs = attribute.value
                    break
                case "maxOccurs":
                    maxOccurs = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }


        values.eachWithIndex{ Node valueNode, int valueNodeIndex ->
            switch (valueNode.name().localPart){
                case "element":
                    XsdElement element= readSACTElement(valueNode, section)
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

        XsdChoice result = new XsdChoice(minOccurs: minOccurs, maxOccurs: maxOccurs, elements: elements, sequenceElements: sequences, choiceElements: choices )
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
                default:
                    logErrors += attribute.key
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
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }
        //SactElementAnnotation result = new SactElementAnnotation(dataItemDescription: dataItemDescription, dataTypeDescription: dataTypeDescription)
        return dataTypeDescription
    }

    def readRestriction (Node node, String elementName){
        String restrictionBase = ""
        ArrayList<XsdPattern> patterns = []
        String restrictionMinLength = ""
        String restrictionMaxLength = ""
        String restrictionLength = ""
        String restrictionMinInclusive = ""
        String restrictionMaxInclusive = ""
        String restrictionMinExclusive = ""
        String restrictionMaxExclusive = ""
        String restrictionEnumeration = ""
        XsdSequence sequence
        ArrayList <XsdAttribute> attributes=[]

        def attributeList = node.attributes()
        attributeList.eachWithIndex{ def attribute, int attributeIndex ->
            switch (attribute.key){
                case "base": restrictionBase = attribute.value
                    break
                default:
                    logErrors += attribute.key
                    break
            }
        }
        NodeList values = node.value()
        values.each { Node valueNode ->
            switch (valueNode.name().localPart) {
                case "pattern":
                    XsdPattern pattern = readPattern(valueNode)
                    patterns << pattern
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
                case "attribute":
                    XsdAttribute attribute = readAttribute(valueNode, elementName)
                    attributes << attribute
                    break
                case "sequence":
                    sequence = readSequence(valueNode, "")
                    break
                default:
                    logErrors += (valueNode.name().localPart  + ": " + valueNode.attributes().get("value") +  "\r\n")   // minExclusive
                    break
            }
        }

        XsdRestriction result = new XsdRestriction(base: restrictionBase,
                patterns: patterns,
                length: restrictionLength,
                minLength: restrictionMinLength,
                maxLength: restrictionMaxLength,
                minInclusive: restrictionMinInclusive,
                maxInclusive: restrictionMaxInclusive,
                minExclusive: restrictionMinExclusive,
                maxExclusive: restrictionMaxExclusive,
                enumeration: restrictionEnumeration,
                attributes: attributes,
                sequence: sequence)

        return  result
    }






}

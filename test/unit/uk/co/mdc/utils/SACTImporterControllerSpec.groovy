package uk.co.mdc.utils

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.ExtendibleElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.dataarchitect.DataImportService
import org.modelcatalogue.core.dataarchitect.Importer
import spock.lang.Specification
import uk.co.mdc.Importers.SACT.SactXsdLoader
import uk.co.mdc.Importers.SACT.XsdComplexType
import uk.co.mdc.Importers.SACT.XsdElement
import uk.co.mdc.Importers.SACT.XsdGroup
import uk.co.mdc.Importers.SACT.XsdRestriction
import uk.co.mdc.Importers.SACT.XsdSimpleType
import org.modelcatalogue.core.DataType

import java.util.regex.Pattern

/**
 * Created by sus_avi on 13/08/2014.
 */
@TestFor(SACTImporterController)
@Mock([ValueDomain, ConceptualDomain, ExtendibleElement, ExtensionValue, DataType, Relationship, CatalogueElement])
class SACTImporterControllerSpec extends Specification {

    String sactRootElement = "SACT"
    String sactTypeRootElement = "SACTSACTType"
    String sactDescription = "Systemic Anti-Cancer Therapy"
    String sactSectionsNotToImport = ["root", "SACTSACTType", "SACTSACTRecordType"]
    ArrayList<XsdElement> sactDataElements = []
    ArrayList<XsdSimpleType> sactSimpleDataTypes = []
    ArrayList<XsdComplexType> sactComplexDataTypes = []
    ArrayList<XsdGroup> sactGroups = []
    ArrayList<XsdElement> sactAllDataElements = []
    ArrayList<XsdElement> sactDataElements2 = []
    ArrayList<XsdSimpleType> sactSimpleDataTypes2 = []
    ArrayList<XsdComplexType> sactComplexDataTypes2 = []
    ArrayList<XsdGroup> sactGroups2 = []
    ArrayList<XsdElement> sactAllDataElements2 = []
    ConceptualDomain conceptualDomain
    def sactHeaders =  ["Data Element Name","Data Element Description",
                        "Parent Model", "Containing Model", "DataType",
                        "Metadata","minOccurs", "maxOccurs"]


    String filename = "test/unit/resources/SACT/XSD_Example.xsd"
    SactXsdLoader loader
    String logSACTErrors =""
    String logCommonTypesErrors = ""
    Importer sactImporter

    //SimpleDataTypes
    def setup(){

        controller.dataImportService = new DataImportService()
        sactImporter = new Importer()
        conceptualDomain = new ConceptualDomain(name:"Example1").save()
        loader = new SactXsdLoader(filename)
        XsdElement xsdElementRoot = new XsdElement(name: sactRootElement, type: sactTypeRootElement, description: sactDescription )
        XsdElement xsdElementLeaf = new XsdElement(name: "Child", type:"xs:string:", description: "Child node" )
        sactDataElements << xsdElementRoot
        sactAllDataElements << xsdElementRoot
        XsdSimpleType xsdSimpleType = new XsdSimpleType(name: "SimpleType", description: "SimpleTypeTest")
        sactSimpleDataTypes << xsdSimpleType
        XsdComplexType xsdComplexType = new XsdComplexType(name:"ComplexType", description: "ComplexTypeTest")
        sactComplexDataTypes << xsdComplexType
    }

    void "Test that the validateSACTFiles validates errors in the SACT XSD"()
    {
        when:"the parser detects errors in the SACT XSD file"
        def exception
        def logSACTErrors = "This is an error"
        try {
            controller.validateSACTFiles(logCommonTypesErrors,logSACTErrors,sactDataElements, sactSimpleDataTypes,sactComplexDataTypes, sactAllDataElements, sactRootElement,  sactTypeRootElement)
        }
        catch (Exception ex)
        {
            exception = ex;
        }
        then:"It should send an exception to indicate that there are errors in the file"
        exception.message == ("SACT xsd file has errors: " + logSACTErrors)
    }

    void "Test that the validateSACTFiles validates errors in the CommonTypes XSD file"()
    {
        when:"the parser detects errors in the CommonTypes XSD file"
        def exception
        def logCommonTypesErrors = "This is an error"
        try {
            controller.validateSACTFiles(logCommonTypesErrors,logSACTErrors,sactDataElements, sactSimpleDataTypes,sactComplexDataTypes, sactAllDataElements, sactRootElement,  sactTypeRootElement)
        }
        catch (Exception ex)
        {
            exception = ex;
        }
        then:"It should send an exception to indicate that there are errors in the file"
        exception.message == ("CommonTypes xsd file has errors: " + logCommonTypesErrors)
    }

    void "Test that the validateSACTFiles validates the name of root node"()
    {
        when:"the name of root element is not SACT"
        def exception
        ArrayList<XsdElement> sactDataElements = []
        XsdElement xsdElementLeaf = new XsdElement(name: "Child", type:"xs:string:", description: "Child node" )
        sactDataElements << xsdElementLeaf

        try {
            controller.validateSACTFiles(logCommonTypesErrors,logSACTErrors,sactDataElements, sactSimpleDataTypes,sactComplexDataTypes, sactAllDataElements, sactRootElement,  sactTypeRootElement)
        }
        catch (Exception ex)
        {
            exception = ex;
        }
        then:"It should send an exception to indicate that there are errors in the file"
        exception.message == ("Wrong SACT file format: root element is not " + sactRootElement)
    }

    void "Test that the validateSACTFiles validates the type of root node"()
    {
        when:"the type of root element is not 'SACTSACTType'"
        def exception
        def logSACTErrors = ""
        def logCommonTypesErrors = ""
        ArrayList<XsdElement> sactDataElements = []
        XsdElement xsdElement = new XsdElement(name: "SACT", type:"xs:string:", description: "Child node" )
        sactDataElements << xsdElement

        try {
           controller.validateSACTFiles(logCommonTypesErrors,logSACTErrors,sactDataElements, sactSimpleDataTypes,sactComplexDataTypes, sactAllDataElements, sactRootElement,  sactTypeRootElement)
        }
        catch (Exception ex)
        {
            exception = ex;
        }
        then:"It should send an exception to indicate that there are errors in the file"
        exception.message == ("Wrong SACT file format: root element is not type: " + sactTypeRootElement)
    }

    //SimpleTypes

    void "Test that the validateSACTFiles validates there is at least one simpleType"()
    {
        when:"when validateSACTFiles is called with an empty arraylist for simpletypes"
        def exception
        ArrayList<XsdSimpleType> sactSimpleDataTypes = []

        try {
            controller.validateSACTFiles(logCommonTypesErrors,logSACTErrors,sactDataElements, sactSimpleDataTypes,sactComplexDataTypes, sactAllDataElements, sactRootElement,  sactTypeRootElement)
        }
        catch (Exception ex)
        {
            exception = ex;
        }
        then:"It should send an exception to indicate that there are errors in the file"
        exception.message == ("SACT xsd file has no simpleTypes defined")
    }

    void "Test that the validateSACTFiles validates there is at least one complexType"()
    {
        when:"the validateSACTFiles is called with an empty arraylist for complexTypes"
        def exception
        ArrayList<XsdComplexType>  sactComplexDataTypes = []
        try {
             controller.validateSACTFiles(logCommonTypesErrors,logSACTErrors,sactDataElements, sactSimpleDataTypes,sactComplexDataTypes, sactAllDataElements, sactRootElement,  sactTypeRootElement)
        }
        catch (Exception ex)
        {
            exception = ex;
        }
        then:"It should send an exception to indicate that there are errors in the file"
        exception.message == ("SACT xsd file has no complexTypes defined")
    }



    void "Test that the 'rows' arrayList is created correctly" ()
    {
        when: "The createDataElementRows function is called"

        ArrayList<XsdElement> sactAllDataElements = []
        XsdElement xsdElement = new XsdElement(name: "Name1", description: "Description1", type: "Type1", section: "Section1", minOccurs: "0", maxOccurs: "1")
        sactAllDataElements << xsdElement
        XsdElement xsdElement2 = new XsdElement(name: "Name2", description: "Description2", type: "Type2", section: "SACTSACTType", minOccurs: "0", maxOccurs: "1")
        sactAllDataElements << xsdElement2

        def rows = []
        SACTImporterController sactImporterController = new SACTImporterController()
        rows = sactImporterController.createDataElementRows(sactHeaders, sactAllDataElements, sactSectionsNotToImport )

        then: "the rows correspond to the elements defined "
        def indexName = sactHeaders.findIndexOf {it =="Data Element Name"}
        def indexDescription = sactHeaders.findIndexOf {it =="Data Element Description"}
        def indexParentModel = sactHeaders.findIndexOf {it =="Parent Model"}
        def indexContainingModel = sactHeaders.findIndexOf {it=="Containing Model"}
        def indexDataType = sactHeaders.findIndexOf {it =="DataType"}
        def indexMinOccurs = sactHeaders.findIndexOf {it =="minOccurs"}
        def indexMaxOccurs = sactHeaders.findIndexOf {it =="maxOccurs"}
        assert rows[0][indexName] == "Name1"
        assert rows[0][indexDescription] == "Description1"
        assert rows[0][indexDataType] == "Type1"
        assert rows[0][indexContainingModel] == "Section1"
        assert rows[0][indexMinOccurs] == "0"
        assert rows[0][indexMaxOccurs] == "1"
        assert rows.size() ==1
    }

    void "Test that the addMetadataToValueDomain function successfully adds metadata to a Valuedomain" () {
        when: "The addMetadataToValueDomain function is called"
        def exception
        ValueDomain valueDomain
        try {


            XsdRestriction xsdRestriction = new XsdRestriction(base: "xs:string", minLength: "0", maxLength: "12", minInclusive: "0", maxInclusive: "2")
            XsdSimpleType xsdSimpleType = new XsdSimpleType(name: "SimpleType1", restriction: xsdRestriction)
            valueDomain = new ValueDomain(name: "SimpleType1").save(failOnError: true)
            controller.addMetadataToValueDomain(valueDomain, xsdSimpleType)
        }
        catch (Exception ex) {
            exception = ex;
            println("error: " + exception.message)
        }

        then: "the valuedomain is updated with the metadata"
        ValueDomain vd = ValueDomain.findByName("SimpleType1")

        assert vd.ext.size() != 0
        assert vd.ext.get("minLength") == "0"
        assert vd.ext.get("maxLength") == "12"
        assert vd.ext.get("minInclusive") == "0"
        assert vd.ext.get("maxInclusive") == "2"

    }


}

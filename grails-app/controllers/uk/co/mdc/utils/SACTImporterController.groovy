package uk.co.mdc.utils

import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.dataarchitect.Importer
import org.springframework.security.access.annotation.Secured
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.mdc.Importers.SACT.SactXsdElement
import uk.co.mdc.Importers.SACT.SactXsdLoader
import uk.co.mdc.Importers.SACT.XsdComplexDataType
import uk.co.mdc.Importers.SACT.XsdElement
import uk.co.mdc.Importers.SACT.XsdGroup
import uk.co.mdc.Importers.SACT.XsdSimpleType

/**
 * Created by sus_avi on 05/06/2014.
 */
@Secured(['ROLE_ADMIN'])
class SACTImporterController {

    def dataImportService

    String sactRootElement = "SACT"
    String sactTypeRootElement = "SACTStructure"
    String sactDescription = "Systemic Anti-Cancer Therapy"
    String sactSectionsNotToImport = ["root", "SACTStructure", "SACTRecordType"]
    ArrayList<SactXsdElement> sactDataElements = []
    ArrayList<XsdSimpleType> sactSimpleDataTypes = []
    ArrayList<XsdComplexDataType> sactComplexDataTypes = []
    ArrayList<XsdGroup> sactGroups = []
    ArrayList<SactXsdElement> sactAllDataElements = []
    ConceptualDomain conceptualDomain
    def sactHeaders =  ["Data Element Name","Data Element Description",
                        "Parent Model", "Containing Model", "DataType",
                        "Metadata","minOccurs", "maxOccurs"]

    def index() {}
    def upload() {

        Importer sactImporter = new Importer()
        if(!(request instanceof MultipartHttpServletRequest))
        {
            flash.error="No File to process!"
            render view:"index"
            return
        }

        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
        MultipartFile fileSACT = multiRequest.getFile("xsdSACTFile");
        MultipartFile fileCommonTypes = multiRequest.getFile("xsdSACTTypesFile");

        def okContentTypes = ['application/xml', 'text/xml', 'application/octet-stream'];
        def confType=fileSACT.getContentType();
        def confTypeCommonTypes=fileCommonTypes.getContentType();
        if ((okContentTypes.contains(confType) && fileSACT.size > 0) && (okContentTypes.contains(confTypeCommonTypes) && fileCommonTypes.size > 0)) {
            try {
                def logErrorsSACT
                def logErrorCommonTypes

                SactXsdLoader parserSACT = new SactXsdLoader(fileSACT.inputStream)
                (sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements, logErrorsSACT) = parserSACT.parse(sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements)
                SactXsdLoader parserCommonTypesSACT = new SactXsdLoader(fileCommonTypes.inputStream)
                (sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements, logErrorCommonTypes) = parserCommonTypesSACT.parse(sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements)

                //Validate results
                validateSACTFiles(logErrorCommonTypes, logErrorsSACT)

                // Create the Conceptual Domain
                conceptualDomain = sactImporter.importConceptualDomain(sactRootElement, sactDescription)

                //Create DataTypes and ValueDomains for SimpleTypes
                createDataTypesAndValueDomains(sactImporter, conceptualDomain)

                // Create Models for ComplexTypes and Groups
                createModels(sactImporter, conceptualDomain)

                // Create HeaderMap
                HeadersMap headersMap = createHeaderMap ()

                //Create Rows
                def rows = createDataElementRows()
                dataImportService.importData(sactHeaders, rows, "SACT", sactRootElement, sactDescription, headersMap)
                flash.message = "DataElements have been created.\n"
            }
            catch(Exception ex)
            {
                log.error("Exception in handling xsd file :" + ex.message)
                flash.message ="Error in importing the xsd   file.";
            }
        }
        render view: 'index'
    }

    private validateSACTFiles(String logErrorCommonTypes, String logErrorsSACT) {

        def sactSections = ["DemographicsAndConsultant", "ClinicalStatus", "ProgrammeAndRegimen",
                            "Cycle", "DrugDetails", "Outcome"]

        if (logErrorCommonTypes != "" )
        {
            log.error(logErrorCommonTypes)
            throw new Exception("CommonTypes xsd file has errors: " + logErrorsSACT )
        }

        if (logErrorsSACT != "")
        {
            log.error(logErrorsSACT)
            throw new Exception("SACT xsd file has errors: " + logErrorsSACT )
        }


        // Check that the root element is SACT
        // Check that the Type is SACTReacordType and has the types defined in sactSections.

        if ((sactDataElements.size() != 1)) {
            log.error("Wrong SACT file format")
            throw new Exception("SACT xsd file has more than one top element")
        }

        if ((sactSimpleDataTypes.size()==0)) {
            log.error("Wrong SACT file format, no simpleTypes found")
            throw new Exception("SACT xsd file has no simpleTypes defined")
        }

        if (sactComplexDataTypes.size() == 0) {
            log.error("Wrong SACT file format, no complexTypes found")
            throw new Exception("SACT xsd file has no complexTypes defined")
        }

        // Check if the root element exists and the type is correct.
        if (sactDataElements.size() ==1)
        {
            // Check if the root element exists and the type is correct.
            if (sactDataElements[0].name != sactRootElement)
            {
                log.error("Wrong SACT file format: root element is not " + sactRootElement)
                throw new Exception("Wrong SACT file format: root element is not " + sactRootElement)
            }
            // Check if the root element exists and the type is correct.
            if (sactDataElements[0].type != sactTypeRootElement)
            {
                log.error("Wrong SACT file format: root element is not type: " + sactTypeRootElement)
                throw  new Exception ("Wrong SACT file format: root element is not type: " + sactTypeRootElement)
            }
        }

        // Validate sact section

        Boolean sectionsOK = true
        sactSections.each { def section ->
            def indexSactSection = sactAllDataElements.findIndexOf { it.name == section }
            if (indexSactSection == -1) {
                log.error("Section: " + section + " not found in file. \r\n")
                throw new Exception ("Section/ComplexType: " + section + " not found in file. \r\n")
                sectionsOK = false
            }
        }


    }


    private createDataElementRows(){

        // Extract all the DataElements
        def rows = []

        def indexName = sactHeaders.findIndexOf {it =="Data Element Name"}
        def indexDescription = sactHeaders.findIndexOf {it =="Data Element Description"}
        def indexParentModel = sactHeaders.findIndexOf {it =="Parent Model"}
        def indexContainingModel = sactHeaders.findIndexOf {it=="Containing Model"}
        def indexDataType = sactHeaders.findIndexOf {it =="DataType"}
        def indexMinOccurs = sactHeaders.findIndexOf {it =="minOccurs"}
        def indexMaxOccurs = sactHeaders.findIndexOf {it =="maxOccurs"}
        sactAllDataElements.each { SactXsdElement element ->
            def row = []

            if (!sactSectionsNotToImport.contains(element.section)) {

                row[indexName] = element.name
                row[indexDescription] = element.description
                row[indexDataType] = element.type
                row[indexParentModel] = "SACT_Record" //element.section
                row[indexContainingModel] = element.section
                row[indexMinOccurs] = element.minOccurs
                row[indexMaxOccurs] = element.maxOccurs
                rows << row
            }
        }
        return rows
    }

    private createHeaderMap(){
        HeadersMap headersMap = new HeadersMap()
        headersMap.dataElementCodeRow = ""
        headersMap.dataElementNameRow = "Data Element Name"
        headersMap.dataElementDescriptionRow = "Data Element Description"
        headersMap.dataTypeRow = "DataType"
        headersMap.parentModelNameRow = "Parent Model"
        headersMap.parentModelCodeRow = ""
        headersMap.containingModelNameRow = "Containing Model"
        headersMap.containingModelCodeRow = ""
        headersMap.measurementUnitNameRow = ""
        headersMap.metadataRow = "Metadata"

        return headersMap

    }

    private addMetadataToValueDomain (vd, XsdSimpleType simpleType)
    {

        def metadata = [minOccurs: simpleType.minOccurs,
                        maxOccurs: simpleType.maxOccurs,
                        minLength: simpleType.restriction.minLength,
                        maxLength: simpleType.restriction.maxLength,
                        lenght: simpleType.restriction.length,
                        minInclusive: simpleType.restriction.minInclusive,
                        maxInclusive: simpleType.restriction.maxInclusive,
                        minExclusive: simpleType.restriction.minExclusive,
                        maxExclusive: simpleType.restriction.maxExclusive,
                        pattern: simpleType.restriction.pattern
        ]


        dataImportService.updateMetadata(metadata, vd)


    }

    private addRulesToSimpleType(Importer sactImporter, XsdSimpleType simpleType, ValueDomain vd)
    {
        //Check the rules/patterns that apply to this type
        String rule = ""
        if (simpleType.restriction.pattern != "") {
            rule = simpleType.restriction.pattern
            def patternLength = simpleType.restriction.pattern.length()
            if (simpleType.restriction.minLength != "" && simpleType.restriction.maxLength == "" && simpleType.restriction.pattern.charAt(patternLength - 1) == "]") {
                rule += ("{" + simpleType.restriction.minLength + "," + simpleType.restriction.maxLength + "}")
            }

            vd.setRegexDef(rule)
            vd.save()

        }
    }

    private createSimpleType(Importer sactImporter, ConceptualDomain cd, XsdSimpleType simpleType)
    {
        String type = simpleType.restriction.base
        String description= simpleType.description
        ValueDomain vd
        DataType dataType
        String name = simpleType.name
        if (simpleType.restriction.base.contains("xs:"))
        {
            dataType = DataType.findByName(simpleType.restriction.base)
            if (dataType==null) {
                dataType =sactImporter.importDataType(simpleType.restriction.base,  "")
            }
            vd = sactImporter.importValueDomain(name, description, dataType,"",cd)
            addMetadataToValueDomain(vd, simpleType)
            vd.save()
            return [vd, dataType]
        }
        else {

            //Check if the value domain already exists
            vd = ValueDomain.findByNameAndDescription(name, description)
            if (vd==null){
                XsdSimpleType simpleDataType = sactSimpleDataTypes.find {it.name == type}
                (vd, dataType) = createSimpleType( sactImporter,  cd,  simpleDataType)
                ValueDomain vd2 = sactImporter.importValueDomain(name, description, dataType,"",cd)
                addMetadataToValueDomain(vd2, simpleType)
                vd2.addToBasedOn(vd)
                vd.addToIsBaseFor(vd2)
                vd.save()
                vd2.save()

                // Check enumerated elements
                if (simpleType.restriction.enumeration != ""){
                    DataType enumeratedDataType = sactImporter.importDataType(simpleType.name, simpleType.restriction.enumeration)
                    ValueDomain vd3 = sactImporter.importValueDomain(simpleType.name, simpleType.description, enumeratedDataType, "", cd)
                    vd3.save()
                    vd2.addToBasedOn(vd3)
                    vd3.addToIsBaseFor(vd2)
                    vd2.save()
                    vd3.save()
                }


                return [vd2,dataType]
            }
            else{
                return [vd, vd.dataType]
            }

        }

    }

    private createDataTypesAndValueDomains(Importer sactImporter, ConceptualDomain cd){
        DataType dataType
        ValueDomain vd
        // Add all the simple data types
        sactSimpleDataTypes.each { XsdSimpleType simpleType ->
            (vd, dataType) = createSimpleType(sactImporter, cd, simpleType)
            addRulesToSimpleType(sactImporter, simpleType, vd)
            if (vd!=null && dataType!=null) println("SimpleType: " + simpleType.name)
        }
    }

    private createModels(Importer sactImporter, ConceptualDomain cd){

        sactComplexDataTypes.each { XsdComplexDataType complexDataType ->
            //Create Model for each Group, Choice and Sequence.
            Model model = sactImporter.matchOrCreateModel([name:complexDataType.name, description: complexDataType.description], cd).save()
        }

        sactGroups.each{ XsdGroup group ->
            Model model = sactImporter.matchOrCreateModel([name:group.name, description: group.description], cd).save()
        }

    }


}



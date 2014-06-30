package uk.co.mdc.utils

import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataType
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

    def index() {}
    def upload() {
        Importer sactImporter = new Importer()
        def sactHeaders =  ["Data Element Name","Data Element Description",
                            "Parent Model", "Containing Model", "DataType",
                            "Metadata","minOccurs", "maxOccurs"]

        def sactSections = ["DemographicsAndConsultant", "ClinicalStatus", "ProgrammeAndRegimen",
                            "Cycle", "DrugDetails", "Outcome"]


        if(!(request instanceof MultipartHttpServletRequest))
        {
            flash.error="No File to process!"
            render view:"index"
            return
        }

        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
        MultipartFile fileSACT = multiRequest.getFile("xsdSACTFile");
        MultipartFile fileCommonTypes = multiRequest.getFile("xsdSACTTypesFile");

        //Microsoft Excel files
        //Microsoft Excel 2007 files
        def okContentTypes = ['application/vnd.ms-excel','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream'];
        def confType=fileSACT.getContentType();
        def confTypeCommonTypes=fileCommonTypes.getContentType();
        if ((okContentTypes.contains(confType) && fileSACT.size > 0) && (okContentTypes.contains(confTypeCommonTypes) && fileCommonTypes.size > 0)) {
            try {

////////////////////

//                def xmlFileName = "test/unit/resources/SACT/SACT-1.0.0_20110810.xsd"
//                def xmlFileNameCommonTypes = "test/unit/resources/SACT/CommonTypes_20110810.xsd"
                def logErrorsSACT
                def logErrorCommonTypes

                SactXsdLoader parserSACT = new SactXsdLoader(fileSACT.inputStream)
                (sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements, logErrorsSACT) = parserSACT.parse(sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements)
                SactXsdLoader parserCommonTypesSACT = new SactXsdLoader(fileCommonTypes.inputStream)
                (sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements, logErrorCommonTypes) = parserCommonTypesSACT.parse(sactDataElements, sactSimpleDataTypes, sactComplexDataTypes, sactGroups, sactAllDataElements)
                if (logErrorCommonTypes != "")
                {
                    log.error(logErrorCommonTypes)
                }
                if (logErrorsSACT != "")
                {
                    log.error(logErrorsSACT)
                }

                // Check that the root element is SACT
                // Check that the Type is SACTReacordType and has the types defined in sactSections.
                if ((sactDataElements.size() != 1) && (sactSimpleDataTypes.size()==0) && (sactComplexDataTypes.size() == 0) && (sactGroups.size()==0)) {
                    log.error("Wrong SACT file format")
                }
                else
                // Check if the root element exists and the type is correct.
                if ((sactDataElements[0].name != sactRootElement) && (sactDataElements[0].type != sactTypeRootElement))
                {
                    log.error("Wrong SACT file format")
                }
                else{
                    // Create the Conceptual Domain
                    conceptualDomain = sactImporter.importConceptualDomain(sactRootElement, sactDescription)

                    // Add all the simple data types
                    sactSimpleDataTypes.each { def simpleType ->
                        String rule = ""
                        if (simpleType.restriction.pattern != "") {
                            rule = simpleType.restriction.pattern
                            def patternLength = simpleType.restriction.pattern.length()
                            if (simpleType.restriction.minLength != "" && simpleType.restriction.maxLength == "" && simpleType.restriction.pattern.charAt(patternLength - 1) == "]") {
                                rule += ("{" + simpleType.restriction.minLength + "," + simpleType.restriction.maxLength + "}")
                            }

                            Map vdParams = [name: simpleType.name, description: simpleType.description, datatype: simpleType.restriction.base, regexDef: rule]

                            DataType dataType = DataType.findByName(simpleType.restriction.base)
                            DataType dataTypeToImport
                            if (dataType==null)
                            {
                                if (simpleType.restriction.base.contains("xs:"))
                                {
                                    dataType = new DataType(name: simpleType.restriction.base, description: simpleType.restriction.base).save()
                                }
                                else {
                                    XsdSimpleType simpleDataType = sactSimpleDataTypes.find {
                                        it.name == simpleType.restriction.base
                                    }
                                    if (simpleDataType != null) {
                                        def type = simpleDataType.restriction.base
                                        while (type != "" && !type.contains("xs:")) {
                                            def st = sactSimpleDataTypes.find { it.name == type }
                                            if (st != null) type = st.restriction.base
                                        }
                                        def description = simpleDataType.description == null ? "" : simpleDataType.description
                                        dataType = sactImporter.importDataType(simpleDataType.name, type)
                                    }
                                }
                            }


                            ValueDomain vd = sactImporter.importValueDomain(simpleType.name, simpleType.description, dataType, rule, conceptualDomain)
                            print("importValueDomain - ")
                        } else {
                            if (simpleType.restriction.enumeration != "") {
                                DataType dataType = sactImporter.importDataType(simpleType.name, simpleType.restriction.enumeration)
                                print("importDataType - ")
                            } else {
                                def type = simpleType.restriction.base
                                while (type!="" && !type.contains("xs:")) {
                                    def st = sactSimpleDataTypes.find {it.name == type}
                                    if (st!=null) type = st.restriction.base
                                }
                                def description = simpleType.description==null ? "": simpleType.description
                                DataType dataType = sactImporter.importDataType(simpleType.name, type)
//                                Map vdParams = [name: simpleType.name, description: description, datatype: type]
//                                ValueDomain vd = sactImporter.importValueDomain(vdParams, null, conceptualDomain)

                                print("ImportDataType - ")
                            }
                        }
                        println("SimpleType: " + simpleType.name)
                    }

                    // Validate sact section

                    Boolean sectionsOK = true
                    sactSections.each { def section ->
                        def indexSactSection = sactAllDataElements.findIndexOf { it.name == section }
                        if (indexSactSection == -1) {
                            log.error("Section: " + section + " not found in file. \r\n")
                            sectionsOK = false
                        }
                    }

                    if (sectionsOK) {
                        // Extract all the DataElements
                        def rows = []
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
//                                                    ( headers,  rows,  conceptualDomain,  conceptualDomainDescription,  headersMap) {
                        dataImportService.importData(sactHeaders, rows, "SACT",  sactRootElement, sactDescription, headersMap)
                        rows
                        flash.message = "DataElements have been created.\n"
                    }


                }

                //////////////////////



            }
            catch(Exception ex)
            {
                log.error("Exception in handling xsd file :"+ex.message)
                flash.message ="Error in importing the xsd   file.";
            }

        }


        render view: 'index'
    }
}



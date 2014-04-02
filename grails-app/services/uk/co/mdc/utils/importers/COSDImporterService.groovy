 package uk.co.mdc.utils.importers

/**
 * Created by sus_avi on 26/03/2014.
 */

 import grails.transaction.Transactional
 import org.springframework.security.acls.domain.BasePermission
 import uk.co.mdc.Importers.ExcelLoader
 import uk.co.mdc.Importers.ExcelSheet
 import uk.co.mdc.model.ConceptualDomain
 import uk.co.mdc.model.DataElement
 import uk.co.mdc.model.DataElementConcept
 import uk.co.mdc.model.DataType
 import uk.co.mdc.model.ValueDomain


 class COSDImporterService {


    def aclUtilService

    @Transactional
    def saveCOSDDataElements(InputStream inputStream) {

        ArrayList <DataElementConcept> sectionDataElementConcepts = new ArrayList<DataElementConcept>() //only for core
        def activeSectionDataElementConceptIndex=-1;
        def dataItemNumber
        def dataItemSection
        def dataItemName
        def dataItemDescription
        def dataItemFormat
        def dataItemNationalCode
        def dataItemNationalCodeDefinition

        def totalDataElementsImported=0;
        ExcelLoader parser = new ExcelLoader(inputStream)
        ExcelSheet[] excelSheets = parser.parseCOSD();
        def conceptualDomain = findOrCreateConceptualDomain("COSD", "COSD");
        grantUserPermissions(conceptualDomain)
        def sectionDataElementConcept;
        for (def contSheet=0; contSheet <excelSheets.size();contSheet++) {
            def headers = []
            headers = excelSheets[contSheet].headers
            def rows = []
            def sheetName = excelSheets[contSheet].name
            rows = excelSheets[contSheet].rows
            def dataItemNumberIndex = headers.indexOf("Data item No.")
            def dataItemSectionIndex = headers.indexOf("Data Item Section")
            def dataItemNameIndex = headers.indexOf("Data Item Name")
            def dataItemDescriptionIndex = headers.indexOf("Data Item Description")

            def dataItemFormatIndex = headers.indexOf("Format")
            def dataItemNationalCodeIndex = headers.indexOf("National Code")
            def dataItemNationalCodeDefinitionIndex = headers.indexOf("National code definition")
            def dataItemDataDictionaryElementIndex = headers.indexOf("Data Dictionary Element")
            def dataItemCurrentCollectionIndex = headers.indexOf("Current Collection")
            def dataItemSchemaSpecificationIndex = headers.indexOf("Schema Specification")

            def dataElements = []

            //Check the Data Item Name column exists
            if (dataItemNameIndex == -1)
                throw new Exception("Can not find 'Data Item Name' column")

            def COSDdataElementConcept = new DataElementConcept([name: sheetName, description: sheetName + " DataElementConcept"]).save();
            grantUserPermissions(COSDdataElementConcept)

            def nextDataItemNumber;

            for (int cont; cont < rows.size(); cont++) {

                dataItemNumber = rows[cont][dataItemNumberIndex];
                //Check dataItemNumber follows the format aa0000[0]
                if (dataItemNumber ==~ /[a-zA-z]{2}[0-9]{4,5}/) {

                    dataItemSection = rows[cont][dataItemSectionIndex];
                    dataItemName = rows[cont][dataItemNameIndex];
                    if (dataItemDescriptionIndex==-1)
                    {
                        dataItemDescriptionIndex = headers.indexOf("Description")
                        if (dataItemDescriptionIndex==-1)
                            dataItemDescription = ""
                        else
                            dataItemDescription = rows[cont][dataItemDescriptionIndex]
                    }
                    else
                    {
                        dataItemDescription = rows[cont][dataItemDescriptionIndex];
                    }

                    dataItemFormat = rows[cont][dataItemFormatIndex];
                    dataItemNationalCode = rows[cont][dataItemNationalCodeIndex].toString();
                    dataItemNationalCodeDefinition = rows[cont][dataItemNationalCodeDefinitionIndex];
                    if (dataElements.count { it.name == dataItemName } == 0)
                        dataElements.add([dataItemNumber                : dataItemNumber,
                                          dataItemSection               : dataItemSection,
                                          dataItemName                  : dataItemName,
                                          dataItemDescription           : dataItemDescription,
                                          dataItemFormat                : dataItemFormat,
                                          dataItemNationalCode          : dataItemNationalCode,
                                          dataItemNationalCodeDefinition: rows[cont][dataItemNationalCodeDefinitionIndex],
                                          dataItemDataDictionaryElement : rows[cont][dataItemDataDictionaryElementIndex],
                                          dataItemCurrentCollection     : rows[cont][dataItemCurrentCollectionIndex],
                                          dataItemSchemaSpecification   : rows[cont][dataItemSchemaSpecificationIndex]]);

                    //Create the sectionDataElement if this doesn't exist
                    def dataSectionNameNoSpaces= dataItemSection.toString().replaceAll(" ", "");
                    if (activeSectionDataElementConceptIndex==-1 ||  sectionDataElementConcepts.count { it.name.toString().replaceAll(" ","") == dataSectionNameNoSpaces } == 0) {
                        def index = activeSectionDataElementConceptIndex==-1 ? 0: sectionDataElementConcepts.size();
                        sectionDataElementConcepts[index] = new DataElementConcept([name       : dataItemSection,
                                                                                    description: dataItemSection + " DataElementConcept",
                                                                                    parent     : COSDdataElementConcept]).save();
                        grantUserPermissions(sectionDataElementConcepts[index])
                        activeSectionDataElementConceptIndex = index;

                    }
                    else //Check is the sectionDataElement-DataElementConcept already exists
                        if (sectionDataElementConcepts[activeSectionDataElementConceptIndex].name.replaceAll(" ", "") != dataItemSection.toString().replaceAll(" ", ""))
                        {
                            def indexAtSectionDataElementConcepts = sectionDataElementConcepts.indexOf {it.name.replaceAll(" ","") ==dataSectionNameNoSpaces }

                            if (indexAtSectionDataElementConcepts!=-1) {
                                //sectionDataElementConcept = sectionDataElementConcepts[indexAtSectionDataElementConcepts]
                                activeSectionDataElementConceptIndex = indexAtSectionDataElementConcepts;
                            }
                        }
                    //Look for any additional value domain
                    //Check if NationalCode is not empty, since National Code Definition may have some
                    // text that shouldn't be considered as part of the value domain.
                    def valueDomainMap
                    if (dataItemNationalCode.toString().trim() != "" && dataItemNationalCodeDefinition.toString().trim()!="") {
                        valueDomainMap = new HashMap()
                        def key = rows[cont][dataItemNationalCodeIndex].toString();
                        def value = rows[cont][dataItemNationalCodeDefinitionIndex].toString().size() <= 255 ? rows[cont][dataItemNationalCodeDefinitionIndex].toString() : rows[cont][dataItemNationalCodeDefinitionIndex].toString().substring(0, 254);
                        valueDomainMap.put(key, value)

                        if (cont + 1 < rows.size()){
                            nextDataItemNumber = rows[cont + 1][dataItemNumberIndex];
                            while (nextDataItemNumber == "" && dataItemNationalCode.toString().trim() != "" && dataItemNationalCodeDefinition.toString().trim()!="") {
                                //create a list of value domain
                                cont++
                                key = rows[cont][dataItemNationalCodeIndex].toString();
                                value = rows[cont][dataItemNationalCodeDefinitionIndex].toString();
                                valueDomainMap.put(key, value)
                                if (cont + 1 < rows.size())
                                    nextDataItemNumber = rows[cont + 1][dataItemNumberIndex];
                                else
                                    break;
                            }
                        }
                    }
                    //start defining the value domain
                    //If it is a value domain, the hashMap has got more than one element
                    def elementDataType
                    if (valueDomainMap != null && valueDomainMap.size() >= 1) {
                        //create the dataType
                            elementDataType = new DataType(name: dataItemName,
                                enumerated: 'True',
                                enumerations: valueDomainMap).save(failOnError: true)
                    } else {   //the element has not enumerated value domain.
                        //create the datatype
                        elementDataType = new DataType(name: dataItemName,
                                enumerated: 'False').save(failOnError: true)
                    }
                    grantUserPermissions(elementDataType);

                    //Create valueDomain
                    def valueDomain = new ValueDomain(name: dataItemName,
                            conceptualDomain: conceptualDomain,
                            dataType: elementDataType,
                            format: dataItemFormat,
                            description: dataItemDescription).save(failOnError: true);

                    grantUserPermissions(valueDomain)

                    //create data element
                    def dataElement = new DataElement([
                            externalIdentifier: dataItemNumber,
                            name       : dataItemName,
                            description: dataItemDescription,
                            dataElementConcept: sectionDataElementConcepts[activeSectionDataElementConceptIndex]
                    ]).save(flush: true);
                    grantUserPermissions(dataElement)

                    sectionDataElementConcepts[activeSectionDataElementConceptIndex].addToDataElements(dataElement)
                    sectionDataElementConcepts[activeSectionDataElementConceptIndex].save()

                    dataElement.addToDataElementValueDomains(valueDomain);
                    dataElement.save()
                }
            }
            totalDataElementsImported += dataElements.size();
        }
        return totalDataElementsImported
    }

     @Transactional
     def findOrCreateConceptualDomain(String name, String description) {
         def cd = ConceptualDomain.findByName(name)

         if (!cd) {
             cd = new ConceptualDomain(name: name, description: description).save(failOnError: true)
             grantUserPermissions(cd)
         }
         return cd
     }

     @Transactional
     private grantUserPermissions(objectOrList) {

         if (objectOrList instanceof java.util.Collection) {
             for (thing in objectOrList) {
                 grantUserPermissions(thing)
             }
         } else {
             aclUtilService.addPermission objectOrList, 'ROLE_ADMIN', BasePermission.ADMINISTRATION
             aclUtilService.addPermission objectOrList, 'ROLE_USER', BasePermission.READ
         }
     }

}

package uk.co.mdc

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap

class RelationshipImportServiceSpec extends IntegrationSpec {

    def fileName= "test/unit/resources/DataTemplate.xlsx"
    def fileName2= "test/unit/resources/relationshipImport.xlsx"
    def dataImportService, initCatalogueService, relationshipImporterService

    def setup(){
        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultMeasurementUnits()
        initCatalogueService.initDefaultDataTypes()
    }

    void "load element from spreadsheet then load relationship from spreadsheet"()
    {
        when:"loading the dataElements"
        def inputStream = new FileInputStream(fileName)
        ExcelLoader parser = new ExcelLoader(inputStream)
        def (headers, rows) = parser.parse()

        HeadersMap headersMap = new HeadersMap()
        headersMap.dataElementCodeRow = "Data Item Unique Code"
        headersMap.dataElementNameRow = "Data Item Name"
        headersMap.dataElementDescriptionRow = "Data Item Description"
        headersMap.dataTypeRow = "Data type"
        headersMap.parentModelNameRow = "Parent Model"
        headersMap.parentModelCodeRow = "Parent Model Unique Code"
        headersMap.containingModelNameRow = "Model"
        headersMap.containingModelCodeRow = "Model Unique Code"
        headersMap.measurementUnitNameRow = "Measurement Unit"
        headersMap.metadataRow = "Metadata"

        dataImportService.importData(headers, rows, "NHIC : TRA", "NHIC TRA conceptual domain for renal transplantation", ["NHIC Datasets", "TRA", "TRA_OUH", "Round 1"], headersMap)
        DataElement de1 = DataElement.findByModelCatalogueId("MC_037e6162-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de2 = DataElement.findByModelCatalogueId("MC_065e6162-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de3 = DataElement.findByModelCatalogueId("MC_067e6162-3b4f-4ae2-a171-2470b64dff10_1")
        DataElement de4 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de5 = DataElement.findByModelCatalogueId("MC_067e6162-1b6f-4ae2-a171-2470b64dff10_1")
        DataElement de6 = DataElement.findByModelCatalogueId("MC_067e6189-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de7 = DataElement.findByModelCatalogueId("MC_067e6232-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de8 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae7-a171-2470b64dff10_1")
        DataElement de9 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae9-a171-2470b64dff10_1")
        DataElement de10 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-2ae2-a171-2470b64dff10_1")
        DataElement de11 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-9ae2-a181-2470b64dff10_1")
        Model admissions = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff00_1")
        Model unit = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b64dff19_1")
        Model demographics = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff01_1")


        then:"the dataElement should have name"
        de1
        de2
        de3
        de4
        de5
        de6
        de7
        de8
        de9
        de10
        de11
        admissions
        demographics
        unit
        admissions.parentOf.contains(demographics)
        admissions.parentOf.contains(unit)
        demographics.contains.contains(de1)
        demographics.contains.contains(de1)
        demographics.contains.contains(de2)
        demographics.contains.contains(de3)
        demographics.contains.contains(de4)
        demographics.contains.contains(de5)
        unit.contains.contains(de6)
        unit.contains.contains(de7)
        unit.contains.contains(de8)
        unit.contains.contains(de9)
        unit.contains.contains(de10)
        unit.contains.contains(de11)

        when:


        when:"loading the dataElements"
        def inputStream2 = new FileInputStream(fileName2)
        ExcelLoader parser2 = new ExcelLoader(inputStream2)
        (headers, rows) = parser2.parse()

        def errors = relationshipImporterService.importRelationships(headers, rows)

        then:

        de1.relations.contains(de4)
        admissions.relations.contains(de6)
        errors

      }
}
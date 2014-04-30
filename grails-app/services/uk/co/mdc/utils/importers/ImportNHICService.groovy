package uk.co.mdc.utils.importers

import org.grails.datastore.mapping.core.Session
import org.json.simple.JSONObject
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.dataarchitect.DataImportService
import org.modelcatalogue.core.dataarchitect.ImportRow
import org.modelcatalogue.core.dataarchitect.Importer
import org.springframework.security.acls.domain.BasePermission

class ImportNHICService {

    static transactional = true

    def grailsApplication, sessionFactory
    def errors = new HashMap()
    Importer newImporter

        private static final QUOTED_CHARS = [
            "\\": "&#92;",
            ":": "&#58;",
            "|": "&#124;",
            "%": "&#37;",
    ]

    def importData() {
        errors = new HashMap()
        getNhicFiles().each {
            filename -> singleImport(filename)
        }
        return errors
    }


    def getNhicFiles() {
        return fileFunctions.keySet()
    }
//
//    /**
//     * Carry out an import for a single file in the NHIC dataset
//     * @param filename The filename. Must exist in the collection returned by <code>getNhicFiles</code>
//     */
    def singleImport(String filename) {

        newImporter = new Importer()
        def applicationContext = grailsApplication.mainContext
        String basePath = applicationContext.getResource("/").getFile().toString()

        Integer counter = 0

        def modelName = filename.replace(".csv", "")
        modelName = modelName.replaceAll("\\/.*?\\/", "")
        def model = Model.findByName(modelName)

        if(model==null) {

            try {
                new File("${basePath}" + "/WEB-INF/bootstrap-data" + filename).toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
                    fileFunctions[filename](tokens);

                    if (counter > 40) {
                        sessionFactory.currentSession.flush()
                        sessionFactory.currentSession.clear()
                        counter = 0
                    } else {
                        counter++
                    }
                }
            }catch(Exception e){
                errors.put("csv", "csv is not in the appropriate format")
            }
        }else{
            errors.put("model exists", "model already exists for ${modelName}")
        }

        sessionFactory.currentSession.flush()
        sessionFactory.currentSession.clear()

        newImporter.actionPendingModels()
        return errors
    }

    private fileFunctions = [
                        '/CAN/CAN_CUH.csv':
                    { tokens ->
                            ImportRow importRow = new ImportRow()
                            importRow.parentModelName = tokens[1]
                            importRow.containingModelName = tokens[2]
                            importRow.dataElementName = tokens[3]
                            importRow.dataType = tokens[5]
                            importRow.dataElementDescription = tokens[4]
                            importRow.conceptualDomainName = "NHIC Ovarian Cancer : CAN_CUH"
                            importRow.conceptualDomainDescription = "NHIC : CAN_CUH"
                            importRow.metadata = [
                                    "NHIC_Identifier"             : tokens[0],
                                    "Link_to_existing_definition": tokens[6],
                                    "Notes_from_GD_JCIS"         : tokens[7],
                                    "Optional_Local_Identifier": tokens[8],
                                    "A"                           : tokens[9],
                                    "B"                           : tokens[10],
                                    "C"                           : tokens[11],
                                    "D"                           : tokens[12],
                                    "E"                           : tokens[13],
                                    "F"                           : tokens[14],
                                    "G"                           : tokens[15],
                                    "H"                           : tokens[16],
                                    "E2"                          : tokens[17],
                            ]

                            newImporter.parentModels = ["NHIC Datasets", "Ovarian Cancer", "CAN_CUH"]
                            newImporter.ingestRow(importRow)

                    },

            '/CAN/CAN_GSTT.csv':
                    { tokens ->

                        ImportRow importRow = new ImportRow()
                        importRow.parentModelName = tokens[1]
                        importRow.containingModelName = tokens[2]
                        importRow.dataElementName = tokens[3]
                        importRow.dataType = tokens[5]
                        importRow.dataElementDescription = tokens[4]
                        importRow.conceptualDomainName = "NHIC Ovarian Cancer : CAN_GSTT"
                        importRow.conceptualDomainDescription = "NHIC : Ovarian Cancer"
                        importRow.metadata = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[6],
                                "Notes_from_GD_JCIS":tokens[7],
                                "Optional_Local_Identifier":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]

                        newImporter.parentModels = ["NHIC Datasets", "Ovarian Cancer", "CAN_GSTT"]
                        newImporter.ingestRow(importRow)

                    },

            '/CAN/CAN_IMP.csv':
                    { tokens ->

                        ImportRow importRow = new ImportRow()
                        importRow.parentModelName = tokens[1]
                        importRow.containingModelName = tokens[2]
                        importRow.dataElementName = tokens[3]
                        importRow.dataType = tokens[5]
                        importRow.dataElementDescription = tokens[4]
                        importRow.conceptualDomainName = "NHIC Ovarian Cancer : CAN_IMP"
                        importRow.conceptualDomainDescription = "NHIC : Ovarian Cancer"
                        importRow.metadata = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[6],
                                "Notes_from_GD_JCIS":tokens[7],
                                "Optional_Local_Identifier":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]

                        newImporter.parentModels = ["NHIC Datasets", "Ovarian Cancer", "CAN_IMP"]
                        newImporter.ingestRow(importRow)

                        //println "importing: " + tokens[0] + "CAN_IMP"
                   },

                '/CAN/CAN_UCL.csv':
                        { tokens ->
                            ImportRow importRow = new ImportRow()
                            importRow.parentModelName = tokens[1]
                            importRow.containingModelName = tokens[2]
                            importRow.dataElementName = tokens[3]
                            importRow.dataType = tokens[6]
                            importRow.dataElementDescription = tokens[5]
                            importRow.conceptualDomainName = "NHIC Ovarian Cancer : CAN_UCL"
                            importRow.conceptualDomainDescription = "NHIC : Ovarian Cancer"
                            importRow.metadata = [
                                    "NHIC_Identifier":tokens[0],
                                    "Link_to_existing_definition":tokens[7],
                                    "Notes_from_GD_JCIS":tokens[8],
                                    "Optional_Local_Identifier":tokens[9],
                                    "A":tokens[10],
                                    "B":tokens[11],
                                    "C":tokens[12],
                                    "D":tokens[13],
                                    "E":tokens[14],
                                    "System": tokens[4]
                            ]

                            newImporter.parentModels = ["NHIC Datasets", "Ovarian Cancer", "CAN_UCL"]
                            newImporter.ingestRow(importRow)
                        },

                '/ACS/ACS_UCL.csv':
                        { tokens ->
                            ImportRow importRow = new ImportRow()
                            importRow.parentModelName = tokens[1]
                            importRow.containingModelName = tokens[2]
                            importRow.dataElementName = tokens[3]
                            importRow.dataType = tokens[5]
                            importRow.dataElementDescription = tokens[4]
                            importRow.conceptualDomainName = "NHIC : Acute Coronary Syndrome : ACS_UCL"
                            importRow.conceptualDomainDescription = "NHIC : Acute Coronary Syndrome"
                            importRow.metadata = [
                                    "NHIC_Identifier":tokens[0],
                                    "Link_to_existing_definition":tokens[6],
                                    "Optional_Local_Identifier":tokens[7],
                                    "A":tokens[8],
                                    "B":tokens[9],
                                    "C":tokens[10],
                                    "D":tokens[11],
                                    "E":tokens[12]
                            ]

                            newImporter.parentModels = ["NHIC Datasets", "Acute Coronary Syndrome", "ACS_UCL"]
                            newImporter.ingestRow(importRow)
                        },

            '/ACS/ACS_OUH.csv':
                    { tokens ->
                        ImportRow importRow = new ImportRow()
                        importRow.parentModelName = tokens[1]
                        importRow.containingModelName = tokens[2]
                        importRow.dataElementName = tokens[3]
                        importRow.dataType = tokens[5]
                        importRow.dataElementDescription = tokens[4]
                        importRow.conceptualDomainName = "NHIC : Acute Coronary Syndrome : ACS_OUH"
                        importRow.conceptualDomainDescription = "NHIC : Acute Coronary Syndrome"
                        importRow.metadata = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[6],
                                "Optional_Local_Identifier":tokens[7],
                                "A":tokens[8],
                                "B":tokens[9],
                                "C":tokens[10],
                                "D":tokens[11],
                                "E":tokens[12]
                        ]

                        newImporter.parentModels = ["NHIC Datasets", "Acute Coronary Syndrome", "ACS_OUH"]
                        newImporter.ingestRow(importRow)
                    },

            '/ACS/ACS_GSTT.csv':
                    { tokens ->
                        ImportRow importRow = new ImportRow()
                        importRow.parentModelName = tokens[1]
                        importRow.containingModelName = tokens[2]
                        importRow.dataElementName = tokens[3]
                        importRow.dataType = tokens[5]
                        importRow.dataElementDescription = tokens[4]
                        importRow.conceptualDomainName = "NHIC : Acute Coronary Syndrome : ACS_GSTT"
                        importRow.conceptualDomainDescription = "NHIC : Acute Coronary Syndrome"
                        importRow.metadata = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[6],
                                "Optional_Local_Identifier":tokens[7],
                                "A":tokens[8],
                                "B":tokens[9],
                                "C":tokens[10],
                                "D":tokens[11],
                                "E":tokens[12]
                        ]

                        newImporter.parentModels = ["NHIC Datasets", "Acute Coronary Syndrome", "ACS_GSTT"]
                        newImporter.ingestRow(importRow)
                    },

            '/HEP/HEP_OUH.csv':
                    { tokens ->
                        ImportRow importRow = new ImportRow()
                        importRow.parentModelName = tokens[1]
                        importRow.containingModelName = tokens[2]
                        importRow.dataElementName = tokens[3]
                        importRow.dataType = tokens[5]
                        importRow.dataElementDescription = tokens[4]
                        importRow.conceptualDomainName = "NHIC Hepatitis : HEP_OUH"
                        importRow.conceptualDomainDescription = "NHIC : Hepatitis"
                        importRow.metadata = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[7],
                                "Optional_Local_Identifier":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]

                        newImporter.parentModels = ["NHIC Datasets", "Hepatitis", "HEP_OUH"]
                        newImporter.ingestRow(importRow)
                    },

            '/HEP/HEP_UCL.csv':
                    { tokens ->
                        ImportRow importRow = new ImportRow()
                        importRow.parentModelName = tokens[1]
                        importRow.containingModelName = tokens[2]
                        importRow.dataElementName = tokens[3]
                        importRow.dataType = tokens[5]
                        importRow.dataElementDescription = tokens[4]
                        importRow.conceptualDomainName = "NHIC Hepatitis : HEP_UCL"
                        importRow.conceptualDomainDescription = "NHIC : Hepatitis"
                        importRow.metadata = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[7],
                                "Optional_Local_Identifier":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]
                        newImporter.parentModels = ["NHIC Datasets", "Hepatitus", "HEP_UCL"]
                        newImporter.ingestRow(importRow)
                    },

                '/TRA/TRA_CUH.csv':
                        { tokens ->
                            ImportRow importRow = new ImportRow()
                            importRow.parentModelName = tokens[1]
                            importRow.containingModelName = tokens[2]
                            importRow.dataElementName = tokens[3]
                            importRow.dataType = tokens[5]
                            importRow.dataElementDescription = tokens[4]
                            importRow.conceptualDomainName = "NHIC Renal Transplantation : TRA_CUH"
                            importRow.conceptualDomainDescription = "NHIC : Renal Transplantation"
                            importRow.metadata = [
                                    "NHIC_Identifier":tokens[0],
                                    "Link_to_existing_definition":tokens[6],
                                    "Optional_Local_Identifier":tokens[7],
                                    "A":tokens[8],
                                    "B":tokens[9],
                                    "C":tokens[10],
                                    "D":tokens[11],
                                    "E":tokens[12],
                                    "F":tokens[13],
                                    "G":tokens[14],
                                    "H":tokens[15]
                            ]
                            newImporter.parentModels = ["NHIC Datasets", "TRA", "TRA_CUH"]
                            newImporter.ingestRow(importRow)
                        },

                    '/TRA/TRA_GSTT.csv':
                            { tokens ->

                                ImportRow importRow = new ImportRow()
                                importRow.parentModelName = tokens[1]
                                importRow.containingModelName = tokens[2]
                                importRow.dataElementName = tokens[3]
                                importRow.dataType = tokens[5]
                                importRow.dataElementDescription = tokens[4]
                                importRow.conceptualDomainName = "NHIC Renal Transplantation : TRA_GSTT"
                                importRow.conceptualDomainDescription = "NHIC : Renal Transplantation"
                                importRow.metadata = [
                                        "NHIC_Identifier":tokens[0],
                                        "Link_to_existing_definition":tokens[6],
                                        "Optional_Local_Identifier":tokens[7],
                                        "A":tokens[8],
                                        "B":tokens[9],
                                        "C":tokens[10],
                                        "D":tokens[11],
                                        "E":tokens[12],
                                ]
                                newImporter.parentModels = ["NHIC Datasets", "TRA", "TRA_GSTT"]
                                newImporter.ingestRow(importRow)

                            },

                    '/TRA/TRA_OUH.csv':
                            { tokens ->
                                println(tokens[2])
                                ImportRow importRow = new ImportRow()
                                importRow.parentModelName = null
                                importRow.containingModelName = tokens[1]
                                importRow.dataElementName = tokens[2]
                                importRow.dataType = null
                                importRow.dataElementDescription = tokens[3]
                                importRow.conceptualDomainName = "NHIC Renal Transplantation : TRA_OUH"
                                importRow.conceptualDomainDescription = "NHIC : Renal Transplantation"
                                importRow.metadata = [
                                        "NHIC_Identifier":tokens[0],
                                        "A":tokens[4],
                                        "B":tokens[5],
                                        "C":tokens[6],
                                        "D":tokens[7],
                                        "E":tokens[8],
                                        "Comments":tokens[9]
                                ]
                                newImporter.parentModels = ["NHIC Datasets", "TRA", "TRA_OUH"]
                                newImporter.ingestRow(importRow)
                            }
    ]

}



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
import org.springframework.security.acls.domain.BasePermission

class ImportNHICService {

    static transactional = true

    def grailsApplication, sessionFactory
    def errors = new HashMap()

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


//
//    private grantUserPermissions(objectOrList) {
//        if (objectOrList instanceof java.util.Collection) {
//            for (thing in objectOrList) {
//                grantUserPermissions(thing)
//            }
//        } else {
//            aclUtilService.addPermission objectOrList, 'ROLE_ADMIN', BasePermission.ADMINISTRATION
//            aclUtilService.addPermission objectOrList, 'ROLE_USER', BasePermission.READ
//        }
//    }
//
//    /**
//     * Get the list of available files for import
//     * @return the list of available files for import
//     */


    def getNhicFiles() {
        return fileFunctions.keySet()
    }
//
//    /**
//     * Carry out an import for a single file in the NHIC dataset
//     * @param filename The filename. Must exist in the collection returned by <code>getNhicFiles</code>
//     */
    def singleImport(String filename) {

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

        return errors
    }

    private fileFunctions = [
                        '/CAN/CAN_CUH.csv':
                    { tokens ->
                            def section = tokens[1]
                            def subsection = tokens[2]
                            def name = tokens[3]
                            def valueDomainInfo = tokens[5]
                            def description = tokens[4]
                            def conceptualDomain = "NHIC : Ovarian Cancer"
                            def conceptualDomainDescription = "NHIC : Ovarian Cancer"
                            def metadataColumns = [
                                    "NHIC_Identifier"             : tokens[0],
                                    "Link_to_existing_definition": tokens[6],
                                    "Notes_from_GD_JCIS:"         : tokens[7],
                                    "[Optional]_Local_Identifier:": tokens[8],
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
                            def categories = ["NHIC Datasets", "Ovarian Cancer", "CAN_CUH", "Round 1", section, subsection]
                            importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)

                        //println "importing: " + tokens[0] + "CAN_CUH"
                    },

            '/CAN/CAN_GSTT.csv':
                    { tokens ->


                        def section = tokens[1]
                        def subsection = tokens[2]
                        def name = tokens[3]
                        def valueDomainInfo = tokens[5]
                        def description = tokens[4]
                        def conceptualDomain = "NHIC : Ovarian Cancer"
                        def conceptualDomainDescription  = "NHIC : Ovarian Cancer"
                        def metadataColumns = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[6],
                                "Notes_from_GD_JCIS:":tokens[7],
                                "[Optional]_Local_Identifier:":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]
                        def categories = ["NHIC Datasets", "Ovarian Cancer", "CAN_GSTT", "Round 1", section, subsection]
                        importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                        //println "importing: " + tokens[0] + "CAN_GSTT"
                    },

            '/CAN/CAN_IMP.csv':
                    { tokens ->
                        def section = tokens[1]
                        def subsection = tokens[2]
                        def name = tokens[3]
                        def valueDomainInfo = tokens[5]
                        def description = tokens[4]
                        def conceptualDomain = "NHIC : Ovarian Cancer"
                        def conceptualDomainDescription  = "NHIC : Ovarian Cancer"
                        def metadataColumns = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[6],
                                "Notes_from_GD_JCIS:":tokens[7],
                                "[Optional]_Local_Identifier:":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]
                        def categories = ["NHIC Datasets", "Ovarian Cancer", "CAN_IMP", "Round 1", section, subsection]
                        importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                        //println "importing: " + tokens[0] + "CAN_IMP"
                   },

                '/CAN/CAN_UCL.csv':
                        { tokens ->
                            def section = tokens[1]
                            def subsection = tokens[2]
                            def name = tokens[3]
                            def valueDomainInfo = tokens[6]
                            def description = tokens[5]
                            def conceptualDomain = "NHIC : Ovarian Cancer"
                            def conceptualDomainDescription  = "NHIC : Ovarian Cancer"
                            def metadataColumns = [
                                    "NHIC_Identifier":tokens[0],
                                    "Link_to_existing_definition":tokens[6],
                                    "Notes_from_GD_JCIS:":tokens[7],
                                    "[Optional]_Local_Identifier:":tokens[8],
                                    "A":tokens[9],
                                    "B":tokens[10],
                                    "C":tokens[11],
                                    "D":tokens[12],
                                    "E":tokens[13],
                                    "System": tokens[4]
                            ]
                            def categories = ["NHIC Datasets", "Ovarian Cancer", "CAN_UCL", "Round 1", section, subsection]
                            importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                            //println "importing: " + tokens[0] + "CAN_UCL"
                        },

                '/ACS/ACS_UCL.csv':
                        { tokens ->
                            def section = tokens[1]
                            def subsection = tokens[2]
                            def name = tokens[3]
                            def valueDomainInfo = tokens[5]
                            def description = tokens[4]
                            def conceptualDomain = "NHIC : Acute Coronary Syndrome"
                            def conceptualDomainDescription  = "NHIC : Acute Coronary Syndrome"
                            def metadataColumns = [
                                    "NHIC_Identifier":tokens[0],
                                    "Link_to_existing_definition":tokens[6],
                                    "[Optional]_Local_Identifier:":tokens[7],
                                    "A":tokens[8],
                                    "B":tokens[9],
                                    "C":tokens[10],
                                    "D":tokens[11],
                                    "E":tokens[12]
                            ]
                            def categories = ["NHIC Datasets", "Acute Coronary Syndrome", "ACS_UCL", "Round 1", section, subsection]
                            importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                            //println "importing: " + tokens[0] + "ACS_UCL"
                        },

            '/ACS/ACS_OUH.csv':
                    { tokens ->
                        def section = tokens[1]
                        def subsection = tokens[2]
                        def name = tokens[3]
                        def valueDomainInfo = tokens[5]
                        def description = tokens[4]
                        def conceptualDomain = "NHIC : Acute Coronary Syndrome"
                        def conceptualDomainDescription  = "NHIC : Acute Coronary Syndrome"
                        def metadataColumns = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[6],
                                "[Optional]_Local_Identifier:":tokens[7],
                                "A":tokens[8],
                                "B":tokens[9],
                                "C":tokens[10],
                                "D":tokens[11],
                                "E":tokens[12]
                        ]
                        def categories = ["NHIC Datasets", "Acute Coronary Syndrome", "ACS_OUH", "Round 1", section, subsection]
                        importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                        //println "importing: " + tokens[0] + "ASC_OUH"
                    },

            '/ACS/ACS_GSTT.csv':
                    { tokens ->
                        def section = tokens[1]
                        def subsection = tokens[2]
                        def name = tokens[3]
                        def valueDomainInfo = tokens[5]
                        def description = tokens[4]
                        def conceptualDomain = "NHIC : Acute Coronary Syndrome"
                        def conceptualDomainDescription  = "NHIC : Acute Coronary Syndrome"
                        def metadataColumns = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[6],
                                "[Optional]_Local_Identifier:":tokens[7],
                                "A":tokens[8],
                                "B":tokens[9],
                                "C":tokens[10],
                                "D":tokens[11],
                                "E":tokens[12]
                        ]
                        def categories = ["NHIC Datasets", "Acute Coronary Syndrome", "ACS_GSTT", "Round 1", section, subsection]
                        importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                        //println "importing: " + tokens[0] + "ACS_GSTT"
                    },


            '/HEP/HEP_OUH.csv':
                    { tokens ->

                        def section = tokens[1]
                        def subsection = tokens[2]
                        def name = tokens[3]
                        def valueDomainInfo = tokens[5]
                        def description = tokens[4]
                        def conceptualDomain = "NHIC : Hepatitus"
                        def conceptualDomainDescription  = "NHIC : Hepatitus"
                        def metadataColumns = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing_definition":tokens[7],
                                "[Optional]_Local_Identifier:":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]
                        def categories = ["NHIC Datasets", "Hepatitus", "HEP_OUH", "Round 1", section, subsection]
                        importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)

                    },

            '/HEP/HEP_UCL.csv':
                    { tokens ->
                            def section = tokens[1]
                            def subsection = tokens[2]
                            def name = tokens[3]
                            def valueDomainInfo = tokens[5]
                            def description = tokens[4]
                            def conceptualDomain = "NHIC : Hepatitus"
                            def conceptualDomainDescription = "NHIC : Hepatitus"
                            def metadataColumns = [
                                    "NHIC_Identifier"             : tokens[0],
                                    "Link_to_existing_definition": tokens[7],
                                    "[Optional]_Local_Identifier:": tokens[8],
                                    "A"                           : tokens[9],
                                    "B"                           : tokens[10],
                                    "C"                           : tokens[11],
                                    "D"                           : tokens[12],
                                    "E"                           : tokens[13]
                            ]
                            def categories = ["NHIC Datasets", "Hepatitus", "HEP_UCL", "Round 1", section, subsection]
                            importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                    },

                '/TRA/TRA_CUH.csv':
                        { tokens ->
                                def section = tokens[1]
                                def subsection = tokens[2]
                                def name = tokens[3]
                                def valueDomainInfo = tokens[5]
                                def description = tokens[4]
                                def conceptualDomain = "NHIC : TRA"
                                def conceptualDomainDescription  = "NHIC : TRA"
                                def metadataColumns = [
                                        "NHIC_Identifier":tokens[0],
                                        "Link_to_existing_definition":tokens[6],
                                        "[Optional]_Local_Identifier:":tokens[7],
                                        "A":tokens[8],
                                        "B":tokens[9],
                                        "C":tokens[10],
                                        "D":tokens[11],
                                        "E":tokens[12],
                                        "F":tokens[13],
                                        "G":tokens[14],
                                        "H":tokens[15]
                                ]
                                def categories = ["NHIC Datasets", "TRA", "TRA_CUH", "Round 1", section, subsection]
                                importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                        },

                    '/TRA/TRA_GSTT.csv':
                            { tokens ->
                                    def section = tokens[1]
                                    def subsection = tokens[2]
                                    def name = tokens[3]
                                    def valueDomainInfo = tokens[5]
                                    def description = tokens[4]
                                    def conceptualDomain = "NHIC : TRA"
                                    def conceptualDomainDescription  = "NHIC : TRA"
                                    def metadataColumns = [
                                            "NHIC_Identifier":tokens[0],
                                            "Link_to_existing_definition:":tokens[6],
                                            "[Optional]_Local_Identifier:":tokens[7],
                                            "A":tokens[8],
                                            "B":tokens[9],
                                            "C":tokens[10],
                                            "D":tokens[11],
                                            "E":tokens[12]
                                    ]
                                    def categories = ["NHIC Datasets", "TRA", "TRA_GSTT", "Round 1", section, subsection]
                                    importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                            },

                    '/TRA/TRA_OUH.csv':
                            { tokens ->
                                    def section = tokens[1]
                                    def subsection = null
                                    def name = tokens[2]
                                    def valueDomainInfo = null
                                    def description = tokens[3]
                                    def conceptualDomain = "NHIC : TRA"
                                    def conceptualDomainDescription  = "NHIC : TRA"
                                    def metadataColumns = [
                                            "NHIC_Identifier":tokens[0],
                                            "A":tokens[4],
                                            "B":tokens[5],
                                            "C":tokens[6],
                                            "D":tokens[7],
                                            "E":tokens[8],
                                            "Comments":tokens[9]
                                    ]
                                    def categories = ["NHIC Datasets", "TRA", "TRA_OUH", "Round 1", section, subsection]
                                    importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                            }
    ]


    private static importModels(categories, ConceptualDomain conceptualDomain) {
        //categories look something like ["Animals", "Mammals", "Dogs"]
        //where animal is a parent of mammals which is a parent of dogs......

        def modelToReturn

        categories.inject { parentName, childName ->

            parentName = parentName.trim()

            //if there isn't a name for the child return the parentName
            if (childName.equals("") || childName==null) {
                return parentName;
            }else{
                childName = childName.trim()
            }

            //def matches = Model.findAllWhere("name" : name, "parentName" : models)

            //see if there are any models with this name
            Model match
            def namedChildren = Model.findAllWhere("name": childName)

            //see if there are any models with this name that have the same parentName
            if (namedChildren.size()>0) {
                namedChildren.each{ Model childModel ->
                    if(childModel.childOf.collect{it.name}.contains(parentName)){
                        match = childModel
                    }
                }
            }

            //if there isn't a matching model with the same name and parentName
            if (!match) {
                //new Model('name': name, 'parentName': parentName).save()
                Model child
                Model parent

                //create the child model
                child = new Model('name': childName).save()
                child.addToHasContextOf(conceptualDomain)

                modelToReturn = child

                //see if the parent model exists
                parent = Model.findWhere("name": parentName)

                //FIXME we should probably have unique names for models (or codes)
                // or at least within conceptual domains
                // or we need to have a way of choosing the model parent to use
                // at the moment it just uses the first one Model that is returned

                if (!parent) {
                    parent = new Model('name': parentName).save()
                    parent.addToHasContextOf(conceptualDomain)
                }

                child.addToChildOf(parent)

                child.name

                //add the parent child relationship between models

            } else {
                modelToReturn = match
                match.name
            }
        }

        modelToReturn
    }

    private static importDataTypes(name, dataType) {

        //default data type to return is the string data type
        def dataTypeReturn

        dataType.each { line ->

            String[] lines = line.split("\\r?\\n");

            def enumerated = false

            if (lines.size() > 0 && lines[] != null) {

                Map enumerations = new HashMap()

                lines.each { enumeratedValues ->

                    def EV = enumeratedValues.split(":")

                    if (EV != null && EV.size() > 1 && EV[0] != null && EV[1] != null) {
                        def key = EV[0]
                        def value = EV[1]

                        if (value.size() > 244) {
                            value = value[0..244]
                        }

                        key = key.trim()
                        value = value.trim()
                        if(value.isEmpty()){ value="_" }

                        enumerated = true
                        enumerations.put(key, value)
                    }
                }

                if (enumerated) {


                    String enumString = enumerations.sort() collect { key, val ->
                        "${this.quote(key)}:${this.quote(val)}"
                    }.join('|')

                    dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumString)

                    if (!dataTypeReturn) {
                        dataTypeReturn = new EnumeratedType(name: name.replaceAll("\\s", "_"), enumerations: enumerations).save()
                    }
                } else {

                    dataTypeReturn = (DataType.findByNameLike(name)) ?: DataType.findByName("String")

                }
            } else {
                dataTypeReturn = DataType.findByName("String")
            }

        }

        return dataTypeReturn
    }

    private static findOrCreateConceptualDomain(String name, String description) {
        name = name.trim()
        def cd = ConceptualDomain.findByName(name)
        if (!cd) {
            cd = new ConceptualDomain(name: name, description: description).save()
        }
        return cd
    }

    private static String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }


    private void importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns){
        def cd = findOrCreateConceptualDomain(conceptualDomain, conceptualDomainDescription)
        def models = importModels(categories, cd)
        def dataTypes
        def dataType

        if(valueDomainInfo!=null && !valueDomainInfo.isEmpty()) {
            dataTypes = [valueDomainInfo]
            dataType = importDataTypes(name, dataTypes)
        }

        def valid = true

        if(name.isEmpty()){
            valid=false
            errors.put(metadataColumns.get("NHIC_Identifier"), "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        }else if(conceptualDomain.isEmpty()){
            valid=false
            errors.put(metadataColumns.get("NHIC_Identifier"), "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        }else if(categories.isEmpty()){
            valid=false
            errors.put(metadataColumns.get("NHIC_Identifier"), "no models specified for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        }

        if(valid) {

            def de = new DataElement(name: name,
                    description: description.take(2000))
            de.save()

            metadataColumns.each { key, value ->
                de.ext.put(key, value.take(255))
            }

            de.addToContainedIn(models)

            if(valueDomainInfo!=null && dataType!=null){

                def vd = new ValueDomain(name: name.replaceAll("\\s", "_"),
                        //conceptualDomain: cd,
                        dataType: dataType,
                        description: valueDomainInfo.take(2000)).save(failOnError: true);

                vd.addToIncludedIn(cd)
                de.addToInstantiatedBy(vd)

            }

            println "importing: " + name + categories.last()
        }else{
            println("invalid data item")
        }
    }

}



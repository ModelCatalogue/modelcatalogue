package uk.co.mdc.utils.importers

import org.grails.datastore.mapping.core.Session
import org.json.simple.JSONObject
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
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
        DataType.initDefaultDataTypes()
        RelationshipType.initDefaultRelationshipTypes()
        getNhicFiles().each {
            filename -> singleImport(filename)
        }
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

        DataType.initDefaultDataTypes()
        RelationshipType.initDefaultRelationshipTypes()
        def applicationContext = grailsApplication.mainContext
        String basePath = applicationContext.getResource("/").getFile().toString()

        Integer counter = 0

        new File("${basePath}" + "/WEB-INF/bootstrap-data" + filename).toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
            fileFunctions[filename](tokens);

            if(counter>40) {
                sessionFactory.currentSession.flush()
                sessionFactory.currentSession.clear()
                counter = 0
            }else{
                counter++
            }
        }

        println(errors)
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
                        def conceptualDomainDescription  = "NHIC : Ovarian Cancer"
                        def metadataColumns = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing definition:":tokens[6],
                                "Notes_from_GD_JCIS:":tokens[7],
                                "[Optional]_Local_Identifier:":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13],
                                "F":tokens[14],
                                "G":tokens[15],
                                "H":tokens[16],
                                "E2":tokens[17],
                        ]
                        def categories = ["NHIC Datasets", "Ovarian Cancer", "CUH", "Round 1", section, subsection]
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
                                "Link_to_existing definition:":tokens[6],
                                "Notes_from_GD_JCIS:":tokens[7],
                                "[Optional]_Local_Identifier:":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]
                        def categories = ["NHIC Datasets", "Ovarian Cancer", "GSTT", "Round 1", section, subsection]
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
                                "Link_to_existing definition:":tokens[6],
                                "Notes_from_GD_JCIS:":tokens[7],
                                "[Optional]_Local_Identifier:":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]
                        def categories = ["NHIC Datasets", "Ovarian Cancer", "IMP", "Round 1", section, subsection]
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
                                    "Link_to_existing definition:":tokens[6],
                                    "Notes_from_GD_JCIS:":tokens[7],
                                    "[Optional]_Local_Identifier:":tokens[8],
                                    "A":tokens[9],
                                    "B":tokens[10],
                                    "C":tokens[11],
                                    "D":tokens[12],
                                    "E":tokens[13],
                                    "System": tokens[4]
                            ]
                            def categories = ["NHIC Datasets", "Ovarian Cancer", "UCL", "Round 1", section, subsection]
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
                                    "Link_to_existing definition:":tokens[6],
                                    "[Optional]_Local_Identifier:":tokens[7],
                                    "A":tokens[8],
                                    "B":tokens[9],
                                    "C":tokens[10],
                                    "D":tokens[11],
                                    "E":tokens[12]
                            ]
                            def categories = ["NHIC Datasets", "Acute Coronary Syndrome", "UCL", "Round 1", section, subsection]
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
                                "Link_to_existing definition:":tokens[6],
                                "[Optional]_Local_Identifier:":tokens[7],
                                "A":tokens[8],
                                "B":tokens[9],
                                "C":tokens[10],
                                "D":tokens[11],
                                "E":tokens[12]
                        ]
                        def categories = ["NHIC Datasets", "Acute Coronary Syndrome", "OUH", "Round 1", section, subsection]
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
                                "Link_to_existing definition:":tokens[6],
                                "[Optional]_Local_Identifier:":tokens[7],
                                "A":tokens[8],
                                "B":tokens[9],
                                "C":tokens[10],
                                "D":tokens[11],
                                "E":tokens[12]
                        ]
                        def categories = ["NHIC Datasets", "Acute Coronary Syndrome", "GSTT", "Round 1", section, subsection]
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
                                "Link_to_existing definition:":tokens[7],
                                "[Optional]_Local_Identifier:":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]
                        def categories = ["NHIC Datasets", "Hepatitus", "OUH", "Round 1", section, subsection]
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
                        def conceptualDomainDescription  = "NHIC : Hepatitus"
                        def metadataColumns = [
                                "NHIC_Identifier":tokens[0],
                                "Link_to_existing definition:":tokens[7],
                                "[Optional]_Local_Identifier:":tokens[8],
                                "A":tokens[9],
                                "B":tokens[10],
                                "C":tokens[11],
                                "D":tokens[12],
                                "E":tokens[13]
                        ]
                        def categories = ["NHIC Datasets", "Hepatitus", "UCL", "Round 1", section, subsection]
                        importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)

                    },

                '/TRA/TRA_CUH.csv':
                        { tokens ->
                            if(tokens.size()==16){
                                def section = tokens[1]
                                def subsection = tokens[2]
                                def name = tokens[3]
                                def valueDomainInfo = tokens[5]
                                def description = tokens[4]
                                def conceptualDomain = "NHIC : TRA"
                                def conceptualDomainDescription  = "NHIC : TRA"
                                def metadataColumns = [
                                        "NHIC_Identifier":tokens[0],
                                        "Link_to_existing definition:":tokens[6],
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
                                def categories = ["NHIC Datasets", "TRA", "CUH", "Round 1", section, subsection]
                                importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                            }
                        },

                    '/TRA/TRA_GSTT.csv':
                            { tokens ->
                                if(tokens.size()==16){
                                    def section = tokens[1]
                                    def subsection = tokens[2]
                                    def name = tokens[3]
                                    def valueDomainInfo = tokens[5]
                                    def description = tokens[4]
                                    def conceptualDomain = "NHIC : TRA"
                                    def conceptualDomainDescription  = "NHIC : TRA"
                                    def metadataColumns = [
                                            "NHIC_Identifier":tokens[0],
                                            "Link_to_existing definition:":tokens[6],
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
                                    def categories = ["NHIC Datasets", "TRA", "GSTT", "Round 1", section, subsection]
                                    importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                                }
                            },

                    '/TRA/TRA_OUH.csv':
                            { tokens ->
                                if(tokens.size()==16){
                                    def section = tokens[1]
                                    def subsection = tokens[2]
                                    def name = tokens[3]
                                    def valueDomainInfo = tokens[5]
                                    def description = tokens[4]
                                    def conceptualDomain = "NHIC : TRA"
                                    def conceptualDomainDescription  = "NHIC : TRA"
                                    def metadataColumns = [
                                            "NHIC_Identifier":tokens[0],
                                            "Link_to_existing definition:":tokens[6],
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
                                    def categories = ["NHIC Datasets", "TRA", "GSTT", "Round 1", section, subsection]
                                    importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)
                                }
                            }
    ]


    private static importModels(categories, ConceptualDomain conceptualDomain) {
        //categories look something like ["Animals", "Mammals", "Dogs"]
        //where animal is a parent of mammals which is a parent of dogs......

        def modelToReturn

        categories.inject { parentName, childName ->

            parentName = parentName.trim()

            //if there isn't a name for the child return the parentName
            if (childName.equals("")) {
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

                    dataTypeReturn = (DataType.findByName(name)) ?: DataType.findByName("String")

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
        def dataTypes = [valueDomainInfo]
        def dataType = importDataTypes(name, dataTypes)
        def valid = true

        if(name.isEmpty()){
            valid=false
            errors.put("name", "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        }else if(conceptualDomain.isEmpty()){
            valid=false
            errors.put("name", "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        }else if(categories.isEmpty()){
            valid=false
            errors.put("models", "no models specified for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        }else if(!dataType){
            valid=false
            errors.put("data type", "no models specified for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        }

        if(valid) {

            def de = new DataElement(name: name,
                    description: description.take(2000))
            de.save()

            metadataColumns.each { key, value ->
                de.ext.put(key, value.take(255))
            }

            de.addToContainedIn(models)

            if(!valueDomainInfo.isEmpty()){

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








//package uk.co.brc.modelcatalogue
//
//import org.modelcatalogue.core.*
//
//class ImportService {
//
//    static transactional = true
//    def grailsApplication
//
//    private static final QUOTED_CHARS = [
//            "\\": "&#92;",
//            ":": "&#58;",
//            "|": "&#124;",
//            "%": "&#37;",
//    ]
//
//    def importData() {
//
//        DataType.initDefaultDataTypes()
//
//        def applicationContext = grailsApplication.mainContext
//        String basePath = applicationContext.getResource("/").getFile().toString()
//
//        functions.keySet().each { filename ->
//            new File("${basePath}" + "/WEB-INF/bootstrap-data" + filename).toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
//                functions[filename](tokens);
//            }
//        }
//    }
//
//
//    private static functions = [
//
//            '/CAN_CUH.csv':
//                    { tokens ->
//                        def categories = ["NHIC Datasets", "Ovarian Cancer", "CUH", "Round 1", tokens[1], tokens[2]]
//                        def cd = findOrCreateConceptualDomain("NHIC", "NHIC conceptual domain i.e. value domains used the NHIC project")
//                        def models = importModels(categories, cd)
//                        def dataTypes = [tokens[5]]
//                        def dataType = importDataTypes(tokens[3], dataTypes)
//                        def ext = new HashMap()
//
//                        def vd = new ValueDomain(name: tokens[3].replaceAll("\\s", "_"),
//                                //conceptualDomain: cd,
//                                dataType: dataType,
//                                description: tokens[5]).save(failOnError: true);
//
//                        vd.addToIncludedIn(cd)
//
//                        def de = new DataElement(name: tokens[3],
//                                description: tokens[4])
//                        //dataElementConcept: models,
//                        //extension: ext).save(failOnError: true)
//
//                        de.save()
//
//                        de.ext.put("NHIC_Identifier:", tokens[0].take(255));
//                        de.ext.put("Link_to_existing definition:", tokens[6].take(255));
//                        de.ext.put("Notes_from_GD_JCIS", tokens[7].take(255));
//                        de.ext.put("[Optional]_Local_Identifier", tokens[8].take(255));
//                        de.ext.put("A", tokens[9].take(255));
//                        de.ext.put("B", tokens[10].take(255));
//                        de.ext.put("C", tokens[11].take(255));
//                        de.ext.put("D", tokens[12].take(255));
//                        de.ext.put("E", tokens[13].take(255));
//                        de.ext.put("F", tokens[14].take(255));
//                        de.ext.put("G", tokens[15].take(255));
//                        de.ext.put("H", tokens[16]);
//                        de.ext.put("E2", tokens[17].take(255))
//
//
//                        de.addToInstantiatedBy(vd)
//
//                        //de.addToDataElementValueDomains(vd);
//                        //de.save();
//                        println "importing: " + tokens[0] + "_Round1_CAN"
//                    }
//    ]
//
//
//    private static importModels(categories, ConceptualDomain conceptualDomain) {
//        //categories look something like ["Animals", "Mammals", "Dogs"]
//        //where animal is a parent of mammals which is a parent of dogs......
//
//        categories.inject { parentName, childName ->
//
//            //if there isn't a name for the child return the parentName
//            if (childName.equals("")) {
//                return parentName;
//            }
//
//            //def matches = Model.findAllWhere("name" : name, "parentName" : models)
//
//            //see if there are any models with this name
//            def matches
//            def namedChildren = Model.findAllWhere("name": childName)
//
//            //see if there are any models with this name that have the same parentName
//            if (namedChildren) {
//                matches = namedChildren.childOf.contains(parentName)
//            }
//
//            //if there isn't a matching model with the same name and parentName
//            if (!matches) {
//                //new Model('name': name, 'parentName': parentName).save()
//                def child
//                def parent
//
//                //create the child model
//                child = new Model('name': childName).save()
//                child.addToHasContextOf(conceptualDomain)
//
//                //see if the parent model exists
//                parent = Model.findWhere("name": parentName)
//
//                //FIXME we should probably have unique names for models (or codes)
//                // or at least within conceptual domains
//                // or we need to have a way of choosing the model parent to use
//                // at the moment it just uses the first one Model that is returned
//
//                if (!parent) {
//                    parent = new Model('name': parentName).save()
//                    parent.addToHasContextOf(conceptualDomain)
//                }
//
//                child.addToChildOf(parent)
//
//                child.name
//
//                //add the parent child relationship between models
//
//            } else {
//                matches.first();
//            }
//        }
//    }
//
//    private static importDataTypes(name, dataType) {
//
//        //default data type to return is the string data type
//        def dataTypeReturn
//
//        dataType.each { line ->
//
//            String[] lines = line.split("\\r?\\n");
//
//            def enumerated = false
//
//            if (lines.size() > 0 && lines[] != null) {
//
//                Map enumerations = new HashMap()
//
//                lines.each { enumeratedValues ->
//
//                    def EV = enumeratedValues.split(":")
//
//                    if (EV != null && EV.size() > 1 && EV[0] != null && EV[1] != null) {
//                        def key = EV[0]
//                        def value = EV[1]
//
//                        if (value.size() > 244) {
//                            value = value[0..244]
//                        }
//
//                        key = key.trim()
//                        value = value.trim()
//
//                        enumerated = true
//                        enumerations.put(key, value)
//                    }
//                }
//
//                if (enumerated) {
//
//
//                    String enumString = enumerations.sort() collect { key, val ->
//                        "${this.quote(key)}:${this.quote(val)}"
//                    }.join('|')
//
//                    dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumString)
//
//                    if (!dataTypeReturn) {
//                        dataTypeReturn = new EnumeratedType(name: name.replaceAll("\\s", "_"), enumerations: enumerations).save()
//                    }
//                } else {
//
//                    dataTypeReturn = (DataType.findByName(name)) ?: DataType.findByName("String")
//
//                }
//            } else {
//                dataTypeReturn = DataType.findByName("String")
//            }
//        }
//        return dataTypeReturn
//    }
//
//    private static findOrCreateConceptualDomain(String name, String description) {
//        def cd = ConceptualDomain.findByName(name)
//        if (!cd) {
//            cd = new ConceptualDomain(name: name, description: description).save()
//        }
//        return cd
//    }
//
//    private static String quote(String s) {
//        if (s == null) return null
//        String ret = s
//        QUOTED_CHARS.each { original, replacement ->
//            ret = ret.replace(original, replacement)
//        }
//        ret
//    }
//}
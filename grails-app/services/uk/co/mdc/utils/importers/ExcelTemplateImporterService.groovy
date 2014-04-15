package uk.co.mdc.utils.importers

import grails.transaction.Transactional
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.springframework.security.acls.domain.BasePermission
import uk.co.mdc.Importers.ExcelLoader
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ValueDomain
import uk.co.mdc.pathways.*

class ExcelTemplateImporterService {

    private static final QUOTED_CHARS = [
            "\\": "&#92;",
            ":": "&#58;",
            "|": "&#124;",
            "%": "&#37;",
    ]

    def aclUtilService

    def importData(InputStream inputStream) {

        ExcelLoader parser = new ExcelLoader(inputStream)
        def (headers, rows) = parser.parse();
        def dataItemNameIndex = headers.indexOf("Data Item Name")
        def dataItemDescriptionIndex = headers.indexOf("Data Item Description")
        def parentSection = headers.indexOf("Parent Section")
        def section= headers.indexOf("Section")
        def unitsIndex = headers.indexOf("Measurement Unit")
        def dataTypeIndex = headers.indexOf("Data type")
        def templateIndex = headers.indexOf("Template")
        def metadataStartIndex = headers.indexOf("Metadata") + 1
        def metadataEndIndex = headers.size() - 1
        def uniqueCodeIndex = headers.indexOf("Unique Code")
        def elements = []


        if (dataItemNameIndex == -1)
            throw new Exception("Can not find 'Data Item Name' column")

        rows.eachWithIndex { def row, int i ->

            def elementName  = row[dataItemNameIndex]

            def parentSec = row[parentSection]
            def subsection = row[section]
            def name = row[dataItemNameIndex]
            def valueDomainInfo = row[dataTypeIndex]
            def description = row[dataItemDescriptionIndex]
            def conceptualDomain = "NHIC : TRA"
            def conceptualDomainDescription  = "NHIC : TRA"

            def counter = metadataStartIndex
            def metadataColumns = [:]
            while(counter<=metadataEndIndex){
                metadataColumns.put(headers[counter], row[counter])
                counter++
            }

            def categories = ["NHIC Datasets", "TRA", "TRA_OUH", "Round 1", parentSec, subsection]
            importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns)

        }

    }
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
        def dataType
        def cd = findOrCreateConceptualDomain(conceptualDomain, conceptualDomainDescription)
        def models = importModels(categories, cd)

        if(description){description = description.take(2000)}

        if(valueDomainInfo!=null && !valueDomainInfo.isEmpty()) {
            dataType = importDataTypes(name, [valueDomainInfo])
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
                    description: description)
            de.save()



            metadataColumns.each { key, value ->
                if(key){key = key.take(255)}
                if(value){value = value.take(255)}
                de.ext.put(key, value)
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
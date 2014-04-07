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

class ICUExcelImporterService {

    def aclUtilService

    def GetICUDataElementNames(InputStream inputStream) {

        ExcelLoader parser = new ExcelLoader(inputStream)
        def (headers, rows) = parser.parse();
        def dataItemNameIndex = headers.indexOf("Data Item Name")
        def dataItemDescriptionIndex = headers.indexOf("Data Item Description")
        def pathway0Index = headers.indexOf("Section_0")
        def pathway1Index= headers.indexOf("Section_1")
        def pathway2Index= headers.indexOf("Section_2")
        def pathway3Index= headers.indexOf("Section_3")
        def commentsIndex = headers.indexOf("Comments")
        def supportingIndex = headers.indexOf("Supporting")
        def associatedDateTimeIndex = headers.indexOf("Associated date and time")
        def unitsIndex = headers.indexOf("Units")
        def dataTypeIndex = headers.indexOf("Data type")
        def templateIndex = headers.indexOf("Template")
        def listContentIndex = headers.indexOf("List content")
        def timingOfDataCollectionIndex = headers.indexOf("Timing of Data Collection")
        def sourceUCHIndex = headers.indexOf("Source UCH")
        def label1Index = headers.indexOf("label1 - UCH")
        def label2Index = headers.indexOf("label2 - UCH")
        def elements = []


        if (dataItemNameIndex == -1)
            throw new Exception("Can not find 'Data Item Name' column")

        rows.eachWithIndex { def row, int i ->
            def elementName  = row[dataItemNameIndex]

            if (elements.count {it.name == elementName} == 0)
                elements.add([name:elementName,
                                description:row[dataItemDescriptionIndex],
                                pathway0:row[pathway0Index],
                                pathway1:row[pathway1Index],
                                pathway2:row[pathway2Index],
                                pathway3:row[pathway3Index],
                                comments :row[commentsIndex],
                                supporting:row[supportingIndex],
                                associatedDateTime :row[associatedDateTimeIndex],
                                units :row[unitsIndex],
                                dataType :row[dataTypeIndex],
                                template :row[templateIndex],
                                listContent :row[listContentIndex],
                                timingDef :row[timingOfDataCollectionIndex],
                                sourceUCH :row[sourceUCHIndex],
                                label1 :row[label1Index],
                                label2 :row[label2Index] ]);
        }
        return elements;
    }


    @Transactional
    def SaveICUDataElement(def elements)
    {
        def cd = findOrCreateConceptualDomain("ICU","ICU");
        grantUserPermissions(cd)

        def dec=new Model([name:"ICU" , description:"ICU main DataElementConcept"]).save();
        grantUserPermissions(dec)

        Pathway mainPathway = new Pathway([name:"ICU",description: "ICU",isDraft:false]).save(failOnError: true);
        grantUserPermissions(mainPathway)

        elements.eachWithIndex { def el, int i ->

        def dataType =  CreateDataType(el.name,el.units,el.listContent);
        def vd = CreateValueDomain(el, dataType,cd)
        def de = CreateDataElement(el,dec)

        dec.addToContains(de)
        dec.save()

        de.addToInstantiates(vd);
        de.save()


        def path= mainPathway
        if(el.pathway0!=null && el.pathway0!="")
            path =findOrCreatePathWay(el.pathway0,path)

        if(el.pathway1!=null && el.pathway1!="")
            path =findOrCreatePathWay(el.pathway1,path)

        if(el.pathway2!=null  && el.pathway2!="")
            path =findOrCreatePathWay(el.pathway2,path)

        if(el.pathway3!=null  && el.pathway3!="")
            path =findOrCreatePathWay(el.pathway3,path)

        path.addToDataElements(de)
        path.save()
        }
        return [conceptualDomain: cd,pathway:mainPathway]
    }

    @Transactional
    def findOrCreatePathWay(name,parent)
    {
        def node
        if(name!="")
        {
            def pathway= parent.nodes.find {it.name==name}
            if(!pathway)
            {
                Random rand = new Random()
                int max = 150
                def y=rand.nextInt(max+1)
                node = new Node([name:name, pathway:parent,x:25,y:y]).save(failOnError: true);
                grantUserPermissions(node)

                parent.addToNodes(node)
                parent.save()
            }
            else
                node= pathway
        }
        node
    }

    @Transactional
    private DataElement CreateDataElement(el,model) {
        def de = new DataElement([
                name: el.name,
                description: el.description
        ]).save(flush: true);
        de.addToContainedIn(model)
        de
    }

    @Transactional
    private ValueDomain CreateValueDomain(el, dataType, conceptualDomain) {
        def vd = new ValueDomain(name: el.name,
                dataType: dataType,
                format: el.template,
                description: el.description).save(failOnError: true);

        vd.addToIncludedIn(conceptualDomain)

        vd
    }


    @Transactional
    def findOrCreateConceptualDomain(String name, String description) {
        def cd = ConceptualDomain.findByName(name)

        if (!cd) {
            cd = new ConceptualDomain(name: name, description: description).save(failOnError: true)
        }
        return cd
    }

    @Transactional
    private CreateDataType(name,units,listContent)
    {
        def enumerated=false
        def dataTypeReturn
        def enumerations

        if(units=="List" && listContent!=null)
        {
            enumerated=true
            String[] lines = listContent.split("\\r?\\n");

            if (lines.size() > 0 && lines[] != null) {
                enumerations = new HashMap()
                lines.each { enumeratedValues ->
                    def EV = enumeratedValues.split("=")
                    if (EV != null && EV.size() > 1 && EV[0] != null && EV[1] != null) {
                        def key = EV[0]
                        def value = EV[1]
                        enumerated = true
                        enumerations.put(key, value)
                    }
                }
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

            dataTypeReturn = (DataType.findByName(name)) ?: new DataType(name: name)

        }

        return dataTypeReturn
    };

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
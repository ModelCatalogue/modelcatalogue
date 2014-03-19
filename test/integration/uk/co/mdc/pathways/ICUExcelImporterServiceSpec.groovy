package uk.co.mdc.pathways

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import spock.lang.Specification
import uk.co.mdc.model.ConceptualDomain
import uk.co.mdc.model.DataElement
import uk.co.mdc.model.DataElementConcept
import uk.co.mdc.model.DataElementValueDomain
import uk.co.mdc.model.DataType
import uk.co.mdc.model.ValueDomain
import uk.co.mdc.utils.importers.ICUExcelImporterService

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ICUExcelImporterService)
@Mock([DataElement,DataElementConcept,DataType,ValueDomain,DataElementValueDomain,Node,Link,Pathway])
class ICUExcelImporterServiceSpec extends Specification {

    def fileName= "test/integration/resources/ICUData.xls"
    def fileNameError="test/integration/resources/ICUDataError.xls"

    def setup()
    {
        service.aclUtilService = Mock(AclUtilService)
        service.aclUtilService.addPermission() >> true
    }

    private def createTestDataElement()
    {
        [
                [name:"element1",description:"desc1",units:"",listContent:"",pathway0:"Admission",pathway1:"Demographic"]
        ];
    }
    
    void "It should throw exception when file does not contains 'Data Item Name' column"()
    {
        when:"file is loaded"
        def exception

        try{
            def InputStream = new FileInputStream(fileNameError)
            service.GetICUDataElementNames(InputStream)
        }
        catch(Exception ex)
        {
            exception=ex
        }

        then:"It should throw an exception 'Can not find 'Data Item Name' column'"
        exception
        exception.message == "Can not find 'Data Item Name' column"
    }

    
    void "Test if DataElements name is not empty"()
    {
        when:"loading the dataElements"
        def InputStream = new FileInputStream(fileName)
        def dataElementNames= service.GetICUDataElementNames(InputStream)

        then:"all the dataElement should have name"
        dataElementNames.eachWithIndex { def dataElement, int i ->
            assert dataElement.name!=""
        }
    }

    
    void "Test if DataElements are not duplicated"()
    {
        when:"calling GenerateICUDataElement"
        def InputStream = new FileInputStream(fileName)
        def dataElements= service.GetICUDataElementNames(InputStream)
        assert  dataElements
        def duplicate = dataElements.groupBy { it.name }.findAll { it.value.size() > 1}


        then:"it should return a unique list of dataElements"
        dataElements.size() != 0
        duplicate.size()==0
    }

    
    void "Test if SaveICUDataElement creates & saves a dataElementConcept"()
    {
        when:"calling SaveICUDataElement"
        def dataElements = createTestDataElement();
        def dataElementConceptCountPre = DataElementConcept.count()
        service.SaveICUDataElement(dataElements)


        then:"it should create & save a dataElementConcept"
        DataElementConcept.count() ==  dataElementConceptCountPre +1

    }

    
    void "Test if SaveICUDataElement creates & saves dataElements"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[[name:"element1",description:"desc1"]];
        def dataElementCountPre = DataElement.count()
        service.SaveICUDataElement(dataElements)

        then:"it should save the dataElements"
        DataElement.count() ==  dataElementCountPre + dataElements.size()
    }

    
    void "Test if SaveICUDataElement saves name and description of a dataElement"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[[name:"ABCDEFG12344",description:"ABCDsc12344",units:"",listContent:"",pathway0:"Admission",pathway1:"Demographic"]];
        service.SaveICUDataElement(dataElements).id;
        def element = DataElement.find {name==dataElements[0].name}


        then:"it should save the dataElements name and description"
        element
        element.name == dataElements[0].name
        element.description == dataElements[0].description
    }


        void "Test if SaveICUDataElement creates dataElementConcept for a dataElement"()
        {
            when:"calling SaveICUDataElement"
            def dataElements =[[name:"ABCDEFG12344",description:"ABCDsc12344",units:"",listContent:"",pathway0:"Admission",pathway1:"Demographic"]];
            service.SaveICUDataElement(dataElements).id;
            def element = DataElement.find {name==dataElements[0].name}


            then:"it should save the dataElements name and description"
            element.dataElementConcept
            element.dataElementConcept.name == "ICU"
            element.dataElementConcept.description == "ICU main DataElementConcept"
        }


    void "Test if SaveICUDataElement creates valueDomain for a dataElement"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[[name:"ABCDEFG12344",description:"ABCDsc12344",units:"",listContent:"",pathway0:"Admission",pathway1:"Demographic"]];
        service.SaveICUDataElement(dataElements).id;
        def element = DataElement.find {name==dataElements[0].name}


        then:"it should save the dataElements name and description"
        element.dataElementValueDomains[0].valueDomain.name == dataElements[0].name
        element.dataElementValueDomains[0].valueDomain.description == dataElements[0].description
    }




    void "Test if CreateDataType saves the enumerated DataType correctly"()
    {
        when:"calling CreateDataType"
        def listOfContent = "01=Number present and verified\n"+
                            "02=Number present but not traced"
        def dtCountPre = DataType.count()
        DataType dataType= service.CreateDataType("test","List",listOfContent);

        then:"it should save the DataType"
        DataType.count() == dtCountPre + 1
        dataType.enumerated
        dataType.enumerations["01"] == "Number present and verified"
        dataType.enumerations["02"] == "Number present but not traced"
    }


    void "Test if CreateDataType saves the simple DataType correctly"()
    {
        when:"calling CreateDataType"
        def dtCountPre = DataType.count()
        DataType dataType= service.CreateDataType("test","text","");

        then:"it should save the DataType"
        DataType.count() == dtCountPre + 1
        dataType
        !dataType.enumerated
    }

    void "Test if SaveICUDataElement creates a conceptualDomain"()
    {
        when:"calling SaveICUDataElement"
        def dataElements = createTestDataElement();
        def preCd= ConceptualDomain.count()
        def  result= service.SaveICUDataElement(dataElements);

        then:"it should save a conceptualDomain"
        ConceptualDomain.count() ==  preCd +1
        result.conceptualDomain.name == "ICU"
        result.conceptualDomain.description == "ICU"
    }


    void "Test if SaveICUDataElement creates and saves a dataType for each item"()
    {
        when:"calling SaveICUDataElement"
        def dataElements = createTestDataElement();
        def preDataType= DataType.count()
        service.SaveICUDataElement(dataElements);


        then:"it should create and save a DataType"
        DataType.count() ==  preDataType+1
    }


    void "Test if SaveICUDataElement creates and saves a valueDomain for each item"()
    {
        when:"calling SaveICUDataElement"
        def dataElements = createTestDataElement();
        def preValueDomain= ValueDomain.count()
        service.SaveICUDataElement(dataElements);

        then:"it should create and save a valueDomain"
        ValueDomain.count() ==  preValueDomain+1
    }


    void "Test if findOrCreateConceptualDomain does not create duplicate conceptualDomain"()
    {
        when:"findOrCreateConceptualDomain is called"
        def preCDCount = ConceptualDomain.count();
        service.findOrCreateConceptualDomain("test123","test")
        service.findOrCreateConceptualDomain("test123","a duplicate one")

        then:"A conceptualDomain should be created"
         ConceptualDomain.count() ==  preCDCount + 1
    }

    void "Test if SaveICUDataElement creates and saves a top level Pathway"()
    {
        when:"calling SaveICUDataElement"
        def dataElements = createTestDataElement();
        //find all top level pathways
        def prePathwayCount= Pathway.list().count {it.class == Pathway }

        def result=service.SaveICUDataElement(dataElements);
        def topLevel = result.pathway
        def postPathwayCount= Pathway.list().count {it.class == Pathway }


        then:"it should create and save a top level Pathway"
        postPathwayCount ==  prePathwayCount+1
        topLevel.name=="ICU"

    }

    void "Test if findOrCreatePathWay creates a subPathway and assign it to parent pathway"()
    {
        when:"calling findOrCreatePathWay"
        def pathway=new Pathway([name:"TopLevel"]).save();

        def preSubPathwayCount= Node.countByPathwayIsNotNull()
        def node=service.findOrCreatePathWay("SubNode",pathway);

        then:"A node is added to the subPathway"
        Node.countByPathwayIsNotNull() ==  preSubPathwayCount+1
        node.pathway == pathway
    }

    void "Test if findOrCreatePathWay creates a subPathway in a subPathway"()
    {
        when:"calling findOrCreatePathWay"
        def mainPathway=new Pathway([name:"TopLevel"]).save();
        def preSubPathwayCount= Node.countByPathwayIsNotNull()

        def subPathway1=service.findOrCreatePathWay("SubPathway1",mainPathway);
        def subPathway2=service.findOrCreatePathWay("SubPathway2",subPathway1);

        then:"A node is added to the subPathway"
        Node.countByPathwayIsNotNull() ==  preSubPathwayCount+2
        subPathway1.pathway == mainPathway
        subPathway2.pathway == subPathway1
    }


    void "Test if findOrCreatePathWay does not create an existing pathway"()
    {
        when:"calling findOrCreatePathWay"
        def pathway=new Pathway([name:"TopLevel"]).save();
        def preSubPathwayCount= Node.countByPathwayIsNotNull()
        def node1=service.findOrCreatePathWay("SubNode",pathway);
        def node2=service.findOrCreatePathWay("SubNode",pathway);

        then:"Jus one node should be added"
        Node.countByPathwayIsNotNull() ==  preSubPathwayCount+1
        node1.pathway == pathway
        node2==node1
    }




    void "Test if SaveICUDataElement creates the pathway and subPathways"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[
                [name:"test1234ForSave",description:"test1234ForSave",units:"",listContent:"",pathway0:"Admission",pathway1: "SubPath1"],
                [name:"test5678ForSave",description:"test5678ForSave",units:"",listContent:"",pathway0:"Admission",pathway1: "SubPath2"],
                [name:"test5678ForSave",description:"test5678ForSave",units:"",listContent:"",pathway0:"Admission",pathway1: "SubPath3"]
        ];
        def result = service.SaveICUDataElement(dataElements);


        then:"it should create a top Pathway and it's subPathway"
        result.pathway
        result.pathway.nodes.size()== 1
        result.pathway.nodes[0].name == "Admission"
        result.pathway.nodes[0].nodes.size() == 3
        result.pathway.nodes[0].nodes[0].name == "SubPath1"
        result.pathway.nodes[0].nodes[1].name == "SubPath2"
        result.pathway.nodes[0].nodes[2].name == "SubPath3"
    }


    void "Test if SaveICUDataElement does not create duplicate subPathways"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[
                [name:"test1234xForSave",description:"test1234xForSave",units:"",listContent:"",pathway0:"Admission",pathway1: "SubPath1"],
                [name:"test5678xForSave",description:"test5678xForSave",units:"",listContent:"",pathway0:"Admission",pathway1: "SubPath1"]
        ];
        def result = service.SaveICUDataElement(dataElements);

        then:"it should not create duplicate subPathway"
        result.pathway
        result.pathway.nodes[0].nodes.size() == 1
        result.pathway.nodes[0].nodes[0].name == "SubPath1"
    }



    void "Test if SaveICUDataElement assigns dataElement to a node"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[[name:"test1234ForSave",description:"test1234ForSave",units:"",listContent:"",pathway0:"Admission"]];
        def result = service.SaveICUDataElement(dataElements);
        def element = DataElement.find {name==dataElements[0].name}
        def subPathway = result.pathway.nodes[0]

        then:"it should assign the dataElement to the pathway node"
        subPathway.dataElements
        subPathway.dataElements.contains(element)
    }
}
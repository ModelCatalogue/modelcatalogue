package uk.co.mdc

import grails.plugin.springsecurity.acl.AclUtilService
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain
import uk.co.mdc.pathways.Pathway

class ICUExcelImporterServiceSpec extends IntegrationSpec {

    def fileName= "test/unit/resources/ICUData.xls"
    def fileNameError="test/unit/resources/ICUDataError.xls"
    def ICUExcelImporterService

    def setup()
    {
        ICUExcelImporterService.aclUtilService = Mock(AclUtilService)
        ICUExcelImporterService.aclUtilService.addPermission() >> true
    }

    private def createTestDataElement()
    {
        [
                [name:"element1",description:"desc1",units:"",listContent:"",pathway0:"Admission",pathway1:"Demographic"]
        ];
    }

    def "It should throw exception when file does not contains 'Data Item Name' column"()
    {
        when:"file is loaded"
        def exception

        try{
            def InputStream = new FileInputStream(fileNameError)
            ICUExcelImporterService.GetICUDataElementNames(InputStream)
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
        def dataElementNames= ICUExcelImporterService.GetICUDataElementNames(InputStream)

        then:"all the dataElement should have name"
        dataElementNames.eachWithIndex { def dataElement, int i ->
            assert dataElement.name!=""
        }
    }


    void "Test if DataElements are not duplicated"()
    {
        when:"calling GenerateICUDataElement"
        def InputStream = new FileInputStream(fileName)
        def dataElements= ICUExcelImporterService.GetICUDataElementNames(InputStream)
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
        def dataElementConceptCountPre = Model.count()
        ICUExcelImporterService.SaveICUDataElement(dataElements)


        then:"it should create & save a dataElementConcept"
        Model.count() ==  dataElementConceptCountPre +1

    }


    void "Test if SaveICUDataElement creates & saves dataElements"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[[name:"element1",description:"desc1"]];
        def dataElementCountPre = DataElement.count()
        ICUExcelImporterService.SaveICUDataElement(dataElements)

        then:"it should save the dataElements"
        DataElement.count() ==  dataElementCountPre + dataElements.size()
    }


    void "Test if SaveICUDataElement saves name and description of a dataElement"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[[name:"ABCDEFG12344",description:"ABCDsc12344",units:"",listContent:"",pathway0:"Admission",pathway1:"Demographic"]];
        ICUExcelImporterService.SaveICUDataElement(dataElements).id;
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
            ICUExcelImporterService.SaveICUDataElement(dataElements).id;
            def element = DataElement.find {name==dataElements[0].name}


            then:"it should save the dataElements name and description"
            element.containedIn
            element.containedIn.contains(Model.findByName("ICU"))
        }


    //this is not the desired behaviour
    //when there is no value domain we should not create one
    //instead we should use the data architect service to create one.

//    void "Test if SaveICUDataElement creates valueDomain for a dataElement"()
//    {
//        when:"calling SaveICUDataElement"
//        def dataElements =[[name:"ABCDEFG12344",description:"ABCDsc12344",units:"",listContent:"",pathway0:"Admission",pathway1:"Demographic"]];
//        ICUExcelImporterService.SaveICUDataElement(dataElements).id;
//        def element = DataElement.find {name==dataElements[0].name}
//
//
//        then:"it should save the dataElements name and description"
//        element.instantiatedBy
//        element.instantiatedBy[0].name == dataElements[0].name
//        element.instantiatedBy[0].description == dataElements[0].description
//    }




    void "Test if CreateDataType saves the enumerated DataType correctly"()
    {
        when:"calling CreateDataType"
        def listOfContent = "01=Number present and verified\n"+
                            "02=Number present but not traced"
        def dtCountPre = DataType.count()
        
        EnumeratedType dataType= ICUExcelImporterService.findOrCreateDataTypeICU("test",listOfContent);

        then:"it should save the DataType"
        DataType.count() == dtCountPre + 1
        dataType.enumerations.get("01") == "Number present and verified"
        dataType.enumerations.get("02") == "Number present but not traced"
    }


    void "Test if CreateDataType finds the DataType correctly"()
    {
        when:"calling CreateDataType"
        def dtCountPre = DataType.count()
        DataType dataType= ICUExcelImporterService.findOrCreateDataTypeICU("test","")

        then:"it should save the DataType"
        DataType.count() == dtCountPre
        dataType
        !dataType.instanceOf(EnumeratedType)
    }

    void "Test if SaveICUDataElement creates a conceptualDomain"()
    {
        when:"calling SaveICUDataElement"
        def dataElements = createTestDataElement();
        def preCd= ConceptualDomain.count()
        def  result= ICUExcelImporterService.SaveICUDataElement(dataElements);

        then:"it should save a conceptualDomain"
        ConceptualDomain.count() ==  preCd +1
        result.conceptualDomain.name == "ICU"
        result.conceptualDomain.description == "ICU"
    }


    void "Test if SaveICUDataElement creates and finds dataType for each item"()
    {
        when:"calling SaveICUDataElement"
        def dataElements = createTestDataElement();
        def preDataType= DataType.count()
        ICUExcelImporterService.SaveICUDataElement(dataElements);


        then:"it should find and the DataType"
        DataType.count() ==  preDataType
    }


    //this is not the desired behaviour
    //when there is no value domain we should not create one
    //instead we should use the data architect service to create one.


//    void "Test if SaveICUDataElement creates and saves a valueDomain for each item"()
//    {
//        when:"calling SaveICUDataElement"
//        def dataElements = createTestDataElement();
//        def preValueDomain= ValueDomain.count()
//        ICUExcelImporterService.SaveICUDataElement(dataElements);
//
//        then:"it should create and save a valueDomain"
//        ValueDomain.count() ==  preValueDomain+1
//    }


    void "Test if findOrCreateConceptualDomain does not create duplicate conceptualDomain"()
    {
        when:"findOrCreateConceptualDomain is called"
        def preCDCount = ConceptualDomain.count();
        ICUExcelImporterService.findOrCreateConceptualDomain("test123","test")
        ICUExcelImporterService.findOrCreateConceptualDomain("test123","a duplicate one")

        then:"A conceptualDomain should be created"
         ConceptualDomain.count() ==  preCDCount + 1
    }

    void "Test if SaveICUDataElement creates and saves a top level Pathway"()
    {
        when:"calling SaveICUDataElement"
        def dataElements = createTestDataElement();
        //find all top level pathways
        def prePathwayCount= Pathway.list().count {it.class == Pathway }

        def result=ICUExcelImporterService.SaveICUDataElement(dataElements);
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

        def preSubPathwayCount= uk.co.mdc.pathways.Node.countByPathwayIsNotNull()
        def node=ICUExcelImporterService.findOrCreatePathWay("SubNode",pathway);

        then:"A node is added to the subPathway"
        uk.co.mdc.pathways.Node.countByPathwayIsNotNull() ==  preSubPathwayCount+1
        node.pathway == pathway
    }

    void "Test if findOrCreatePathWay creates a subPathway in a subPathway"()
    {
        when:"calling findOrCreatePathWay"
        def mainPathway=new Pathway([name:"TopLevel"]).save();
        def preSubPathwayCount= uk.co.mdc.pathways.Node.countByPathwayIsNotNull()

        def subPathway1=ICUExcelImporterService.findOrCreatePathWay("SubPathway1",mainPathway);
        def subPathway2=ICUExcelImporterService.findOrCreatePathWay("SubPathway2",subPathway1);

        then:"A node is added to the subPathway"
        uk.co.mdc.pathways.Node.countByPathwayIsNotNull() ==  preSubPathwayCount+2
        subPathway1.pathway == mainPathway
        subPathway2.pathway == subPathway1
    }


    void "Test if findOrCreatePathWay does not create an existing pathway"()
    {
        when:"calling findOrCreatePathWay"
        def pathway=new Pathway([name:"TopLevel"]).save();
        def preSubPathwayCount= uk.co.mdc.pathways.Node.countByPathwayIsNotNull()
        def node1=ICUExcelImporterService.findOrCreatePathWay("SubNode",pathway);
        def node2=ICUExcelImporterService.findOrCreatePathWay("SubNode",pathway);

        then:"Jus one node should be added"
        uk.co.mdc.pathways.Node.countByPathwayIsNotNull() ==  preSubPathwayCount+1
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
        def result = ICUExcelImporterService.SaveICUDataElement(dataElements);


        then:"it should create a top Pathway and it's subPathway"
        result.pathway
        result.pathway.nodes.size()== 1
        result.pathway.nodes[0].name == "Admission"
        result.pathway.nodes[0].nodes.size() == 3
    }


    void "Test if SaveICUDataElement does not create duplicate subPathways"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[
                [name:"test1234xForSave",description:"test1234xForSave",units:"",listContent:"",pathway0:"Admission",pathway1: "SubPath1"],
                [name:"test5678xForSave",description:"test5678xForSave",units:"",listContent:"",pathway0:"Admission",pathway1: "SubPath1"]
        ];
        def result = ICUExcelImporterService.SaveICUDataElement(dataElements);

        then:"it should not create duplicate subPathway"
        result.pathway
        result.pathway.nodes[0].nodes.size() == 1
        result.pathway.nodes[0].nodes[0].name == "SubPath1"
    }



    void "Test if SaveICUDataElement assigns dataElement to a node"()
    {
        when:"calling SaveICUDataElement"
        def dataElements =[[name:"test1234ForSave",description:"test1234ForSave",units:"",listContent:"",pathway0:"Admission"]];
        def result = ICUExcelImporterService.SaveICUDataElement(dataElements);
        def element = DataElement.find {name==dataElements[0].name}
        def subPathway = result.pathway.nodes[0]

        then:"it should assign the dataElement to the pathway node"
        subPathway.dataElements
        subPathway.dataElements.contains(element)
    }
}
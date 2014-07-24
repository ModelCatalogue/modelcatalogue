package uk.co.mdc.utils

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import spock.lang.Specification
import uk.co.mdc.Importers.COSDExcelLoader
import uk.co.mdc.Importers.ExcelSheet
import uk.co.mdc.Importers.SACT.SactXsdElement
import uk.co.mdc.Importers.SACT.XsdComplexDataType
import uk.co.mdc.Importers.SACT.XsdElement
import uk.co.mdc.Importers.SACT.XsdGroup
import uk.co.mdc.Importers.SACT.XsdPattern
import uk.co.mdc.Importers.SACT.XsdRestriction
import uk.co.mdc.Importers.SACT.XsdSimpleType

/**
 * Created by sus_avi on 11/07/2014.
 */


class SactXsdlLoaderSpec extends Specification {


    //SimpleDataTypes
    def setup(){
        XsdRestriction restriction_A = new XsdRestriction(base:"AlphaNumericType",
                enumeration: "A:A\n" +
                        "N:N\n" +
                        "C:C\n" +
                        "P:P\n" +
                        "9:9")

        XsdSimpleType simpleType_A = new XsdSimpleType(name:"DrugTreatmentIntentType", description: "", restriction: restriction_A)

        XsdPattern patternB = new XsdPattern(value:  "[A-Za-z0-9]+")
        ArrayList<XsdPattern> patternsB = []
        patternsB<<patternB
        XsdRestriction restriction_B = new XsdRestriction(base:"RestrictedStringType",
                patterns: patternsB)

        XsdSimpleType simpleType_B = new XsdSimpleType(name:"AlphaNumericType",
                description: "Only allows letters and numbers. Empty string disallowed. Whitespace disallowed.",
                restriction: restriction_B)

        XsdPattern pattern_C = new XsdPattern(value:"[[A-Za-z0-9\\s~!\"@#\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?\\[\\\\\\]_\\{\\}\\^£€†]*", description: "" )
        ArrayList<XsdPattern> patterns_C = []
        patterns_C << pattern_C
        XsdRestriction restriction_C = new XsdRestriction(base:"PopulatedStringType",
                patterns:patterns_C )

        XsdSimpleType simpleType_C = new XsdSimpleType(name:"RestrictedStringType",
                description: "Only allows letters, numbers and common punctuation and symbols. Empty string disallowed. Definition as GDSC plus dagger symbol to support ICD-10",
                restriction: restriction_C)

        XsdRestriction restriction_D = new XsdRestriction(base:"xs:string", minLength: "1")

        XsdSimpleType simpleType_D = new XsdSimpleType(name:"PopulatedStringType",
                description: "A non-empty string",
                restriction: restriction_D)


        XsdComplexDataType complexType_A = new XsdComplexDataType(name:"SACTStructure", description: "")
        XsdComplexDataType complexType_B = new XsdComplexDataType(name: "DemographicsAndConsultantStructure")

        XsdGroup group = new XsdGroup(name: "Group A", description: "Description Group A")

        SactXsdElement element = new SactXsdElement(name: "NHSNumber", description: "NHS Number", type:"NHSNumberType", section: "DemographicsAndConsultantStructure" )



    }

    void "Test that an Xsd file is loaded properly"() {

        when:"An XSD file is loaded"

        then: "File content is loaded"

    }

    void "Test that readSACTElement successfully reads an element with attributes only"(){
//<xs:element name="SACTRecord" type="SACTSACTRecordType" minOccurs="1" maxOccurs="unbounded"/>
        when: "I have a valueNode for an Element and I call the readSACTElement"

        then: "I get the associated XsdElement structure for the given valueNode"
    }

    void "Test that readSACTSimpleType read an element properly"(){

        when: "I have a valueNode for an SimpleType and I call the readSACTSimpleType"

        then: "I get the associated SimpleType structure for the given valueNode"
    }

    void "Test that readUnion read an element properly"(){

        when: "I have a valueNode for an Element and I call the readUnion"

        then: "I get the associated XsdElement structure for the given valueNode"
    }

    void "Test that readList read an element properly"(){

        when: "I have a valueNode for an Element and I call the readList"

        then: "I get the associated XsdElement structure for the given valueNode"
    }

    void "Test that readComplexType read an element properly"(){

        when: "I have a valueNode for an Element and I call the readComplexType"

        then: "I get the associated XsdElement structure for the given valueNode"
    }





}


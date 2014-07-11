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
import uk.co.mdc.Importers.SACT.XsdRestriction
import uk.co.mdc.Importers.SACT.XsdSimpleType

/**
 * Created by sus_avi on 11/07/2014.
 */


class SactXsdlLoaderSpec extends Specification {

    //SimpleDataTypes
     XsdRestriction restriction_A = new XsdRestriction(base:"AlphaNumericType",
                                                    pattern: "",
                                                    enumeration: "A:A\n" +
                                                            "N:N\n" +
                                                            "C:C\n" +
                                                            "P:P\n" +
                                                            "9:9")

    XsdSimpleType simpleType_A = new XsdSimpleType(name:"DrugTreatmentIntentType", description: "", restriction: restriction)

    XsdRestriction restriction_A_1 = new XsdRestriction(base:"RestrictedStringType",
            pattern: "[A-Za-z0-9]+")

    XsdSimpleType simpleType_A_1 = new XsdSimpleType(name:"AlphaNumericType",
    description: "Only allows letters and numbers. Empty string disallowed. Whitespace disallowed.",
    restriction: restriction_A_1)

    XsdRestriction restriction_A_1_1 = new XsdRestriction(base:"PopulatedStringType",
            pattern: "[[A-Za-z0-9\\s~!\"@#\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?\\[\\\\\\]_\\{\\}\\^£€†]*")

    XsdSimpleType simpleType_A_1_1 = new XsdSimpleType(name:"RestrictedStringType",
            description: "Only allows letters, numbers and common punctuation and symbols. Empty string disallowed. Definition as GDSC plus dagger symbol to support ICD-10",
            restriction: restriction_A_1_1)

    XsdRestriction restriction_A_1_1 = new XsdRestriction(base:"xs:string", minLength: "1")

    XsdSimpleType simpleType_A_1_1 = new XsdSimpleType(name:"PopulatedStringType",
            description: "A non-empty string",
            restriction: restriction_A_1_1)


    XsdComplexDataType complexType_A = new XsdComplexDataType(name:"SACTStructure", description: "")
    XsdComplexDataType complexType_B = new XsdComplexDataType(name: "DemographicsAndConsultantStructure")

    XsdGroup group = new XsdGroup(name: "Group A", description: "Description Group A")

    SactXsdElement element = new SactXsdElement(name: "NHSNumber", description: "NHS Number", type:"NHSNumberType", section: "DemographicsAndConsultantStructure" )



    def setup(){

    }

    void "Test that an Xsd file is loaded properly"() {

        when:"An XSD file is loaded"

        then: "File content is loaded"

    }


}


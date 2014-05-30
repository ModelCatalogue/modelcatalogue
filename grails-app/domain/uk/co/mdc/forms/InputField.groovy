package uk.co.mdc.forms

abstract class InputField {
	
	String defaultValue
	String placeholder
	Integer maxCharacters
	String unitOfMeasure
	String format
	
    static constraints = {
		 defaultValue nullable:true, maxSize: 255
		 placeholder nullable:true, maxSize: 255
		 maxCharacters nullable:true
		 unitOfMeasure nullable:true, maxSize: 255
		 format nullable:true
		}

}

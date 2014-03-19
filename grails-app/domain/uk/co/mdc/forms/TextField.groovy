package uk.co.mdc.forms

class TextField extends InputField{
	
	Integer minValue
    Integer maxValue

    static constraints = {
        minValue nullable:true
        maxValue nullable:true
    }
}

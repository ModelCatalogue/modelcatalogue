package uk.co.mdc.forms

class FormDesignElement {
	
	String label
	String style
	//I'm assuming
	Integer designOrder
	//N.B. At present rules contains the hide/show logic for the given element
	
	static hasMany = [rules: Rule]
	
    static constraints = {
		label nullable: true, maxSize: 255
		style nullable:true, maxSize: 255
        designOrder max: 1000
    }
	
}

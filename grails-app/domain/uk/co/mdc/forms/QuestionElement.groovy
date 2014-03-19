package uk.co.mdc.forms

class QuestionElement extends FormDesignElement implements Comparable{

	String questionNumber
	String prompt 
	String additionalInstructions
    Integer dataElement
    Integer valueDomain
	InputField inputField
	
	static belongsTo = [section: SectionElement]
	
    static constraints = {
		inputField nullable: true
		prompt nullable: true
		additionalInstructions nullable: true
		questionNumber nullable: true
		section nullable: true
	}
	
	static mapping = {
		additionalInstructions type: 'text'
		prompt type: 'text'
	}
	
	
	@Override
	public int compareTo(obj){
	  if(obj){
	    this.designOrder?.compareTo(obj.designOrder)
	  }
	}
}

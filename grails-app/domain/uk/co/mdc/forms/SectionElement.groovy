package uk.co.mdc.forms

class SectionElement extends FormDesignElement implements Comparable{

	String sectionNumber
	String title
	//N.B. Ordered?
	String[] instructions

	static hasMany = [presentationElements: PresentationElement, questionElements: QuestionElement]

	static belongsTo = [formDesign: FormDesign]

    static constraints = {
		sectionNumber nullable:true
		title nullable:true
		instructions nullable:true
    }
	
	static mapping = {
		questionElements cascade: 'all-delete-orphan'
		questionElements sort: 'designOrder'
	}
	
	@Override
	public int compareTo(obj){
	  if(obj){
		this.designOrder?.compareTo(obj.designOrder)
	  }
	}
	
	//this implements the sorting for the sortable set (question elements)
	
	
}

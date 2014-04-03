package uk.co.mdc.forms

class FormDesign {
	
	String name 
	String versionNo
	Boolean isDraft = true
	//n.b. description is just for storage purposes not displayed when the form is rendered
	String description
	FormDesignElement header
	FormDesignElement footer

	static hasMany = [sections: SectionElement]
	
    static constraints = {
        name size: 1..255
        description nullable: true, maxSize: 2000
		header nullable:true
		footer nullable:true
    }

	static mapping = {
		description type: 'text'
		formDesignElements sort: 'designOrder'
	}
	
//	def getQuestions(){
//		Set elements = this.formDesignElements
//		Set questions =  [];
//		elements.each{ formDesignElement ->
//
//			if(formDesignElement instanceof QuestionElement){
//				questions.add(formDesignElement)
//			}
//		}
//
//		return questions
//
//	}
	
}

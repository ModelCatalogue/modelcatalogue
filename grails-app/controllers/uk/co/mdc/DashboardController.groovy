package uk.co.mdc
import uk.co.mdc.forms.FormDesign

class DashboardController {

    def pathwayService

	def index() {
		
	    def finalizedPathways = pathwayService.topLevelPathways([isDraft: false])

	    def draftPathways = pathwayService.topLevelPathways([isDraft: true])

	    def finalizedForms = FormDesign.findAll {
	    	isDraft == false
	    }
	    def draftForms = FormDesign.findAll {
	    	isDraft == true
	    }

	    [
            finalizedPathways : finalizedPathways,
	    	draftPathways : draftPathways,
	    	finalizedForms : finalizedForms,
	    	draftForms : draftForms,
        ]

	}

    def metadataCurator() {
        []
    }

}

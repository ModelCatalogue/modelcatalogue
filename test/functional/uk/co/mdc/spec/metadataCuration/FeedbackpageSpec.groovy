package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage

/**
 * Created by soheil on 18/05/2014.
 */
class FeedbackpageSpec extends GebReportingSpec {

	def setup() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
	}

	def "Clicking on feedback link, will open Jira feedback page"() {

		when: "Feedback link is clicked"
		waitFor {
			at ModelListPage
		}
		interact {
			click(footer.feedbackLink)
		}
		then: "Feedback page is displayed"
		//as it is loaded form an external script, it may take a long time to load it
		//120 seconds is a long time, but have to wait !
		waitFor (120){
			$("div.atlwdg-popup#atlwdg-container").displayed
		}
	}
}

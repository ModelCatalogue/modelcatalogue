package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import spock.lang.Unroll
import uk.co.mdc.pages.authentication.ChangePasswordPage
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.authentication.ReadOnlyUnAuthorizedPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage

/**
 * Created by soheil on 18/05/2014.
 */
class ReadOnlyUserLoginSpec extends GebReportingSpec{



	def setup(){
		to LoginPage
		loginReadOnlyUser()
		waitFor{
			at ModelListPage
		}
	}


	def "ReadOnly user can change password from Account Menu"(){

		when:"user changes her/his password"
		waitFor {
			$(ModelListPage.accountLink).displayed
		}

		$(ModelListPage.accountLink).click()

		waitFor {
			$(ModelListPage.changePasswordLink).displayed
		}
		$(ModelListPage.changePasswordLink).click()

		waitFor {
			at ChangePasswordPage
		}

		password  = "rpassword1@"
		password2 = "rpassword1@"
		submitButton.click()
		waitFor {
			at ChangePasswordPage
		}

		then:"It will show the success alert message"
		waitFor {
			successAlert.displayed
			successAlert.text().contains("Your password has been successfully updated.")
		}


	}




	def"ReadOnly user can not access administration urls #NotAuthorizedUrl"(){
		when:"ReadOnly user goes to an admin url"
		at ModelListPage
		go NotAuthorizedUrl

		then:"it faces not authorized error"
		waitFor {
			at ReadOnlyUnAuthorizedPage
		}
		where:""
		NotAuthorizedUrl <<
							[
							"role/search",
							"role/create",
							"user/search",
							"user/create",
							"registrationCode/search",
							"role/pendingUsers",
							"bootstrap-data",
							"admin",
							"securityInfo",
							"role",
							"registrationCode",
							"user",
							"aclClass",
							"aclSid",
							"aclEntry",
							"aclObjectIdentity"
							]


	}
}

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

	def "Regular user does not have access to Administrator Menu"(){

		expect:"administration menu should be disabled for regular users"
		at ModelListPage
		nav
		!nav.administrationLink.displayed
	}

	def "Regular user does not have access to Administrator Menu in any other pages"(){

		when:"user goes to ConceptualDomainlist page"
		to ConceptualDomainListPage

		then:"administration menu should be disabled for regular users"
		waitFor {
			ConceptualDomainListPage
		}
		nav
		!nav.administrationLink.displayed
	}



	def "Regular user has access to Account Menu"(){

		when:"user goes to ConceptualDomainlist page"
		to ConceptualDomainListPage

		then:"account menu should be enabled for regular users"
		waitFor {
			ConceptualDomainListPage
		}
		nav
		nav.accountLink.displayed
	}



	def "Regular user can change password from Account Menu"(){

		when:"user goes to ConceptualDomainlist page"
		waitFor {
			nav.accountLink.displayed
		}
		interact {
			click(nav.accountLink)
		}
		waitFor {
			nav.changePasswordLink.displayed
		}
		interact {
			click(nav.changePasswordLink)
		}
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




	@Unroll
	def"regular user can not access administration urls #NotAuthorizedUrl"(){
		when:"regular user goes to an admin url"
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

package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import spock.lang.Unroll
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.authentication.RegistrationCodePage
import uk.co.mdc.pages.authentication.RoleCreatePage
import uk.co.mdc.pages.authentication.RolePendingPage
import uk.co.mdc.pages.authentication.RoleSearchPage
import uk.co.mdc.pages.authentication.UserCreatePage
import uk.co.mdc.pages.authentication.UserSearchPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage

/**
 * Created by soheil on 03/06/2014.
 */
class AdminUserLoginSpec  extends GebReportingSpec{


		def setup(){
			to LoginPage
			loginAdminUser()
			waitFor{
				at DashboardPage
			}
			to ModelListPage
		}

		def "Admin user has access to Administrator Menu"(){

			expect:"administration menu should be enabled for admin users"
			at ModelListPage
			nav
			nav.administrationLink.displayed
		}

		def "Admin user has access to Administrator Menu in any other pages"(){

			when:"Admin user goes to ConceptualDomainlist page"
			waitFor {
				at ModelListPage
			}
			to ConceptualDomainListPage

			then:"administration menu should be enabled for admin users"
			waitFor {
				ConceptualDomainListPage
			}
			nav
			nav.administrationLink.displayed
		}



//		@Unroll
//		def"Admin user can access administration urls #AuthorizedUrl"(){
//			when:"Admin user goes to an admin url"
//			waitFor {
//				at ModelListPage
//			}
//			go AuthorizedUrl
//
//			then:"Admin will have access to those pages"
//			waitFor {
//				at AuthorizedPage
//			}
//
//			where:""
//			AuthorizedUrl | AuthorizedPage
//			"role/search" | RoleSearchPage
//			"role/create" | RoleCreatePage
//			"user/search" | UserSearchPage
//			"user/create" | UserCreatePage
//			"registrationCode/search" | RegistrationCodePage
//			"role/pendingUsers"		  | RolePendingPage
//		}
}



package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.DataElementShowPage

/**
 * Created by soheil on 22/05/2014.
 */
class SearchSpec  extends GebReportingSpec{

	def setup(){
		to LoginPage
		loginReadOnlyUser()
		waitFor{
			at ModelListPage
		}
	}

	def "Search text box should be displayed on Model List page"(){

		when: "Going to Model List Page"
		waitFor {
			at ModelListPage
		}

		then:"It should display the Search text box"
		waitFor {
			$(nav.searchTextInput).displayed
		}
	}

	def "Searching for an existing term will return all related elements"(){

		given: "In Model List Page"
		at ModelListPage
		waitFor {
			$(nav.searchTextInput).displayed
		}

		when:"Searching for a term"
		$(nav.searchTextInput).value("DE1")


		then:"it will return all found elements"
		//It may take a bit long to load the search result
		waitFor (30){
			$(nav.searchResultUl).displayed
		}
 		$(nav.searchResultUl).find("li").size() == 3
		$(nav.searchResultUl).find("li",0).text().trim().contains("Search Catalogue Element for DE1")
		$(nav.searchResultUl).find("li",1).text().trim().contains("Search Model for DE1")
		$(nav.searchResultUl).find("li",2).text().trim().contains("DE1")

	}

	def "Clicking on a returned result from search will lead us to that element"(){

		given: "In Model List Page and the search text box is displayed"
		at ModelListPage
		waitFor {
			$(nav.searchTextInput).displayed
		}

		when:"Searching for a term and clicking on the result"
		$(nav.searchTextInput).value("DE1")
		waitFor {
			$(nav.searchResultUl).displayed
		}
		$(nav.searchResultUl).find("li",0).find("a")

		then:"it will lead us to the show page of that element"
		waitFor {
			at DataElementShowPage
		}
	}
}

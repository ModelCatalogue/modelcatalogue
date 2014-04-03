package uk.co.mdc.pages.model;

import geb.Browser
import geb.Page
import geb.Module

class MetadataListPage extends Page{
	
	static url = "/metadataCurator#/catalogueElement/dataElement"
	
	static at = {
		url == "/metadataCurator#/catalogueElement/dataElement" &&
		title == "Model Catalogue Core"
	}
	
	static content = {
        metadataApp { $("#metadataCurator") }
        heading { metadataApp.find("h2") }
        actor {metadataApp.find("table.dl-table td a", text: "actor") }
        page3 {metadataApp.find("table.dl-table ul.pagination a", text: "3") }
        version {metadataApp.find("table.dl-table td a", text: "version") }
        enumerations {metadataApp.find("table.dl-table td a", text: "enumerations") }
        conceptualDomain {metadataApp.find("table.dl-table td a", text: "ConceptualDomain") }
        next {metadataApp.find("table.dl-table ul.pagination a", text: "Next") }
        previous {metadataApp.find("table.dl-table ul.pagination a", text: "Previous") }
        heading3 { metadataApp.find("h3") }
        containsTab { metadataApp.find("div.tabbable ul.nav-tabs a", text: "Contains") }
        dataElement {metadataApp.find("div td.dl-table-item-cell a", text: "Data Element: 123") }
	}
}
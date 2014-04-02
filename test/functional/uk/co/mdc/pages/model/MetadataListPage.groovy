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
        actor {metadataApp.find("table.dl-table td a", text: "actor")}
	}
}
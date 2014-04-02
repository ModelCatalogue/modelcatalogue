package uk.co.mdc.pages.model;

import geb.Browser
import geb.Page
import geb.Module

class ModelListPage extends Page{
	
	static url = "/metadataCurator#/catalogueElement/dataElement"
	
	static at = {
		url == "metadataCurator#/catalogueElement/dataElement" &&
		title == "Model Catalogue Core"
	}
	
	static content = {
		
	}
}
package uk.co.mdc.forms

class MediaElement extends PresentationElement {

	String url
	
    static constraints = {
        url size: 1..255
    }
}

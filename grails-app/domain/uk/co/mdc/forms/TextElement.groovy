package uk.co.mdc.forms

class TextElement extends PresentationElement{
	
	String text

    static constraints = {
		text maxSize: 10000
    }

    static mapping = {
        text type: 'text'
    }
}

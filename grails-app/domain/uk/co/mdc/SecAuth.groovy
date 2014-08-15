package uk.co.mdc

class SecAuth {

	String authority



	static constraints = {
		authority blank: false, unique: true
	}
}

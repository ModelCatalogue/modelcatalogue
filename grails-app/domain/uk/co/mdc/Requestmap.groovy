package uk.co.mdc

import org.springframework.http.HttpMethod

class Requestmap {

	String url
	String configAttribute
	HttpMethod httpMethod



	static constraints = {
		url blank: false, unique: 'httpMethod'
		configAttribute blank: false
		httpMethod nullable: true
	}
}

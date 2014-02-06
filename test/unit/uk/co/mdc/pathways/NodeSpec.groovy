
package uk.co.mdc.pathways

import grails.test.mixin.*

import uk.co.mdc.model.Collection

/**
 * Simple unit tests for pathway nodes
 * @author Ryan Brooks (ryan.brooks@ndm.ox.ac.uk)
 *
 */
@TestFor(Node)
@Mock(Collection)
class NodeSpec extends spock.lang.Specification {

	def "Nodes are simple objects with getters and setters"(){

		setup:
		def node1

		def expected = [
			name: "Bill",
			description: "One half of the flowerpot men",
			x: 10,
			y: 15,
            pathway: new Pathway(
                    name: "pathway 1",
                    userVersion: "1.0",
                    isDraft: true,
            ).save()
		]
		
		//
		// Test a standard object
		when: 'everything is fine'
		node1 = new Node(expected)

		then: 'the node should validate and contain the right things'
		node1.validate()
		!node1.hasErrors()
		node1.name == expected.name
		node1.description == expected.description
		node1.x.toInteger() == expected.x
		node1.y.toInteger() == expected.y
        node1.pathway == expected.pathway

		//
		// Ensure name can't be null
		when: 'name is null'
		expected.name = null
		node1 = new Node(expected)
		
		then:'the object should fail validation'
		!node1.validate()
		node1.errors.hasFieldErrors("name")
		
		//
		// But x and y as we;; as pathwaysModel
		when: 'x and y are null'
		expected.name = "Bob"
		expected.x = null
		expected.y = null
		node1 = new Node(expected)
		
		then: 'it is all fine'
		println node1.validate().toString() + ": " + node1.errors
		!node1.hasErrors()
	}
}

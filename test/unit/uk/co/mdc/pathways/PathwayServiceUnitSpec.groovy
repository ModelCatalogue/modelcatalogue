package uk.co.mdc.pathways

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.mock.interceptor.MockFor
import spock.lang.Specification
import spock.lang.Unroll


/**
 * Unit tests for the Pathway Service
 * Created by rb on 23/01/2014.
 */
@TestFor(PathwayService)
@Mock([Pathway, Node, Link])
class PathwayServiceUnitSpec extends Specification {

	/**
	 * FIXME Need to add checks for max, offset params
	 @Unroll
	  def "list with max #max, offset #offset"(){
	  when: "I call list() with max #max & offset #offset params"
	  then: "The subset of results from pathwayService.topLevelPathways is returned"
	  and: "The count value is correct"
	  }
	 */

    def "topLevelPathways returns the correct number of draft pathways"() {
        when: "I create a single draft pathway"
        //mockDomain(Pathway)
        def s = new Pathway(name: "Pathway 1", isDraft: true)
        s.save()

        then: "The pathway pathwayService returns a single pathway when queried for drafts"
        s != null
        Pathway.list().size() == 1
        service.topLevelPathways().size() == 1
        service.topLevelPathways([isDraft: true]).size() == 1

        and: "The pathway pathwayService returns nothing when queried for finalised pathways"
        service.topLevelPathways([isDraft: false]).size() == 0
    }

    def "topLevelPathways returns the correct number of finalised pathways"() {
        when: "I create two pathways and finalise them"
        new Pathway(name: "Pathway 1", isDraft: true).save()
        new Pathway(name: "Pathway 2", isDraft: false).save()
        new Pathway(name: "Pathway 3", isDraft: false).save()

        then: "The pathway pathwayService returns 3 pathways when queried for everything"
        Pathway.list().size() == 3
        service.topLevelPathways().size() == 3

        and: "There is 1 draft pathway"
        service.topLevelPathways([isDraft: true]).size() == 1

        and: "There are 2 finalised pathways"
        service.topLevelPathways([isDraft: false]).size() == 2
    }

    def "topLevelPathways doesn't include nodes, just pathways"() {
        when: "I create a single draft pathway"
        Pathway p1 = new Pathway(name: "Pathway 1", isDraft: true).save()
        Node node1 = new Node(name: "Node 1").save()
        p1.addToNodes(node1)

        then: "The pathway pathwayService returns a single pathway when queried for drafts"
        Pathway.list().size() == 2
        service.topLevelPathways().size() == 1
    }

}

package uk.co.mdc.pathways

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import grails.validation.ValidationException
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


/**
 * Unit tests for the Pathway Service
 * Created by rb on 23/01/2014.
 */
@TestFor(PathwayService)
@Mock([ Pathway, Node, Link])
@TestMixin(DomainClassUnitTestMixin)
class PathwayServiceSpec extends Specification{

    def "topLevelPathways returns the correct number of draft pathways"(){
        when: "I create a single draft pathway"
        mockDomain(Pathway)
        def s = new Pathway(name:"Pathway 1", isDraft: true)
        s.save()

        then: "The pathway service returns a single pathway when queried for drafts"
        s != null
        Pathway.list().size() == 1
        service.topLevelPathways().size() == 1
        service.topLevelPathways([isDraft: true]).size() == 1

        and: "The pathway service returns nothing when queried for finalised pathways"
        service.topLevelPathways([isDraft: false]).size() == 0
    }

    def "topLevelPathways returns the correct number of finalised pathways"(){
        when: "I create two pathways and finalise them"
        new Pathway(name:"Pathway 1", isDraft: true).save()
        new Pathway(name:"Pathway 2", isDraft: false).save()
        new Pathway(name:"Pathway 3", isDraft: false).save()

        then: "The pathway service returns 3 pathways when queried for everything"
        Pathway.list().size() == 3
        service.topLevelPathways().size() == 3

        and: "There is 1 draft pathway"
        service.topLevelPathways([isDraft: true]).size() == 1

        and: "There are 2 finalised pathways"
        service.topLevelPathways([isDraft: false]).size() == 2
    }

    def "topLevelPathways doesn't include nodes, just pathways"(){
        when: "I create a single draft pathway"
        Pathway p1 = new Pathway(name:"Pathway 1", isDraft: true).save()
        Node node1 = new Node(name:"Node 1").save()
        p1.addToNodes(node1)

        then: "The pathway service returns a single pathway when queried for drafts"
        Pathway.list().size() == 2
        service.topLevelPathways().size() == 1
    }



    @Unroll
    def "createOrSaveNodesForPathway takes a new node and adds it to an existing pathway"(){
        mockDomain(Pathway)
        mockDomain(Node)
        mockDomain(Link)
        def pathway = fixture[0]
        def clientPathway = fixture[1]
        pathway.save()
        Pathway.list().size() == 1

        when: "We have a client with a new node"

        def idMappings = [:]
        pathway?.name == clientPathway?.name
        service.createOrSaveNodesForPathway(clientPathway, idMappings)

        then: "The node is persisted"
        pathway?.name == clientPathway?.name
        getPathwayNodeCount(pathway) == getPathwayNodeCount(clientPathway)

        !(pathway.nodes[0].id =~ /LOCAL/ )
        pathway.nodes[0].name == "New node"

        cleanup:
        pathway.delete()

        where:
        // Simple 1 node pathway
        fixture << [createPathwayAndUseId( [
                description: null,
                isDraft: true,
                links: [],
                name: "Transplanting and Monitoring Pathway",
                nodes:
                        [
                                [
                                        id: "LOCAL1",
                                        links: [],
                                        name: "New node",
                                        nodes: [],
                                        x: 347.867711330764,
                                        y: 342.5946973264217,
                                ],
                        ],
                userVersion: "0.2",
                version: 1
        ]),

        // Nested new node inside new node
        createPathwayAndUseId( [
                description: null,
                isDraft: true,
                links: [],
                name: "Transplanting and Monitoring Pathway",
                nodes:[
                        [
                                id: "LOCAL1",
                                links: [],
                                name: "New node1",
                                nodes: [[
                                        id: "LOCAL2",
                                        links: [],
                                        name: "New node2",
                                        nodes: [[
                                                id: "LOCAL3",
                                                links: [],
                                                name: "New node3",
                                                nodes: [],
                                                x: 347.867711330764,
                                                y: 342.5946973264217,
                                        ],],
                                        pathway: 1,
                                        x: 347.867711330764,
                                        y: 342.5946973264217,
                                ]],
                                x: 347.867711330764,
                                y: 342.5946973264217,
                        ],
                ],
                userVersion: "0.2",
                version: 1
            ]),
        ]
    }

    def createPathwayAndUseId(def incomingMap){
        Pathway pathway = new Pathway(name: incomingMap?.name, userVersion: incomingMap?.userVersion, isDraft: incomingMap?.isDraft)
        if(!pathway.validate()){
            throw new IllegalArgumentException("Pathway fixture has errors and doesn't validate")
        }
        pathway.save()

        incomingMap.id = pathway.id
        incomingMap.nodes.each { node ->
            node.pathway = pathway.id
        }
        [pathway, incomingMap]
    }

    def getPathwayNodeCount(def clientPathway){
        println "Getting nodecount for "+ clientPathway.name
        int nodeCount = 1
        clientPathway.nodes.each{ node ->
            nodeCount = nodeCount + getNodeCount(node)
        }
        println "count for "+clientPathway.name+" ("+clientPathway.id+") "+nodeCount
        return nodeCount
    }

    def getNodeCount(def node){
        println node.name + " {"
        int nodeCount = 1
        node.nodes.each{ childNode ->
            nodeCount = nodeCount + getNodeCount(childNode)
        }
        println "\t count for "+node.name+" ("+node.id+") "+nodeCount
        println "}"
        return nodeCount
    }
}

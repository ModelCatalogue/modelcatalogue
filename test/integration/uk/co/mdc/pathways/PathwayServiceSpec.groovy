package uk.co.mdc.pathways

import grails.test.spock.IntegrationSpec
import org.eclipse.jetty.util.ajax.JSON
import spock.lang.Unroll


/**
 * Integration tests for the Pathway Service
 *
 * This lives in integration tests (rather than unit) because the pathwayService uses addTo* methods, which aren't mocked
 * correctly for unit tests.
 *
 * Created by rb on 23/01/2014.
 */
class PathwayServiceSpec extends IntegrationSpec{

    def pathwayService

    @Unroll
    def "createOrSaveNodesForPathway takes a new node and adds it to an existing pathway"(){

        def pathway = fixture[0]
        def clientPathway = fixture[1]
        pathway.save()
        Pathway.list().size() == 1

        when: "We have a client with a new node"

        def idMappings = [:]
        pathway?.name == clientPathway?.name
        pathwayService.createLocalNodes(clientPathway, idMappings)

        then: "The nodes are all persisted"
        pathway?.name == clientPathway?.name
        getPathwayNodeCount(pathway) == getPathwayNodeCount(clientPathway)


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
                                        ]],
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

         //createPathwayAndUseId(JSON.parse(new FileInputStream("test/integration/uk/co/mdc/pathways/pathwayUpdatePUT.json"), "UTF-8"))
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

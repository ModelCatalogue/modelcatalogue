package uk.co.mdc.pathways

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.mock.interceptor.MockFor
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll


/**
 * Unit tests for the Pathway Service
 * Created by rb on 23/01/2014.
 */
@TestFor(PathwayService)
@Mock([Pathway, Node, Link])
class PathwayServiceUnitSpec extends Specification {

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


    def fixGORMGet()
    {
        // This is a hack to make the Grails mock behave like *real* GORM.
        // If the pathway isn't found, look it up in Node (happens automatically outside of test)
        Pathway.metaClass.static.get = { Long id ->
            if(Pathway.findById(id)){
                return Pathway.findById(id)
            }
            return Node.get(id)
        }
    }

    @Ignore
    def "createLocalNodes removes the deleted node and its link from the pathway" ()
    {
        setup:
        fixGORMGet()

        when:"Deleting a node from pathway"
        def pathway = new Pathway(name: "Pathway 1", isDraft: false).save()
        Node node1 = new Node(name: "Node 1").save()
        Node node2 = new Node(name: "Node 2").save()
        Node node3 = new Node(name: "Node 3").save()
        pathway.addToNodes(node1)
        pathway.addToNodes(node2)
        pathway.addToNodes(node3)

        Link link12=new Link(name: "Link12",source: node1,target: node2,pathway: pathway).save();
        pathway.addToLinks(link12)

        Link link13 =new Link(name: "Link13",source: node1,target: node3,pathway: pathway).save();
        pathway.addToLinks(link13)


        assert pathway.links.size() ==2
        assert pathway.nodes.size() ==3

        //user removed node3 from pathway and
        //added one new node
        def clientPathway = [
                id: pathway.id,
                name:"Pathway 1",
                nodes:[
                        [id:"nodeLOCAL1",name:'Local new node1',nodes: [],links: []],
                        [id:node1.id, name:"Node 1",nodes: [],links: []],
                        [id:node2.id, name:"Node 2",nodes: [],links: []]
                ],
                links:[[id:link12.id, source:node1,target: node2,name: "Link12",pathway: pathway]]
        ]
        def idMappings = [:]
        service.createLocalNodes(clientPathway ,idMappings)
        service.cleanAndCreateLinks(clientPathway ,idMappings)


        then:"the node and its links should have been removed from pathway"
        Pathway.get(pathway.id).nodes.size() == 3
        Pathway.get(pathway.id).links.size() == 1
    }



    @Ignore
    def "cleanAndCreateLinks removes the deleted link from the pathway" ()
    {

        setup:
        fixGORMGet()


        when:"Deleting a node from pathway"
        def pathway = new Pathway(name: "Pathway 1", isDraft: false).save()
        Node node1 = new Node(name: "Node 1").save()
        Node node2 = new Node(name: "Node 2").save()
        Node node3 = new Node(name: "Node 3").save()
        pathway.addToNodes(node1)
        pathway.addToNodes(node2)
        pathway.addToNodes(node3)

        Link link12=new Link(name: "Link12",source: node1,target: node2,pathway: pathway).save();
        pathway.addToLinks(link12)

        Link link13 =new Link(name: "Link13",source: node1,target: node3,pathway: pathway).save();
        pathway.addToLinks(link13)


        //user removed link 1-3 from pathway and
        //added one new node
        def clientPathway = [
                id: pathway.id,
                name:"Pathway 1",
                nodes:[
                        [id:"nodeLOCAL1",name:'Local new node1',nodes: [],links: []],
                        [id:node1.id, name:"Node 1",nodes: [],links: []],
                        [id:node2.id, name:"Node 2",nodes: [],links: []],
                        [id:node3.id, name:"Node 3",nodes: [],links: []]
                ],
                links:[
                        [id:link12.id, source:node1,target: node2,name: "Link12"]
                ]
        ]
        def idMappings = [:]
        service.createLocalNodes(clientPathway ,idMappings)
        service.cleanAndCreateLinks(clientPathway ,idMappings)

        then:"the link should have been removed from pathway"
        Pathway.get(pathway.id).nodes.size() == 4
        Pathway.get(pathway.id).links.size() == 1
        Link.get(link12.id).source.id == node1.id
        Link.get(link12.id).target.id == node2.id
    }




    @Ignore
    def "createLocalNodes removes the deleted node and its link from the subPathway" ()
    {
        setup:
        fixGORMGet()

        when:"Deleting a node from subPathway"
        def pathway = new Pathway(name: "Pathway 1", isDraft: false).save()
        Node subPathwayNode = new Node(name: "subPathway node",parent:pathway).save()

        Node node1 = new Node(name: "Node 1").save()
        pathway.addToNodes(node1)


        Node node2 = new Node(name: "inNode 2").save()
        subPathwayNode.addToNodes(node2)

        Node node3 = new Node(name: "inNode 3").save()
        subPathwayNode.addToNodes(node3)

        pathway.addToNodes(subPathwayNode)

        assert Pathway.get(pathway.id).nodes.size() == 2
        assert Node.get(subPathwayNode.id).nodes.size() == 2

        //user removed node2 from subPathway
        def clientPathway = [
                id: pathway.id,
                name:"Pathway 1",
                nodes:[
                        [id:node1.id, name:"Node 1",nodes: [],links: []],
                        [id:subPathwayNode.id, name:"subPathway node",parent:pathway,
                                nodes:
                                        [
                                                [id:node3.id, name:"inNode 3",nodes: [],links: []],
                                                [id:node2.id, name:"inNode 2",nodes: [],links: []]
                                        ],
                                links:[]
                        ]
                ]
        ]
        def idMappings = [:]
        service.createLocalNodes(clientPathway ,idMappings)
        //service.cleanAndCreateLinks(clientPathway ,idMappings)


        then:"the node in subPathway should have been removed from subPathway"
        Pathway.get(pathway.id).nodes.size() == 2
        Node.get(subPathwayNode.id).nodes.size() == 2

    }


}

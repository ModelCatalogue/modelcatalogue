package uk.co.mdc.pathways

import grails.test.spock.IntegrationSpec

/**
 * Created by soheil on 26/03/2014.
 */
class SampleIntTest extends IntegrationSpec{

    def "Test Sample IntegrationTests"()
    {
        when:"Saving a Pathway"
        def pathwayCount = Pathway.list().size()

        Pathway pathway = new Pathway(name: "TestA", userVersion: "2.1", isDraft: true)
        if(!pathway.validate()){
            throw new IllegalArgumentException("Pathway has errors and doesn't validate")
        }
        pathway.save()

        then:"It should be saved!"
        Pathway.list().size() == pathwayCount + 1
    }
}
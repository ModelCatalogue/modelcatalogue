package uk.co.mdc.pathways

import grails.rest.Linkable


@Linkable
class Node extends Pathway{

    String name
    String description

    // The owning pathway. We define the ownership because we want
    // the delete to cascade from Pathway to Link (but not vice-versa)
    static belongsTo = [pathway: Pathway]

    // Coordinates for rendering node
    Integer x
    Integer y

    // TODO Forms
    // TODO Data elements
//    static hasMany = [
//            dataElements: DataElement
//            form: Form
//    ]


    static constraints = {
        description nullable:true
        x nullable:true
        y nullable:true
    }
}

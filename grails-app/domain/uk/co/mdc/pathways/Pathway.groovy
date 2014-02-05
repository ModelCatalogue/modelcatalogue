package uk.co.mdc.pathways


class Pathway {
	
	String  name
	String  userVersion = "1"
	Boolean isDraft = true
	String  description
	
	static hasMany = [
            nodes: Node,
            links: Link
    ]
    static mappedBy = [
            nodes: 'pathway',
            links: 'pathway'
    ]

    static constraints = {
        description nullable: true
		nodes       nullable: true
        links       nullable: true
    }

    static mapping = {
        nodes cascade: "all-delete-orphan"
        links cascade: "all-delete-orphan"
    }
}

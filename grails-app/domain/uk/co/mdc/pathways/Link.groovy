package uk.co.mdc.pathways

class Link {
    // The owning pathway. We define the ownership because we want
    // the delete to cascade from Pathway to Link (but not vice-versa)
	static belongsTo = [pathway: Pathway]

	Node source
	Node target
}

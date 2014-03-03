#
# Generic resource querier for Grails controllers. Surprisingly little code :), and all taken from
# http://claymccoy.blogspot.co.uk/2012/09/grails-with-angularjs-and-coffeescript.html
#

angular.module('pathway.services', ['ngResource'])

.service 'NodeSelector', ->
		selectedNode = null

		selectNode: (node) ->
			selectedNode = node
		isSelected: (node) ->
			selectedNode == node
		getSelectedNode: ->
			selectedNode
		unSelectNode: ->
			selectedNode = null

		#
		# Remove a node from specified pathway.
		# @param pathway the (sub)pathway the node belongs to
		# @param node the node to remove
		deleteNode: (pathway, node) ->
			# 1. Get index of selected node in pathway
			index = pathway.nodes.indexOf(node)
			# If not found, indexOf() returns -1 (i.e. last item's index in array). We don't want that.
			if index >= 0
				# 2. Remove the node
				pathway.nodes.splice(index, 1);

				# 3. Remove the links associated with the node from the model
				angular.forEach(pathway.links, (link)->
					if link.source == node.id || link.target == node.id
						index = pathway.links.indexOf(link)
						pathway.links.splice(index, 1)
				)

				# 4. Prompt jsPlumb to remove the links connecting the node
				jsPlumb.detachAllConnections($('#node' + node.id))

				# 5. Update the NodeSelector so it doesn't reference the old object
				this.selectNode(null)




.service 'PathwayPersistence', ['Grails', (Grails) ->
		fixNodeIds = (node, idMappings) ->
			for node in node.nodes
				node.id = idMappings[node.id] if idMappings[node.id]
				fixNodeIds(node, idMappings)
			return
		fixLinkIds = (node, idMappings) ->
			for link in node.links
				link.id = idMappings[link.id] if idMappings[link.id]
				link.source = idMappings[link.source] if idMappings[link.source]
				link.target = idMappings[link.target] if idMappings[link.target]
			fixLinkIds(childNode, idMappings) for childNode in node.nodes
			return

		# Save the pathway.
		# The response from Grails is expected to be:
		#   idMappings: a map of LOCAL to server-side IDs, to facilitate partial updates.
		#   pathway: the server-side representation of the pathway.
		#   hasErrors: boolean value indicating success
		#   errors: a list of errors returned from the server (if any)
		save: (scope, pathway) ->
			grailsResponse = Grails.getRestResource(scope).update pathway, (response) ->
				# If there aren't any errors lets update the local references
				if !response.hasErrors
					fixNodeIds(pathway, response.idMappings)
					fixLinkIds(pathway, response.idMappings)

			return grailsResponse
	]
#
# Generic resource querier for Grails controllers. Surprisingly little code :), and all taken from
# http://claymccoy.blogspot.co.uk/2012/09/grails-with-angularjs-and-coffeescript.html
#

angular.module('pathway.services', ['ngResource', 'ui.router'])

.service 'Currentpathway', ->
		currentPathway=null
		setPathway: (pathway) ->
			currentPathway=pathway
		getPathway: ->
			return currentPathway


.service 'ItemSelector',($state) ->
		selectedItem=null

		unSelectItem: ->
			selectedItem = null
			$state.go('empty')

		selectItem: (item,type) ->
			selectedItem={item:item,type:type}
			if(type=='link')
				if item then $state.go('link', {linkId:item.id})
			else if	(type=='node')
				if item then $state.go('node', {nodeId:item.id})

		isItemSelected: (item)->
			selectedItem &&  selectedItem.item == item

		getSelectedItem: ->
			return selectedItem


.service 'LinkService',($state,ItemSelector) ->
		deleteLink: (pathway,link)->
			if !link then return
			index = pathway.links.indexOf(link)
			if(index>-1)
				pathway.links.splice(index, 1);
				#select the connection based on it's conenctionId
				connections = jsPlumb.getConnections()
				for con in connections
					if(con._jsPlumb.parameters.connectionId == link.id )
						jsPlumb.detach(con)
						ItemSelector.unSelectItem()
						return


.service 'NodeService', ($state,ItemSelector) ->
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

				# 5. Update the ItemSelector so it doesn't reference the old object
				ItemSelector.unSelectItem()




.service 'PathwayPersistence', ['Grails','LinkService', (Grails,LinkService) ->
		fixNodeIds = (node, idMappings) ->
			for node in node.nodes
				node.id = idMappings[node.id] if idMappings[node.id]
				fixNodeIds(node, idMappings)
			return

		fixLinkIds = (node, idMappings) ->
			curLinks = node.links.slice()
			for link in curLinks
#				if any properties of a link changes,the link should be updated so we remove it and add a new one
				if idMappings[link.id] || idMappings[link.source] || idMappings[link.target]
					replaceLink(node,link,idMappings);
			fixLinkIds(childNode, idMappings) for childNode in node.nodes
			return

		replaceLink = (pathway,link,idMappings) ->
			# first remove the old link
			LinkService.deleteLink(pathway,link);
			# then add a new link
			newLink={
				id:idMappings[link.id],
				source :link.source,
				target : link.target};
			#	and also consider that the source and target may have been updated
			#	so update its source and target if they are changed
			if (idMappings[link.source])
				newLink.source =idMappings[link.source]
			if (idMappings[link.target])
				newLink.target = idMappings[link.target]
			pathway.links.push(newLink)
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
					fixLinkIds(pathway, response.idMappings)
					fixNodeIds(pathway, response.idMappings)
			return grailsResponse
	]

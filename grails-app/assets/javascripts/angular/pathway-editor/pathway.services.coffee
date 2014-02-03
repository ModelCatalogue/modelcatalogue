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

.service 'PathwayPersistence', ['Grails', (Grails) ->

        fixNodeIds = (node, idMappings) ->
            for node in node.nodes
                console.log "node "+ node.id + idMappings[node.id]
                node.id = idMappings[node.id] if idMappings[node.id]
                console.log "       -> "+node.id
                fixNodeIds(node, idMappings)
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

            return grailsResponse
    ]
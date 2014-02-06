pathwayEditor = angular.module('pathway.controllers', ['pathway.services'])

.controller('PathwayEditorCtrl', ['$scope', 'Grails', 'NodeSelector','PathwayPersistence', ($scope, Grails, NodeSelector,PathwayPersistence) ->

        $scope.controller = "pathways" # override because we want to use the RestController endpoints
        $scope.pathway = Grails.getRestResource($scope).get {action: 'show'}

        $scope.selectNode = (node) ->
            NodeSelector.selectNode(node)
        $scope.isSelected = (node) ->
            NodeSelector.isSelected(node)
        $scope.selectedNode = ->
            NodeSelector.getSelectedNode()

        $scope.deleteNode = (node) ->

            # 1. Remove the node
            index = $scope.pathway.nodes.indexOf(node)
            $scope.pathway.nodes.splice(index, 1);

            # 2. Remove the links associated with the node from the model
            angular.forEach($scope.pathway.links, (link)->
                if link.source == node.id || link.target == node.id
                    index = $scope.pathway.links.indexOf(link)
                    $scope.pathway.links.splice(index, 1)
            )

            # 3. Prompt jsPlumb to remove the links connecting the node
            jsPlumb.detachAllConnections($('#node' + node.id))

            # 4. Update the NodeSelector so it doesn't reference the old object
            NodeSelector.selectNode(null)

        $scope.save = () ->
            grailsResponse = PathwayPersistence.save($scope, $scope.pathway)
            if grailsResponse.hasErrors
                for error in grailsResponse.errors
                    console.log error


    ])

.controller('NodePropertiesCtrl', ['$scope', 'NodeSelector', ($scope, NodeSelector) ->
        $scope.selectedNode = null
        $scope.switchToSubPathway = ->
            console.log("FIXME: this should switch the pathway viewer's scope to node " + $scope.selectedNode.id)

        $scope.deleteNode = ->
            # FIXME, change this so it doesn't reference parent
            $scope.$parent.deleteNode($scope.selectedNode)

        $scope.removeForm = (form) ->
            console.log("FIXME: this should remove form " + form.id + " from node " + $scope.selectedNode.id)

        $scope.addForm = ->
            console.log("FIXME: this should add a form to node " + $scope.selectedNode.id)

        $scope.removeDataElement = (dataElement) ->
            console.log("FIXME: this should remove the dataElement " + dataElement.id + " from node " + $scope.selectedNode.id)

        $scope.addDataElement = ->
            console.log("FIXME: this should add a dataElement to node " + $scope.selectedNode.id)

        # Watch the NodeSelector function for changes. The second function actions a change, setting the
        # selectedNode scope variable to be the new value retrieved from the service
        $scope.$watch(->
            NodeSelector.getSelectedNode()
        , (selectedNode) ->
            $scope.selectedNode = selectedNode
        , false # Just check for object equality
        )
    ])

.controller('GraphCanvasCtrl', ['$scope', 'NodeSelector', ($scope, NodeSelector) ->
        $scope.pathway = $scope.$parent.pathway

        $scope.selectNode = (node) ->
            NodeSelector.selectNode(node)

        $scope.viewSubpathway = (node) ->
            jsPlumb.deleteEveryEndpoint(); # FIXME this should be handled in the directive. Controllers are for data model, directives are for DOM manipulation
            $scope.pathway = node

        $scope.isSelected = (node) ->
            NodeSelector.isSelected(node)

        $scope.levelsAbove = false

        $scope.$watch 'pathway', ->
            $scope.levelsAbove = ($scope.pathway != $scope.$parent.pathway)

        $scope.upALevel = ->
            newParent = getParentOfSelectedNode($scope.$parent.pathway, $scope.pathway)
            if newParent isnt $scope.pathway
                jsPlumb.deleteEveryEndpoint(); # FIXME this should be handled in the directive. Controllers are for data model, directives are for DOM manipulation
                $scope.pathway = newParent

        getParentOfSelectedNode = (pathway, node)->
            if pathway is node
                return node
            if pathway and pathway.nodes.length > 0
                if pathway.nodes.indexOf(node) != -1
                    return pathway
                for subPathway in pathway.nodes
                    response =  getParentOfSelectedNode(subPathway, node)
                    if(response?)
                        return response

        # TODO is it a good idea to use DOM IDs here? Probably not...
        $scope.addNode = ->
            nodeX = 0
            nodeY = 0
            selectedNode = NodeSelector.getSelectedNode()
            if selectedNode
                sWidth=$('#node'+selectedNode.id).width()
                sX = selectedNode.x
                if(sX==NaN)
                    sX= $('#model-panel').scrollLeft() + 150
                nodeX = sX + sWidth + 50
                nodeY = selectedNode.y
            else
                nodeX = $('#model-panel').scrollLeft() + 150 + (Math.random()*300)
                nodeY = $('#model-panel').scrollTop() + 150 + (Math.random()*300)

            newNode = {
                name: "New node"
                id: "LOCAL" + nextLocalId()
                pathway: $scope.pathway.id
                nodes: []
                links: []
                x: nodeX
                y: nodeY
            }
            $scope.pathway.nodes.push newNode
            NodeSelector.selectNode(newNode)

        lastLocalId = 0
        nextLocalId = ->
            lastLocalId++
            return lastLocalId
    ])


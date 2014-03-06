<%@ page import="uk.co.mdc.pathways.Pathway" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main_fullwidth">
    <g:set var="entityName"
           value="${message(code: 'pathway.label', default: 'Pathway')}"/>
    <title>Pathway Editor</title>

    <parameter name="name" value="Pathway Editor"/>

    <link rel="stylesheet" href="${resource(dir: 'css', file: 'layout.css')}" type="text/css">

    <asset:stylesheet href="jquery.layout/dist/jquery.layout-latest.css"/>

</head>

<body>
<g:set var="grailsParams" value="${params.collect { it.key + '=\'' + it.value + '\'' }.join('; ')}"/>
<!-- FIXME remove hardcoded grails app name and put it in params -->
<div ng-app="pathway-editor" ng-init="${grailsParams}; grailsAppName='model_catalogue'" class="pathwayEditor">
<div ng-controller="PathwayEditorCtrl" class="ng-cloak" >
    <div class="row">
        <div class="col-xs-12">
            <div class="pull-right">
                <small id="pathwayDescription" editable-textarea="pathway.description" e-form="pathwayDescriptionForm">
                        {{pathway.description || 'This pathway needs a description'}}
                    <small><i class="fa fa-edit" ng-click="pathwayDescriptionForm.$show()" ng-hide="pathwayDescriptionForm.$visible"></i></small>
                </small>
            </div>

            <div>
                <h3>
                    <span id="pathwayName" editable-text="pathway.name"
                          e-style="width: 25em">{{ pathway.name || 'Please set the pathway name' }}</span>
                    <small id="userVersion" editable-text="pathway.userVersion"
                           e-style="width: 5em">{{pathway.userVersion}}</small>
                </h3>
            </div>
        </div>
    </div>


    <div id="container" class="row">

        <div class="ui-layout-west">
            <button type="button" class="btn btn-success btn-lg btn-block" ng-click="save()">Save pathway</button>

            <div class="panel panel-primary">
                <div class="panel-body" ng-controller="TreeViewCtrl">
                    <h4>Tree view</h4>
                    <ul>
                        <li class="tree-node"
                            ng-keyup="deleteKeyPressed($event, node)" tabindex="{{100 + $index}}"
                            ng-repeat="node in rootPathway.nodes"
                            ng-include="'templates/pathway/pathwayTreeView.html'"></li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="ui-layout-center panel panel-primary">
            <div mc-graph-container
                 pathway="pathway"
                 up-a-level="goUp()"
                 add-node="addNode()"
                 un-select-node="unSelectNode()"
                 un-select-link="unSelectLink()"
                 ng-controller="GraphCanvasCtrl"
                 class="jsplumb-container canvas">
                <div class="palette">
                    <span class="fa-stack">
                        <i class="fa fa-stack-2x fa-square" style="color: #ffffff"></i>
                        <i class="fa fa-stack-2x fa-plus-square" ng-click="addNode()"></i>
                    </span>
                    <span class="fa-stack">
                        <i class="fa fa-stack-2x fa-reply" ng-click="upALevel()" ng-show="levelsAbove"></i>
                    </span>
                    <br>
                </div>

                <div mc-graph-node graph-node="node" select-node="selectNode(node)" dbl-click="viewSubpathway(node)" ng-keyup="deleteKeyPressed($event, node)" tabindex="{{100 + $index}}" is-selected="isSelected(node)" ng-repeat="node in pathway.nodes"></div>

                <div mc-graph-link graph-link="link" select-link="selectLink(link)"
                     ng-repeat="link in pathway.links">{{link.name}}</div>
            </div>
        </div>

        <!-- If selectedItem is undefined, the right panel will be empty -->
        <div class="ui-layout-east" ng-controller="NodePropertiesCtrl" width="100px" >
            <div ui-view class="panel panel-primary"></div>
        </div>
    </div>

    <!-- FIXME refactor into a separate file -->
    <script type="text/ng-template" id="templates/pathway/pathwayTreeView.html">
        <span ng-click="selectNode(node)"  ng-class="{selectedItem: isSelected(node)}">{{node.name}}</span>
        <ul>
            <li class="tree-node" ng-keyup="deleteKeyPressed($event, node)" tabindex="{{100 + $index}}" ng-repeat="node in node.nodes" ng-include="'templates/pathway/pathwayTreeView.html'" pathway="node"></li>
        </ul>
    </script>

    <script type="text/ng-template" id="templates/pathway/jsPlumbNode.html">
        <div class="node" id="node{{node.id}}" ng-click="selectNode(node, $event)" ng-dblclick="dblClick()"  ng-class="{selectedItem: isSelected()}" style="left: {{node.x}}px; top: {{node.y}}px">
            <div><i class="fa " ng-class="{'fa-sitemap': node.nodes.length > 0}"></i> <a href="#" editable-text="node.name">{{ node.name || "empty" }}</a></div>
            <div class="fa fa-arrow-circle-o-right ep right"></div>
            <div class="fa fa-arrow-circle-o-left ep left"></div>
            <div class="fa fa-arrow-circle-o-up ep up"></div>
            <div class="fa fa-arrow-circle-o-down ep down"></div>
        </div>
    </script>


        <script type="text/ng-template" id="templates/pathway/jsPlumbLink.html">
        <div id="link{{link.id}}" ng-click="selectLink(link)">
    </div>
    </script>
</div>

<!-- FIXME remove this and use asset pipeline only. The only reference left is for the layout... -->
<g:javascript disposition="defer" library="pathways"/>

</body>
</html>
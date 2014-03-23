<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en" >
<head>

    <meta name="layout" content="metadata_curation">
    <title>Model Catalogue Core Demo Page</title>

    %{--<!-- example of customization -->--}%
    %{--<script type="text/ng-template" id="modelcatalogue/core/ui/decoratedList.html">--}%
    %{--<div>--}%
    %{--<p ng-hide="list.list">No data</p>--}%
    %{--<ul>--}%
    %{--<li ng-repeat="item in list.list">{{item.name}}</li>--}%
    %{--</ul>--}%
    %{--</div>--}%
    %{--</script>--}%

    <asset:stylesheet href="demo.css"/>
    <asset:javascript src="angular/metadataCurator.js"/>
    <script type="text/javascript">
        angular.module('demo.config', ['mc.core.modelCatalogueApiRoot']).value('modelCatalogueApiRoot', '${request.contextPath ?: ''}/api/modelCatalogue/core')
    </script>

</head>

<body>
<div ng-app="metadataCurator">
    <div class="navbar navbar-default">
        <div class="navbar-inner">
            <ul class="nav navbar-nav">
                <li><a href="#/catalogueElement/model">Models</a></li>
                <li><a href="#/catalogueElement/dataElement">Data Elements</a></li>
                <li><a href="#/catalogueElement/valueDomain">Value Domains</a></li>
                <li><a href="#/catalogueElement/conceptualDomain">Conceptual Domains</a></li>
                <li><a href="#/catalogueElement/dataType">Data Types</a></li>
                <li><a href="#/catalogueElement/enumeratedType">Enumerated Data Types</a></li>
                <li><a href="#/catalogueElement/relationshipType">Relationship Types</a></li>
            </ul>
        </div>
    </div>

    <div class="row">
        <div class="span12">
            <div class="well" ui-view></div>
        </div>
    </div>
</div>
</body>
</html>
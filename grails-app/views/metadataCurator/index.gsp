<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en" >
<head>

    <meta name="layout" content="metadata_curation">
    <title>Metadata Curation </title>

    %{--<!-- example of customization -->--}%
    %{--<script type="text/ng-template" id="modelcatalogue/core/ui/decoratedList.html">--}%
    %{--<div>--}%
    %{--<p ng-hide="list.list">No data</p>--}%
    %{--<ul>--}%
    %{--<li ng-repeat="item in list.list">{{item.name}}</li>--}%
    %{--</ul>--}%
    %{--</div>--}%
    %{--</script>--}%

    <asset:stylesheet href="metaDataCurator.css"/>
    <asset:javascript src="angular/metaDataCurator.js"/>
    <script type="text/javascript">
        angular.module('demo.config', ['mc.core.modelCatalogueApiRoot']).value('modelCatalogueApiRoot', '${request.contextPath ?: ''}/api/modelCatalogue/core')
    </script>

</head>

<body>
<div id="metadataCurator" ng-app="metadataCurator">
    <div class="navbar navbar-default navbar-fixed" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">Curator</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li class="dropdown" ui-sref-active="active">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Catalogue Elements<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li ui-sref-active="active"><a id="modelLink" ui-sref="mc.resource.list({resource: 'model', page:'1'})">Models</a></li>
                            <li ui-sref-active="active"><a id="dataElementLink" ui-sref="mc.resource.list({resource: 'dataElement', page:'1'})">Data Elements</a></li>
                            <li ui-sref-active="active"><a id="valueDomainLink" ui-sref="mc.resource.list({resource: 'valueDomain', page:'1'})">Value Domains</a></li>
                            <li ui-sref-active="active"><a id="conceptualDomainLink" ui-sref="mc.resource.list({resource: 'conceptualDomain', page:'1'})">Conceptual Domains</a></li>
                            <li ui-sref-active="active"><a id="dataTypeLink" ui-sref="mc.resource.list({resource: 'dataType', page:'1'})">Data Types</a></li>
                        </ul>
                    </li>
                    <li class="dropdown" ui-sref-active="active">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Data Architect<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li ui-sref-active="active" ><a id="relationshipTypeLink" ui-sref="mc.resource.list({resource: 'relationshipType'})">Relationship Types</a></li>
                            <li ui-sref-active="active"><a id="uninstantiatedElements" ui-sref="mc.dataArchitect.uninstantiatedDataElements">Uninstantiated Data Elements</a></li>
                            <li ui-sref-active="active"><a id="metadataKeyCheck" ui-sref="mc.dataArchitect.metadataKey">Data Elements without Metadata Key</a></li>
                            <li><a href="../model_catalogue/api/modelCatalogue/core/dataElement?format=xlsx&report=NHIC">Export All Data Elements NHIC report</a></li>
                            <li><a href="../model_catalogue/api/modelCatalogue/core/dataElement?format=xlsx&report=COSD">Export All Data Elements COSD report</a></li>
                            <li><a href="../model_catalogue/api/modelCatalogue/core/dataArchitect/uninstantiatedDataElements?format=xlsx&report=NHIC">Export Uninstantiated Elements</a></li>
                        </ul>
                    </li>
                </ul>
                <form class="navbar-form navbar-right navbar-input-group" role="search" autocomplete="off" ng-submit="search()" ng-controller="metadataCurator.searchCtrl">
                    <div class="form-group">
                        <input ng-model="searchSelect" type="text" name="search-term" id="search-term" placeholder="Search" catalogue-element-picker typeahead-on-select="search()">
                    </div>
                    <button class="btn btn-default" type="submit"><i class="glyphicon glyphicon-search"></i></button>
                </form>
            </div><!--/.nav-collapse -->
        </div>
    </div>

    <div class="container">
        <div class="row">
            <messages-panel max="3"></messages-panel>
        </div>
        <div class="row">
            <div class="col-md-12">
                <ui-view></ui-view>
            </div>
        </div>
    </div>
</div>

</body>
</html>
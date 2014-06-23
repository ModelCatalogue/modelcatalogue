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
        var demoConfig = angular.module('demo.config', ['mc.core.modelCatalogueApiRoot', 'mc.util.security'])
        demoConfig.config(function (securityProvider) {
            securityProvider.springSecurity({
                contextPath: '${request.contextPath ?: ''}',
                roles: {
                    VIEWER:     ['ROLE_METADATA_CURATOR', 'ROLE_ADMIN','ROLE_READONLY_USER'],
                    CURATOR:    ['ROLE_METADATA_CURATOR', 'ROLE_ADMIN','ROLE_USER'],
                    ADMIN:      ['ROLE_ADMIN']
                },
                <sec:ifLoggedIn>
                currentUser: {
                    roles: ${org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils.getPrincipalAuthorities()*.authority.encodeAsJSON()},
                    username: '${sec.username()}'
                }
                </sec:ifLoggedIn>
            })
        })
        demoConfig.value('modelCatalogueApiRoot', '${request.contextPath ?: ''}/api/modelCatalogue/core')
    </script>


</head>

<body>
<div id="metadataCurator" ng-app="metadataCurator" style="width: 100%">
    <div class="navbar navbar-default navbar-static-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>

                <a class="navbar-brand"  href="${createLink(uri: '/')}">Model Catalogue</a>
             </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li><a  href="${createLink(uri: '/metadataCurator')}">Data Curator</a></li>

                    <li class="dropdown" ui-sref-active="active">


                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Catalogue Elements<b
                                class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li ui-sref-active="active"><a id="assetLink"
                                                           ui-sref="mc.resource.list({resource: 'asset'})"
                                                           ui-sref-opts="{inherit: false}">Assets</a>
                            </li>
                            <li ui-sref-active="active"><a id="modelLink"
                                                           ui-sref="mc.resource.list({resource: 'model'})"
                                                           ui-sref-opts="{inherit: false}">Models</a></li>
                            <li ui-sref-active="active"><a id="dataElementLink"
                                                           ui-sref="mc.resource.list({resource: 'dataElement'})"
                                                           ui-sref-opts="{inherit: false}">Data Elements</a>
                            </li>
                            <li ui-sref-active="active"><a id="valueDomainLink"
                                                           ui-sref="mc.resource.list({resource: 'valueDomain'})"
                                                           ui-sref-opts="{inherit: false}">Value Domains</a>
                            </li>
                            <li ui-sref-active="active"><a id="conceptualDomainLink"
                                                           ui-sref="mc.resource.list({resource: 'conceptualDomain'})"
                                                           ui-sref-opts="{inherit: false}">Conceptual Domains</a>
                            </li>
                            <li ui-sref-active="active"><a id="dataTypeLink"
                                                           ui-sref="mc.resource.list({resource: 'dataType'})"
                                                           ui-sref-opts="{inherit: false}">Data Types</a>
                            </li>
                            <li ui-sref-active="active"><a id="measurementUnitLink"
                                                           ui-sref="mc.resource.list({resource: 'measurementUnit'})"
                                                           ui-sref-opts="{inherit: false}">Measurement Unit</a>
                            </li>
                        </ul>
                    </li>
                    <li show-for-role="ADMIN" ui-sref-active="active"><a id="relationshipTypeLink"
                                                                         ui-sref="mc.resource.list({resource: 'relationshipType'})"
                                                                         ui-sref-opts="{inherit: false}">Relationship Types</a>
                    </li>


                    <sec:ifAnyGranted roles="ROLE_READONLY_USER">
                        <li class="dropdown">
                            <a class="dropdown-toggle" data-toggle="dropdown" href="#" id="accountLink">Account <b  class="caret"></b></a>
                            <ul class="dropdown-menu">
                                <li id="changePasswordLink"><g:link controller="register" action='changePassword' >Change Password</g:link></li>
                            </ul>
                        </li>
                    </sec:ifAnyGranted>

                </ul>


                <ul class="nav navbar-nav navbar-right">
                    <li><g:link data-placement="bottom" class="btn btn-inverse" data-original-title="Logout" rel="tooltip" controller="logout"> Logout </g:link></li>
                </ul>

                <form class="navbar-form navbar-right navbar-input-group" role="search" autocomplete="off"
                      ng-submit="search()" ng-controller="metadataCurator.searchCtrl">
                    <div class="form-group">
                        <input ng-model="searchSelect" type="text" name="search-term" id="search-term"
                               placeholder="Search" catalogue-element-picker typeahead-on-select="search()">
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
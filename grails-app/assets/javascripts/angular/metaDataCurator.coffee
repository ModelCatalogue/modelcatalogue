#= require jquery/dist/jquery
#= require bootstrap/dist/js/bootstrap
#= require angular/angular
#= require angular-ui-router/release/angular-ui-router
#= require modelcatalogue/util/index
#= require modelcatalogue/core/index
#= require modelcatalogue/core/ui/index
#= require modelcatalogue/core/ui/bs/index
#= require modelcatalogue/core/ui/bs/elementViews/index

@grailsAppName = 'model_catalogue'

metadataCurator = angular.module('metadataCurator', [
  'ui.router',
  'demo.config'
  'mc.core.ui.bs'
  'ui.bootstrap'
])

metadataCurator.controller('metadataCurator.elementTypeList', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', '$stateParams','$state', 'list', (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q, $stateParams, $state, list)->
  emptyList =
    list: []
    next: {size: 0}
    previous: {size: 0}
    total: 0
    empty: true
    source: 'metadataCurator'

  $scope.list = list

  $scope.type = $stateParams.elementType

  $scope.valueDomainColumns = () -> [
    {header: 'Code', value: 'code', classes: 'col-md-2', show: true}
    {header: 'Name', value: 'name', classes: 'col-md-4', show: true}
    {header: 'Description', value: 'description', classes: 'col-md-6'}
  ]

  $scope.idAndNameColumns = () -> [

    {header: 'Type', value: 'elementTypeName', classes: 'col-md-5'}
    {header: 'ID', value: 'id', classes: 'col-md-2', show: true}
    {header: 'Name', value: 'name', classes: 'col-md-5', show: true}
  ]


  $scope.selection = []

  $scope.columns = $scope.idAndNameColumns()

  $scope.removeColumn = (index) ->
    return if $scope.columns.length <= 1
    $scope.columns.splice(index, 1)

  $scope.addColumn = (index, column = {header: 'ID', value: 'id', classes: 'col-md-2'}) ->
    $scope.columns.splice(index + 1, 0, angular.copy(column))


  $scope.$on 'showCatalogueElement', (event, element) ->
    $state.go('catalogueElement.show', {elementType: element.elementType.split(".").pop(), elementId: element.id})

])

metadataCurator.run(['$rootScope', '$state', '$stateParams', ($rootScope,   $state,   $stateParams) ->
# It's very handy to add references to $state and $stateParams to the $rootScope
# so that you can access them from any scope within your applications.For example,
# <li ui-sref-active="active }"> will set the <li> // to active whenever
# 'contacts.list' or one of its decendents is active.
  $rootScope.$state = $state
  $rootScope.$stateParams = $stateParams
])

metadataCurator.config(($stateProvider, $urlRouterProvider)->
# For any unmatched url, send to /route1
  $urlRouterProvider.otherwise("/catalogueElement/dataElement")

  $stateProvider.state('catalogueElement', {
      abstract: true,
      url: "/catalogueElement"
      templateUrl: '/' + grailsAppName + "/assets/angular/partials/metadata-curator/metadata.html",
    }).state('catalogueElement.list', {
      url: "/{elementType}",
      templateUrl: '/' + grailsAppName + "/assets/angular/partials/metadata-curator/list.html",
      resolve: {
        list: ['$stateParams','catalogueElementResource', ($stateParams,catalogueElementResource) ->
          return catalogueElementResource($stateParams.elementType).list()
        ]
      },
      controller: "metadataCurator.elementTypeList"
    }).state('catalogueElement.show', {
      url: "/{elementType}/{elementId}",
      templateUrl: '/' + grailsAppName + "/assets/angular/partials/metadata-curator/show.html",
      resolve: {
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          return catalogueElementResource($stateParams.elementType).get($stateParams.elementId)
        ]
      },
      controller: "metadataCurator.elementTypeShow",
    })
)


metadataCurator.controller('metadataCurator.elementTypeShow', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', '$state', 'element', (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q, $state, element)->

  $scope.element = element

  $scope.$on 'showCatalogueElement', (event, element) ->
    $state.go('catalogueElement.show', {elementType: element.elementType.split(".").pop(), elementId: element.id})

])
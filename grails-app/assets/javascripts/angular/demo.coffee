##= require jquery/dist/jquery
##= require jquery-ui/ui/jquery-ui
##= require jquery.layout/dist/jquery.layout-latest
##= require bootstrap/dist/js/bootstrap
##= require angular/angular
##= require modelcatalogue/util/index
##= require modelcatalogue/core/index
##= require modelcatalogue/core/ui/index
##= require modelcatalogue/core/ui/bs/index
##= require modelcatalogue/core/ui/bs/elementViews/index
#
#
#demo = angular.module('demo', [
#  'demo.config'
#  'mc.core.ui.bs'
#  'ui.bootstrap'
#
#]).controller('demo.DemoCtrl', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', 'columns', '$rootScope', (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q, columns, $rootScope)->
#  emptyList =
#    list: []
#    next: {size: 0}
#    previous: {size: 0}
#    total: 0
#    empty: true
#    source: 'demo'
#
#
#  $scope.listResource     = """resource("dataElement").list()"""
#  $scope.listRelTypes     = """resource("relationshipType").list()"""
#  $scope.searchSomething  = """search("patient")"""
#  $scope.searchModel      = """resource("model").search("patient")"""
#  $scope.outgoing         = """resource("dataElement").list() >>> $r.list[0].outgoingRelationships()"""
#  $scope.indicator        = """resource("dataElement").search("NHS_NUMBER_STATUS_INDICATOR_CODE") >>> $r.list[0]"""
#
#  $scope.resource         = catalogueElementResource
#  $scope.search           = modelCatalogueSearch
#  $scope.expression       = $scope.indicator
#
#  $scope.show = () ->
#    $log.info "Evaluating: #{$scope.expression}"
#    if $scope.expression.indexOf('>>>') == -1
#      $q.when($scope.$eval($scope.expression)).then (result) ->
#        if result?.size?
#          $scope.columns = columns(result.itemType)
#          $scope.list = result
#          $scope.element = null
#        if result?.elementType?
#          $scope.list = emptyList
#          $scope.element = result
#        else
#          $log.info "Instead of list or element got: ", result
#    else
#      parts     = $scope.expression?.split /\s*>>>\s*/g
#      lastPart  = parts[0]
#      promise   = $q.when $scope.$eval(lastPart)
#      for part, i in parts when i != 0
#        promise = promise.then (result) ->
#          $log.info 'expression in chain {{', lastPart, '}} resolved to ', result
#          lastPart = part
#          $q.when $scope.$eval lastPart, $r: result
#      promise.then (result) ->
#        $log.info 'expression in chain {{', lastPart, '}} resolved to ', result
#        if result?.size?
#          $scope.list = result
#          $scope.columns = columns(result.itemType)
#          $scope.element = null
#        if result?.elementType?
#          $scope.list = emptyList
#          $scope.element = result
#        else
#          $log.info "Instead of list got: ", result
#
#
#  $scope.selection = []
#
#  $scope.columns = columns()
#
#  $scope.actions = [
#    {type: 'primary', title: 'Test', icon: 'info-sign', action: (element) -> alert(element.name)}
#    #{title: 'Test 2', action: (element) -> alert(element.name)}
#  ]
#
#  $scope.removeColumn = (index) ->
#    return if $scope.columns.length <= 1
#    $scope.columns.splice(index, 1)
#
#  $scope.addColumn = (index, column = {header: 'ID', value: 'id', classes: 'col-md-2'}) ->
#    $scope.columns.splice(index + 1, 0, angular.copy(column))
#
#
#  $scope.show()
#
#  $scope.$on 'showCatalogueElement', (event, element) ->
#    $scope.element = element
#
#  $scope.$on 'treeviewElementSelected', (event, element) ->
#    $scope.selectedInTreeview = element
#
#  onDescendPathChange = (path) ->
#    $scope.descend = path.split(/\s*,\s*/)
#
#  $scope.descendPath = 'includes, instantiates'
#  $scope.selectedInTreeview = null
#
#  $scope.$watch 'descendPath', onDescendPathChange
#  $scope.$watch 'selectedInTreeview', (selectedInTreeview) ->
#    $rootScope.$broadcast 'treeviewElementSelected', selectedInTreeview
#
#  onDescendPathChange $scope.descendPath
#])
#
##thanks to jsfiddle http://jsfiddle.net/IgorMinar/jfn5z/3/
##demo.directive "layout", ->
##  link: (scope, elm, attrs) ->
##    layout = elm.layout(applyDefaultStyles: true)
##    scope.layout = layout
##    return
#
#$(document).ready ->
#  # pane can open & close
#  # when open, pane can be resized
#  # when closed, pane can 'slide' open over other panes - closes on mouse-out
#  # log and/or display messages from debugging & testing code
#  resizeWindows = ->
#    surround = $("#footer").height() + $(".navbar").height() + $("h1").height() + 70
#    $("#container").height $("body").height() - surround
#    return
#  resizeWindows()
#  mainLayout = $("#container").layout(
#    closable: true
#    resizable: true
##    slidable: true
#    livePaneResizing: true
#    showDebugMessages: true
#    west:
#      size: "20%"
#  )
#  $(window).resize ->
#    resizeWindows()
#    return
#
#  return


<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main">
    <title>Import data</title>
  </head>
  <body>
    <div id="main">
      <div>
        <ul>
          <li><g:link controller="dataImport" action="importDataSet" params="[dataset: 'nhic']">Import NHIC dataset</g:link></li>
            <ul>
                <g:each in="${nhicFiles}" var="file">
                    <li><g:link controller="dataImport" action="importDataSet" params="[dataset: 'nhic', nhicFile: file]">Only ${file}</g:link></li>
                </g:each>
            </ul>
        </ul>
      </div>

    </div>
    <g:if test="${flash.error}">
        <div class="alert alert-error"><g:message code="${flash.message}" args="${flash.args}" default="${flash.default}"/></div>
    </g:if>
    <g:if test="${flash.message}">
        <div class="message" style="display: block">${flash.message}</div>
    </g:if>

  </body>
</html>

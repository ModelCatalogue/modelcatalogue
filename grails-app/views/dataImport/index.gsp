<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Import Data</title>
</head>
<body>
<div id="main">

    <g:form class="form-horizontal" method="post" action="upload" controller="dataImport"  enctype="multipart/form-data" encoding="multipart/form-data" role="form">
        <div class="form-group">
            <label for="conceptualDomainName">Conceptual Domain Name</label></br>
            <g:field type="text" name="conceptualDomainName" min="5" max="10" required="true"/>
        </div>
        <div class="form-group">
            <label for="conceptualDomainDescription"> Conceptual Domain Description</label></br>
            <input type="text" id="conceptualDomainDescription" name="conceptualDomainDescription"/>
        </div>
        <div class="form-group">
            <label for="excelFile">Please select files</label></br>
            <input type="file" id="excelFile" name="excelFile" multiple="false" />
        </div>
        <button id='btnUpload' type="submit" class="btn btn-primary">Upload</button>
    </g:form>
</div>
<g:if test="${flash.error}">
    <div class="alert alert-error"><g:message code="${flash.message}" args="${flash.args}" default="${flash.default}"/></div>
</g:if>
<g:if test="${flash.message}">
    <div class="message" style="display: block">${flash.message}</div>
</g:if>

</body>
</html>

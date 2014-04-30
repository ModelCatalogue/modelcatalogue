<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Import Data</title>
</head>
<body>
<div id="main">

    <g:form method="post" action="upload" controller="dataImport"  enctype="multipart/form-data" encoding="multipart/form-data">
        <p> ConceptualDomain: <g:field type="text" name="conceptualDomainName" min="5" max="10" required="true"/> </p>
        <p> ConceptualDomain Description: <input type="text" id="conceptualDomainDescription" name="conceptualDomainDescription"/> </p>
        <p> Parent Models: <input type="text" id="parentModels" name="parentModels"/> (i.e. COSD, CORE)</p>
        <h5>Please select files:</h5>
        <p>
            <input type="file" id="excelFile" name="excelFile" multiple="false" />
            <button id='btnUpload' type="submit" class="btn btn-primary">Upload</button>
        </p>
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

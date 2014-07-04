<html>

<head>
    <title>Change Password</title>
    <meta name='layout' content='main'/>

    <asset:javascript src="jquery/dist/jquery.js"/>
    <asset:javascript src="jquery-ui/ui/jquery-ui.js"/>
</head>

<body>

<p/>



    <g:form action='changePassword' name='resetPasswordForm' autocomplete='off' style="width:100%">
        <g:hiddenField name='t' value='${token}'/>
        <div class="sign-in">
            <br/>
            <h4><g:message code='spring.security.ui.resetPassword.description'/></h4>

            <g:if test="${flash.success}">
                <div class='alert alert-success'>
                    ${flash.success}
                    </br>
                    <a href="${createLink(uri: '/')}">Return to the homepage</a>
                </div>
            </g:if>

            <table id="s2ui_registration">
                <tbody>
                        <s2ui:passwordFieldRow name='password' labelCode='resetPasswordCommand.password.label' bean="${command}" labelCodeDefault='Password' value="${command?.password}"/>
                        <s2ui:passwordFieldRow name='password2' labelCode='resetPasswordCommand.password2.label' bean="${command}" labelCodeDefault='Password (again)' value="${command?.password2}"/>

                 </tbody>
            </table>

            <s2ui:submitButton elementId='reset' form='resetPasswordForm'
                               messageCode='spring.security.ui.resetPassword.submit' class="btn btn-large btn-primary"/>

        </div>
    </g:form>


<script>
    $(document).ready(function () {
        $('#password').focus();
    });
</script>

</body>
</html>

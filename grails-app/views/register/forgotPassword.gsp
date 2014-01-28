<html>

<head>
    <title><g:message code='spring.security.ui.forgotPassword.title'/></title>
    <meta name='layout' content='main'/>
</head>

<body>

<g:if test='${emailSent}'>
    <div class='alert alert-danger'>
        <g:message code='spring.security.ui.forgotPassword.sent'/>
    </div>
</g:if>

<h2 class="text-center">Forgot Password</h2>

<form action='${forgotPassword}' name="forgotPasswordForm" method='POST' id='loginForm' class="form-signin" autocomplete='off'>

    <p>Enter your username and we'll send a link to reset your password to the address we have for your account.</p>

    <input type='text' placeholder="Username"
           class='text_ input-block-level input-full' name='j_username' id='username' />

    <p>
        <input type='submit' class="btn btn-large btn-primary btn-block"
               id="submit"
               value='${message(code: "spring.security.ui.forgotPassword.submit")}' />
    </p>


</form>

<script>
    $(document).ready(function() {
        $('#username').focus();
    });
</script>

</body>
</html>

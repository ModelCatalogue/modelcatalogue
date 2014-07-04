import grails.util.Environment
import uk.co.mdc.loginHandler.CustomAuthenticationHandler
import uk.co.mdc.pathways.LinkMarshaller
import uk.co.mdc.pathways.NodeMarshaller
import uk.co.mdc.pathways.PathwayMarshaller
import util.marshalling.CustomObjectMarshallers
import util.marshalling.DataElementMarshaller
import org.modelcatalogue.core.security.ajax.AjaxAwareLoginUrlAuthenticationEntryPoint

// Place your Spring DSL code here
beans = {

	Environment.executeForCurrentEnvironment {
		// Override mail server for dummy in 'development' mode only.
		development {
			mailService(uk.co.mdc.mail.DummyMailService)
		}
	}

    authenticationEntryPoint(AjaxAwareLoginUrlAuthenticationEntryPoint) {
        loginFormUrl = '/login/auth' // has to be specified even though it's ignored
        portMapper = ref('portMapper')
        portResolver = ref('portResolver')
    }

	//CustomAuthenticationHandler class will manage users welcome page
	//authenticationSuccessHandler is a Spring Security bean for success authenticationHandler
	authenticationSuccessHandler(CustomAuthenticationHandler)


	customObjectMarshallers( CustomObjectMarshallers ) {
		marshallers = [
            new PathwayMarshaller(),
            new LinkMarshaller(),
            new NodeMarshaller(),
            new DataElementMarshaller()
		]
	}

}




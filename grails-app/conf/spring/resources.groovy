import grails.util.Environment
import org.modelcatalogue.core.ModelCatalogueSearchService
import uk.co.mdc.loginHandler.CustomAuthenticationHandler
import uk.co.mdc.pathways.LinkMarshaller
import uk.co.mdc.pathways.NodeMarshaller
import uk.co.mdc.pathways.PathwayMarshaller
import util.marshalling.CustomObjectMarshallers
import util.marshalling.DataElementMarshaller

// Place your Spring DSL code here
beans = {

	Environment.executeForCurrentEnvironment {
		// Override mail server for dummy in 'development' mode only.
		development {
			mailService(uk.co.mdc.mail.DummyMailService)
		}
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




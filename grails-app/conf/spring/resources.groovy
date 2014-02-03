import groovy_metadata_registry.APIAuthenticationFilters
import uk.co.mdc.pathways.PathwayMarshaller
import util.marshalling.CustomObjectMarshallers
import uk.co.mdc.model.DataElementMarshaller
import uk.co.mdc.forms.FieldMarshaller
import uk.co.mdc.forms.QuestionElementMarshaller
import uk.co.mdc.forms.FormDesignMarshaller
import uk.co.mdc.forms.SectionElementMarshaller
import uk.co.mdc.model.ValueDomainMarshaller
import uk.co.mdc.model.DataElementConceptMarshaller
import uk.co.mdc.model.ConceptualDomainMarshaller
import uk.co.mdc.model.CollectionMarshaller
import uk.co.mdc.pathways.LinkMarshaller
import uk.co.mdc.pathways.NodeMarshaller
import grails.util.Environment

// Place your Spring DSL code here
beans = {

	Environment.executeForCurrentEnvironment {
		// Override mail server for dummy in 'development' mode only.
		development {
			mailService(uk.co.mdc.mail.DummyMailService)
		}
	}


	customObjectMarshallers( CustomObjectMarshallers ) {
		marshallers = [
            new DataElementMarshaller(),
            new FieldMarshaller(),
            new ValueDomainMarshaller(),
            new DataElementConceptMarshaller(),
            new CollectionMarshaller(),
            new ConceptualDomainMarshaller(),
            new QuestionElementMarshaller(),
            new FormDesignMarshaller(),
            new SectionElementMarshaller(),

            new PathwayMarshaller(),
            new LinkMarshaller(),
            new NodeMarshaller(),
		]
	}
}




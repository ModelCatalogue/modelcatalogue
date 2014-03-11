import uk.co.mdc.pathways.PathwayMarshaller
import util.marshalling.CustomObjectMarshallers
import uk.ac.ox.brc.modelcatalogue.forms.forms.FieldMarshaller
import uk.ac.ox.brc.modelcatalogue.forms.forms.QuestionElementMarshaller
import uk.ac.ox.brc.modelcatalogue.forms.forms.FormDesignMarshaller
import uk.ac.ox.brc.modelcatalogue.forms.forms.SectionElementMarshaller
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
            new FieldMarshaller(),
            new QuestionElementMarshaller(),
            new FormDesignMarshaller(),
            new SectionElementMarshaller(),

            new PathwayMarshaller(),
            new LinkMarshaller(),
            new NodeMarshaller(),
		]
	}
}




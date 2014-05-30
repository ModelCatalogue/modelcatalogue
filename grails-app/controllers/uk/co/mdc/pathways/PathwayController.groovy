package uk.co.mdc.pathways

import grails.rest.RestfulController
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.AccessDeniedException

class PathwayController extends RestfulController<Pathway>{

    def pathwayService

    PathwayController() {
        super(Pathway)
    }

	static defaultAction = "index"

	@Override
	protected List<Pathway> listAllResources(Map params) {
		pathwayService.topLevelPathways(params)
	}

	@Override
    def show(){
		Pathway pathway
		try{
			pathway = pathwayService.get(params.id)


        }catch(AccessDeniedException ADE){
			pathway = null
        }
		if(pathway){
			respond pathway, [model: [pathway: pathway]]
		}else{
			def model = [ success: false, msg: [ code: 404, text: "The item could not be found or you do not have access to it"]]
			respond model as Object, [status: 404, view: 'error404']
		}
    }

	/**
	 * Creates a new instance of the resource for the given parameters, or throws an exception.
	 *
	 * @param params The Map representing the new pathway
	 * @throws grails.validation.ValidationException if the pathway could not be created
	 * @return The resource instance
	 */
	protected Pathway createResource(Map params) {
		pathwayService.create(params)
	}



    @Override
    def update(){
        Pathway pathway = queryForResource(params.id)
        if (pathway == null) {
            notFound()
            return
        }

        def idMappings = [:]
        def clientPathway = request.JSON
        pathway = pathwayService.update(pathway, clientPathway, idMappings)

        def response = [
                pathway: pathway,
                idMappings: idMappings
        ]
        //FIXME should return errors
        if(pathway.hasErrors()){
            response.hasErrors = true
            response.errors = pathway.errors
        }

        respond response
    }


    def delete(Pathway pathway) {

        def model
        def msg

        if (!pathway) {
            msg = message(code: 'default.not.found.message', args: [message(code: 'pathway.label', default: 'Pathway'), params.id])
            model = [errors: true, details: msg]

        }else{
			try {
				pathwayService.delete(pathway)
				msg = message(code: 'default.deleted.message', args: [message(code: 'pathway.label', default: 'Pathway'), pathway.id])
				model = [success: true, details: msg]
			}
			catch (DataIntegrityViolationException e) {
				msg = message(code: 'default.not.deleted.message', args: [message(code: 'pathway.label', default: 'Pathway'), pathway.id])
				model = [errors: true, details: msg]
			}
		}

        respond model
    }

    /**
     * Utility method to return the current pathway instance. Uses PathwayService to honor security.
     * @return the pathway or null.
     */
    private Pathway findInstance() {
        return findInstance(params.long('id'))
    }

    /**
     * Utility method to return the current pathway instance. Uses PathwayService to honour security.
     * @return the pathway or null.
     */
    private Pathway findInstance(Long id) {
        def pathway = pathwayService.get(id)
        if (!pathway) {
            flash.message = "Pathway not found with id $params.id"
        }
        pathway
    }
}
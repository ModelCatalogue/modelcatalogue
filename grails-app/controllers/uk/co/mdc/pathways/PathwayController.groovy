package uk.co.mdc.pathways

import grails.rest.RestfulController

import grails.plugins.springsecurity.Secured
import grails.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException

@Secured(['ROLE_USER'])
class PathwayController extends RestfulController<Pathway>{

    def pathwayService

    PathwayController() {
        super(Pathway)
    }

    @Override
    def index() {
        list()
    }

    def list() {
        def model = [items: pathwayService.topLevelPathways()]
        respond model
    }

    def show(Pathway pathway){
        if(!pathway){
            def model = [ success: false, msg: [ code: 404, text: "The item could not be found"]]
            respond model as Object, [status: 404, view: 'error404']
        }else{
            respond pathway, [model: [pathway: pathway]]
        }
    }

    @Transactional
    def save(Pathway pathway){
        pathway = pathwayService.create(pathway)
        if(pathway.hasErrors()){
            respond pathway.errors
        }else{
            redirect pathway
        }
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
            msg = message(code: 'default.not.found.message', args: [message(code: 'pathway.label', default: 'Pathway'), pathway.id])
            model = [errors: true, details: msg]
            respond model
        }

        try {
            pathwayService.delete(pathway)
            msg = message(code: 'default.deleted.message', args: [message(code: 'pathway.label', default: 'Pathway'), pathway.id])
            model = [success: true, details: msg]
        }
        catch (DataIntegrityViolationException e) {
            msg = message(code: 'default.not.deleted.message', args: [message(code: 'pathway.label', default: 'Pathway'), pathway.id])
            model = [errors: true, details: msg]
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
            redirect action: list
        }
        pathway
    }
}
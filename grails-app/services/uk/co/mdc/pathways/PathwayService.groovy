package uk.co.mdc.pathways

import grails.validation.ValidationException
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PathwayService {

   	static transactional = false
	
	def aclPermissionFactory
	def aclService
	def aclUtilService
	def springSecurityService

	void addPermission(Pathway pathway, String roleOrUsername, int permission){
		addPermission pathway, roleOrUsername, aclPermissionFactory.buildFromMask(permission)
	}

    @PreAuthorize("hasPermission(#pathway, admin)")
    @Transactional
    void addPermission(Pathway pathway, String roleOrUsername, Permission permission) {
        aclUtilService.addPermission pathway, roleOrUsername, permission
    }

    /**
     *
     * Because links depend on nodes, we're assuming that all nodes have been created at this point, and
     * there aren't any invalid references loitering.
     * @param pathway
     * @param nodesToCreate a map defining the nodes to be created, with the frontend ID as a key and the Node as the value
     * @return
     */
    @Transactional
    @PreAuthorize("hasPermission(#pathway, write) or hasPermission(#pathway, admin)")
    Pathway update(Pathway pathway, def clientPathway, def idMappings) {

        assert clientPathway.id == pathway.id

        createLocalNodes(clientPathway, idMappings)
        cleanAndCreateLinks(clientPathway, idMappings)

        // Only grab the attributes of the current pathway - deep nodes have been saved in the last 2 method calls
        pathway.properties = clientPathway.findAll { key, value -> key != 'nodes' && key != 'links'}
        pathway.save(failOnError: true)
        return pathway
    }

    /**
     * Adds or saves nodes for a given pathway (or subpathway :)).
     * This method assumes the ID in the clientPathway is valid and persisted
     * @param clientPathway
     * @param idMappings
     * @return
     */
    @Transactional
    void createLocalNodes( def clientPathway, def idMappings ){
        def savedPathway = Pathway.get(clientPathway?.id)
        if(!savedPathway){
            throw new IllegalArgumentException("clientPathway '"+clientPathway.name+"' does not have a valid and present ID "+clientPathway?.id)
        }

        clientPathway.nodes.each{ node ->
            // if new, create
            if(node.id =~ /^LOCAL/){
                println "adding node "+node.name
                def oldId = node.id
                node.id = null

                if(idMappings[node.pathway]){
                    node.pathway = idMappings[node.pathway]
                }

                Node savedNode = Node.newInstance(node.findAll { key, value -> key != 'nodes' && key != 'links'}).save(failOnError: true)
                savedPathway.addToNodes(savedNode)

                idMappings[oldId] = savedNode.id

                // Set the node ID to match the saved node ID (so subsequent calls down the tree can find the parent...
                node.id = savedNode.id
            }
            // otherwise just save
            else{
                Node nodeInstance = Node.get(node.id)
                nodeInstance.properties = node.findAll { key, value -> key != 'nodes' && key != 'links'}
                nodeInstance.dataElements = null// node.dataElements
                nodeInstance.forms = node.forms
                nodeInstance.save(failOnError: true)
            }

            // finally recurse
            createLocalNodes(node, idMappings)

        }
    }

    /**
     * Utility method to clean a list of links, replacing LOCAL ids with
     * the IDs of the persisted objects, using the idMappings map. Where a link doesn't exist, it will be saved and the ID
     * added to the mappings table
     *
     * A sample idMappings object might look like: ['LOCAL1':12, 'LOCAL2':144]
     *
     * @param unsavedPathway The unsaved pathway map, containing uncoerced, unvalidated values.
     * @param idMappings a map of local to newly created database IDs
     */
    @Transactional
    void cleanAndCreateLinks( def unsavedPathway, def idMappings){

        unsavedPathway.links.each{ link ->
            if(link.source =~ /^LOCAL/){
                if(!idMappings[link.source]){
                    throw new IllegalArgumentException("Node ID is not valid: "+link.source)
                }else{
                    link.source = idMappings[link.source]
                }
            }
            if(link.target =~ /^LOCAL/){
                if(!idMappings[link.target]){
                    throw new IllegalArgumentException("Node ID is not valid: "+link.target)
                }else{
                    link.target = idMappings[link.target]
                }
            }

            Pathway parent = get(unsavedPathway.id)
            Link savedLink;
            if(link.id =~ /^LOCALLINK/){
                def oldId = link.id
                link.id = null
                link.pathway = parent
                savedLink = Link.newInstance(link)
                savedLink.save(failOnError: true)
                parent.addToLinks(savedLink).save(failOnError: true)
                idMappings[oldId] = savedLink.id

            }else{
                savedLink = Link.get(link.id)
                if(savedLink){
                    savedLink.properties = link
                    savedLink.save(failOnError: true)
                }else{
                    log.error("Could not find link " + link.id)
                }
            }
        }
        unsavedPathway.nodes.each{ childNode ->
            cleanAndCreateLinks(childNode, idMappings)
        }
    }

    /**
     * Deletes a pathway, all it's associated links and sub-pathways, and its associated ACL
     * @param pathway The pathway to delete
     */
    @Transactional
    @PreAuthorize("hasPermission(#pathway, delete) or hasPermission(#pathway, admin)")
    void delete(Pathway pathway){
        pathway.delete()
        aclUtilService.deleteAcl pathway
    }

	/**
	 * Throws a validation exception if the creation fails
	 * @param pathwayParams
	 * @throws ValidationException if the passed params are invalid
	 */
	@Transactional
	@PreAuthorize("hasRole('ROLE_USER')")
	Pathway create(Map pathwayParams) {
		Pathway pathway = new Pathway(pathwayParams)

		pathway.save(failOnError: true) // bubble up the exception to the controller


        // Update permissions for owner (read for all, write + delete for owner
        addPermission pathway, springSecurityService.authentication.name, BasePermission.READ
        addPermission pathway, springSecurityService.authentication.name, BasePermission.WRITE
        addPermission pathway, springSecurityService.authentication.name, BasePermission.DELETE
        return pathway
	}

    /**
     * Return a list of top-level pathways, with a given set of search criteria.
     * The structure of the searchCriteria should be (e.g.):
     *
     * {
     *  name: "pathway 1",
     *  isDraft: false
     * }
     *
     * @param searchCriteria The search criteria
     * @return a list of pathways (top level only)
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    List<Pathway> topLevelPathways(def searchCriteria) {

        List<Pathway> pathways
        if(searchCriteria == null){
            pathways = Pathway.list()
        }else{

            def nodeProps = Pathway.metaClass.properties*.name
            pathways = Pathway.withCriteria {
                and {
                    searchCriteria.each { field, value ->
                        if (nodeProps.grep(field)) {
                            eq(field, value)
                        }
                    }
                }
            }
        }
        // FIXME this should be in the criteria, but I had problems getting that to work :(
        return pathways.findAll { it.class == Pathway }

    }


	@PostAuthorize("hasPermission(returnObject, read) or hasPermission(returnObject, admin)")
	Pathway get(long id) {
	   Pathway.get id
	}
}

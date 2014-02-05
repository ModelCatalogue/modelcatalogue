package uk.co.mdc.pathways

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

	void addPermission(Pathway pathwaysModel, String roleOrUsername, int permission){
		addPermission pathwaysModel, roleOrUsername, aclPermissionFactory.buildFromMask(permission)
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
    @PreAuthorize("hasPermission(#pathway, write) or hasPermission(#pathway, admin)")
    Pathway update(Pathway pathway, def clientPathway, def idMappings) {

        assert clientPathway.id == pathway.id

        cleanAndCreateNodes(clientPathway, idMappings)

        cleanAndCreateLinks(clientPathway, idMappings)

        pathway.properties = clientPathway
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
    void createOrSaveNodesForPathway( def clientPathway, def idMappings ){
        def savedPathway = Pathway.get(clientPathway?.id)
        if(!savedPathway){
            throw new IllegalArgumentException("clientPathway does not have a valid and present ID "+clientPathway?.id)
        }

        clientPathway.nodes.each{ node ->
            // if new, create
            if(node.id =~ /^LOCAL/){
                println "adding node "+node.name
                def oldId = node.id
                node.id = null
                node.pathway = savedPathway

                Node savedNode = new Node()

                savedNode.properties = node.findAll { key, value -> key != 'nodes' && key != 'links'}
                savedNode.pathway = savedPathway

                savedNode.save()
                node.id = savedNode.id
                savedPathway.addToNodes(savedNode)
                idMappings[oldId] = savedNode.id
            }
            // else save

            // finally recurse
            createOrSaveNodesForPathway(node, idMappings)

        }
    }

//    void cleanAndCreateNodes(def unsavedPathway, def idMappings){
//        unsavedPathway.nodes.each{ node ->
//            if(node.id =~ /^LOCAL/){
//                // need to create node
//                def oldId = node.id
//                node.id = null
//                log.error("Parent: "+unsavedPathway.id)
//                Pathway parent = get(unsavedPathway.id)
//                node.pathway = parent
//                log.error(node)
//                Node savedNode = new Node()
//
//
//
//                savedNode.properties = node
//                savedNode.save(failOnError: true)
//                parent.addToNodes(savedNode)
//
//                node.id = savedNode.id
//                idMappings[oldId] = savedNode.id
//                log.error " mapping "+ oldId + " to " + idMappings[oldId]
//            }
//            cleanAndCreateNodes(node, idMappings)
//            log.error "leaving cleanandcreate for "+node.id
//        }
//    }

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
    void cleanAndCreateLinks( def unsavedPathway, def idMappings){

        unsavedPathway.links.each{ link ->
            if(link.source =~ /^LOCAL/){
                log.error(idMappings)
                if(!idMappings[link.source]){
                    throw new IllegalArgumentException("Node ID is not valid: "+link.source)
                }else{
                    link.source = idMappings[link.source]
                }
            }
            if(link.target =~ /^LOCAL/){
                log.error(idMappings)
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
                savedLink.properties = link
                savedLink.save(failOnError: true)
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
	
	@Transactional
	@PreAuthorize("hasRole('ROLE_USER')")
	Pathway create(Pathway pathway) {

        pathway.save()

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

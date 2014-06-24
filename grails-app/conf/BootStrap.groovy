import grails.rest.render.RenderContext
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElement
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.dataarchitect.ImportRow

import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.web.context.support.WebApplicationContextUtils
import uk.co.mdc.Requestmap
import uk.co.mdc.SecAuth
import uk.co.mdc.SecUser
import uk.co.mdc.SecUserSecAuth
import uk.co.mdc.pathways.Link
import uk.co.mdc.pathways.Node
import uk.co.mdc.pathways.Pathway
import org.springframework.security.acls.domain.BasePermission


class BootStrap {

	def aclService, aclUtilService, sessionFactory, springSecurityService, grailsApplication, domainModellerService, initCatalogueService, dataArchitectService

    XLSXListRenderer xlsxListRenderer
 
	def importService
	def publishedElementService

    @Autowired ReportsRegistry reportsRegistry


	def init = { servletContext ->

		def springContext = WebApplicationContextUtils.getWebApplicationContext( servletContext )
		
		//register custom json Marshallers
		registerJSONMarshallers(springContext)

        initCatalogueService.initDefaultDataTypes()
        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultMeasurementUnits()

		xlsxListRenderer.registerRowWriter('COSD') {
			title: "COSD"
			headers "Parent Model Unique Code",	"Parent Model",	"Model Unique Code", "Model", "Data Item Unique Code", "Data Item Name", "Data Item Description", "Measurement Unit", "Data type",	"Metadata", "Data item No.","Schema Specification","Data Dictionary Element", "Current Collection", "Format"
			when { ListWrapper container, RenderContext context ->
				context.actionName in ['index', 'search', 'metadataKeyCheck', 'uninstantiatedDataElements', 'getSubModelElements'] && DataElement.isAssignableFrom(container.itemType)
			} then { DataElement element ->
				[[getParentModel(element)?.modelCatalogueId, getParentModel(element)?.name, getContainingModel(element)?.modelCatalogueId, getContainingModel(element)?.name, element.modelCatalogueId, element.name, element.description, getUnitOfMeasure(element), getDataType(element), "-", element.ext.get("Data item No."), element.ext.get("Schema Specification"), element.ext.get("Data Dictionary Element"), element.ext.get("Current Collection"), element.ext.get("Format") ]]
			}
		}

		xlsxListRenderer.registerRowWriter('NHIC') {
			title: "NHIC"
			headers "Parent Model Unique Code",	"Parent Model",	"Model Unique Code", "Model", "Data Item Unique Code", "Data Item Name", "Data Item Description", "Measurement Unit", "Data type",	"Metadata", "NHIC_Identifier","Link_to_existing_definition", "Notes_from_GD_JCIS" ,"Optional_Local_Identifier","A" ,"B","C" ,"D" ,"E" ,"F" ,"G","H","E2", "System", "Comments", "Group"
			when { ListWrapper container, RenderContext context ->
				context.actionName in ['index', 'search', 'metadataKeyCheck', 'uninstantiatedDataElements', 'getSubModelElements'] && DataElement.isAssignableFrom(container.itemType)
			} then { DataElement element ->
				[[getParentModel(element)?.modelCatalogueId, getParentModel(element)?.name, getContainingModel(element)?.modelCatalogueId, getContainingModel(element)?.name, element.modelCatalogueId, element.name, element.description, getUnitOfMeasure(element), getDataType(element), "-", element.ext.NHIC_Identifier, element.ext.Link_to_existing_definition, element.ext.Notes_from_GD_JCIS , element.ext.Optional_Local_Identifier, element.ext.A, element.ext.B, element.ext.C , element.ext.D , element.ext.E , element.ext.F , element.ext.G, element.ext.H, element.ext.E2, element.ext.System, element.ext.Comments, element.ext.Group]]
			}
		}


		reportsRegistry.register {
			title 'Export All to COSD'
			type Model
			link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report: 'COSD'], id: true
		}

		reportsRegistry.register {
			title 'Export All to NHIC'
			type Model
			link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report: 'NHIC'], id: true
		}


		environments {
			production {
                createBaseRoles()
				createAdminAccount()
			}
			staging{
				importDevData()
			}
			test{
				importDevData()
                //domainModellerService.modelDomains()
			}
			development {
				importDevData()
			}
		}

        configureRequestMapSecurity()
	}

    private configureRequestMapSecurity(){

        //permit all for assets and initial pages
        for (String url in [
                '/',
                '/**/favicon.ico',
                '/fonts/**',
                '/assets/**',
                '/plugins/**/js/**',
                '/js/vendor/**',
                '/**/*.less',
                '/**/js/**',
                '/**/css/**',
                '/**/images/**',
                '/**/img/**',
                '/login', '/login.*', '/login/*',
                '/logout', '/logout.*', '/logout/*',
                '/register/*', '/errors','/errors/*'
        ]) {
            new Requestmap(url: url, configAttribute: 'permitAll').save()
        }

        //only permit users
        new Requestmap(url: '/dashboard', configAttribute: 'ROLE_ADMIN, ROLE_USER, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/dashboard/**', configAttribute: 'ROLE_ADMIN, ROLE_USER, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/pathways', configAttribute: 'ROLE_ADMIN, ROLE_USER, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/pathways/**', configAttribute: 'ROLE_ADMIN, ROLE_USER, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/pathway', configAttribute: 'ROLE_ADMIN, ROLE_USER, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/pathway/**', configAttribute: 'ROLE_ADMIN, ROLE_USER, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/pathways.json', configAttribute: 'ROLE_ADMIN, ROLE_USER, IS_AUTHENTICATED_FULLY').save()


		//add ROLE_READONLY_USER for metadataCurator
        new Requestmap(url: '/metadataCurator', configAttribute: 'ROLE_READONLY_USER,ROLE_ADMIN, ROLE_USER, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/metadataCurator/**', configAttribute: 'ROLE_READONLY_USER,ROLE_ADMIN, ROLE_USER,ROLE_USER, IS_AUTHENTICATED_FULLY').save()


        //only permit admin user registrationCode
        new Requestmap(url: '/bootstrap-data/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/dataImport/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/relationshipImport/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/oldDataImport/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/excelImporter/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/admin', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/admin/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/securityInfo/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/role', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/role/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/registrationCode', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/registrationCode/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/user', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/user/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclClass', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclClass/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclSid', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclSid/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclEntry', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclEntry/**', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclObjectIdentity', configAttribute: 'ROLE_ADMIN, IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/cosdimporter/**', configAttribute: 'ROLE_ADMIN, ROLE_USER, IS_AUTHENTICATED_FULLY').save()

        //only permit metadatacurator users access to the api

		//add ROLE_READONLY_USER for metadataCurator
		new Requestmap(url: '/api/modelCatalogue/core/**', configAttribute: 'ROLE_READONLY_USER,ROLE_USER, ROLE_ADMIN, ROLE_METADATA_CURATOR', httpMethod: org.springframework.http.HttpMethod.GET).save()
		new Requestmap(url: '/api/modelCatalogue/core/*/*/outgoing/**', configAttribute: 'ROLE_READONLY_USER,ROLE_ADMIN, ROLE_METADATA_CURATOR', httpMethod: org.springframework.http.HttpMethod.POST).save()
        new Requestmap(url: '/api/modelCatalogue/core/*/*/incoming/**', configAttribute: 'ROLE_READONLY_USER,ROLE_ADMIN, ROLE_METADATA_CURATOR', httpMethod: org.springframework.http.HttpMethod.POST).save()
        new Requestmap(url: '/api/modelCatalogue/core/search/**', configAttribute: 'ROLE_READONLY_USER,ROLE_USER, ROLE_ADMIN, ROLE_METADATA_CURATOR', httpMethod: org.springframework.http.HttpMethod.GET).save()


		new Requestmap(url: '/api/modelCatalogue/core/*/create', configAttribute: 'ROLE_USER, ROLE_ADMIN,ROLE_METADATA_CURATOR', httpMethod: org.springframework.http.HttpMethod.GET).save()
        new Requestmap(url: '/api/modelCatalogue/core/*/edit', configAttribute: 'ROLE_USER, ROLE_ADMIN,ROLE_METADATA_CURATOR', httpMethod: org.springframework.http.HttpMethod.GET).save()
        new Requestmap(url: '/api/modelCatalogue/core/*/save', configAttribute: 'ROLE_USER, ROLE_ADMIN,ROLE_METADATA_CURATOR', httpMethod: org.springframework.http.HttpMethod.POST).save()
        new Requestmap(url: '/api/modelCatalogue/core/*/update', configAttribute: 'ROLE_USER, ROLE_ADMIN,ROLE_METADATA_CURATOR', httpMethod: org.springframework.http.HttpMethod.PUT).save()
        new Requestmap(url: '/api/modelCatalogue/core/*/delete', configAttribute: 'ROLE_USER, ROLE_ADMIN,ROLE_METADATA_CURATOR', httpMethod: org.springframework.http.HttpMethod.DELETE).save()

    }

	private importDevData(){

		if(!SecUser.findByUsername('user1')){
			//this if needs to be removed....only for development purposes

			//create user if none exists
			createUsers()

			//login as admin so you can create the prepopulated data
			loginAsAdmin()


			//populate with some test data....there will be more
			populateWithTestData()

            sessionFactory.currentSession.flush()

			//grant relevant permissions (i.e. admin user has admin on everything)
			grantPermissions()

			sessionFactory.currentSession.flush()

			// logout
			SCH.clearContext()
		}
	}
    /**
     * Adds the requisite roles to the system, unless they are aleady present.
     */
    private createBaseRoles(){
        SecAuth.findByAuthority('ROLE_ADMIN') ?: new SecAuth(authority: 'ROLE_ADMIN').save(failOnError: true)
        SecAuth.findByAuthority('ROLE_METADATA_CURATOR') ?: new SecAuth(authority: 'ROLE_METADATA_CURATOR').save(failOnError: true)
        SecAuth.findByAuthority('ROLE_PENDING') ?: new SecAuth(authority: 'ROLE_PENDING').save(failOnError: true)
        SecAuth.findByAuthority('ROLE_USER') ?: new SecAuth(authority: 'ROLE_USER').save(failOnError: true)
        SecAuth.findByAuthority('ROLE_READONLY_USER') ?: new SecAuth(authority: 'ROLE_READONLY_USER').save(failOnError: true)
    }

	private createAdminAccount(){
		def roleUser = SecAuth.findByAuthority('ROLE_USER') ?: new SecAuth(authority: 'ROLE_USER').save(failOnError: true)
		def roleAdmin = SecAuth.findByAuthority('ROLE_ADMIN') ?: new SecAuth(authority: 'ROLE_ADMIN').save(failOnError: true)
        def metadataCurator = SecAuth.findByAuthority('ROLE_METADATA_CURATOR') ?: new SecAuth(authority: 'ROLE_METADATA_CURATOR').save(failOnError: true)

        def admin = SecUser.findByUsername('localadmin') ?: new SecUser(username: 'localadmin', emailAddress: "brcmodelcatalogue@gmail.com", enabled: true, password: 'QpAsN#6HVP.6da').save(failOnError: true)
		
				if (!admin.authorities.contains(roleAdmin)) {
					SecUserSecAuth.create admin, roleUser
                    SecUserSecAuth.create admin, metadataCurator
					SecUserSecAuth.create admin, roleAdmin, true
				}
	}


	private registerJSONMarshallers(springContext) {

		//register custom marshallers
		springContext.getBean('customObjectMarshallers').register()
	}

	private void createUsers() {

        def roleAdmin = SecAuth.findByAuthority('ROLE_ADMIN') ?: new SecAuth(authority: 'ROLE_ADMIN').save(failOnError: true)
        def roleUser = SecAuth.findByAuthority('ROLE_USER') ?: new SecAuth(authority: 'ROLE_USER').save(failOnError: true)
        def roleReadOnlyUser = SecAuth.findByAuthority('ROLE_READONLY_USER') ?: new SecAuth(authority: 'ROLE_READONLY_USER').save(failOnError: true)


		if(!SecUser.findByUsername('user1') ){
			def user = new SecUser(username: "user1", enabled: true, emailAddress: "user1@example.org", password: "password1").save(failOnError: true)
			SecUserSecAuth.create user, roleUser			
		}


		if(!SecUser.findByUsername('ruser1') ){
			def readOnlyUser = new SecUser(username: "ruser1", enabled: true, emailAddress: "user1@example.org", password: "rpassword1").save(failOnError: true)
			SecUserSecAuth.create readOnlyUser, roleReadOnlyUser
		}


		def admin = SecUser.findByUsername('admin') ?: new SecUser(username: 'admin', emailAddress: "testadmin1@example.org", enabled: true, password: 'admin123').save(failOnError: true)

		if (!admin.authorities.contains(roleAdmin)) {
			SecUserSecAuth.create admin, roleUser
			SecUserSecAuth.create admin, roleAdmin, true
		}

	}


	private void loginAsAdmin() {
		// have to be authenticated as an admin to create ACLs
		SCH.context.authentication = new UsernamePasswordAuthenticationToken( 'admin', 'admin123', AuthorityUtils.createAuthorityList('ROLE_ADMIN'))

	}

	private void grantPermissions() {
		def dataElements = []

		// grant ROLE_ADMIN on everything

		grantAdminPermissions(Node.list())
		grantAdminPermissions(Link.list())

        // We don't need to add permissions for nodes and links
		grantAdminPermissions(Pathway.list())


		// Grant ROLE_USER on everything

        // We don't need to add permissions for nodes and links
		grantUserPermissions(Pathway.list())

	}


	def grantAdminPermissions(objectList){
		for (object in objectList) {
			aclUtilService.addPermission object, 'ROLE_ADMIN', BasePermission.ADMINISTRATION
			
		}
	}
	
	
	def grantUserPermissions(objectList){
		for (object in objectList) {
			//FIX me - by default user will have the 
            aclUtilService.addPermission object, 'ROLE_USER', BasePermission.READ
            aclUtilService.addPermission object, 'ROLE_USER', BasePermission.WRITE
            aclUtilService.addPermission object, 'ROLE_USER', BasePermission.DELETE
		}
	}
	
	
	

	def destroy = {
	}


	/*
	 * **********************POPULATE WITH PATHWAYS TEST DATA********************************
	 *
	 * */

	private populateWithTestData(){

		if(!Pathway.count()){

			//Add a form to the pathways
            def pathway1 = new Pathway(
                    name: 'Transplanting and Monitoring Pathway',
                    userVersion: '0.2',
                    isDraft: true
            ).save(failOnError: true, flush:true)


            Node subPathway1 = new Node(
                    name: 'Guarding Patient on recovery and transfer to nursing ward',
                    description: 'transfer patient to the Operating Room',
                    userVersion: '0.1',
                    isDraft: true,
                    x: '325px',
                    y: '330px',
                    parent: pathway1,
            ).save(failOnError:true, flush:true)

            Node node1 = new Node(
                    name: 'Guard Patient',
                    x: '250px',
                    y: '0px',
                    description: 'guard patient on recovery',
            ).save(failOnError: true, flush:true)

            Node node2 = new Node(
                    name: 'Recovery',
                    x: '150px',
                    y: '100px',
                    description: 'recover',
            ).save(failOnError: true, flush:true)

            Node node3 = new Node(
                    name: 'Transfer to nursing ward',
                    x: '250px',
                    y: '300px',
                    description: 'transfer patient to the nursing ward',
            ).save(failOnError: true, flush:true)

            def link1 = new Link(
                    name: 'TM1',
                    pathway: subPathway1,
                    source: node1,
                    target: node2,
            ).save(failOnError:true, flush:true)

            def link2 = new Link(
                    name: 'TM2',
                    pathway: subPathway1,
                    source: node2,
                    target: node3,
            ).save(failOnError:true, flush:true)

            subPathway1.addToNodes(node1)
            subPathway1.addToNodes(node2)
            subPathway1.addToNodes(node3)
            subPathway1.addToLinks(link1)
            subPathway1.addToLinks(link2)

            def node21 = new Node(
                    name: 'transfer to O.R.',
                    x: '455px',
                    y: '0px',
                    description: 'transfer patient to the Operating Room',
            ).save(flush:true)


            def node22 = new Node(
                    name: 'Anaesthesia and Operating Procedure',
                    x: '115px',
                    y: '110px',
                    description: 'perform the operation',
            ).save(flush:true)

            def link21 = new Link(
                    name: 'TM21',
                    source: node21,
                    target: node22,
                    pathway: pathway1,
            ).save(flush:true)

            def link22 = new Link(
                    name: 'TM22',
                    source: node22,
                    target: subPathway1,
                    pathway: pathway1,
            ).save(flush:true)


            pathway1.addToNodes(node21)
                    .addToNodes(node22)
                    .addToNodes(subPathway1)
                    .addToLinks(link21)
                    .addToLinks(link22)
		}


		def de1  = new DataElement(name: "DE",  modelCatalogueId: "MC_a6ff28a6-d214-4fca-824f-e5c8fc8c6b5d_1").save(failOnError: true)
		def de2 = new DataElement(name: "DE2", modelCatalogueId: "MC_a7ff77a6-d777-7fca-777f-e7c7fc7c7b7d_1").save(failOnError: true)
		de1.ext["NHIC_Identifier"] = "123"
		de1.ext["Multiplicity"]	   = "2"
		de1.ext["Comment"] 		   = "SimpleComment"


		def topParentModel = new Model(name: "NHIC Datasets",modelCatalogueId:"MC_a6ff28a6-d214-4fca-811f-e7c8fc8c6b5d_1").save(failOnError: true)
		def parentModel    = new Model(name: "ParentModel1", modelCatalogueId:"MC_a6ff28a6-d214-4fca-824f-e7c8fc8c6b5d_1").save(failOnError: true)
		def model          = new Model(name: "Model1",       modelCatalogueId:"MC_a6ff28a6-d216-4fca-824f-e5c8fc8c6b5d_1").save(failOnError: true)


		topParentModel.addToParentOf(parentModel)
		parentModel.addToParentOf(model)

		model.addToContains de1
		model.addToContains de2

		//de1.addToRelatedto de2

		PublishedElement.list().each {
			it.status = PublishedElementStatus.FINALIZED
			it.save(failOnError: true)
		}

	}


    def getContainingModel(DataElement dataElement){
        if(dataElement.containedIn) {
            return dataElement.containedIn.first()
        }
        return null
    }

    def getParentModel(DataElement dataElement){
        Model containingModel = getContainingModel(dataElement)
        if(containingModel.childOf) {
            return containingModel.childOf.first()
        }
        return null
    }

    def getValueDomain(DataElement dataElement){
        if(dataElement.instantiatedBy) {
            return dataElement.instantiatedBy.first()
        }
        return null
    }

    def getDataType(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            DataType dataType = valueDomain.dataType
            if (dataType instanceof EnumeratedType) {
                return dataType.enumAsString
            }
            return dataType.name
        }
        return null
    }

    def getUnitOfMeasure(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            MeasurementUnit unitOfMeasure = valueDomain?.unitOfMeasure
            return unitOfMeasure?.name
        }
        return null
    }
	


	
}
	

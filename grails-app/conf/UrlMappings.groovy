import java.rmi.Naming.ParsedNamingURL;

import org.springframework.security.access.AccessDeniedException

class UrlMappings {

	static mappings = {
		
		// Default for controllers
		"/$controller/$action?/$id?(.$format)?"{
			constraints {
				// apply constraints here
			}
		}

        "/pathways"(resources: "pathway")
		name pendingUsers: "/role/pendingUsers"( controller: "role", action: "listPendingUsers" )
        name importData: "/admin/importData"(controller: "dataImport")
        name importCOSD: "/admin/importCOSD"(controller:"COSDImporter")
        name importRelationships: "/admin/importRelationships"(controller:"relationshipImport")

        "/"(view:"/index")

        "/metadataCurator"(view: "metadataCurator/index")

		"403"(controller: "errors", action: "error403") 
		"404"(controller: "errors", action: "error404") 
		"500"(controller: "errors", action: "error500") 
		"500"(controller: "errors", action: "error403", exception: AccessDeniedException) 
	}
}

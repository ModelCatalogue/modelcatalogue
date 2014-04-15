import java.rmi.Naming.ParsedNamingURL;

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.acls.model.NotFoundException

class UrlMappings {

	static mappings = {
		
		// Default for controllers
		"/$controller/$action?/$id?(.$format)?"{
			constraints {
				// apply constraints here
			}
		}

        "/pathways"(resources: "pathway")

        // API endpoints
//        "/api/forms"(version:'1.0', resources:"form", namespace:'v1')
//       "/api/dataelements"(version:'1.0', resources:"dataElement", namespace:'v1')

		name pendingUsers: "/role/pendingUsers"( controller: "role", action: "listPendingUsers" )
		name importData: "/admin/importData"(controller: "dataImport")
		name importICU: "/admin/importICU"(controller: "excelImporter")
        name importCOSD: "/admin/importCOSD"(controller:"COSDImporter")

        "/"(view:"/index")

		"403"(controller: "errors", action: "error403") 
		"404"(controller: "errors", action: "error404") 
		"500"(controller: "errors", action: "error500") 
		"500"(controller: "errors", action: "error403", exception: AccessDeniedException) 
		"500"(controller: "errors", action: "error403", exception: NotFoundException) 
	}
}

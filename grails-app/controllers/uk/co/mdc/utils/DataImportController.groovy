package uk.co.mdc.utils

import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_ADMIN'])
class DataImportController {

	static allowedMethods = [importDataSet: "GET", ]
	
	def importNHICService

    def index(){
        [nhicFiles: importNHICService.getNhicFiles()]
    }
    def importDataSet() { 
		def dataset = params.dataset

		if(dataset == "nhic"){
            if(params.nhicFile){
                importNHICService.singleImport(params.nhicFile)
            }else{
                importNHICService.importData()
            }
            flash.message = "dataimport.complete"
            flash.default = "Process complete"
		}
        else{
            flash.message = "dataimport.paramError"
            flash.default = "Error: invalid dataset"
        }

        flash.args = [dataset]
		render(view:"/dataImport/index")
	}

}

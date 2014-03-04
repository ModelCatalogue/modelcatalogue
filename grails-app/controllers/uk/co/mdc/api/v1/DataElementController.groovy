package uk.co.mdc.api.v1

import grails.rest.RestfulController
import uk.co.mdc.model.DataElement

class DataElementController extends RestfulController{

    static namespace ="v1"

    DataElementController(){
        super(DataElement)
    }
}

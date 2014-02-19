package uk.co.mdc.forms.v1

import grails.rest.RestfulController
import uk.co.mdc.forms.FormDesign

class FormController extends RestfulController{

    static namespace ="v1"

    FormController(){
        super(FormDesign)
    }
}
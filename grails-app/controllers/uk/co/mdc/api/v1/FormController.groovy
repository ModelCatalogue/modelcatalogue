package uk.co.mdc.api.v1

import uk.co.mdc.forms.FormDesign

class FormController extends BetterRestfulController{

    static namespace ="v1"

    FormController(){
        super(FormDesign)
    }
}
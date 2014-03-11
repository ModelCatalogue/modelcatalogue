package uk.co.mdc.api.v1

import uk.ac.ox.brc.modelcatalogue.forms.FormDesign

class FormController extends BetterRestfulController{

    static namespace ="v1"

    FormController(){
        super(FormDesign)
    }
}
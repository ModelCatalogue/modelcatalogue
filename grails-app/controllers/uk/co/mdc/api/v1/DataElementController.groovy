package uk.co.mdc.api.v1

import org.modelcatalogue.core.DataElement

class DataElementController extends BetterRestfulController{

    static namespace ="v1"

    DataElementController(){
        super(DataElement)
    }
}

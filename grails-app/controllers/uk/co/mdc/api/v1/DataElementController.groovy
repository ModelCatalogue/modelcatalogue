package uk.co.mdc.api.v1

import uk.co.mdc.model.DataElement

class DataElementController extends BetterRestfulController{

    static namespace ="v1"

    DataElementController(){
        super(DataElement)
    }
}

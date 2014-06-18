package util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.reports.ReportDescriptor
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.marshalling.ExtendibleElementMarshallers
import org.springframework.beans.factory.annotation.Autowired

class DataElementMarshaller extends ExtendibleElementMarshallers {

    @Autowired ReportsRegistry reportsRegistry

    DataElementMarshaller() {
        super(DataElement)
    }

    protected void buildXml(element, XML xml) {
        super.buildXml(element, xml)
        def dt = getDataType(element)
        if (dt instanceof EnumeratedType) {
            xml.build {
                enumerations {
                    for (e in dt.enumerations) {
                        enumeration key: e.key, e.value
                    }
                }
            }
        }else{
            xml.build { dataType dt?.name }
        }
    }

    @Override
    protected getAvailableReports(CatalogueElement el) {
        def reports = []

        for (ReportDescriptor descriptor in reportsRegistry.getAvailableReports(el)) {
            reports << [title: descriptor.title, url: descriptor.getLink(el)]
        }

        reports
    }


    protected getContainingModel(DataElement dataElement){
        if(dataElement.containedIn) {
            return dataElement.containedIn.first()
        }
        return null
    }

    protected getParentModel(DataElement dataElement){
        Model containingModel = getContainingModel(dataElement)
        if(containingModel.childOf) {
            return containingModel.childOf.first()
        }
        return null
    }

    protected getValueDomain(DataElement dataElement){
        if(dataElement.instantiatedBy) {
            return dataElement.instantiatedBy.first()
        }
        return null
    }

    protected getDataType(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            DataType dataType = valueDomain.dataType
            return dataType
        }
        return null
    }

    protected getUnitOfMeasure(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            MeasurementUnit unitOfMeasure = valueDomain?.unitOfMeasure
            return unitOfMeasure?.name
        }
        return null
    }

}





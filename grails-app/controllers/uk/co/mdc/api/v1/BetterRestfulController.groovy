package uk.co.mdc.api.v1

import grails.rest.RestfulController

/**
 * A RestfulController with better support for paginated list results.
 *
 * The default Grails RestfulController (at least at 2.3.5) just returns a list of values for `index`.
 * When using this in an AJAX call you often need to specify `max` and `offset` values to paginate the data,
 * so this extension provides that information back, as well as the overall count.
 *
 * Created by rb on 05/03/2014.
 */
class BetterRestfulController<T> extends RestfulController<T>{

	BetterRestfulController(Class<T> resource) {
		this(resource, false)
	}

	BetterRestfulController(Class<T> resource, boolean readOnly) {
		super(resource, readOnly)
	}

	/**
	 * Return a simple object with metadata about the list
	 * @return
	 */
	def index(Integer max, Integer offset){
		params.max = Math.min(max ?: 10, 100)
		params.offset = offset ?: 0
		def returnValue = [
			objects: listAllResources(params),
			max: params.max,
			offset: params.offset,
			total: countResources()
		]

		respond returnValue as Object, model: [("${resourceName}Count".toString()): countResources()]
	}
}

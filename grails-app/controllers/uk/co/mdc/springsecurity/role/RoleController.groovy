package uk.co.mdc.springsecurity.role

import uk.co.mdc.SecAuth
import uk.co.mdc.SecUserSecAuth

class RoleController extends grails.plugins.springsecurity.ui.RoleController {
	def pendingRole = SecAuth.findByAuthority('ROLE_PENDING')
	def standardUserRole = SecAuth.findByAuthority('ROLE_USER')
	
	def listPendingUsers() {
		[pendingUsers: SecUserSecAuth.findAllBySecAuth(pendingRole)?.secUser]
	}
	

}

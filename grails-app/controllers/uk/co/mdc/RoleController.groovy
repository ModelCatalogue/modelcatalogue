package uk.co.mdc

class RoleController extends grails.plugin.springsecurity.ui.RoleController {


    def pendingRole = SecAuth.findByAuthority('ROLE_PENDING')
    def standardUserRole = SecAuth.findByAuthority('ROLE_USER')

    def listPendingUsers() {
        [pendingUsers: SecUserSecAuth.findAllBySecAuth(pendingRole)?.secUser]
    }



}

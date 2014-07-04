package uk.co.mdc

class RoleController extends grails.plugins.springsecurity.ui.RoleController {


    def pendingRole = SecAuth.findByAuthority('ROLE_READONLY_PENDING')
    def standardUserRole = SecAuth.findByAuthority('ROLE_READONLY_USER')

    def listPendingUsers() {
        [pendingUsers: SecUserSecAuth.findAllBySecAuth(pendingRole)?.secUser]
    }



}

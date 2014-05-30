package org.codehaus.groovy.grails.plugins.springsecurity.acl

class AclEntry {

	org.codehaus.groovy.grails.plugins.springsecurity.acl.AclObjectIdentity aclObjectIdentity
	int aceOrder
	org.codehaus.groovy.grails.plugins.springsecurity.acl.AclSid sid
	int mask
	boolean granting
	boolean auditSuccess
	boolean auditFailure

	@Override
	String toString() {
		"AclEntry id $id, aceOrder $aceOrder, mask $mask, granting $granting, " +
		"aclObjectIdentity $aclObjectIdentity"
	}

	static mapping = {
		version false
		sid column: 'sid'
		aclObjectIdentity column: 'acl_object_identity'
	}

	static constraints = {
		aceOrder unique: 'aclObjectIdentity'
	}
}

package org.picketlink.test.integration.util.serversetuptasks;

import java.util.HashMap;
import java.util.Map;

public class IDPSecurityDomainServerSetupTask {
	
	private static final String IDP = "idp";
	private static final String PICKETLINK_STS = "picketlink-sts";
	private static final String SP = "sp";
	private static final String AUTHENTICATOR = "authenticator";
	private static final String STS = "sts";
	
	protected static SecurityDomain IDP_DOMAIN = getIdp();
	protected static SecurityDomain PICKETLINK_STS_DOMAIN = getPicketlinkSts();
	protected static SecurityDomain SP_DOMAIN = getSp();
	protected static SecurityDomain AUTHENTICATOR_DOMAIN = getAuthenticator();
	protected static SecurityDomain STS_DOMAIN = getSts();
	
	public static SecurityDomain getIdp() {
		final Map<String, String> lmOptions = new HashMap<String, String>();
        final SecurityModule.Builder loginModuleBuilder = new SecurityModule.Builder().name("UsersRoles").options(lmOptions);
        lmOptions.put("usersProperties", "users.properties");
        lmOptions.put("rolesProperties", "roles.properties");
        final SecurityDomain idp = new SecurityDomain.Builder().name(IDP).loginModules(loginModuleBuilder.build()).build();
        return idp;
	}
	
	public static SecurityDomain getPicketlinkSts() {
		final Map<String, String> lmOptions = new HashMap<String, String>();
        final SecurityModule.Builder loginModuleBuilder1 = new SecurityModule.Builder().name("UsersRoles").options(lmOptions);
        lmOptions.put("usersProperties", "users.properties");
        lmOptions.put("rolesProperties", "roles.properties");
        final SecurityDomain picketlinkSts = new SecurityDomain.Builder().name(PICKETLINK_STS).loginModules(loginModuleBuilder1.build()).build();
        return picketlinkSts;
	}
	
	public static SecurityDomain getAuthenticator() {
		final Map<String, String> lmOptions = new HashMap<String, String>();
        final SecurityModule.Builder loginModuleBuilder = new SecurityModule.Builder().name("org.picketlink.test.trust.loginmodules.TestRequestUserLoginModule").options(lmOptions);
        lmOptions.put("usersProperties", "users.properties");
        lmOptions.put("rolesProperties", "roles.properties");
        final SecurityDomain authenticator = new SecurityDomain.Builder().name(AUTHENTICATOR).loginModules(loginModuleBuilder.build()).build();  
        return authenticator;
	}
	
	public static SecurityDomain getSp() {
		final Map<String, String> lmOptions = new HashMap<String, String>();
        final SecurityModule.Builder loginModuleBuilder = new SecurityModule.Builder().name("org.picketlink.identity.federation.bindings.jboss.auth.SAML2LoginModule").options(lmOptions);
        final SecurityDomain sp = new SecurityDomain.Builder().name(SP).loginModules(loginModuleBuilder.build()).build();
        return sp;
	}
	
	public static SecurityDomain getSts() {
		final Map<String, String> lmOptions = new HashMap<String, String>();
		final SecurityModule.Builder loginModuleBuilder1 = new SecurityModule.Builder().name("org.picketlink.identity.federation.bindings.jboss.auth.SAML2STSLoginModule").options(lmOptions);
        final Map<String, String> lmOptions2 = new HashMap<String, String>();
        final SecurityModule.Builder loginModuleBuilder2 = new SecurityModule.Builder().name("UsersRoles").options(lmOptions2);
        
        lmOptions.put("configFile", "sts-config.properties");
        lmOptions.put("password-stacking", "useFirstPass");
        
        lmOptions2.put("usersProperties", "users.properties");
        lmOptions2.put("rolesProperties", "roles.properties");
        lmOptions2.put("password-stacking", "useFirstPass");
        
        final SecurityDomain sts = new SecurityDomain.Builder().name(STS).loginModules(loginModuleBuilder1.build(),loginModuleBuilder2.build()).build();
        return sts;
	}

	public static class IdpDomain extends AbstractSecurityDomainsServerSetupTask {
		protected SecurityDomain[] getSecurityDomains() throws Exception {
			return new SecurityDomain[] { IDP_DOMAIN };
		}
	}
	
	public static class PicketlinkStsDomain extends AbstractSecurityDomainsServerSetupTask {
		protected SecurityDomain[] getSecurityDomains() throws Exception {
			return new SecurityDomain[] { PICKETLINK_STS_DOMAIN };
		}
	}
	
	public static class SpDomain extends AbstractSecurityDomainsServerSetupTask {
		protected SecurityDomain[] getSecurityDomains() throws Exception {
			return new SecurityDomain[] { SP_DOMAIN };
		}
	}
	
	public static class AuthenticatorDomain extends AbstractSecurityDomainsServerSetupTask {
		protected SecurityDomain[] getSecurityDomains() throws Exception {
			return new SecurityDomain[] { AUTHENTICATOR_DOMAIN };
		}
	}
	
	public static class StsDomain extends AbstractSecurityDomainsServerSetupTask {
		protected SecurityDomain[] getSecurityDomains() throws Exception {
			return new SecurityDomain[] { STS_DOMAIN };
		}
	}

	public static class IdpAndSpDomain extends AbstractSecurityDomainsServerSetupTask {
		protected SecurityDomain[] getSecurityDomains() throws Exception {
			return new SecurityDomain[] { IDP_DOMAIN, SP_DOMAIN};
		}
	}
	
}

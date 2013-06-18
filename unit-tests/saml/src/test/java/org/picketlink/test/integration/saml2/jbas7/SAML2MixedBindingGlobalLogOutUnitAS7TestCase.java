package org.picketlink.test.integration.saml2.jbas7;

import org.jboss.as.arquillian.api.ServerSetup;
import org.picketlink.test.integration.saml2.commons.SAML2MixedBindingGlobalLogOutUnitTestCase;
import org.picketlink.test.integration.util.serversetuptasks.IDPSecurityDomainServerSetupTask.IdpDomain;
import org.picketlink.test.integration.util.serversetuptasks.IDPSecurityDomainServerSetupTask.SpDomain;

@ServerSetup({ IdpDomain.class, SpDomain.class })
public class SAML2MixedBindingGlobalLogOutUnitAS7TestCase extends SAML2MixedBindingGlobalLogOutUnitTestCase {

}

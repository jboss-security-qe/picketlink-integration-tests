package org.picketlink.test.integration.saml11.jbas7;

import org.jboss.as.arquillian.api.ServerSetup;
import org.picketlink.test.integration.saml11.commons.SAML11IDPFirstUnitTestCase;
import org.picketlink.test.integration.util.serversetuptasks.SecurityDomainServerSetupTask.IdpDomain;
import org.picketlink.test.integration.util.serversetuptasks.SecurityDomainServerSetupTask.SpDomain;

@ServerSetup({ IdpDomain.class, SpDomain.class })
public class SAML11IDPFirstUnitAS7TestCase extends SAML11IDPFirstUnitTestCase {
	
}

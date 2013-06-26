package org.picketlink.test.integration.saml2.jbas7;

import org.jboss.as.arquillian.api.ServerSetup;
import org.picketlink.test.integration.saml2.commons.SAML2EncryptionUnitTestCase;
import org.picketlink.test.integration.util.serversetuptasks.SecurityDomainServerSetupTask.IdpDomain;
import org.picketlink.test.integration.util.serversetuptasks.SecurityDomainServerSetupTask.SpDomain;

@ServerSetup({ IdpDomain.class, SpDomain.class })
public class SAML2EncryptionUnitAS7TestCase extends SAML2EncryptionUnitTestCase {

}

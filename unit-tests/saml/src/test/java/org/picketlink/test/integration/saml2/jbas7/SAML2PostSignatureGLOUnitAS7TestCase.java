package org.picketlink.test.integration.saml2.jbas7;

import org.jboss.as.arquillian.api.ServerSetup;
import org.picketlink.test.integration.saml2.commons.SAML2PostSignatureGLOUnitTestCase;
import org.picketlink.test.integration.util.serversetuptasks.SecurityDomainServerSetupTask.IdpDomain;
import org.picketlink.test.integration.util.serversetuptasks.SecurityDomainServerSetupTask.SpDomain;

@ServerSetup({ IdpDomain.class, SpDomain.class })
public class SAML2PostSignatureGLOUnitAS7TestCase extends SAML2PostSignatureGLOUnitTestCase {

}

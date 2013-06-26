package org.picketlink.test.integration.sts.jbas7;

import org.jboss.as.arquillian.api.ServerSetup;
import org.picketlink.test.integration.sts.commons.PicketLinkSTSIntegrationUnitTestCase;
import org.picketlink.test.integration.util.serversetuptasks.SecurityDomainServerSetupTask.PicketlinkStsDomain;

@ServerSetup({ PicketlinkStsDomain.class })
public class PicketLinkSTSIntegrationUnitAS7TestCase extends PicketLinkSTSIntegrationUnitTestCase {

}

package org.jboss.aerogear.jaxrs.rest.test;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ALLOW_RESOURCE_SERVICE_RESTART;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.EXTENSION;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_HEADERS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ROLLBACK_ON_RUNTIME_FAILURE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.dmr.ModelNode;
import org.picketlink.test.integration.util.ModelUtil;

/**
 *
 * @author Hynek Mlnarik <hmlnarik@redhat.com>
 */
public class InstallPicketLinkExtensionSetupTask implements ServerSetupTask {

    private static final Logger LOG = Logger.getLogger(InstallPicketLinkExtensionSetupTask.class.getName());

    private static final PathAddress PICKETLINK_EXTENSION = PathAddress.pathAddress(PathElement.pathElement(EXTENSION,
            "org.wildfly.extension.picketlink"));

    private static final PathAddress PICKETLINK_IDENTITY_MANAGEMENT = PathAddress.pathAddress(PathElement.pathElement(
            SUBSYSTEM, "picketlink-identity-management"));

    @Override
    public void setup(ManagementClient managementClient, String containerId) throws Exception {
        staticSetup(managementClient);

        // FIXME reload is not working due to https://bugzilla.redhat.com/show_bug.cgi?id=900065
        // managementClient.getControllerClient().execute(ModelUtil.createOpNode(null, "reload"));
    }

    @Override
    public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
        staticTearDown(managementClient);

        // FIXME reload is not working due to https://bugzilla.redhat.com/show_bug.cgi?id=900065
        // managementClient.getControllerClient().execute(ModelUtil.createOpNode(null, "reload"));
    }

    public static void staticSetup(ManagementClient managementClient) throws Exception {
        LOG.log(Level.INFO, "Installing PicketLink extension into AS/EAP container.");

        ModelNode step1 = Util.createAddOperation(PICKETLINK_EXTENSION);
        allowServiceRestart(step1);

        boolean success = ModelUtil.execute(managementClient, step1);
        LOG.log(success ? Level.INFO : Level.WARNING, "Installing PicketLink extension into AS/EAP container {0}",
                new Object[] { success ? "passed" : "failed" });

        ModelNode step2 = Util.createAddOperation(PICKETLINK_IDENTITY_MANAGEMENT);
        allowServiceRestart(step2);

        success = ModelUtil.execute(managementClient, step2);
        LOG.log(success ? Level.INFO : Level.WARNING, "Installing PicketLink subsystem into AS/EAP container {0}",
                new Object[] { success ? "passed" : "failed" });
    }

    public static void staticTearDown(ManagementClient managementClient) throws Exception {
        LOG.log(Level.INFO, "Deinstalling PicketLink extension from AS/EAP container");

        ModelNode step1 = Util.createRemoveOperation(PICKETLINK_IDENTITY_MANAGEMENT);
        allowServiceRestart(step1);

        ModelNode step2 = Util.createRemoveOperation(PICKETLINK_EXTENSION);
        allowServiceRestart(step2);

        // remove picketlink subsystem
        ModelNode op = ModelUtil.createCompositeNode(step1, step2);

        boolean success = ModelUtil.execute(managementClient, op);
        LOG.log(success ? Level.INFO : Level.WARNING, "Deinstalling PicketLink extension from AS/EAP container {0}",
                new Object[] { success ? "passed" : "failed" });
    }

    // *
    private static void allowServiceRestart(ModelNode... ops) {
        for (ModelNode op : ops) {
            if (op != null) {
                op.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
                // Don't rollback when the AS detects the war needs the module
                op.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
            }
        }
    }
    // */
}

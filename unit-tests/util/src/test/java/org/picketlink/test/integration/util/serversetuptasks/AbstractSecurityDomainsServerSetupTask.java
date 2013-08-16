/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.test.integration.util.serversetuptasks;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ALLOW_RESOURCE_SERVICE_RESTART;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.COMPOSITE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_HEADERS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REMOVE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ROLLBACK_ON_RUNTIME_FAILURE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.STEPS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;

/**
 * {@link ServerSetupTask} instance for security domain setup. It supports JSSE configuration, JASPI authentication
 * configuration and stacks of login-modules (classic authentication), policy-modules and (role-)mapping-modules.
 *
 * @author Josef Cacek
 */
public abstract class AbstractSecurityDomainsServerSetupTask implements ServerSetupTask {

    private static final Logger LOGGER = Logger.getLogger(AbstractSecurityDomainsServerSetupTask.class);

    private static final String AUTH_MODULE = "auth-module";
    private static final String CLASSIC = "classic";
    private static final String FLAG = "flag";
    private static final String JSSE = "jsse";
    private static final String KEYSTORE = "keystore";
    //private static final String LOGIN_MODULE = "login-module";
    private static final String LOGIN_MODULE = "login-modules";
    private static final String MODULE_OPTIONS = "module-options";
    private static final String PASSWORD = "password";
    private static final String SECURITY_DOMAIN = "security-domain";
    private static final String TRUSTSTORE = "truststore";
    private static final String TYPE = "type";
    private static final String URL = "url";
    private static final String CACHE_TYPE = "cache-type";
    private static final String SUBSYSTEM_SECURITY = "security";
	private static final String AUTHENTICATION = "authentication";
	private static final String REQUIRED = "required";
	private static final String AUTHORIZATION = "authorization";
	private static final String POLICY_MODULE = "policy-module";
	private static final String MAPPING = "mapping";
	private static final String MAPPING_MODULE = "mapping-module";
	private static final String JASPI = "jaspi";
	private static final String MODULE = "module";
	private static final String LOGIN_MODULE_STACK_REF = "login-module-stack-ref";
	private static final String LOGIN_MODULE_STACK = "login-module-stack";
    
    
    /**
     * The type attribute value of mapping-modules used for role assignment.
     */
    private static final String ROLE = "role";
    /**
     * The SUBSYSTEM_SECURITY
     */


	protected ManagementClient managementClient;
    private SecurityDomain[] securityDomains;

    // Public methods --------------------------------------------------------

    /**
     * Adds a security domain represented by this class to the AS configuration.
     *
     * @param managementClient
     * @param containerId
     * @throws Exception
     * @see org.jboss.as.arquillian.api.ServerSetupTask#setup(org.jboss.as.arquillian.container.ManagementClient,
     *      java.lang.String)
     */
    public final void setup(final ManagementClient managementClient, String containerId) throws Exception {
        this.managementClient = managementClient;
        securityDomains = getSecurityDomains();

        if (securityDomains == null || securityDomains.length == 0) {
            LOGGER.warn("Empty security domain configuration.");
            return;
        }

        final List<ModelNode> updates = new LinkedList<ModelNode>();
        for (final SecurityDomain securityDomain : securityDomains) {
            final String securityDomainName = securityDomain.getName();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Adding security domain " + securityDomainName);
            }
            final ModelNode compositeOp = new ModelNode();
            compositeOp.get(OP).set(COMPOSITE);
            compositeOp.get(OP_ADDR).setEmptyList();
            ModelNode steps = compositeOp.get(STEPS);

            PathAddress opAddr = PathAddress.pathAddress()
                    .append(SUBSYSTEM, SUBSYSTEM_SECURITY)
                    .append(SECURITY_DOMAIN, securityDomainName);
            ModelNode op = Util.createAddOperation(opAddr);
            if (StringUtils.isNotEmpty(securityDomain.getCacheType())) {
                op.get(CACHE_TYPE).set(securityDomain.getCacheType());
            }
            steps.add(op);

            //only one can occur - authenticationType or authenticationJaspiType
            final boolean authNodeAdded = createSecurityModelNode(AUTHENTICATION, LOGIN_MODULE, FLAG,
                    REQUIRED, securityDomain.getLoginModules(), securityDomainName, steps);
            if (!authNodeAdded) {
                final List<ModelNode> jaspiAuthnNodes = createJaspiAuthnNodes(securityDomain.getJaspiAuthn(), securityDomain.getName());
                if (jaspiAuthnNodes != null) {
                    for (ModelNode node : jaspiAuthnNodes) {
                        steps.add(node);
                    }
                }
            }
            createSecurityModelNode(AUTHORIZATION, POLICY_MODULE, FLAG, REQUIRED, securityDomain.getAuthorizationModules(), securityDomainName, steps);
            createSecurityModelNode(MAPPING, MAPPING_MODULE, TYPE, ROLE, securityDomain.getMappingModules(), securityDomainName, steps);

            final ModelNode jsseNode = createJSSENode(securityDomain.getJsse(), securityDomain.getName());
            if (jsseNode != null) {
                steps.add(jsseNode);
            }
            updates.add(compositeOp);
        }
        applyUpdates(updates, managementClient.getControllerClient());
    }

    /**
     * Removes the security domain from the AS configuration.
     *
     * @param managementClient
     * @param containerId
     * @see org.jboss.as.test.integration.security.common.AbstractSecurityDomainSetup#tearDown(org.jboss.as.arquillian.container.ManagementClient,
     *      java.lang.String)
     */
    public final void tearDown(ManagementClient managementClient, String containerId) throws Exception {
        if (securityDomains == null || securityDomains.length == 0) {
            LOGGER.warn("Empty security domain configuration.");
            return;
        }

        final List<ModelNode> updates = new ArrayList<ModelNode>();
        for (final SecurityDomain securityDomain : securityDomains) {
            final String domainName = securityDomain.getName();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Removing security domain " + domainName);
            }
            final ModelNode op = new ModelNode();
            op.get(OP).set(REMOVE);
            op.get(OP_ADDR).add(SUBSYSTEM, "security");
            op.get(OP_ADDR).add(SECURITY_DOMAIN, domainName);
            // Don't rollback when the AS detects the war needs the module
            op.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
            op.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            updates.add(op);
        }
        applyUpdates(updates, managementClient.getControllerClient());

        this.managementClient = null;
    }
    
    public static void applyUpdates(final List<ModelNode> updates, final ModelControllerClient client) throws Exception {
        for (ModelNode update : updates) {
            applyUpdate(update, client);
        }
    }
    
    public static void applyUpdate(ModelNode update, final ModelControllerClient client) throws Exception {
        ModelNode result = client.execute(update);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Client update: " + update);
            LOGGER.info("Client update result: " + result);
        }
        if (result.hasDefined("outcome") && "success".equals(result.get("outcome").asString())) {
            LOGGER.debug("Operation succeeded.");
        } else if (result.hasDefined("failure-description")) {
            throw new RuntimeException(result.get("failure-description").toString());
        } else {
            throw new RuntimeException("Operation not successful; outcome = " + result.get("outcome"));
        }
    }

    // Protected methods -----------------------------------------------------

    /**
     * Returns configuration for creating security domains.
     *
     * @return array of SecurityDomain
     */
    protected abstract SecurityDomain[] getSecurityDomains() throws Exception;

    // Private methods -------------------------------------------------------

    /**
     * Creates authenticaton=>jaspi node and its child nodes.
     *
     * @param securityConfigurations
     * @return
     */
    private List<ModelNode> createJaspiAuthnNodes(JaspiAuthn securityConfigurations, String domainName) {
        if (securityConfigurations == null) {
            LOGGER.info("No security configuration for JASPI module.");
            return null;
        }
        if (securityConfigurations.getAuthnModules() == null || securityConfigurations.getAuthnModules().length == 0
                || securityConfigurations.getLoginModuleStacks() == null
                || securityConfigurations.getLoginModuleStacks().length == 0) {
            throw new IllegalArgumentException("Missing mandatory part of JASPI configuration in the security domain.");
        }

        final List<ModelNode> steps = new ArrayList<ModelNode>();

        PathAddress domainAddress = PathAddress.pathAddress()
                .append(SUBSYSTEM, SUBSYSTEM_SECURITY)
                .append(SECURITY_DOMAIN, domainName);
        PathAddress jaspiAddress = domainAddress.append(AUTHENTICATION, JASPI);
        steps.add(Util.createAddOperation(jaspiAddress));

        for (final AuthnModule config : securityConfigurations.getAuthnModules()) {
            LOGGER.info("Adding auth-module: " + config);
            final ModelNode securityModuleNode = Util.createAddOperation(jaspiAddress.append(AUTH_MODULE,config.getName()));
            steps.add(securityModuleNode);
            securityModuleNode.get(ModelDescriptionConstants.CODE).set(config.getName());
            if (config.getFlag() != null) {
                securityModuleNode.get(FLAG).set(config.getFlag());
            }
            if (config.getModule() != null) {
                securityModuleNode.get(MODULE).set(config.getModule());
            }
            if (config.getLoginModuleStackRef() != null) {
                securityModuleNode.get(LOGIN_MODULE_STACK_REF).set(config.getLoginModuleStackRef());
            }
            Map<String, String> configOptions = config.getOptions();
            if (configOptions == null) {
                LOGGER.info("No module options provided.");
                configOptions = Collections.emptyMap();
            }
            final ModelNode moduleOptionsNode = securityModuleNode.get(MODULE_OPTIONS);
            for (final Map.Entry<String, String> entry : configOptions.entrySet()) {
                final String optionName = entry.getKey();
                final String optionValue = entry.getValue();
                moduleOptionsNode.add(optionName, optionValue);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Adding module option [" + optionName + "=" + optionValue + "]");
                }
            }
        }
        //Unable to use securityComponentNode.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true) because the login-module-stack is empty 


        for (final LoginModuleStack lmStack : securityConfigurations.getLoginModuleStacks()) {
            PathAddress lmStackAddress = jaspiAddress.append(LOGIN_MODULE_STACK, lmStack.getName());
            steps.add(Util.createAddOperation(lmStackAddress));

            for (final SecurityModule config : lmStack.getLoginModules()) {
                final String code = config.getName();
                final ModelNode securityModuleNode = Util.createAddOperation(lmStackAddress.append(LOGIN_MODULE, code));

                final String flag = StringUtils.defaultIfEmpty(config.getFlag(), REQUIRED);
                securityModuleNode.get(ModelDescriptionConstants.CODE).set(code);
                securityModuleNode.get(FLAG).set(flag);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Adding JASPI login module stack [code=" + code + ", flag=" + flag + "]");
                }
                Map<String, String> configOptions = config.getOptions();
                if (configOptions == null) {
                    LOGGER.info("No module options provided.");
                    configOptions = Collections.emptyMap();
                }
                final ModelNode moduleOptionsNode = securityModuleNode.get(MODULE_OPTIONS);
                for (final Map.Entry<String, String> entry : configOptions.entrySet()) {
                    final String optionName = entry.getKey();
                    final String optionValue = entry.getValue();
                    moduleOptionsNode.add(optionName, optionValue);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Adding module option [" + optionName + "=" + optionValue + "]");
                    }
                }
                securityModuleNode.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
                steps.add(securityModuleNode);
            }

        }

        return steps;
    }

    /**
     * Creates a {@link ModelNode} with the security component configuration. If the securityConfigurations array is empty or
     * null, then null is returned.
     *
     * @param securityComponent name of security component (e.g. {@link Constants#AUTHORIZATION})
     * @param subnodeName       name of the security component subnode, which holds module configurations (e.g.
     *                          {@link Constants#POLICY_MODULES})
     * @param flagAttributeName name of attribute to which the value of {@link SecurityModule#getFlag()} is set
     * @param flagDefaultValue  default value for flagAttributeName attr.
     * @param securityModules   configurations
     * @return ModelNode instance or null
     */
    /*private boolean createSecurityModelNode(String securityComponent, String subnodeName, String flagAttributeName,
                                            String flagDefaultValue, final SecurityModule[] securityModules, String domainName, ModelNode operations) {
        if (securityModules == null || securityModules.length == 0) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("No security configuration for " + securityComponent + " module.");
            }
            return false;
        }
        PathAddress address = PathAddress.pathAddress()
                .append(SUBSYSTEM, SUBSYSTEM_SECURITY)
                .append(SECURITY_DOMAIN, domainName)
                .append(securityComponent, CLASSIC);
        operations.add(Util.createAddOperation(address));

        for (final SecurityModule config : securityModules) {
            final String code = config.getName();
            final ModelNode securityModuleNode = Util.createAddOperation(address.append(subnodeName, code));

            final String flag = StringUtils.defaultIfEmpty(config.getFlag(), flagDefaultValue);
            securityModuleNode.get(ModelDescriptionConstants.CODE).set(code);
            securityModuleNode.get(flagAttributeName).set(flag);
            Map<String, String> configOptions = config.getOptions();
            if (configOptions == null) {
                LOGGER.info("No module options provided.");
                configOptions = Collections.emptyMap();
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Adding " + securityComponent + " module [code=" + code + ", " + flagAttributeName + "=" + flag
                        + ", options = " + configOptions + "]");
            }
            final ModelNode moduleOptionsNode = securityModuleNode.get(MODULE_OPTIONS);
            for (final Map.Entry<String, String> entry : configOptions.entrySet()) {
                final String optionName = entry.getKey();
                final String optionValue = entry.getValue();
                moduleOptionsNode.add(optionName, optionValue);
            }
            securityModuleNode.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            operations.add(securityModuleNode);
        }
        return true;
    }*/
    
    private boolean createSecurityModelNode(String securityComponent, String subnodeName, String flagAttributeName,
            String flagDefaultValue, final SecurityModule[] securityModules, String domainName, ModelNode operations) {
		if (securityModules == null || securityModules.length == 0) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("No security configuration for " + securityComponent + " module.");
			}
			return false;
		}		
		
		PathAddress address = PathAddress.pathAddress()
                .append(SUBSYSTEM, SUBSYSTEM_SECURITY)
                .append(SECURITY_DOMAIN, domainName)
                .append(securityComponent, CLASSIC);  
		final ModelNode securityModuleNode = Util.createAddOperation(address);
        
        for (final SecurityModule config : securityModules) {           
                        
            ModelNode loginModule = securityModuleNode.get("login-modules").add();
            
            final String code = config.getName();
            final String flag = StringUtils.defaultIfEmpty(config.getFlag(), flagDefaultValue);
        	loginModule.get(ModelDescriptionConstants.CODE).set(code);
        	loginModule.get(flagAttributeName).set(flag);        	
        	
        	//ModelNode moduleOptions = loginModule.get("module-options");
            

            Map<String, String> configOptions = config.getOptions();
            if (configOptions == null) {
                LOGGER.info("No module options provided.");
                configOptions = Collections.emptyMap();
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Adding " + securityComponent + " module [code=" + code + ", " + flagAttributeName + "=" + flag
                        + ", options = " + configOptions + "]");
            }
            //final ModelNode moduleOptionsNode = securityModuleNode.get(MODULE_OPTIONS);
            final ModelNode moduleOptionsNode = loginModule.get(MODULE_OPTIONS);
            for (final Map.Entry<String, String> entry : configOptions.entrySet()) {
                final String optionName = entry.getKey();
                final String optionValue = entry.getValue();
                moduleOptionsNode.add(optionName, optionValue);
            }
            securityModuleNode.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            //operations.add(securityModuleNode);
        }
        operations.add(securityModuleNode);
        return true;
    }
        

    /**
     * Creates a {@link ModelNode} with configuration of the JSSE part of security domain.
     *
     * @param jsse
     * @param domainName
     * @return
     */
    private ModelNode createJSSENode(final JSSE jsse, String domainName) {
        if (jsse == null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("No security configuration for JSSE module.");
            }
            return null;
        }
        final ModelNode securityComponentNode = new ModelNode();
        securityComponentNode.get(OP).set(ADD);
        securityComponentNode.get(OP_ADDR).add(SUBSYSTEM, SUBSYSTEM_SECURITY);
        securityComponentNode.get(OP_ADDR).add(SECURITY_DOMAIN, domainName);

        securityComponentNode.get(OP_ADDR).add(JSSE, CLASSIC);
        addSecureStore(jsse.getTrustStore(), TRUSTSTORE, securityComponentNode);
        addSecureStore(jsse.getKeyStore(), KEYSTORE, securityComponentNode);
        securityComponentNode.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        return securityComponentNode;
    }

    /**
     * Adds given secureStore to a JSSE configuration represented by given ModelNode.
     *
     * @param secureStore
     * @param storeName
     * @param jsseNode
     */
    private void addSecureStore(SecureStore secureStore, String storeName, ModelNode jsseNode) {
        if (secureStore == null) {
            return;
        }
        if (secureStore.getUrl() != null) {
            jsseNode.get(storeName, URL).set(secureStore.getUrl().toExternalForm());
        }
        if (secureStore.getPassword() != null) {
            jsseNode.get(storeName, PASSWORD).set(secureStore.getPassword());
        }
        if (secureStore.getType() != null) {
            jsseNode.get(storeName, TYPE).set(secureStore.getType());
        }
    }
}

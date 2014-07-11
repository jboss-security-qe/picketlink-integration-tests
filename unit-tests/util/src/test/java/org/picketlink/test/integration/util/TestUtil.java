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

package org.picketlink.test.integration.util;

import org.apache.commons.lang.StringUtils;
import static org.picketlink.test.integration.util.PicketLinkConfigurationUtil.addKeyStoreAlias;
import static org.picketlink.test.integration.util.PicketLinkConfigurationUtil.addValidatingAlias;

import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class TestUtil {

    private static final String TEST_CONTAINER_BIND_ADDRESS = "test.container.bind.address";
    private static final String TEST_CONTAINER_BIND_HTTP_PORT = "test.container.bind.http.port";
    private static final String TEST_PICKETLINK21_COMPATIBILITY_RUN = "test.picketlink21.compatibility.run";

    public static String getTargetURL(String uri) {
        return "http://" + getServerAddress() + ":" + System.getProperty(TEST_CONTAINER_BIND_HTTP_PORT, "8080") + uri;
    }

    public static String getServerAddress() {
        return System.getProperty(TEST_CONTAINER_BIND_ADDRESS, "localhost");
    }

    public static WebArchive createSTSDeployment() {
        if (Boolean.getBoolean("picketlink.sts.skipDeploy")) {
            return null;
        }
        WebArchive sts = MavenArtifactUtil.getQuickstartsMavenArchive("picketlink-sts");

        addValidatingAlias(sts, "/WEB-INF/classes/picketlink.xml", getServerAddress(), getServerAddress());
        addKeyStoreAlias(sts, "/WEB-INF/classes/sts_keystore.jks", "sts", "testpass", getServerAddress());

        return sts;
    }

    /**
     * Returns true if the current execution is done for checking backward
     * compatibility of PicketLink 2.5 with older PL 2.1.
     * <p>
     * This is done by checking whether system property {@code test.picketlink21.compatibility.run}
     * is set to {@code true}.
     */
    public static boolean isPicketLink21CompatibilityRun() {
        return StringUtils.equalsIgnoreCase("true", System.getProperty(TEST_PICKETLINK21_COMPATIBILITY_RUN));
    }

}

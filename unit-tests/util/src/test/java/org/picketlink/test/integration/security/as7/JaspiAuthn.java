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
package org.picketlink.test.integration.security.as7;

/**
 * A simple config holder for JASPIC authentication in a security domain.
 * 
 * @author Josef Cacek
 */
public class JaspiAuthn {

    private final LoginModuleStack[] loginModuleStacks;
    private final AuthnModule[] authnModules;

    // Constructors ----------------------------------------------------------
    /**
     * Create a new JaspiAuthn.
     * 
     * @param builder
     */
    private JaspiAuthn(Builder builder) {
        this.loginModuleStacks = builder.loginModuleStacks;
        this.authnModules = builder.authnModules;
    }

    // Public methods --------------------------------------------------------

    /**
     * Get the loginModuleStacks.
     * 
     * @return the loginModuleStacks.
     */
    public LoginModuleStack[] getLoginModuleStacks() {
        return loginModuleStacks;
    }

    /**
     * Get the authnModules.
     * 
     * @return the authnModules.
     */
    public AuthnModule[] getAuthnModules() {
        return authnModules;
    }

    // Embedded classes ------------------------------------------------------

    public static class Builder {
        private LoginModuleStack[] loginModuleStacks;
        private AuthnModule[] authnModules;

        public Builder loginModuleStacks(LoginModuleStack... loginModuleStacks) {
            this.loginModuleStacks = loginModuleStacks;
            return this;
        }

        public Builder authnModules(AuthnModule... authnModules) {
            this.authnModules = authnModules;
            return this;
        }

        public JaspiAuthn build() {
            return new JaspiAuthn(this);
        }
    }

}

package org.jboss.aerogear.jaxrs.rest.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.picketbox.ldap.EmbeddedApacheDS;

public class LDAPTestUtil {

    protected String adminPW = "secret";
    protected String dn = "dc=jboss,dc=org";
    protected String adminDN = "uid=admin,ou=system";
    protected String port = "10389";

    protected String serverHost = "localhost";

    protected EmbeddedApacheDS ds = null;

    public LDAPTestUtil(int port) {
        this.port = Integer.toString(port);
    }

    public void setup(File workDir) throws Exception {
        if (workDir == null)
            throw new IllegalArgumentException("WorkDir for LDAP can't be null");

        deleteDir(workDir);
        workDir.mkdirs();

        ds = new EmbeddedApacheDS(workDir);
        ds.disableShutdownhook(); // Verbose
        ds.createBaseDN("apache", "dc=apache,dc=org");

        createBaseDN();

        long current = System.currentTimeMillis();
        System.out.println("Starting Apache DS server");
        ds.startServer();

        System.out.println("Time taken = " + (System.currentTimeMillis() - current) + "milisec");
    }

    public void tearDown(File workDir) throws Exception {
        if (ds != null) {
            ds.stopServer();
        }
        String tempDir = System.getProperty("java.io.tmpdir");
        System.out.println("java.io.tmpdir=" + tempDir);

        System.out.println("Going to delete the server-work directory");
        deleteDir(workDir);
    }

    private void deleteDir(File workDir) {
        try {
            FileUtils.deleteDirectory(workDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a base DN
     * 
     * @param partitionName
     * @param dn
     * @throws Exception
     */
    public void createBaseDN(String partitionName, String dn) throws Exception {
        ds.createBaseDN(partitionName, dn);
    }

    /**
     * Override to supply your own admin password
     *
     * @return
     */
    protected String getAdminPW() {
        return "secret";
    }

    /**
     * Mandatory method that needs to be overridden This class creates a base dn "dc=jboss,dc=org"
     *
     * @throws Exception
     */
    protected void createBaseDN() throws Exception {
        ds.createBaseDN("jboss", "dc=jboss,dc=org");
    }

    public void importLDIF(String fileName) throws Exception {
        long current = System.currentTimeMillis();
        System.out.println("Going to import LDIF:" + fileName);

        InputStream is = null;
        try {
            is = new FileInputStream(new File(fileName));
            ds.importLdif(is);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load LDAP database", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load LDAP database", e);
            }
        }
        System.out.println("Time taken = " + (System.currentTimeMillis() - current) + "milisec");
    }
}

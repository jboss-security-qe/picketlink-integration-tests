package org.picketlink.test.integration.saml2;

import static org.picketlink.test.integration.util.TestUtil.getTargetURL;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public abstract class AbstractSAMLIntegrationTests {

    String IDP_URL = getTargetURL("/idp/");
    static String IDP_SIG_URL = getTargetURL("/idp-sig/");

    protected String SALES_POST_URL = getTargetURL("/sales-post/");
    String SALES_POST_SIG_URL = getTargetURL("/sales-post-sig/");
    protected String SALES_POST_VALVE_URL = getTargetURL("/sales-post-valve/");

    protected String EMPLOYEE_REDIRECT_URL = getTargetURL("/employee/");
    String EMPLOYEE_REDIRECT_SIG_URL = getTargetURL("/employee-sig/");
    protected String EMPLOYEE_REDIRECT_VALVE_URL = getTargetURL("/employee-redirect-valve/");

    protected String LOGOUT_URL = "?GLO=true";

}

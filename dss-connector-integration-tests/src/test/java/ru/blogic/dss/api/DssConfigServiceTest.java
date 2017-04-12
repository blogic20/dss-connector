package ru.blogic.dss.api;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.websphere.security.auth.callback.WSCallbackHandlerImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.cert.X509Certificate;

/**
 * @author dgolubev
 */
@RunWith(Arquillian.class)
public class DssConfigServiceTest {

    @Deployment
    public static EnterpriseArchive createDeployment() {
        final EnterpriseArchive arch = ShrinkWrap.create(EnterpriseArchive.class, "dss-service-test.ear")
                .addAsModule(ShrinkWrap.create(JavaArchive.class, "dss-service-impl.jar")
                        .addPackages(true, "ru.blogic.dss")

                        .addClass("com.sun.xml.ws.api.security.trust.client.IssuedTokenManager")
                        .addClass("com.sun.xml.wss.jaxws.impl.DssSecurityClientTube")

                        .addAsManifestResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
                        .addAsManifestResource("META-INF/beans.xml", "beans.xml")
                        .addAsManifestResource("META-INF/metro-default.xml", "metro-default.xml")
                        .addAsManifestResource("META-INF/wsit-client.xml", "wsit-client.xml")

                        .addAsManifestResource("META-INF/0.xsd", "0.xsd")
                        .addAsManifestResource("META-INF/1.xsd", "1.xsd")
                        .addAsManifestResource("META-INF/2.xsd", "2.xsd")
                        .addAsManifestResource("META-INF/3.xsd", "3.xsd")
                        .addAsManifestResource("META-INF/4.xsd", "4.xsd")

                        .addAsManifestResource("META-INF/Active.wsdl", "Active.wsdl")
                        .addAsManifestResource("META-INF/LECM-STS.wsdl", "LECM-STS.wsdl")

                        .addAsManifestResource("META-INF/SignService.policy.wsdl", "SignService.policy.wsdl")
                        .addAsManifestResource("META-INF/SignService.wsdl", "SignService.wsdl")

                        .addAsManifestResource("META-INF/UserManagement.0.xsd", "UserManagement.0.xsd")
                        .addAsManifestResource("META-INF/UserManagement.1.xsd", "UserManagement.1.xsd")
                        .addAsManifestResource("META-INF/UserManagement.2.xsd", "UserManagement.2.xsd")
                        .addAsManifestResource("META-INF/UserManagement.3.xsd", "UserManagement.3.xsd")
                        .addAsManifestResource("META-INF/UserManagement.4.xsd", "UserManagement.4.xsd")
                        .addAsManifestResource("META-INF/UserManagement.5.xsd", "UserManagement.5.xsd")

                        .addAsManifestResource("META-INF/UserManagement.policy.wsdl", "UserManagement.policy.wsdl")
                        .addAsManifestResource("META-INF/UserManagement.wsdl", "UserManagement.wsdl")
                        .addAsManifestResource("META-INF/UserManagement.xjb", "UserManagement.xjb")

                        .addAsManifestResource("META-INF/VerificationService.policy.wsdl", "VerificationService.policy.wsdl")
                        .addAsManifestResource("META-INF/VerificationService.wsdl", "VerificationService.wsdl")
                ).addAsLibraries(
                        Maven.resolver().loadPomFromFile("dss-service-integration-tests/pom.xml").importCompileAndRuntimeDependencies()
                                .resolve("ru.blogic.dss:dss-service-impl").withTransitivity().asFile()
                ).add(new ClassLoaderAsset("application/deployment.xml"), "/deployment.xml");
        System.out.println(arch.toString(true));
        return arch;
    }


    @Before
    public void getSubject() {
        LoginContext lc;

        try {
            lc = new LoginContext("WSLogin", new WSCallbackHandlerImpl("gcd_admin@rshbank.ru", "o9p0[-]="));
        } catch (LoginException le) {
            System.out.println("Cannot create LoginContext. " + le.getMessage());
            throw new RuntimeException(le);
        } catch (SecurityException se) {
            System.out.println("Cannot create LoginContext." + se.getMessage());
            throw new RuntimeException(se);
        }

        try {
            lc.login();
        } catch (LoginException le) {
            System.out.println("Failed to create Subject. " + le.getMessage());
            throw new RuntimeException(le);
        }

        final Subject subject = lc.getSubject();
        try {
            WSSubject.setRunAsSubject(subject);
        } catch (WSSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject
    private DssConfigService dssConfigService;

    @Test
    public void testConfig() throws Exception {
        final X509Certificate cert = dssConfigService.getDssCertificate();
        System.out.println(cert.getIssuerX500Principal().getName());
    }
}

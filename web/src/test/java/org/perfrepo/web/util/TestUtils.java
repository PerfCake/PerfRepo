package org.perfrepo.web.util;

import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestUtils {

    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");

        // we need to exclude the actual UserSessionBean and substitute it with test one
        war.addPackages(true, Filters.exclude(".*UserSessionBean.*"),"org.perfrepo.web");
        war.addClass(UserSessionMock.class);
        war.addPackages(true, "org.perfrepo.dto");
        war.addPackages(true, "org.perfrepo.enums");

        war.addAsLibraries(Maven.resolver().resolve("commons-codec:commons-codec:1.9").withTransitivity().asFile());
        war.addAsLibraries(Maven.resolver().resolve("org.antlr:antlr:3.5.2").withTransitivity().asFile());
        war.addAsLibraries(Maven.resolver().resolve("org.apache.maven:maven-artifact:3.0.3").withTransitivity().asFile());

        war.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
        war.addAsResource("test-beans.xml", "META-INF/beans.xml");
        war.addAsWebInfResource("test-ejb-jar.xml", "ejb-jar.xml");
        war.addAsWebInfResource(new File("src/main/webapp/WEB-INF/classes/ValidationMessages.properties"), "classes/ValidationMessages.properties");
        war.addAsResource(new File("src/main/resources/lang"));

        return war;
    }
}

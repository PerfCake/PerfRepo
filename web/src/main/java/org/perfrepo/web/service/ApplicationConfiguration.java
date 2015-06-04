package org.perfrepo.web.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Globally scoped bean used for loading configuration from properties file.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@ApplicationScoped
public class ApplicationConfiguration {

   private static final String CONFIGURATION_FILE = "app_config.properties";

   private String url;

   @PostConstruct
   public void init() {
      Properties properties = new Properties();

      try {
         properties.load(this.getClass().getClassLoader().getResourceAsStream(CONFIGURATION_FILE));
      } catch (IOException ex) {
         throw new IllegalStateException("Error while opening configuration file.", ex);
      }

      url = properties.getProperty("application.url");
   }

   public String getUrl() {
      return url;
   }
}

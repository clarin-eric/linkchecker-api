/**
 * @author Wolfgang Walter SAUER (wowasa) &lt;clarin@wowasa.com&gt;
 *
 */
package eu.clarin.linkchecker.web.conf;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 */
@Configuration
public class LcCorsConfig {
   
   @Autowired
   Environment env;
   
   @Bean
   public WebMvcConfigurer corsMappingConfigurer() {
      return new WebMvcConfigurer() {
          @Override
          public void addCorsMappings(CorsRegistry registry) {
              if(StringUtils.isNotBlank(env.getProperty("web.cors.allowed-origins"))) {
                 registry.addMapping("/**")
                   .allowedOrigins(env.getProperty("web.cors.allowed-origins"));
              }
          }
      };
   }
}

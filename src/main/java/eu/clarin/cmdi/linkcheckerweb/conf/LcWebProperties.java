/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
@ConfigurationProperties(prefix = "linkckecker-web")
public class LcWebProperties {
   
   private String address;
   
   private String adminUsername;
   
   private String adminPassword;

}

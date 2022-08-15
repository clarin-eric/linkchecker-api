/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.conf;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import eu.clarin.cmdi.cpa.model.Role;
import eu.clarin.cmdi.cpa.model.Client;
import eu.clarin.cmdi.cpa.repository.ClientRepository;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Configuration
@EnableConfigurationProperties(LcWebProperties.class)
public class LcAdminRegistration {
   
   @Autowired
   private LcWebProperties props;
   @Autowired
   private ClientRepository usRep;
   @Autowired
   private PasswordEncoder pwEncoder;

   
   
   @PostConstruct
   public void createAdmin() {
      
      usRep.findByName(props.getAdminUsername()).orElseGet(() -> {
         Client client = new Client(props.getAdminUsername(), pwEncoder.encode(props.getAdminPassword()), Role.ADMIN);
         client.setEnabled(true);
         
         return usRep.save(client);
      });     
   }
}

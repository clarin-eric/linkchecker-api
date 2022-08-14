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
import eu.clarin.cmdi.cpa.model.User;
import eu.clarin.cmdi.cpa.repository.UserRepository;

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
   private UserRepository usRep;
   @Autowired
   private PasswordEncoder pwEncoder;

   
   
   @PostConstruct
   public void createAdmin() {
      
      usRep.findByName(props.getAdminUsername()).orElseGet(() -> {
         return usRep.save(new User(props.getAdminUsername(), pwEncoder.encode(props.getAdminPassword()), Role.ADMIN));
      });     
   }
}

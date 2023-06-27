/**
 * 
 */
package eu.clarin.linkchecker.api.conf;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import eu.clarin.linkchecker.persistence.model.Role;
import eu.clarin.linkchecker.persistence.model.Client;
import eu.clarin.linkchecker.persistence.repository.ClientRepository;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Configuration
public class LcAdminRegistration {
   
   @Value("${spring.security.user.name}")
   private String username;
   @Value("${spring.security.user.password}")
   private String password;
   @Autowired
   private ClientRepository usRep;
   @Autowired
   private PasswordEncoder pwEncoder;

   
   
   @PostConstruct
   public void createAdmin() {     
      
      usRep.findByName(username).ifPresentOrElse(client -> {
         
         client.setPassword(pwEncoder.encode(password)); //change password
         
         usRep.save(client);         
      }, () -> {
         Client client = new Client(username, pwEncoder.encode(password), Role.ADMIN);
         client.setEnabled(true);
         
         usRep.save(client);
      });    
   }
}

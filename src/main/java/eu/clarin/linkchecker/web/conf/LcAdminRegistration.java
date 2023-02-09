/**
 * 
 */
package eu.clarin.linkchecker.web.conf;

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
      
      usRep.findByName(username).orElseGet(() -> {
         Client client = new Client(username, pwEncoder.encode(password), Role.ADMIN);
         client.setEnabled(true);
         
         return usRep.save(client);
      });     
   }
}
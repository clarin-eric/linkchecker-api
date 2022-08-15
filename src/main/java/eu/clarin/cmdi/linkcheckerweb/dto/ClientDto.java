/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.dto;

import eu.clarin.cmdi.cpa.model.Role;
import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
public class ClientDto {
   
   private Long id;
   
   private String username;
   
   private String password;
   
   private String email;
   
   private Boolean enabled;
   
   private Long quota;
   
   private Role role;

}

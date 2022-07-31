/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.dto;

import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
public class ClientDto {
   
   private Long id;
   
   private String email;
   
   private String token;
   
   private Long quota;

}

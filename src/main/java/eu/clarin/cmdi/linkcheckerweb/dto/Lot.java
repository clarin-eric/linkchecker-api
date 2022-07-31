/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.dto;

import java.util.Collection;

import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
public class Lot {
   
   private String email;
   
   private String token;
   
   private Collection<LTC> ltcs;

}

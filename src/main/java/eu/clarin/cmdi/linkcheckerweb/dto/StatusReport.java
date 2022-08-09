/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.dto;

import java.time.LocalDateTime;
import java.util.Collection;

import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
public class StatusReport {
   
   private LocalDateTime creationDate;
   
   private Long numberOfLinks;
   
   Collection<CheckedLink> checkedLinks;

}

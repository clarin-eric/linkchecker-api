/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
public class StatusReport {
   
   private LocalDateTime creationDate;
   
   @JsonProperty("checked-links")
   Collection<CheckedLink> checkedLinks = new ArrayList<CheckedLink>();

}

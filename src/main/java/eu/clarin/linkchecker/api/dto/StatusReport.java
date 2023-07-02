/**
 * 
 */
package eu.clarin.linkchecker.api.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
public class StatusReport {
   
   @Schema(description = "creation date/time of the report", nullable = false, type = "string", format = "date-time", example = "2022-10-02T19:00:20")
   private LocalDateTime creationDate = LocalDateTime.now();
   
   @Schema(description = "a list of link checking results", nullable = false, type = "array")
   @JsonProperty("checked-links")
   Collection<CheckedLink> checkedLinks = new ArrayList<CheckedLink>();

}

/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
@AllArgsConstructor
public class CheckedLink {
   
   private String url;
   
   private String method;
   @JsonProperty("status-code")
   private Integer statusCode;
   @JsonProperty("content-type")
   private String contentType;
   @JsonProperty("content-length")
   private Long contentLength;
   
   private Integer duration;
   @JsonProperty("checking-date")
   private LocalDateTime checkingDate;
   
   private String message;
   
}

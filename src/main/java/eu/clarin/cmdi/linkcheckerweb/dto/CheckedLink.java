/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.dto;

import java.time.LocalDateTime;

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
   
   private Integer statusCode;
   
   private String contentType;
   
   private Long contentLength;
   
   private Integer duration;
   
   private LocalDateTime checkingDate;
   
   private String message;
   
}

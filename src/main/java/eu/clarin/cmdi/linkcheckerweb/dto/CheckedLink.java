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
public class CheckedLink {
   
   private String url;
   
   private String batchId;
   
   private String method;
   
   private Integer statusCode;
   
   private String contentType;
   
   private Long contentLength;
   
   private Integer duration;
   
   private String message;
   
}

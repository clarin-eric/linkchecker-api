/**
 * 
 */
package eu.clarin.linkchecker.api.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.clarin.linkchecker.persistence.utils.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
@AllArgsConstructor
public class CheckedLink {
   
   @Schema(description = "checked URL", nullable = false, type = "string", format = "uri", example = "http://www.wowasa.com/page1")   
   private String url;
   @Schema(description = "HEAD or GET request", nullable = false, type = "string", example = "GET")
   private String method;
   @Schema(description = "Http status code", nullable = true, type = "integer", example = "401")
   @JsonProperty("status-code")
   private Integer statusCode;
   @Schema(description = "content-type from returned header", nullable = true, type = "string", example = "text/html; charset=iso-8859-1")
   @JsonProperty("content-type")
   private String contentType;
   @Schema(description = "content-length from returned header - most commonly null in case of Https requests", nullable = true, type = "integer", example = "465")
   @JsonProperty("content-length")
   private Long contentLength;
   @Schema(description = "time in ms between request and response - in case of redirects only the time of the final request/response", nullable = true, type = "integer", example = "10")
   private Integer duration;
   @Schema(description = "date/time of the the check", nullable = false, type = "string", format = "date-time", example = "2022-10-02T19:00:20")
   @JsonProperty("checking-date")
   private LocalDateTime checkingDate;
   @Schema(description = "the category deppending on the status-code", nullable = false, type = "string", example = "Restricted_Access")
   private Category category;
   @Schema(description = "message returned from server or generated message", nullable = false, type = "string", example = "Restricted access, Status code: 401")
   private String message;
}

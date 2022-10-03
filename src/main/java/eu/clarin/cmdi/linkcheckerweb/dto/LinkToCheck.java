/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
public class LinkToCheck {
   
   @Schema(description = "the URL to be checked", required = true, nullable = false, type = "string", format = "uri", example = "http://www.wowasa.com/page1")
   String url;
   @Schema(description = "expected mime type of the resource - saved but not processed so far", required = false, nullable = true, type = "string", example = "application/text")
   String expectedMimeType;

}

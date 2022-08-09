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
public class LinkToCheck {
   
   String url;
   
   String expectedMimeType;

}

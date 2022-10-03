/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.dto;

import eu.clarin.cmdi.cpa.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
public class ClientDto {
   
   @Schema(description = "the client id - generated automatically at creation time", required = false, type = "integer", example = "1")
   private Long id;
   @Schema(description = "unique client name", required = true, nullable = false, type = "string", example = "wowasa")
   private String name;
   @Schema(description = "the password for the client - generated automatically at creation time", required = false, type = "String", example = "1234-5678")
   private String password;
   @Schema(description = "client's email address - might be used for notifications later", required = false, type = "string", example = "devnull@wowasa.com")
   private String email;
   @Schema(description = "enable/disable client access", required = false, type = "boolean", example = "true")
   private Boolean enabled;
   @Schema(description = "a quota for the amount of links a client can upload", required = false, type = "integer", example = "5000")
   private Long quota;
   @Schema(description = "ADMIN or USER role - only ADMIN can manage clients", required = false, type = "string", example = "USER")
   private Role role;

}

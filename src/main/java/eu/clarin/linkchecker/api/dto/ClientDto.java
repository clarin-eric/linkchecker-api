/**
 * 
 */
package eu.clarin.linkchecker.api.dto;

import eu.clarin.linkchecker.persistence.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Data
public class ClientDto {
   
   @Schema(description = "the client id - generated automatically at creation time", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "integer", example = "1")
   private Long id;
   @Schema(description = "unique client name", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", example = "wowasa")
   private String name;
   @Schema(description = "the password for the client - generated automatically at creation time", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "String", format = "password", example = "1234-5678")
   private String password;
   @Schema(description = "client's email address - might be used for notifications later", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", format = "email", example = "devnull@wowasa.com")
   private String email;
   @Schema(description = "enable/disable client access", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "boolean", example = "true")
   private Boolean enabled;
   @Schema(description = "a quota for the amount of links a client can upload", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "integer", example = "5000")
   private Long quota;
   @Schema(description = "ADMIN or USER role - only ADMIN can manage clients", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "USER")
   private Role role;

}

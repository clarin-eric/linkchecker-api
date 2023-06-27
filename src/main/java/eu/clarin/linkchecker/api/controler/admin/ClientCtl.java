/**
 * 
 */
package eu.clarin.linkchecker.api.controler.admin;

import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.linkchecker.api.dto.ClientDto;
import eu.clarin.linkchecker.api.exception.ClientNotFoundException;
import eu.clarin.linkchecker.persistence.model.Client;
import eu.clarin.linkchecker.persistence.model.Role;
import eu.clarin.linkchecker.persistence.repository.ClientRepository;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@RestController()
@RequestMapping(path = "/admin/client")
public class ClientCtl {

   @Autowired
   ClientRepository clRep;
   @Autowired
   PasswordEncoder pwEncoder;

   @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @ResponseStatus(code = HttpStatus.CREATED)
   @Operation(summary = "create new client", description = "create new client with the specific data - the id- and the password-field are irgnored if set, since they are generated automatically")
   public ClientDto createClient(@RequestBody ClientDto clientDto) {

      return createNewClient(clientDto);
   }

   @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(
      summary = "update client data", 
      description = "update client data as name, email, etc - the field id is not updatetabale, a new password is generated with parameter updatepw=true"
   )
   public ClientDto updateClient(@RequestBody ClientDto userDto,
         @RequestParam(name = "updatepw", required = false, defaultValue = "false") Boolean updatePw) {

      return updateExistingClient(userDto, updatePw);
   }

   @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(
      summary = "get a list of all clients", 
      description = "get a list of all clients - no password's are shown"
   )
   public Stream<ClientDto> getAllClients() {

      return StreamSupport.stream(clRep.findAll().spliterator(), false).map(client -> {
         ClientDto clientDto = new ClientDto();
         clientDto.setId(client.getId());
         clientDto.setName(client.getName());
         clientDto.setPassword("*****");
         clientDto.setEmail(client.getEmail());
         clientDto.setEnabled(client.getEnabled());
         clientDto.setQuota(client.getQuota());
         clientDto.setRole(client.getRole());

         return clientDto;
      });
   }

   private ClientDto createNewClient(ClientDto clientDto) {
      clientDto.setPassword(UUID.randomUUID().toString());
      clientDto.setRole(Role.USER);

      Client client = new Client(clientDto.getName(), pwEncoder.encode(clientDto.getPassword()), clientDto.getRole());
      client.setEmail(clientDto.getEmail());
      client.setQuota(clientDto.getQuota());
      client.setEnabled(true);

      clRep.save(client);
      clientDto.setId(client.getId());

      return clientDto;

   }

   private ClientDto updateExistingClient(ClientDto clientDto, boolean newPassword) {

      return clRep.findById(clientDto.getId()).map(client -> {

         if (clientDto.getName() != null) {
            client.setName(clientDto.getName());
         }
         else {
            clientDto.setName(client.getName());
         }

         if (clientDto.getEmail() != null) {
            client.setEmail(clientDto.getEmail());
         }
         else {
            clientDto.setEmail(client.getEmail());
         }
         if (clientDto.getQuota() != null) {
            client.setQuota(clientDto.getQuota());
         }
         else {
            clientDto.setQuota(client.getQuota());
         }
         if (newPassword) {
            clientDto.setPassword(UUID.randomUUID().toString());
            client.setPassword(pwEncoder.encode(clientDto.getPassword()));
         }
         else {
            clientDto.setPassword("*****");
         }
         if (clientDto.getEnabled() != null) {
            client.setEnabled(clientDto.getEnabled());
         }
         else {
            clientDto.setEnabled(client.getEnabled());
         }
         if(clientDto.getRole() != null) {
            client.setRole(clientDto.getRole());
         }
         else {
            clientDto.setRole(client.getRole());
         }

         clRep.save(client);

         return clientDto;
      }).orElseThrow(() -> new ClientNotFoundException("client with id " + clientDto.getId() + " doesn't exist"));
   }
}

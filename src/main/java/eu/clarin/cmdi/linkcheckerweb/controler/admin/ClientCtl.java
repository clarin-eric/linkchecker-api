/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.controler.admin;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.cmdi.cpa.repository.ClientRepository;
import eu.clarin.cmdi.linkcheckerweb.dto.ClientDto;
import eu.clarin.cmdi.linkcheckerweb.service.ClientService;


/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@RestController
@RequestMapping(path = "/admin/client")
public class ClientCtl {
   
   @Autowired
   ClientRepository clRep;
   @Autowired
   ClientService clService;
   
   
   @PostMapping
   @ResponseStatus(code = HttpStatus.CREATED)
   public ClientDto createClient(@RequestBody ClientDto clientDto) {

      return clService.createNewClient(clientDto);
   }
   
   @PutMapping()
   public ClientDto updateClient(@RequestBody ClientDto userDto, @RequestParam(name = "updatepw", required = false, defaultValue = "false") Boolean updatePw) {
      
      return clService.updateClient(userDto, updatePw);
   }
   
   
     @GetMapping() 
     public Stream<ClientDto> getAllClients(){
     
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
}

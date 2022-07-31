/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.controler.admin;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.cmdi.cpa.model.Client;
import eu.clarin.cmdi.cpa.repository.ClientRepository;
import eu.clarin.cmdi.linkcheckerweb.dto.ClientDto;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@RestController
@RequestMapping(path = "/admin/client", produces = "application/json")
public class ClientCtl {
   
   @Autowired
   ClientRepository clRep;
   
   @PostMapping(consumes = "application/json")
   @ResponseStatus(code = HttpStatus.CREATED)
   public Client addClient(@RequestBody ClientDto clientDto) {
      
      Client client = new Client(clientDto.getEmail(), UUID.randomUUID().toString());
      client.setQuota(clientDto.getQuota());
      
      return clRep.save(client);
   }
   
   @PutMapping(value = "/{id}", consumes = "application/json")
   public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody ClientDto clientDto) {
      
      if(clRep.findById(id).isPresent()) {
         Client client = clRep.findById(id).get();
         
         client.setEmail(clientDto.getEmail());
         client.setToken(clientDto.getToken());
         client.setQuota(clientDto.getQuota());
         
         return new ResponseEntity<Client>(clRep.save(client), HttpStatus.OK);
      };
     return new ResponseEntity<Client>((Client) null, HttpStatus.NOT_FOUND); 
   }
   
   
     @GetMapping() public Iterable<Client> getAllClients(){
     
     return clRep.findAll(); 
     
     }
}

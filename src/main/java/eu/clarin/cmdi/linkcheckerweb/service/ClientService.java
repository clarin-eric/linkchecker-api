/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import eu.clarin.cmdi.cpa.model.Client;
import eu.clarin.cmdi.cpa.model.Role;
import eu.clarin.cmdi.cpa.repository.ClientRepository;
import eu.clarin.cmdi.linkcheckerweb.dto.ClientDto;
import eu.clarin.cmdi.linkcheckerweb.exception.ClientNotFoundException;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Service
public class ClientService {
   
   @Autowired
   ClientRepository clRep;
   @Autowired
   PasswordEncoder pwEncoder;
   
   public ClientDto createNewClient(ClientDto clientDto) {
      clientDto.setPassword(UUID.randomUUID().toString());
      clientDto.setRole(Role.USER);
      
      Client client = new Client(clientDto.getName(), pwEncoder.encode(clientDto.getPassword()), clientDto.getRole());
      client.setQuota(clientDto.getQuota());
      client.setEnabled(true);
      
      clRep.save(client);    
      clientDto.setId(client.getId());

      return clientDto;
   
   }
   
   public ClientDto updateClient(ClientDto clientDto, boolean newPassword) {
      
      return clRep.findById(clientDto.getId()).map(client -> {
         
         if(clientDto.getName() != null) {
            client.setName(clientDto.getName());
         }
         else {
            clientDto.setName(client.getName());
         }
         
         if(clientDto.getEmail() != null) {
            client.setEmail(clientDto.getEmail());
         }
         else {
            clientDto.setEmail(client.getEmail());
         }
         if(clientDto.getQuota() != null) {
            client.setQuota(clientDto.getQuota());
         }
         else {
            clientDto.setQuota(client.getQuota());
         }
         if(newPassword) {
            clientDto.setPassword(UUID.randomUUID().toString());
            client.setPassword(pwEncoder.encode(clientDto.getPassword()));
         }
         else {
            clientDto.setPassword("*****");
         }
         if(clientDto.getEnabled()!= null) {
            client.setEnabled(clientDto.getEnabled());
         }
         else {
            clientDto.setEnabled(client.getEnabled());
         }
         
         clRep.save(client);
         
         return clientDto;
      })
      .orElseThrow(() -> new ClientNotFoundException("client with id " + clientDto.getId() + " doesn't exist"));
   
   }
}

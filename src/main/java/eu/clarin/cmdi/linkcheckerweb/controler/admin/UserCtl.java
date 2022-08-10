/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.controler.admin;

import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.cmdi.cpa.model.Role;
import eu.clarin.cmdi.cpa.model.User;
import eu.clarin.cmdi.cpa.repository.UserRepository;
import eu.clarin.cmdi.linkcheckerweb.dto.UserDto;


/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@RestController
@RequestMapping(path = "/admin/user")
public class UserCtl {
   
   @Autowired
   UserRepository usRep;
   @Autowired
   PasswordEncoder pwEncoder;
   
   
   @PostMapping
   @ResponseStatus(code = HttpStatus.CREATED)
   public UserDto createUser(@RequestBody UserDto userDto) {
      
      userDto.setPassword(UUID.randomUUID().toString());
      userDto.setRole(Role.USER);
      
      User user = new User(userDto.getUsername(), pwEncoder.encode(userDto.getPassword()), userDto.getRole());
      user.setQuota(userDto.getQuota());
      
      usRep.save(user);    
      userDto.setId(user.getId());

      return userDto;
   }
   
   @PutMapping()
   public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
      
      return usRep.findById(userDto.getId()).map(user -> {
         
         user.setEmail(userDto.getEmail());
         user.setQuota(user.getQuota());
         
         usRep.save(user);
         
         return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
      })
      .orElseGet(() -> new ResponseEntity<UserDto>(userDto, HttpStatus.NOT_FOUND));
   }
   
   
     @GetMapping() 
     public Stream<UserDto> getAllUsers(){
     
        return StreamSupport.stream(usRep.findAll().spliterator(), false).map(user -> {
           UserDto userDto = new UserDto();
           userDto.setId(user.getId());
           userDto.setUsername(user.getUsername());
           userDto.setPassword("####");
           userDto.setEmail(user.getEmail());
           userDto.setQuota(user.getQuota());
           userDto.setRole(user.getRole());
           
           return userDto;
        });
     }     
}

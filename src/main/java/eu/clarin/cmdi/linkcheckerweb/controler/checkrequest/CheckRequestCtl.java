/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.controler.checkrequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.cmdi.cpa.model.Role;
import eu.clarin.cmdi.cpa.model.User;
import eu.clarin.cmdi.cpa.repository.UserRepository;
import eu.clarin.cmdi.cpa.service.LinkService;
import eu.clarin.cmdi.linkcheckerweb.dto.StatusReport;
import eu.clarin.cmdi.linkcheckerweb.dto.LinkToCheck;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@RestController
@RequestMapping(path = "/checkrequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces =  MediaType.APPLICATION_JSON_VALUE)
public class CheckRequestCtl {
   
   @Autowired
   UserRepository clRep;
   @Autowired
   LinkService lService;
   
   @GetMapping(value = "/{batch-id}")
   public StatusReport getResults(@RequestHeader("username") String username, @RequestHeader("token") String token, @PathVariable(required = false) String batch) {
      

      
      return null;
   }
   
   @PostMapping
   public void upload(@RequestHeader("username") String username, @RequestHeader("token") String token, @RequestBody Collection<LinkToCheck> ltcs) {
      //TODO: 
      User user = new User("dummy", "xx", Role.ADMIN);
      

      
      //check if the array size exceeds the quota
      if(user.getQuota() != null && ltcs.size() < user.getQuota()) {
         user.setQuota(user.getQuota() - ltcs.size());
         clRep.save(user);
      }
      
      LocalDateTime now = LocalDateTime.now();
      final String origin = "upload_" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
      
      ltcs.forEach(ltc -> {
         lService.save(user, ltc.getUrl(), origin, null, ltc.getExpectedMimeType(), now);
      });
   }
   

   
//   @DeleteMapping(value = "/{id}")
   

}

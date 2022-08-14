/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.controler.checkrequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.cmdi.cpa.model.Status;
import eu.clarin.cmdi.linkcheckerweb.dto.StatusReport;
import eu.clarin.cmdi.linkcheckerweb.exception.BatchToLargeException;
import eu.clarin.cmdi.linkcheckerweb.service.RepositoryService;
import eu.clarin.cmdi.linkcheckerweb.dto.CheckedLink;
import eu.clarin.cmdi.linkcheckerweb.dto.LinkToCheck;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@RestController
@RequestMapping(path = "/checkrequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces =  MediaType.APPLICATION_JSON_VALUE)
public class CheckRequestCtl {
   
   @Autowired
   RepositoryService rService;
   
   @GetMapping(value = "/{batch-id}")
   public StatusReport getResults(Authentication auth, @PathVariable(required = false) String batchId) {
      
      final StatusReport report = new StatusReport();
      report.setCreationDate(LocalDateTime.now());
      
      try(Stream<Status> stream = rService.findAllStatus(auth.getName(), batchId)){
         
         stream.forEach(status -> report.getCheckedLinks().add(
               new CheckedLink(
                     status.getUrl().getName(),
                     status.getMethod(), 
                     status.getStatusCode(),
                     status.getContentType(),
                     status.getContentLength(),
                     status.getDuration(), 
                     status.getCheckingDate(),
                     status.getMessage()
               )
            ));
      }
      catch(Exception ex) {
         
      }
      
      return report;
   }
   
   @PostMapping
   public ResponseEntity<String> upload(Authentication auth, @RequestBody Collection<LinkToCheck> ltcs) {
      
      String message; 
      
      try {
         message = rService.saveLTCs(auth.getName(), ltcs);
      }
      catch(BatchToLargeException ex) {         
         return new ResponseEntity<String>(ex.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
      }
      catch(Exception ex) {
         return new ResponseEntity<String>("batch upload failed - please contact clarin", HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return ResponseEntity.ok(message);
   }
   

   
   @DeleteMapping(value = "/{urlId}")
   public void deleteUrl(Authentication auth, @PathVariable Long urlId) {
      
      
      
   }
   

}

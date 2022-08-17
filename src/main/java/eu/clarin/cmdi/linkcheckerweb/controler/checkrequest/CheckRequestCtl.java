/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.controler.checkrequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.cmdi.cpa.model.Status;
import eu.clarin.cmdi.cpa.repository.StatusRepository;
import eu.clarin.cmdi.linkcheckerweb.dto.StatusReport;
import eu.clarin.cmdi.linkcheckerweb.exception.BatchToLargeException;
import eu.clarin.cmdi.linkcheckerweb.service.LinkService;
import lombok.extern.slf4j.Slf4j;
import eu.clarin.cmdi.linkcheckerweb.dto.CheckedLink;
import eu.clarin.cmdi.linkcheckerweb.dto.LinkToCheck;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Slf4j
@RestController
@RequestMapping(path = "/")
public class CheckRequestCtl {
   
   @Autowired
   StatusRepository sRep;
   @Autowired
   LinkService lService;
   
   @Transactional
   @GetMapping(value = {"/checkrequest", "/checkrequest/{batchId}"})
   public ResponseEntity<StatusReport> getResults(Authentication auth, @PathVariable Optional<String> batchId) {
      
      final StatusReport report = new StatusReport();
      report.setCreationDate(LocalDateTime.now());
      
      try(Stream<Status> stream = (batchId.isEmpty()?sRep.findAllByUrlUrlContextsContextClientName(auth.getName())
            :sRep.findAllByUrlUrlContextsContextClientNameAndUrlUrlContextsContextOrigin(auth.getName(), batchId.get()))){
         
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
         log.error("exception in status download", ex);
         log.error("clientname: {}, batchId: {}", auth.getName(), batchId);
         
         return new ResponseEntity<StatusReport>(report, HttpStatus.INTERNAL_SERVER_ERROR);
      }
      
      return ResponseEntity.ok(report);
   }
   
   @PostMapping("/checkrequest")
   public ResponseEntity<String> upload(Authentication auth, @RequestBody Collection<LinkToCheck> ltcs) {
      
      String message; 
      
      try {
         message = lService.saveLTCs(auth.getName(), ltcs);
      }
      catch(BatchToLargeException ex) {         
         return new ResponseEntity<String>(ex.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
      }
      catch(Exception ex) {
         return new ResponseEntity<String>("batch upload failed - please contact clarin", HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return ResponseEntity.ok(message);
   }
}

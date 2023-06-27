/**
 * 
 */
package eu.clarin.linkchecker.api.controler.checkrequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.linkchecker.api.dto.CheckedLink;
import eu.clarin.linkchecker.api.dto.LinkToCheck;
import eu.clarin.linkchecker.api.dto.StatusReport;
import eu.clarin.linkchecker.api.exception.BatchToLargeException;
import eu.clarin.linkchecker.persistence.model.*;
import eu.clarin.linkchecker.persistence.repository.*;
import eu.clarin.linkchecker.persistence.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Slf4j
@RestController
@RequestMapping(path = "/")
public class CheckRequestCtl {
   
   @Autowired
   private StatusRepository sRep;
   @Autowired
   private ClientRepository usRep;
   @Autowired
   LinkService lService;
   
   @Transactional
   @GetMapping(value = "/checkrequest", produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(
      summary = "receive list of checking results", 
      description = "returns a list of all checking results for the logged-in in client"
   )
   public ResponseEntity<StatusReport> getResults(Authentication auth){
      
      return getResults(auth, null);
   }
   
   @Transactional
   @GetMapping(value = "/checkrequest/{batchId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(
      summary = "receive list of checking results for specific upload", 
      description = "returns a list of all checking results of a specific upload for the logged-in in client"
   )
   public ResponseEntity<StatusReport> getResults(Authentication auth, @PathVariable String batchId) {
      
      final StatusReport report = new StatusReport();
      report.setCreationDate(LocalDateTime.now());
      
      try(Stream<Status> stream = (batchId==null?sRep.findAllByUrlUrlContextsContextClientName(auth.getName())
            :sRep.findAllByUrlUrlContextsContextClientNameAndUrlUrlContextsContextOrigin(auth.getName(), batchId))){
         
         stream.forEach(status -> report.getCheckedLinks().add(
               new CheckedLink(
                     status.getUrl().getName(),
                     status.getMethod(), 
                     status.getStatusCode(),
                     status.getContentType(),
                     status.getContentLength(),
                     status.getDuration(), 
                     status.getCheckingDate(),
                     status.getCategory(),
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
   
   @PostMapping(value = "/checkrequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(
      summary = "upload URLs to check", 
      description = "upload an array of URLs to check"
   )
   public ResponseEntity<String> upload(Authentication auth, @RequestBody Collection<LinkToCheck> ltcs) {
      
      String message; 
      
      try {
         message = saveLTCs(auth.getName(), ltcs);
      }
      catch(BatchToLargeException ex) {         
         return new ResponseEntity<String>(ex.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
      }
      catch(Exception ex) {
         return new ResponseEntity<String>("batch upload failed - please contact clarin", HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return ResponseEntity.ok(message);
   }
   
   @Transactional
   private String saveLTCs(String username,  Collection<LinkToCheck> ltcs) throws BatchToLargeException{
      
      Client client = usRep.findByName(username).get();
      
      //check if the array size exceeds the quota
      if(client.getQuota() != null) {
         if(ltcs.size() < client.getQuota()) {

            client.setQuota(client.getQuota() - ltcs.size());
            usRep.save(client);
         }
         else {
            throw new BatchToLargeException("the batch size is " + ltcs.size() + " but your remaining upload limit is " + client.getQuota());
         }
      }
      
      final LocalDateTime now = LocalDateTime.now();
      final String origin = "upload_" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));     
      
      Collection<Pair<String,String>> urlMimes = ltcs.stream().map(ltc -> Pair.of(ltc.getUrl(), ltc.getExpectedMimeType())).collect(Collectors.toList());
      
      lService.savePerOrigin(client, null, origin, urlMimes, 10);
      
      return origin;
   }
}

/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.controler.checkrequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import eu.clarin.cmdi.cpa.model.Client;
import eu.clarin.cmdi.cpa.model.Context;
import eu.clarin.cmdi.cpa.model.Status;
import eu.clarin.cmdi.cpa.model.Url;
import eu.clarin.cmdi.cpa.model.UrlContext;
import eu.clarin.cmdi.cpa.repository.ClientRepository;
import eu.clarin.cmdi.cpa.repository.ContextRepository;
import eu.clarin.cmdi.cpa.repository.StatusRepository;
import eu.clarin.cmdi.cpa.repository.UrlContextRepository;
import eu.clarin.cmdi.cpa.repository.UrlRepository;
import eu.clarin.cmdi.cpa.utils.Category;
import eu.clarin.cmdi.cpa.utils.UrlValidator;
import eu.clarin.cmdi.cpa.utils.UrlValidator.ValidationResult;
import eu.clarin.cmdi.linkcheckerweb.dto.StatusReport;
import eu.clarin.cmdi.linkcheckerweb.exception.BatchToLargeException;
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
   private UrlRepository uRep;
   @Autowired
   private UrlContextRepository ucRep;
   @Autowired
   private ContextRepository cRep;
   @Autowired
   private StatusRepository sRep;
   @Autowired
   private ClientRepository usRep;
   
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
      
      ltcs.forEach(ltc -> {
         
         final String urlName = ltc.getUrl().trim();
         
         Url url = uRep.findByName(urlName)
               .map(u -> {
                  // if the URL has already a status, flag it for immediate recheck 
                  sRep.findByUrl(u).ifPresent(status -> {
                     
                     status.setRecheck(true);
                     sRep.save(status);
                  });
                  
                  return u;
               })
               .orElseGet(() -> {
            
                  ValidationResult validation = UrlValidator.validate(urlName);
                  
                  Url newUrl = uRep.save(new Url(urlName, validation.getHost(), validation.isValid()));
                  
                  if(!validation.isValid() && sRep.findByUrl(newUrl).isEmpty()) { //create a status entry if Url is not valid
                     Status status = new Status(newUrl, Category.Invalid_URL, validation.getMessage(), LocalDateTime.now());
                     
                     sRep.save(status);
                  }
                  return newUrl;
               });
         
         Context context = cRep.findByOriginAndProvidergroupAndClient(origin, null, client)
               .orElseGet(() -> cRep.save(new Context(origin, null, client)));                
            
         
         UrlContext urlContext = new UrlContext(url, context, now, true);
         urlContext.setExpectedMimeType(ltc.getExpectedMimeType());
         
         ucRep.save(urlContext);   
         

      });  
      
      return origin;
   }
}

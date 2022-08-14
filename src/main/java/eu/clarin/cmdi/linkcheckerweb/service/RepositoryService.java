/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.clarin.cmdi.cpa.model.Context;
import eu.clarin.cmdi.cpa.model.Obsolete;
import eu.clarin.cmdi.cpa.model.Status;
import eu.clarin.cmdi.cpa.model.Url;
import eu.clarin.cmdi.cpa.model.UrlContext;
import eu.clarin.cmdi.cpa.model.User;
import eu.clarin.cmdi.cpa.repository.ContextRepository;
import eu.clarin.cmdi.cpa.repository.ObsoleteRepository;
import eu.clarin.cmdi.cpa.repository.StatusRepository;
import eu.clarin.cmdi.cpa.repository.UrlContextRepository;
import eu.clarin.cmdi.cpa.repository.UrlRepository;
import eu.clarin.cmdi.cpa.repository.UserRepository;
import eu.clarin.cmdi.cpa.service.StatusService;
import eu.clarin.cmdi.cpa.utils.Category;
import eu.clarin.cmdi.cpa.utils.UrlValidator;
import eu.clarin.cmdi.cpa.utils.UrlValidator.ValidationResult;
import eu.clarin.cmdi.linkcheckerweb.dto.LinkToCheck;
import eu.clarin.cmdi.linkcheckerweb.exception.BatchToLargeException;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Service
public class RepositoryService {
   
   @Autowired
   private UrlRepository uRep;
   @Autowired
   private UrlContextRepository ucRep;
   @Autowired
   private ContextRepository cRep;
   @Autowired
   private StatusService sService;
   @Autowired
   private StatusRepository sRep;
   @Autowired
   private UserRepository usRep;
   @Autowired
   private ObsoleteRepository oRep;

   
   @Transactional
   public String saveLTCs(String username,  Collection<LinkToCheck> ltcs) throws BatchToLargeException{
      
      User user = usRep.findByName(username).get();
      
      //check if the array size exceeds the quota
      if(user.getQuota() != null) {
         if(ltcs.size() < user.getQuota()) {

            user.setQuota(user.getQuota() - ltcs.size());
            usRep.save(user);
         }
         else {
            throw new BatchToLargeException("the batch size is " + ltcs.size() + " but your remaining upload limit is " + user.getQuota());
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
                  
                  Url newUrl = new Url(urlName, validation.getHost(), validation.isValid());
                  
                  if(!validation.isValid()) { //create a status entry if Url is not valid
                     Status status = new Status(newUrl, Category.Invalid_URL, validation.getMessage(), LocalDateTime.now());
                     
                     sService.save(status);
                  }
                  return newUrl;
               });
         
         Context context = cRep.findByOriginAndProvidergroupAndExpectedMimeTypeAndUser(origin, null, ltc.getExpectedMimeType(), user)
               .orElseGet(() -> cRep.save(new Context(origin, null, ltc.getExpectedMimeType(), user)));                
            
         ucRep.save(new UrlContext(url, context, now, true));             

      });  
      
      return origin;
   }
   
   public Stream<Status> findAllStatus(String username, String batchId){
        
      
      return (batchId!=null?sRep.findAllByUrlUrlContextsContextUserName(username)
            :sRep.findAllByUrlUrlContextsContextUserNameAndUrlUrlContextsContextOrigin(username, batchId));
   }
   
   @Transactional
   public void deleteUrl(String username, Long urlId) {
      
      uRep.findById(urlId).ifPresent(url -> {
         
         url.getUrlContexts().stream().filter(urlContext -> urlContext.getContext().getUser().getName().equals(username))
            .findFirst()
            .ifPresent(urlContext -> {
               if(url.getStatus() != null) { //first save status info
                  
                  Obsolete obsolete = new Obsolete(
                        url.getName(),
                        url.getStatus().getCategory(),
                        url.getStatus().getMessage(),
                        url.getStatus().getCheckingDate()
                     );
                  
                  oRep.save(obsolete);
                  
               }
            });
      });
      
      
      

      
   }
}

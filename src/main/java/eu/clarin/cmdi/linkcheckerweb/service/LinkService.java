/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.clarin.cmdi.cpa.model.Context;
import eu.clarin.cmdi.cpa.model.Status;
import eu.clarin.cmdi.cpa.model.Url;
import eu.clarin.cmdi.cpa.model.UrlContext;
import eu.clarin.cmdi.cpa.model.Client;
import eu.clarin.cmdi.cpa.repository.ContextRepository;
import eu.clarin.cmdi.cpa.repository.StatusRepository;
import eu.clarin.cmdi.cpa.repository.UrlContextRepository;
import eu.clarin.cmdi.cpa.repository.UrlRepository;
import eu.clarin.cmdi.cpa.repository.ClientRepository;
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
public class LinkService {
   
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
   public String saveLTCs(String username,  Collection<LinkToCheck> ltcs) throws BatchToLargeException{
      
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

/**
 * @author Wolfgang Walter SAUER (wowasa) &lt;clarin@wowasa.com&gt;
 *
 */
package eu.clarin.linkchecker.api.controler;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.linkchecker.api.dto.CheckedLink;
import eu.clarin.linkchecker.api.dto.LinkToCheck;
import eu.clarin.linkchecker.api.dto.StatusReport;
import eu.clarin.linkchecker.persistence.repository.HistoryRepository;

/**
 *
 */
@RestController
@RequestMapping("/history")
public class HistoryCtl {
   
   @Autowired
   private HistoryRepository hRep;
   
   @PostMapping
   @Transactional
   public ResponseEntity<StatusReport> getStatus(@RequestBody Collection<LinkToCheck> ltcs){
      
      String[] urlNames = new String[ltcs.size()];
      
      AtomicInteger index = new AtomicInteger();
      
      ltcs.stream().forEach(ltc -> urlNames[index.getAndIncrement()] = ltc.getUrl());
      
      final StatusReport report = new StatusReport();
      
      hRep.findAllByUrlNameIn(urlNames).forEach(history -> report.getCheckedLinks().add(
            new CheckedLink(
                  history.getUrl().getName(),
                  history.getMethod(), 
                  history.getStatusCode(),
                  history.getContentType(),
                  history.getContentLength(),
                  history.getDuration(), 
                  history.getCheckingDate(),
                  history.getCategory(),
                  history.getMessage(), 
                  history.getRedirectCount()
            )
         ));
      
      return ResponseEntity.ok(report);
   }

}

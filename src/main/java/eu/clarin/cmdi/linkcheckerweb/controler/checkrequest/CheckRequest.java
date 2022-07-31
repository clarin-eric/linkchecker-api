/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.controler.checkrequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.clarin.cmdi.linkcheckerweb.dto.CheckResults;
import eu.clarin.cmdi.linkcheckerweb.dto.Lot;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@RestController
@RequestMapping(path = "/checkrequest")
public class CheckRequest {
   
   @GetMapping(value = "/{id}")
   public CheckResults getResults(@PathVariable("id") Long id) {
      
      return null;
   }
   
   @PostMapping
   public void upload(@RequestBody Lot lot) {
      
   }

}

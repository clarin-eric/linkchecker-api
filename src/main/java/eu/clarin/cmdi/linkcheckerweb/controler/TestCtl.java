/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.controler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@RestController
@RequestMapping(path = "/test")
public class TestCtl {
   
   @GetMapping
   public String test() {
      return "hello world!";
   }

}

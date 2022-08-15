/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.exception;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
public class ClientNotFoundException extends RuntimeException{
   
   private static final long serialVersionUID = 1L;

   public ClientNotFoundException(String message) {
      super(message);
   }

}

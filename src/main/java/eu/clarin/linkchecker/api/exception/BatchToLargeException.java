/**
 * 
 */
package eu.clarin.linkchecker.api.exception;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
public class BatchToLargeException extends Exception {

   private static final long serialVersionUID = 1L;
   
   
   public BatchToLargeException(String message) {
      
      super(message);
      
   }
}

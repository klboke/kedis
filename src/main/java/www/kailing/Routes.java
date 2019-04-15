package www.kailing;

import io.netty.handler.codec.http.HttpMethod;
import org.restexpress.RestExpress;

/**
 * @author: kl @kailing.pub
 * @date: 2019/4/12
 */
public abstract class Routes {
   public static void define(RestExpress server,KedisCore core){
       server.uri("/put", core).action("put", HttpMethod.GET).noSerialization();
       server.uri("/get", core).action("get", HttpMethod.GET).noSerialization();
   }
}

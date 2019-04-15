package www.kailing;

import org.restexpress.RestExpress;

/**
 * @author: kl @kailing.pub
 * @date: 2019/4/12
 */
public class Main {

    public static void main(String[] args) {
        Configs configs = new Configs();
        configs.fromArgs(args);
        RestExpress server = new RestExpress()
                .setName("kedis-server")
                .setBaseUrl("http://localhost:" +configs.getPort());
        KedisCore core =new KedisCore(configs.getDbPath());
        Routes.define(server,core);
        server.bind(configs.getPort());
        server.awaitShutdown();
    }
}

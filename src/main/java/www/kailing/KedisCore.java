package www.kailing;

import org.restexpress.Request;
import org.restexpress.Response;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.Map;

/**
 * @author: kl @kailing.pub
 * @date: 2019/4/12
 */
public class KedisCore {

    private RocksDB db;

    public KedisCore(String path) {
        RocksDB.loadLibrary();
        try {
            final Options options = new Options().setCreateIfMissing(true);
            this.db = RocksDB.open(options, path);
        } catch (RocksDBException ex) {
            ex.printStackTrace();
        }
    }

    public String put(Request request, Response response) throws Exception {
        Map<String, String> map = request.getQueryStringMap();
        String key = map.get("key");
        String value = map.get("value");
        db.put(key.getBytes(), value.getBytes());
        return "ok";
    }

    public String get(Request request, Response response) throws Exception {
        Map<String, String> map = request.getQueryStringMap();
        String key = map.get("key");
        byte[] values = db.get(key.getBytes());
        if(values != null){
            return new String(values,"utf-8");
        }else {
           return null;
        }
    }
}

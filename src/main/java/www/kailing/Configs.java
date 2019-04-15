package www.kailing;

import io.netty.util.internal.StringUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: kl @kailing.pub
 * @date: 2019/4/12
 */
public  class Configs {

    protected static final String NO_VALUE_KEY = "__NO_VALUE_KEY";
    private final static String PORT_KEY = "port";
    private final static String DBPATH_KEY = "dbPath";
    private  int port = 8081;
    private String dbPath = System.getProperty("user.dir");

    public Map fromArgs(String[] args) {
        final Map<String, String> map = new HashMap<String, String>(args.length / 2);
        int i = 0;
        while (i < args.length) {
            final String key;

            if (args[i].startsWith("--")) {
                key = args[i].substring(2);
            } else if (args[i].startsWith("-")) {
                key = args[i].substring(1);
            } else {
                throw new IllegalArgumentException(
                        String.format("Error parsing arguments '%s' on '%s'. Please prefix keys with -- or -.",
                                Arrays.toString(args), args[i]));
            }
            if (key.isEmpty()) {
                throw new IllegalArgumentException(
                        "The input " + Arrays.toString(args) + " contains an empty argument");
            }
            i += 1;
            if (i >= args.length) {
                map.put(key, NO_VALUE_KEY);
            } else if (isNumber(args[i])) {
                map.put(key, args[i]);
                i += 1;
            } else if (args[i].startsWith("--") || args[i].startsWith("-")) {
                // the argument cannot be a negative number because we checked earlier
                // -> the next argument is a parameter name
                map.put(key, NO_VALUE_KEY);
            } else {
                map.put(key, args[i]);
                i += 1;
            }
        }
          Object port = map.get(PORT_KEY);
          Object dbPath = map.get(DBPATH_KEY);
          if(port != null){
              setPort(Integer.parseInt(port.toString()));
          }
          if(dbPath !=null){
              setDbPath(dbPath.toString());
        }
        return map;
    }

    public static boolean isNumber(final String str) {
        if (StringUtil.isNullOrEmpty(str)) {
            return false;
        }
        final char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        final int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1 && chars[start] == '0') { // leading 0
            if (
                    (chars[start + 1] == 'x') ||
                            (chars[start + 1] == 'X')
                    ) { // leading 0x/0X
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                            && (chars[i] < 'a' || chars[i] > 'f')
                            && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            } else if (Character.isDigit(chars[start + 1])) {
                // leading 0, but not hex, must be octal
                int i = start + 1;
                for (; i < chars.length; i++) {
                    if (chars[i] < '0' || chars[i] > '7') {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns
                    && (chars[i] == 'd'
                    || chars[i] == 'D'
                    || chars[i] == 'f'
                    || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l'
                    || chars[i] == 'L') {
                // not allowing L with an exponent or decimal point
                return foundDigit && !hasExp && !hasDecPoint;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbPath() {
        return dbPath + "\\db";
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }
}

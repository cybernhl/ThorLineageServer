package sun.misc;

import java.util.Base64;
//FIXME fake class because replace to java.util.Base64
public class BASE64Encoder {
    private final Base64.Encoder encoder = Base64.getEncoder();
    public byte[] encode(byte[] var1) {
         return encoder.encode(var1);
    }
}

// Della Matera Lorenzo 5E

import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.*;

public class Keychain {
    private ArrayList<AESKey> keys;

    public Keychain(){
        DBConnector dbConnector = new DBConnector();
        keys = dbConnector.querySelectKeys();

        //System.out.println(keys.toString());
    }

    public AESKey handshake_findKey(String enc){
        for (AESKey aesKey : keys) {
            try {
                String dec = AES.decrypt(enc, aesKey.getKey(), aesKey.getIvParameterSpec());
                //System.out.println(dec);
                if(dec.equals(aesKey.getSha1key()))
                    return aesKey;
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

}

class AESKey {
    private byte[] bytekey;
    private SecretKey key; 
    private byte[] byteiv;
    private IvParameterSpec ivParameterSpec;
    private String sha1key;
    private String sha1key_enc;

    public AESKey(String sha1key, String hexkey){
        this.sha1key = sha1key;
        this.bytekey = hexStringToByteArray(hexkey);
        this.key = new SecretKeySpec(bytekey, 0, bytekey.length, "AES");
        this.byteiv = new byte[]{65, 66, 67, 68, 65, 66, 67, 68, 65, 66, 67, 68, 65, 66, 67, 68};
        this.ivParameterSpec = new IvParameterSpec(byteiv);
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public byte[] getBytekey() {
        return bytekey;
    }
    public SecretKey getKey() {
        return key;
    }
    public byte[] getByteiv() {
        return byteiv;
    }
    public IvParameterSpec getIvParameterSpec() {
        return ivParameterSpec;
    }
    public void setIvParameterSpec(IvParameterSpec ivParameterSpec) {
        this.ivParameterSpec = ivParameterSpec;
    }
    public String getSha1key() {
        return sha1key;
    }
    public String getSha1key_enc() {
        return sha1key_enc;
    }
    public void setSha1key_enc(String sha1key_enc) {
        this.sha1key_enc = sha1key_enc;
    }

    public void reset(){
        ivParameterSpec = new IvParameterSpec(byteiv);;
        sha1key_enc = null;
    }

    @Override
    public String toString() {
        return "AESKey [bytekey=" + Arrays.toString(bytekey) + ", sha1key=" + sha1key + "]";
    }

    
}
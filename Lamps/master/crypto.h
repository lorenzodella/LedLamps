// password used for encypt/decrypt
byte key[] = {'m', 'y', 's', 'e', 'c', 'r', 'e', 't', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
// init vector, for now just constant
byte default_iv[N_BLOCK] = {65, 66, 67, 68, 65, 66, 67, 68, 65, 66, 67, 68, 65, 66, 67, 68};
byte my_iv[N_BLOCK];
//my authkey
String sha1key = "08cd923367890009657eab812753379bdb321eeb";
String sha1key_enc;
AES aes;

// helper method for printing array of bytes
void printArray(String name, byte *arr, int length){
    Serial.print(name + ": ");
    for (int i = 0; i < length; i++){
        Serial.write(arr[i]);
    }
    Serial.println();
}

String AES_encrypt(String msg){
    byte iv[N_BLOCK];
    memcpy(iv, my_iv, 16);
    // plain message in array of bytes
    byte plain[300];
    // encrypted message
    char cipher[500];
    char cipherb64[500];
    
    // set password
    aes.set_key(key, sizeof(key));

    // transform string to byte[]
    msg.getBytes(plain, sizeof(plain));
    printArray("Plain message", plain, msg.length());
    Serial.print("msglen: "); Serial.println(msg.length());

    // encrypt message with AES128 CBC pkcs7 padding with key and IV
    aes.do_aes_encrypt(plain, strlen((char *)plain), (byte*)cipher, key, 128, iv);
    printArray("Encrypted message", (byte*)cipher, aes.get_size());

    Serial.print("size: "); Serial.println((int)aes.get_size());

    // BASE64 encode ciphered message
    byte cipherb64len = base64_encode(cipherb64, (char *)cipher, aes.get_size());
    Serial.println("Encrypted message in B64: " + String(cipherb64));

    Serial.print("cipherb64len: "); Serial.println(cipherb64len);

    aes.clean();

    String enc = "";
    enc += String(msg.length())+"$"+String(cipherb64);

    //return enc;
    return String(cipherb64);
}

String AES_decrypt(String enc){
    byte iv[N_BLOCK];
    memcpy(iv, my_iv, 16);
    // plain message in array of bytes
    char plain[300];
    // encrypted message
    byte cipher[500];
    byte cipherb64[500];
    
    // set password
    aes.set_key(key, sizeof(key));

    String s="";
    int i;
    for(i=0; i<4; i++){
      if(enc.charAt(i) == '$') break;
      s += enc.charAt(i);
    }
    int msgsize = s.toInt();
    Serial.print("s: "); Serial.println(s);
    Serial.print("size: "); Serial.println(msgsize);

    enc.remove(0,i+1);

    // transform string to byte[]
    enc.getBytes(cipherb64, sizeof(cipherb64));
    printArray("Encrypted message", cipherb64, enc.length());

    /*int size = base64_dec_len((char*)cipherb64, enc.length());

    Serial.print("size: "); Serial.println(size);*/

    // BASE64 decode ciphered message
    byte cipherlen = base64_decode((char *)cipher, (char *)cipherb64, enc.length());
    Serial.print("Encrypted message: "); Serial.println(String((char*)cipher));
    Serial.print("chiperlen: "); Serial.println(cipherlen);
    //printArray("Encrypted message", cipher, String((char*)cipher).length());

    // decrypt message with AES128 CBC pkcs7 padding with key and IV
    aes.do_aes_decrypt((byte*)cipher, cipherlen, (byte*)plain, key, 128, iv);
    //Serial.print("Decrypted message"); Serial.println(String(plain));
    //printArray("Decrypted message", (byte*)plain, msgsize);
    plain[msgsize]='\0';
    Serial.print("Decrypted message"); Serial.println(String(plain));
    
    aes.clean();

    return String(plain);
}

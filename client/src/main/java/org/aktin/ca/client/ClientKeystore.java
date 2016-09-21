package org.aktin.ca.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class ClientKeystore {
	private static char[] keyStorePass = "asdf".toCharArray();
	private static char[] mykeyPass = "fdsa".toCharArray();
	
	static void createKeyStore(KeyPair kp, Certificate cert) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
	{
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(null, keyStorePass);
		ks.setKeyEntry("mykey", kp.getPrivate(), mykeyPass, new Certificate[]{cert});
		
		File f = new File("target/keystore.p12");
		f.createNewFile();
		try( 
			FileOutputStream out = new FileOutputStream(f) ){
			ks.store(out, keyStorePass);
		}
	}
}

package org.aktin.ca.clientgui;

import org.junit.Test;

public class TestKeystore {
/**
	@Test
	public void readWriteKeyStore() throws Exception{
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(null, "asdf".toCharArray());
		KeyPair kp = MainApp.generateKeyPair();
		System.out.println(kp.getPrivate().hashCode());
		Certificate[] certChain = new Certificate[]{MainApp.getCertificateForKeyPair(kp, new Record("a","b","c","d","e","f","g","h"))};
//		Certificate[] certChain = new Certificate[]{};
		ks.setKeyEntry("mykey", kp.getPrivate(), "privpass".toCharArray(), certChain);
		Key bla = ks.getKey("mykey", "privpass".toCharArray());
		System.out.println(bla.hashCode());
		X509Certificate cert = (X509Certificate) ks.getCertificateChain("mykey")[0];
		System.out.println(cert.toString());
		Path p = Files.createTempFile("keystore", ".p12");
		try( 
			OutputStream out = Files.newOutputStream(p, StandardOpenOption.CREATE) ){
			ks.store(out, "asdf".toCharArray());
		}
		
		assertTrue(Files.isReadable(p));
		Files.delete(p);
		System.out.println("Hallo Mark, ALLES (!) gut!");
	}
	
	
	 * @Test
	public void readWriteKeyStore2() throws Exception{
		KeyStore ks = KeyStore.getInstance("PKCS12");
		try( InputStream in = getClass().getResourceAsStream("/bla.p12") ){
			ks.load(in, "abcdef".toCharArray());
		}
		X509Certificate cert = (X509Certificate) ks.getCertificateChain("mykey")[0];
		System.out.println(cert.toString());
	}
	 */
	
	@Test
	public void createRequest()
	{
		Record record = new Record("a","b","c","d","e","f","g","h");
		MainApp mainApp = new MainApp();
		mainApp.setRecord(record);
		mainApp.confirmRecord();
	}
}

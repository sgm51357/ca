package org.aktin.ca.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.bouncycastle.operator.OperatorCreationException;
import org.junit.Test;

public class TestCAKeyStore {
	private static char[] storePassword = "storePassword".toCharArray();
	private static char[] privateKeyPassword = "privateKeyPassword".toCharArray();
	
	@Test
	public void testWriteCertificationRequest() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, OperatorCreationException, UnrecoverableKeyException 
	{
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(null, storePassword);	
		CertificateManager cm = new CertificateManager(ks);

		cm.addKeyPair("mykey", privateKeyPassword, "commonName", "unit", "organization", "location", "state", "country", "emailAdress");
		cm.addCertificate("ca", new FileInputStream(getClass().getResource("/client-test/ca.pem").getFile()));
		cm.addCertificate("custodian", new FileInputStream(getClass().getResource("/client-test/custodian.pem").getFile()));
		cm.addCertificate("datawarehouse", new FileInputStream(getClass().getResource("/client-test/datawarehouse.pem").getFile()));
		cm.addCertificate("rootCA", new FileInputStream(getClass().getResource("/client-test/rootCA.pem").getFile()));
		
		PrintWriter writer = new PrintWriter(System.out, true);
		//FileWriter writer = new FileWriter("src/test/resources/client-test/request.csr");
		cm.writeCertificationRequest("mykey", privateKeyPassword, writer);
		writer.flush();
		//FileOutputStream fos = new FileOutputStream("src/test/resources/client-test/keystore.p12");
		//cm.getKeyStore().store(fos, storePassword);
	}

	@Test
	public void testResponseImport() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException
	{
		File f = new File(testRes("/client-test/keystore.p12"));
		FileInputStream fis = new FileInputStream(f);
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(fis, storePassword);
		CertificateManager cm = new CertificateManager(ks);
		InputStream is = new FileInputStream(getClass().getResource("/client-test/response.crt").getFile());
		cm.importCertificationResponse("mykey", privateKeyPassword, is);
//		FileOutputStream fos = new FileOutputStream("src/test/resources/client-test/keystore.p12");
//		cm.getKeyStore().store(fos, storePassword);
	}
	
	private String testRes(String path)
	{
		return getClass().getResource(path).getFile();		
	}
}

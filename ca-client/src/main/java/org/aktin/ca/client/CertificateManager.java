package org.aktin.ca.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

public class CertificateManager {	
	private KeyStore keystore;
	
	public CertificateManager(KeyStore keystore)
	{
		this.keystore=keystore;
	}

	public void addKeyPair(String alias, char[] privateKeyPassword, String commonName, String unit, String organization, String location, String state, String country, String emailAdress) throws OperatorCreationException, CertificateException, KeyStoreException, NoSuchAlgorithmException, FileNotFoundException
	{
		//generating random KeyPair
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		//generating certificate for KeyPair
		X500Name issuer = new X500Name("CN="+commonName+",OU="+unit+",O="+organization+",L="+location+",ST="+state+",C="+country+",EmailAddress="+emailAdress);
	    BigInteger serial = BigInteger.valueOf(1);
	    X500Name subject = issuer;
	    PublicKey pubKey = keyPair.getPublic();

	    //generate certificate
	    X509v3CertificateBuilder generator = new JcaX509v3CertificateBuilder(issuer, serial, new Date(System.currentTimeMillis()),
	            new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 20), subject, pubKey);
	    
	    ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA").build(keyPair.getPrivate());
	    X509CertificateHolder certHolder = generator.build(sigGen);
	    X509Certificate cert = new JcaX509CertificateConverter().getCertificate( certHolder );
	    
	    //add certificate
		keystore.setKeyEntry(alias, keyPair.getPrivate(), privateKeyPassword, new Certificate[] {cert});
	}
	
	public void addCertificate(String alias, InputStream input) throws CertificateException, KeyStoreException
	{
    	CertificateFactory cf = CertificateFactory.getInstance("X.509");		
		X509Certificate cert = (X509Certificate) cf.generateCertificate(input);
		keystore.setCertificateEntry(alias, cert);
	}

	public void writeCertificationRequest(String alias, char[] privateKeyPassword, Writer dest) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, OperatorCreationException, IOException
	{
		//reading information from self-signed certificate
		X509Certificate cert = (X509Certificate)keystore.getCertificate(alias);
		KeyPair keyPair = new KeyPair(cert.getPublicKey(), (PrivateKey)keystore.getKey(alias, privateKeyPassword));
		Principal principal = cert.getSubjectDN();
		
		//generate certification request
		X500Name x500Name = new X500Name(principal.toString());
		PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
			    x500Name, keyPair.getPublic());
		JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
		ContentSigner signer = csBuilder.build(keyPair.getPrivate());
		PKCS10CertificationRequest csr = p10Builder.build(signer);
		
		//write certification request
		String csrString = csrToString(csr);
		dest.write(csrString);
	}

	public void importCertificationResponse(String alias, char[] privateKeyPassword, InputStream response) throws CertificateException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException
	{
		CertificateFactory cf = CertificateFactory.getInstance("X.509");		
		X509Certificate cert = (X509Certificate) cf.generateCertificate(response);
		PrivateKey privateKey = (PrivateKey)keystore.getKey(alias, privateKeyPassword);
		keystore.setKeyEntry(alias, privateKey, privateKeyPassword, new Certificate[] {cert});
	}

	public KeyStore getKeyStore()
	{
		return keystore;
	}
	
	private String csrToString(PKCS10CertificationRequest csr) throws IOException{
		StringWriter w = new StringWriter();
		JcaPEMWriter p = new JcaPEMWriter(w);
		p.writeObject(csr);
		p.close();
		return w.toString();
	}
}

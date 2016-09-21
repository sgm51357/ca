package org.aktin.ca.client;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
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

public class RequestHandler {
	public static void createCSR(X500Name x500Name)
	{
		try {
			KeyPair kp = generateKeyPair();
			X509Certificate cert = getCertificateSignPublicWithPrivate(kp, x500Name);			
			ClientKeystore.createKeyStore(kp, cert);
			PKCS10CertificationRequest csr = generateCSR(kp, x500Name);			
			System.out.println(csrToString(csr));
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperatorCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
     * Erstellt ein RSA-KeyPair der Größe 2048.
     * @return
     * @throws NoSuchAlgorithmException
     */
	static KeyPair generateKeyPair() throws NoSuchAlgorithmException{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		return keyPairGenerator.generateKeyPair();
	}
	static PKCS10CertificationRequest generateCSR(KeyPair keypair, X500Name x500Name) throws Exception
	{
		PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
		    x500Name, keypair.getPublic());
		JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
		ContentSigner signer = csBuilder.build(keypair.getPrivate());
		return p10Builder.build(signer);
	}
	static String csrToString(PKCS10CertificationRequest csr) throws IOException{
		StringWriter w = new StringWriter();
		JcaPEMWriter p = new JcaPEMWriter(w);
		p.writeObject(csr);
		p.close();
		return w.toString();
	}
	static X509Certificate getCertificateSignPublicWithPrivate(KeyPair keyPair, X500Name x500Name) throws CertificateException, OperatorCreationException
	{
	    X500Name issuer = x500Name;
	    BigInteger serial = BigInteger.valueOf(1);
	    X500Name subject = issuer;
	    PublicKey pubKey = keyPair.getPublic();

	    X509v3CertificateBuilder generator = new JcaX509v3CertificateBuilder(
	            issuer, serial, new Date(System.currentTimeMillis()),
	            new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 5), subject, pubKey);
	    
	    ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA").build(keyPair.getPrivate());
	    X509CertificateHolder certHolder = generator.build(sigGen);
	    return new JcaX509CertificateConverter().getCertificate( certHolder );
	}
}

package org.aktin.ca;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

import javax.inject.Singleton;

import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

@Singleton
public class CertificateManager {
	PrivateKey cakey;
	X500Name caname;
	X509Certificate cacert;
	Path csrdir;
	Path certdir;
	
	public CertificateManager(KeyStore keystore, String alias, char[] password) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException{
		// load private key
		cakey = (PrivateKey) keystore.getKey(alias, password);
		cacert = (X509Certificate) keystore.getCertificate(alias);
		caname = new X500Name(cacert.getSubjectX500Principal().getName());
	}
	
	/**
	 * Get the CA certificate (self signed public key)
	 * @return certificate string in PEM form
	 * @throws IOException 
	 */
	public String getCertificatePEM() throws IOException{
		StringWriter w = new StringWriter();
		JcaPEMWriter writer = new JcaPEMWriter(w);
		writer.writeObject(cacert);
		writer.close();
		return w.toString();
	}

	public PKCS10CertificationRequest parseCSR(Reader pem) throws IOException{
		PEMParser reader = new PEMParser(pem);
		PKCS10CertificationRequest csr = new PKCS10CertificationRequest((CertificationRequest) reader.readObject());
		reader.close();
		return csr;
	}
	private String getFingerprint(PKCS10CertificationRequest csr) throws NoSuchAlgorithmException, IOException{
		byte[] digest = MessageDigest.getInstance("SHA1").digest(csr.getSubjectPublicKeyInfo().getEncoded());
		return Base64.getUrlEncoder().encodeToString(digest);
		// or use bouncycastle directly: SHA1Digest sha1 = new SHA1Digest();
	}
	public String findResponse(PKCS10CertificationRequest csr) throws NoSuchAlgorithmException, IOException{
		String fp = getFingerprint(csr);
		// TODO find file in csrdir. Replace if already exists
		// TODO if autocertify then generate certificate
		// TODO find cert in certdir. Return content if exists, or null otherwise
		return fp;
	}
	public String certify(PKCS10CertificationRequest csr){
		// TODO revoke old certificates for the same user or public key, if requested again
		throw new UnsupportedOperationException();
	}
	/**
	 * Read the current revocation list. Will generate a new revocation list, 
	 * if the currently stored one is expired.
	 * @return input stream containing the DER encoded revocation list
	 */
	public InputStream readCRL(){
		// TODO check CRL file date and compare to current timestamp
		// TODO generate new CRL if expired
		X509v2CRLBuilder crl = new X509v2CRLBuilder(caname, new Date());
		crl.getClass();
		
		return null;
	}
	/**
	 * Given a Keystore containing a private key and certificate and a Reader
	 * containing a PEM-encoded Certificiate Signing Request (CSR), sign the CSR
	 * with that private key and return the signed certificate as a PEM-encoded
	 * PKCS#7 signedData object. The returned value can be written to a file and
	 * imported into a Java KeyStore with "keytool -import -trustcacerts -alias
	 * subjectalias -file file.pem"
	 *
	 * @param pemcsr
	 *            a Reader from which will be read a PEM-encoded CSR (begins
	 *            "-----BEGIN NEW CERTIFICATE REQUEST-----")
	 * @param validity
	 *            the number of days to sign the Certificate for
	 *
	 * @return a String containing the PEM-encoded signed Certificate (begins
	 *         "-----BEGIN PKCS #7 SIGNED DATA-----")
	 */
	public String signCSR(Reader pemcsr, int validity)
			throws Exception {
		PEMParser reader = new PEMParser(pemcsr);
		PKCS10CertificationRequest csr = new PKCS10CertificationRequest((CertificationRequest) reader.readObject());
		reader.close();

		AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
		AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
		X500Name issuer = caname;
		BigInteger serial = new BigInteger(32, new SecureRandom());
		Date from = new Date();
		Date to = new Date(System.currentTimeMillis() + (validity * 86400000L));

		X509v3CertificateBuilder certgen = new X509v3CertificateBuilder(issuer, serial, from, to, csr.getSubject(),
				csr.getSubjectPublicKeyInfo());
		certgen.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
		certgen.addExtension(Extension.subjectKeyIdentifier, false,
				new SubjectKeyIdentifier(csr.getSubjectPublicKeyInfo().getEncoded()));
		certgen.addExtension(Extension.authorityKeyIdentifier, false,
				new AuthorityKeyIdentifier(
						new GeneralNames(new GeneralName(new X500Name(cacert.getSubjectX500Principal().getName()))),
						cacert.getSerialNumber()));

		ContentSigner signer = new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
				.build(PrivateKeyFactory.createKey(cakey.getEncoded()));
		X509CertificateHolder holder = certgen.build(signer);
		byte[] certencoded = holder.toASN1Structure().getEncoded();

		CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
		signer = new JcaContentSignerBuilder("SHA1withRSA").build(cakey);
		generator.addSignerInfoGenerator(
				new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(signer,
						cacert));
		generator.addCertificate(new X509CertificateHolder(certencoded));
		generator.addCertificate(new X509CertificateHolder(cacert.getEncoded()));
		CMSTypedData content = new CMSProcessableByteArray(certencoded);
		CMSSignedData signeddata = generator.generate(content, true);

		StringBuilder builder = new StringBuilder();
		builder.append("-----BEGIN PKCS #7 SIGNED DATA-----\n");
		builder.append(Base64.getEncoder().encodeToString(signeddata.getEncoded()));
		builder.append("\n-----END PKCS #7 SIGNED DATA-----\n");
		return builder.toString();
	}
}

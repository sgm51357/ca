package org.aktin.ca.client;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

public class ClientKeystore {
	private KeyPair generateKeyPair(){
		return null;
	}
	private String generateCSR() throws OperatorCreationException, IOException{
		KeyPair pair = generateKeyPair();
		PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
		    new X500Principal("CN=Requested Test Certificate"), pair.getPublic());
		JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
		ContentSigner signer = csBuilder.build(pair.getPrivate());
		PKCS10CertificationRequest csr = p10Builder.build(signer);
		return csrString(csr);
	}
	private String csrString(PKCS10CertificationRequest csr) throws IOException{
		StringWriter w = new StringWriter();
		JcaPEMWriter p = new JcaPEMWriter(w);
		p.writeObject(csr);
		p.close();
		return w.toString();
	}

}

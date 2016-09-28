package org.aktin.ca;

import java.io.IOException;
import java.io.Reader;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(CertificationAuthorityEndpoint.SERVICE_URL)
public class CertificationAuthorityEndpoint {
	public static final String SERVICE_URL = "/ca/";

	@Inject
	private CertificateManager manager;
	
	@POST
	@Path("csr")
	@Consumes("application/pkcs10")
	public Response submitCSR(Reader csr){
		// TODO return created with location
		// check for generated response
		// if response is there, return the response and HTTP_OK
		// if the response is not there, return accepted
		return Response.accepted().build();
	}

	@GET
	@Path("cert")
	@Produces("application/x-pem-file")
	public String getCert() throws IOException{
		return manager.getCertificatePEM();
	}

	@GET
	@Path("crl")
	@Produces("application/pkix-crl")
	public Response getCertificateRevocationList(){
		return Response.ok(manager.readCRL()).build();
	}
}

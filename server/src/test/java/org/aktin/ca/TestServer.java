package org.aktin.ca;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.SQLException;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * li2b2 server for unit tests
 * or demonstrations.
 * 
 * @author R.W.Majeed
 *
 */
public class TestServer {

	private ResourceConfig rc;
	private Server jetty;
	
	public TestServer() throws SQLException{
		rc = new ResourceConfig();
		rc.register(CertificationAuthorityEndpoint.class);
		rc.register(new MyBinder());		
	}
	public void register(Class<?> componentClass){
		rc.register(componentClass);
	}
	
	protected void start_local(int port) throws Exception{
		start(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
	}
	public URI getCAServiceURI(){
		return jetty.getURI().resolve(CertificationAuthorityEndpoint.SERVICE_URL);
	}
	public void start(InetSocketAddress addr) throws Exception{
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		jetty = new Server(addr);
		jetty.setHandler(context);

		ServletHolder jersey = new ServletHolder(new ServletContainer(rc));
//		jersey.setInitOrder(0);
		context.addServlet(jersey, "/*");

		jetty.start();
	}
	public void join() throws InterruptedException{
		jetty.join();
	}
	public void destroy() throws Exception{
		jetty.destroy();
	}
	public void stop() throws Exception{
		jetty.stop();
	}

	/**
	 * Run the test server with with the official i2b2
	 * webclient.
	 * @param args command line arguments: port can be specified optionally
	 * @throws Exception any error
	 */
	public static void main(String[] args) throws Exception{
		// use port if specified
		int port;
		if( args.length == 0 ){
			port = 8080;
		}else if( args.length == 1 ){
			port = Integer.parseInt(args[0]);
		}else{
			System.err.println("Too many command line arguments!");
			System.err.println("Usage: "+TestServer.class.getCanonicalName()+" [port]");
			System.exit(-1);
			return;
		}

		// start server
		TestServer server = new TestServer();
		try{
			server.start(new InetSocketAddress(port));
			System.err.println("CA service at: "+server.getCAServiceURI());
			server.join();
		}finally{
			server.destroy();
		}
	}
}

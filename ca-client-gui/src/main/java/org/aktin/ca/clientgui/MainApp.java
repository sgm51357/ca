package org.aktin.ca.clientgui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.aktin.ca.client.CertificateManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private FormController controller;
    
    private Record record = new Record();
    
    public static enum FormScene
    {
       CREATE, CONFIRM, MESSAGE;
    }
    
    private FormScene currentScene;

    @Override
    public void start(Stage primaryStage) {    	
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Zertifizierung");

    	File fKeyStore = new File("tls/keystore.p12");
    	if (fKeyStore.exists())
    	{
    		KeyStore ks = null;
        	try
        	{
				FileInputStream fis = new FileInputStream(fKeyStore);
				ks = KeyStore.getInstance("PKCS12");
				ks.load(fis, "storePassword".toCharArray());
        	}
        	catch (Exception e)
        	{
        		showError("Fehler beim Öffnen des Keystore!");
        	}
    		File fResponse = new File("tls/response.crt");
    		if (fResponse.exists())
    		{
    			showMessage("Es ist ein signiertes Zertifikat mit folgenden Daten vorhanden:");
    			try
    			{
	    			CertificateManager cm = new CertificateManager(ks);
	    			InputStream is = new FileInputStream(fResponse);
	    			
	    			//Anzeige der Inhalte
					CertificateFactory cf = CertificateFactory.getInstance("X.509");		
					X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
					String issuerDN = cert.getIssuerDN().getName();
					showMessage("von:\n"+dnToReadable(issuerDN,false));
					String subjectDN = cert.getSubjectDN().getName();
					showMessage("für:\n"+dnToReadable(subjectDN,false));
					
					//Import
					is = new FileInputStream(fResponse);
	    			cm.importCertificationResponse("mykey", "privateKeyPassword".toCharArray(), is);
	    			FileOutputStream fos = new FileOutputStream("tls/keystore.p12");
	    			cm.getKeyStore().store(fos, "storePassword".toCharArray());
    				showSuccess("Das Zertifikat wurde erfolgreich importiert.");
    			}
    			catch (Exception e)
    			{
    				showError("Fehler beim Import des Zertifikats!");
    				e.printStackTrace();
    			}
    		}
    		else
    		{
				showMessage("Es wurde bereits ein Zertifikatsantrag mit fologenden Daten erstellt:");
				try {
					String issuerDN = ((X509Certificate)ks.getCertificate("mykey")).getIssuerDN().getName();
					showMessage(dnToReadable(issuerDN,true));
				} catch (KeyStoreException e) {
					showError("Fehler beim Auslesen der Daten!");
					e.printStackTrace();
				}
				showMessage("Bitte senden Sie diesen an die Omma.\n\nFalls Sie einen neuen Antrag erstellen möchten, löschen Sie die Dateien keystore.p12 und request.csr und starten Sie das Programm erneut.");
    		}
    	}
    	else
    	{
        	//noch kein KeyStore angelegt
            switchScene(FormScene.CREATE);
        }

        primaryStage.show();
    }
    
    private String dnToReadable(String s, boolean reverse)
    {
    	List<String> list = Arrays.asList(s.split(", "));
    	if (reverse) Collections.reverse(list);
    	s = String.join(",   ", list);
    	s = s.replaceAll("EMAILADDRESS=", "E-Mail: ");
    	s = s.replaceAll("O=", "Organisation: ");
    	s = s.replaceAll("OU=", "Organisationseinheit: ");
    	s = s.replaceAll("C=", "Land: ");
    	s = s.replaceAll("CN=", "Name: ");
    	s = s.replaceAll("ST=", "Bundesland: ");
    	s = s.replaceAll("L=", "Stadt: ");    	
    	return s;
    }

    Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    void setRecord(Record record)
    {
    	this.record = record;
    }
    
    void showMessage(String message)
    {
    	switchScene(FormScene.MESSAGE);
    	controller.showMessage(message);
    }
    
    void showError(String message)
    {
    	showMessage(message);
    	controller.error();
    }
    
    void showSuccess(String message)
    {
    	showMessage(message);
    }
    
    void switchScene(FormScene fs)
    {
    	if (fs != currentScene)
    	{
	        try {
	        	String fxmlPath;
	        	switch (fs)
	        	{
	        		default:
	        		case CREATE:
	        			fxmlPath = "/FormCreateRecord.fxml";
	        			break;
	        		case CONFIRM:
	        			fxmlPath = "/FormConfirmRecord.fxml";
	        			break;
	        		case MESSAGE:
	        			fxmlPath = "/FormMessage.fxml";
	        			break;
	        			
	        	}
	            
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(MainApp.class.getResource(fxmlPath));
	            rootLayout = (AnchorPane) loader.load();
	            
	            controller = loader.getController();
	            controller.setMainApp(this);
	            controller.loadRecord(record, fs);
	
	            // Show the scene containing the root layout.
	            Scene scene = new Scene(rootLayout);
	            primaryStage.setScene(scene);
	            currentScene = fs;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
    	}
    }
    
    void close()
    {
    	primaryStage.close();
    }
    
    protected void confirmRecord()
    {
    	try {
			createRequest();
			showSuccess("Es wurde ein Zertifikatsantrag erstellt. Bitte senden Sie diesen an die Omma.");
		} catch (Exception e) {
			showMessage(e.getMessage());
			e.printStackTrace();
		}
    }
    
    protected void createRequest() throws Exception
    {
    	CertificateManager cm = null;
    	try
    	{
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(null, "storePassword".toCharArray());	
			cm = new CertificateManager(ks);
    	}
    	catch (Exception e) {
    		throw new Exception("Fehler beim Anlegen des KeyStores!");
    	}
    	
    	try
    	{
			cm.addKeyPair("mykey", "privateKeyPassword".toCharArray(), record.getGivenName()+" "+record.getSurName(), 
					record.getUnit(), record.getOrganization(), record.getCity(), record.getFederalState(),
					record.getCountryCode(), record.getEmail());
    	}
    	catch (Exception e) {
    		throw new Exception("Fehler beim Erstellen des KeyPairs!");
    	}
    	try
    	{
//			cm.addCertificate("ca", new FileInputStream(getClass().getResource("/ca.pem").getFile()));
//			cm.addCertificate("custodian", new FileInputStream(getClass().getResource("/custodian.pem").getFile()));
//			cm.addCertificate("datawarehouse", new FileInputStream(getClass().getResource("/datawarehouse.pem").getFile()));
			cm.addCertificate("rootCA", new FileInputStream(getClass().getResource("/gui-test/rootCA.pem").getFile()));
    	}
    	catch (Exception e) {
    		throw new Exception("Fehler beim Anfügen der drei Zertifikate ca.pem, custodian.pem, datawarehouse.pem!");
    	}
    	try
    	{
			FileWriter writer = new FileWriter("tls/request.csr");
			cm.writeCertificationRequest("mykey", "privateKeyPassword".toCharArray(), writer);
			writer.flush();
    	}
    	catch (Exception e) {
    		throw new Exception("Fehler beim Erstellen des Requests!");
    	}
    	try
    	{
			FileOutputStream fos = new FileOutputStream("tls/keystore.p12");
			cm.getKeyStore().store(fos, "storePassword".toCharArray());
    	} 
    	catch (Exception e) {
    		throw new Exception("Fehler beim Schreiben des KeyStores!");
    	}
    }    
}
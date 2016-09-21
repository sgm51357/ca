package org.aktin.ca.clientgui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.aktin.ca.client.ClientKeystore;
import org.aktin.ca.client.RequestHandler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    
    private Record record = new Record();
    
    public static enum FormScene
    {
       CREATE, CONFIRM, TRUST;
    }

    @Override
    public void start(Stage primaryStage) {    	
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Zertifizierung");

        switchScene(FormScene.CREATE);
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public void setRecord(Record record)
    {
    	this.record = record;
    }
    
    public void switchScene(FormScene fs)
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
        		case TRUST:
        			fxmlPath = "/FormTrustCA.fxml";
        			break;
        			
        	}
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(fxmlPath));
            rootLayout = (AnchorPane) loader.load();
            
            FormController controller = loader.getController();
            controller.setMainApp(this);
            controller.loadRecord(record, fs);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected void confirmRecord()
    {
    	RequestHandler.createCSR(record.getX500Name());
//    	try {
//    		KeyStore ks = KeyStore.getInstance("PKCS12");
//    		ks.load(null, "asdf".toCharArray());
//    		
//    		KeyPair kp = ClientKeystore.generateKeyPair();
//    		X509Certificate cert = ClientKeystore.getCertificateForKeyPair(kp, record.getX500Name());
//    		ks.setKeyEntry("mykey", kp.getPrivate(), "Passw0rt".toCharArray(), new Certificate[]{cert});
//    		
//    		File f = new File("target/keystore.p12");
//    		f.createNewFile();
//    		try( 
//    			FileOutputStream out = new FileOutputStream(f) ){
//    			ks.store(out, "asdf".toCharArray());
//    		}
//    		
//			System.out.println(ClientKeystore.csrToString(ClientKeystore.generateCSR(kp,record.getX500Name())));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }

    
}
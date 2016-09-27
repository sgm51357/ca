package org.aktin.ca.clientgui;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.aktin.ca.client.CertificateManager;
import org.bouncycastle.operator.OperatorCreationException;

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

//        FileReader fr = new FileReader("mystore.keystore");
//        KeyStore keystore = 
//		X509Certificate cert = (X509Certificate)keystore.getCertificate(alias);
//        try {
//            KeyStoreManager ksm = new KeyStoreManager("target/mystore.keystore");
//			ksm.load("asdf");
//			if (ksm.hasKeyPair("mykey", "fdsa"))
//			{
//				showMessage("Es wurde bereits ein Zertifikatsantrag erstellt. Bitte senden Sie diesen an die Omma.");
//			}
//		} catch (KeyStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CertificateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnrecoverableKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        switchScene(FormScene.CREATE);
        primaryStage.show();
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
//    	KeyPair kp;
//		try {
//			kp = CertificateManager.generateKeyPair();
//	    	KeyStoreManager ksm = new KeyStoreManager("target/mystore.keystore");
//			ksm.load("asdf");
//			Certificate cert = CertificateManager.getCertificateSignPublicWithPrivate(kp, record.getX500String());
//			ksm.putKeyPair(kp, cert, "mykey", "fdsa");
//			CertificateManager.generateCSR(kp, record.getX500Name(), "target/request.csr");
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KeyStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CertificateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OperatorCreationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		showMessage("Es wurde ein Zertifikatsantrag erstellt. Bitte senden Sie diesen an die Omma.");
		showMessage("eieiei");
    }

    
}
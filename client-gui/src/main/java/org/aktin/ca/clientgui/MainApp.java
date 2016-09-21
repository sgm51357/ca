package org.aktin.ca.clientgui;

import java.io.IOException;

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
    }

    
}
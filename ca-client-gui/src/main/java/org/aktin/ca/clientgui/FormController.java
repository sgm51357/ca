package org.aktin.ca.clientgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class FormController {
	private MainApp mainApp;
	
	@FXML private TextField txtGivenName;
	@FXML private TextField txtSurName;
	@FXML private TextField txtEmail;
	@FXML private TextField txtUnit;
	@FXML private TextField txtOrganization;
	@FXML private TextField txtCity;
	@FXML private TextField txtFederalState;
	@FXML private TextField txtCountryCode;
	
	@FXML private Label lblGivenName;
	@FXML private Label lblSurName;
	@FXML private Label lblEmail;
	@FXML private Label lblUnit;
	@FXML private Label lblOrganization;
	@FXML private Label lblCity;
	@FXML private Label lblFederalState;
	@FXML private Label lblCountryCode;

	@FXML private Label lblMessage;
	
	@FXML protected void createRecord(ActionEvent event) {
        Record record = new Record(txtGivenName.getText(), txtSurName.getText(), txtUnit.getText(), txtOrganization.getText(),
        		txtCity.getText(), txtFederalState.getText(), txtCountryCode.getText(), txtEmail.getText());
        mainApp.setRecord(record);
        mainApp.switchScene(MainApp.FormScene.CONFIRM);
    }
	
	@FXML protected void confirmRecord(ActionEvent event) {
		mainApp.confirmRecord();
	}

	@FXML protected void editRecord(ActionEvent event) {
		mainApp.switchScene(MainApp.FormScene.CREATE);
	}
	
	@FXML protected void closeForm(ActionEvent event) {
		mainApp.close();
	}
	
	public void loadRecord(Record record, MainApp.FormScene fs)
	{
		switch (fs)
		{
			case CREATE:
				txtGivenName.setText(record.getGivenName());
				txtSurName.setText(record.getSurName());
				txtUnit.setText(record.getUnit());
				txtOrganization.setText(record.getOrganization());
				txtCity.setText(record.getCity());
				txtFederalState.setText(record.getFederalState());
				txtCountryCode.setText(record.getCountryCode());
				txtEmail.setText(record.getEmail());
				break;
			case CONFIRM:
				lblGivenName.setText(record.getGivenName());
				lblSurName.setText(record.getSurName());
				lblUnit.setText(record.getUnit());
				lblOrganization.setText(record.getOrganization());
				lblCity.setText(record.getCity());
				lblFederalState.setText(record.getFederalState());
				lblCountryCode.setText(record.getCountryCode());
				lblEmail.setText(record.getEmail());
				break;
			case MESSAGE:
				break;
		}
	}
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
	}
	
	public void showMessage(String message)
	{
		if (lblMessage.getText().isEmpty()) lblMessage.setText(message);
		else lblMessage.setText(lblMessage.getText()+"\n\n"+message);		
	}
	
	public void error()
	{
		lblMessage.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	public void success()
	{
		lblMessage.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
	}
}

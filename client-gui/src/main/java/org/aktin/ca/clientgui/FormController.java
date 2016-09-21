package org.aktin.ca.clientgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
			case TRUST:
				break;
		}
	}
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
	}
}

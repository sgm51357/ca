package org.aktin.ca.clientgui;

import org.junit.Test;

public class TestKeystore {	
	@Test
	public void createRequest()
	{
		Record record = new Record("a","b","c","d","e","f","g","h");
		MainApp mainApp = new MainApp();
		mainApp.setRecord(record);
		mainApp.confirmRecord();
	}
}

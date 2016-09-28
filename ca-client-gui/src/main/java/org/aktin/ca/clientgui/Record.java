package org.aktin.ca.clientgui;

import org.bouncycastle.asn1.x500.X500Name;

public class Record {
	private String givenName;
	private String surName;
	private String unit;			//organisatorische Einheit
	private String organization;	//Organisation
	private String city;			//Stadt/Gemeinde
	private String federalState; 	//Bundesland
	private String countryCode;		//Ländercode (zwei Buchstaben)
	private String email;
	
	public Record()
	{
		
	}
	
	public Record(String gn, String sn, String unit, String org, String city, String fs, String cc, String email)
	{
		this.setRecord(gn, sn, unit, org, city, fs, cc, email);
	}
	
	public void setRecord(String gn, String sn, String unit, String org, String city, String fs, String cc, String email)
	{
		givenName = gn;
		surName = sn;
		this.unit = unit;
		organization = org;
		this.city = city;
		federalState = fs;
		countryCode = cc;
		this.email = email;
	}
	
	String getX500String()
	{
		return "CN="+givenName+" "+surName+",OU="+unit+",O="+organization+",L="+city+",ST="+federalState+",C="+countryCode+",EmailAddress="+email;
	}
	
	X500Name getX500Name()
	{
		return new X500Name(getX500String());
	}
	
	public String getGivenName()
	{
		return givenName;
	}	
	public String getSurName()
	{
		return surName;
	}
	public String getUnit()
	{
		return unit;
	}
	public String getOrganization()
	{
		return organization;
	}
	public String getCity()
	{
		return city;
	}
	public String getFederalState()
	{
		return federalState;
	}
	public String getCountryCode()
	{
		return countryCode;
	}
	public String getEmail()
	{
		return email;
	}
}

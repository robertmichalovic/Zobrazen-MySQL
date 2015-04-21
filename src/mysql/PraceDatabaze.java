package mysql;
import gui.frame.GUIFrameOkno;

import java.sql.*;

import javax.swing.*;

import tabulka.TabulkaDatabaze;
import datab.spusteni.*;
public class PraceDatabaze {													//	trida obsluhujici pripojeni k MySQL databazi
	static { 															// staticky blok pro nacteni driveru databaze MySQL
		try{ 
			Class.forName("com.mysql.jdbc.Driver").newInstance();	}
		 catch(Exception e){  											// neni k dispozici driver v path - nastavit external *.jar v libraries
			 System.out.println("Driver nenacten - Chyba driveru");
			 System.out.println("Zde je zprava :  "+e.getMessage());
			 e.printStackTrace();	}	}
	private String Url,User,Pass;
	private Connection pripojeniCon=null;
	private Statement dotazState=null;
	private ResultSet vysledekResultSet=null;
	private static boolean pripojen=false;
	public static boolean isPripojen()									{	return pripojen;	}
	public static void setPripojen(boolean pripojen) 					{	PraceDatabaze.pripojen = pripojen;	}
	public ResultSet getVysledekResultSet() 							{	return vysledekResultSet;	}
	public void setVysledekResultSet(ResultSet vysledekResultSet) 		{	this.vysledekResultSet = vysledekResultSet;	}
	public Connection getPripojeniCon() {
		return pripojeniCon;
	}
	public void setPripojeniCon(Connection pripojeniCon) {
		this.pripojeniCon = pripojeniCon;
	}
	public PraceDatabaze(String adresa,String user,String pass) {				//	konstruktor objektu
		this.Url=adresa;this.User=user;this.Pass=pass;	}
	public void napojeniMySQLServeru(JFrame frame) {								//	provedem pripojeni k serveru a nastavime boolean na true
		try{ 
			setPripojeniCon(DriverManager.getConnection(Url,User,Pass));
			setPripojen(true);	}
		catch(SQLException e){ 
			JOptionPane.showMessageDialog(frame, "Údaje nejsou správné - nepøipojily jsme se ","POZOR - Server nebyl pøipojen", JOptionPane.WARNING_MESSAGE );
			// uprava proti nevhodnemu chovani - kdyz se nepodari pripojit byla vytvorena reference a nefungovalo tlacitko Ukonci
			ZobrazeniDatabazi.sqlPripojeni=null;	
			System.out.println("Nepodarilo se pripojit k serveru MySQL");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());
			e.printStackTrace();	}	}
	public void ziskaniDatabazi(JFrame frame) {									//	do resultSetuUlozime seznam databazi
		try{ 
			dotazState = getPripojeniCon().createStatement();						//	vytvorime objekt dotazu
			setVysledekResultSet(dotazState.executeQuery("SHOW DATABASES"));	}	//	do vysledkove mnoziny ulozime seznam databazi
		catch(SQLException e){ 
			JOptionPane.showMessageDialog(frame, "Nepodaøilo se ziskat seznam databazi ","POZOR - Server nebyl pøipojen", JOptionPane.WARNING_MESSAGE );
			System.out.println("Nepodarilo se zadat dotaz serveru nebo ziskat seznam databazi");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());
			e.printStackTrace();	}	}
	public void ziskaniTabulek(String jmenoDatabaze){				//	do resultSetu ulozime seznam tabulek
		try {
			dotazState = getPripojeniCon().createStatement();										//	vytvorime objekt dotazu
			setVysledekResultSet(dotazState.executeQuery("SHOW TABLES FROM "+jmenoDatabaze));	} 	//	objektu dotazu nacteme tabuky
		catch (SQLException e) {
			System.out.println("Nepodarilo se vytvorit dotaz serveru nebo ziskat seznam tabulek");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());
			e.printStackTrace();	}	}
	public void odpojeniMySQLServeru(GUIFrameOkno frame) {		//	odpojime objekty a vymazeme a opet nastavime pripojeni na false
		try {
			if(TabulkaDatabaze.isNactenaDatabaze() == true) {		//	pokud je nactena databaze je nutne provest vymazani tabulky
				frame.zniceniTabulky();							//	do defaultniho stavu
				ZobrazeniDatabazi.vysledkovaTabulka =  new JTable(40, 10);
				frame.getTabulkaPanel().NastaveniScrollPane(ZobrazeniDatabazi.vysledkovaTabulka, frame);
				TabulkaDatabaze.setNactenaDatabaze(false);	}
			frame.getNejnizsiPanel().nastaveniTlacitek();  			//	nastavime spodni tri tlacitka novy/uprav/smaz
			getVysledekResultSet().close();
			setVysledekResultSet(null);
			dotazState.close();
			dotazState=null;
			getPripojeniCon().close();
			setPripojeniCon(null);
			setPripojen(false);	} 
		catch (SQLException e) {
			System.out.println("Nepodarilo se provest odpojeni serveru");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());
			e.printStackTrace();	}	}
	public void odpojeniServeruPripojeniDatabaze(String jmenoDatabaze,GUIFrameOkno frame) {	//	odpojime se od serveru a prihlasime se k databazi
		try {
			if(TabulkaDatabaze.isNactenaDatabaze() == true) {		//	pokud je nactena databaze je nutne provest vymazani tabulky
				frame.zniceniTabulky();							//	do defaultniho stavu
				ZobrazeniDatabazi.vysledkovaTabulka =  new JTable(40, 10);
				frame.getTabulkaPanel().NastaveniScrollPane(ZobrazeniDatabazi.vysledkovaTabulka, frame);
				TabulkaDatabaze.setNactenaDatabaze(false);	}
			getVysledekResultSet().close();
			setVysledekResultSet(null);
			dotazState.close();
			dotazState=null;
			getPripojeniCon().close();
			setPripojeniCon(null);
			setPripojen(false);
			setPripojeniCon(DriverManager.getConnection(Url+"/"+jmenoDatabaze,User,Pass));
			setPripojen(true);	} 
		catch (SQLException e) {
			System.out.println("Nepodarilo se provest pripojeni ke konkretni databazi");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());
			e.printStackTrace();	}	}
}

package gui.panel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;

import datab.spusteni.*;
import mysql.PraceDatabaze;
import gui.frame.*;
public class HorniHorniPanel extends JPanel {	
	private static final long serialVersionUID = 442506149444989698L;
	private JTextField vstupAdresa,vstupUser,vstupPass;
	private JButton pripojPotvr,odpojeniPotvr;
	public HorniHorniPanel(GUIFrameOkno frame){
		vstupAdresa = new JTextField(30);
		vstupAdresa.setText("jdbc:mysql://");
		vstupAdresa.setToolTipText("napø. jdbc:mysql://localhost");
		vstupUser = new JTextField(15);
		vstupUser.setToolTipText("napø. Java1");
		vstupPass = new JPasswordField(15);
		vstupPass.setToolTipText("napø. asdasdasd");
		JLabel textAdresa = new JLabel("Vložte adresu serveru :");
		JLabel textUser = new JLabel("Vložte uživatele :");
		JLabel textPass = new JLabel("Vložte heslo :");
		pripojPotvr = new JButton("Pøipojení serveru");
		odpojeniPotvr = new JButton("Odpojení serveru");
		this.add(textAdresa);
		this.add(vstupAdresa);
		this.add(textUser);
		this.add(vstupUser);
		this.add(textPass);
		this.add(vstupPass);
		this.add(pripojPotvr);
		this.add(odpojeniPotvr);
		odpojeniPotvr.setEnabled(false);
		this.setBackground(new Color((float)0.55,(float)0.77,(float)0.88));
		pripojPotvr.addActionListener(new PripojServer(frame));						//	touto udalosti se pripojime a nacteme databaze
		pripojPotvr.setToolTipText("Pripojeni MySQL serveru");
		odpojeniPotvr.addActionListener(new OdpojServer(frame));  	
		odpojeniPotvr.setToolTipText("Odpojeni MySQL serveru");}					//	touto udalosti se odpojime od serveru
	//	****** Po stisknuti tlacitka pripojit se naplni choice1 seznamem databazi, POZOR jsme pripojeni na server a nikoliv na databazi
	private class PripojServer implements ActionListener {
		private GUIFrameOkno frame;
		PripojServer(GUIFrameOkno frame)	{	this.frame=frame;	}
		@SuppressWarnings("static-access")
		public void actionPerformed(ActionEvent arg0) {
			ZobrazeniDatabazi.sqlPripojeni = new PraceDatabaze(vstupAdresa.getText(),vstupUser.getText(),vstupPass.getText());	//	vytvorime objekt pripojeni
			ZobrazeniDatabazi.sqlPripojeni.napojeniMySQLServeru(frame);							//	napojeni se na SQLServer
			ZobrazeniDatabazi.sqlPripojeni.ziskaniDatabazi(frame);									//	do objektu kolekce ResultSet ulozime seznam databazi
			if (ZobrazeniDatabazi.sqlPripojeni.isPripojen())	{ 									//	pokud jsme pripojeni aktivujeme Choice a pridame databaze 
				pridaniDatabazi();											//	pridame databaze
				odpojeniPotvr.setEnabled(true);								//	zapneme tlacitko odpojeni
				pripojPotvr.setEnabled(false);								//	vypneme tlacitko pripojeni
				HorniSpodniPanel.vyberDatab.setEnabled(true);				//	deaktivujeme choice - vyber databaze
				HorniSpodniPanel.vyberTabulku.removeAll();					//	vymazeme prvky z vyberu tabulek
				HorniSpodniPanel.vyberTabulku.add("--------");		}		//	musime pridat jeden vychozi prvek do vyberu tabulek
			else 	
				JOptionPane.showMessageDialog(frame, "Údaje nejsou správné - nepøipojily jsme se ","POZOR - Server nebyl pøipojen", JOptionPane.WARNING_MESSAGE );		
			frame.setVisible(true); 	}									//	prekreslime GUI Frame
		private void pridaniDatabazi(){
			try {									//	dokud nebude konec vysledkove mnoziny jednotlive stringy vloz do Choice1
				while (ZobrazeniDatabazi.sqlPripojeni.getVysledekResultSet().next()) {		// provadej dokud je co	
					HorniSpodniPanel.vyberDatab.add(ZobrazeniDatabazi.sqlPripojeni.getVysledekResultSet().getString(1));	}	}	// Choice naplnime nazvy tabulek
			catch (SQLException e) {
				System.out.println("Nepodarilo vlozit seznam databazi ze serveru do choice - vyberu databazi");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
				System.out.println("Zde je zprava :  "+e.getMessage());
				e.printStackTrace();	}
			HorniSpodniPanel.vyberDatab.remove("--------");	}	}		
	class OdpojServer implements ActionListener {
		private GUIFrameOkno frame;
		OdpojServer(GUIFrameOkno frame)	{	this.frame=frame;	}
		public void actionPerformed(ActionEvent e) {
			odebraniDatabazi();												//	vycistime choice1
			odebraniTabulek();												//	vycistime choice2
			pripojPotvr.setEnabled(true);									//	aktivujem tlacitko pro pripojeni
			odpojeniPotvr.setEnabled(false);								//	vypneme tlacitko odpojeni
			HorniSpodniPanel.vyberTabulku.setEnabled(false);				//	deaktuvujeme choice 1 - vyber databazi
			HorniSpodniPanel.vyberDatab.setEnabled(false);					//	deaktivujeme choice 2 - vyber tabulel
			ZobrazeniDatabazi.sqlPripojeni.odpojeniMySQLServeru(frame);	//	odpojime se od serveru
			ZobrazeniDatabazi.sqlPripojeni=null;
			frame.refreshNastaveniHorniSpodniPanel();		
			frame.setVisible(true);	}										//	prekreslime GUI Frame
		private void odebraniDatabazi() {
			HorniSpodniPanel.vyberDatab.removeAll();						//	vymazeme seznam choice vyber databazi
			HorniSpodniPanel.vyberDatab.add("--------");	}				//	ponechame defaultni prvek v choice
		private void odebraniTabulek() {
			HorniSpodniPanel.vyberTabulku.removeAll();						//	vymazeme seznam choice vyber tabulek
			HorniSpodniPanel.vyberTabulku.add("--------");	}				//	ponechame defaultni prvek v choice
	}
}

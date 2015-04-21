package gui.panel;
import gui.frame.GUIFrameOkno;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;

import tabulka.TabulkaDatabaze;
import datab.spusteni.ZobrazeniDatabazi;
public class HorniSpodniPanel extends JPanel {
	protected static String jmenoDatabaze,jmenoTabulky;
	protected static Choice vyberDatab,vyberTabulku;
	private static final long serialVersionUID = 8024367216856608168L;
	public HorniSpodniPanel(GUIFrameOkno frame) {
		JLabel textSeznamDatab = new JLabel("Seznam databázi :");
		JLabel textSeznamTabulek = new JLabel("Seznam tabulek :");
		vyberDatab = new Choice();
		vyberDatab.setEnabled(false);
		vyberDatab.add("--------");
		vyberDatab.setPreferredSize(new Dimension(200,15));
		vyberDatab.addItemListener(new NacteniTabulek(frame)); 			//	touto udalosti nacteme tabulky ve zvolene databazi
		vyberTabulku = new Choice();
		vyberTabulku.add("--------");
		vyberTabulku.setPreferredSize(new Dimension(200,15));
		vyberTabulku.setEnabled(false);
		vyberTabulku.addItemListener(new VytvoreniTabulky(frame)); 		//	touto udalosti nacteme tabulky ve zvolene databazi
		this.add(textSeznamDatab);
		this.add(vyberDatab);
		this.add(textSeznamTabulek);
		this.add(vyberTabulku);
		this.setBackground(new Color((float)0.55,(float)0.77,(float)0.88));	}
	//	******	Jsme napojeni na server a proto je nutne se odhlasit a pripojit na konkretni databazi v danem serveru
	private class NacteniTabulek implements ItemListener {	//	a presunuti do Choice2
		private GUIFrameOkno frame;
		NacteniTabulek(GUIFrameOkno Frame){	this.frame=Frame;	}
		@SuppressWarnings("static-access")
		public void itemStateChanged(ItemEvent arg0) {
			if (ZobrazeniDatabazi.sqlPripojeni.isPripojen())	{ 	//	pokud jsme pripojeni aktivujeme Choice a pridame tabulky 
				jmenoDatabaze = arg0.getItem().toString();			//	do stringu ulozime jmeno konkretni databaze
				ZobrazeniDatabazi.sqlPripojeni.odpojeniServeruPripojeniDatabaze(jmenoDatabaze,frame);
				vyberTabulku.removeAll();							//	vymazeme vsechny tabulky
				ZobrazeniDatabazi.sqlPripojeni.ziskaniTabulek(jmenoDatabaze);
				pridaniTabulek();
				vyberTabulku.setEnabled(true);	}
				//zobrazeniPrvniTabulky();	}		//	vypnuli metodu pak se nebude automaticky zobrazovat prvni tabulka
			else 	
				JOptionPane.showMessageDialog(frame, "Údaje nejsou správné - nepøipojily jsme se ","POZOR - Server nebyl pøipojen", JOptionPane.WARNING_MESSAGE );		
			frame.setVisible(true); 	}									//	prekreslime GUI Frame
		private void pridaniTabulek() {
			try {		//	dokud nebude konec vysledkove mnoziny jednotlive stringy vloz do Choice1
				while (ZobrazeniDatabazi.sqlPripojeni.getVysledekResultSet().next()) {			
					HorniSpodniPanel.vyberTabulku.add(ZobrazeniDatabazi.sqlPripojeni.getVysledekResultSet().getString(1));	}	} 
			catch (SQLException e) {
				System.out.println("Nepodarilo vlozit seznam tabulek ze serveru do choice - vyberu tabulek");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
				System.out.println("Zde je zprava :  "+e.getMessage());
				e.printStackTrace();	}	}
		/*
		private void zobrazeniPrvniTabulky() {
			String nazevPrvniho = vyberTabulku.getItem(0).toLowerCase();
			System.out.println("Test muj"+nazevPrvniho);
			//	nacteme nove zvolenou tabulku
			H02_ZobrazeniDatabazi.tabulka = new TabulkaDatabaze02(H02_ZobrazeniDatabazi.sqlPripojeni,nazevPrvniho);
			frame.zniceniTabulky();								//	odeberem tabulku z Frame
			frame.nejnizsiPanel.nastaveniTlacitek();  			//	nastavime spodni tri tlacitka novy/uprav/smaz
			// nastavime nove data do tabulky
			H02_ZobrazeniDatabazi.vysledkovaTabulka = new JTable(H02_ZobrazeniDatabazi.tabulka);	
			// pridame tabulku do Frame
			frame.tabulkaPanel.NastaveniScrollPane(H02_ZobrazeniDatabazi.vysledkovaTabulka, frame);
		}*/
	}
	private class VytvoreniTabulky implements ItemListener {		//	do objektu JTable
		private GUIFrameOkno frame;
		VytvoreniTabulky(GUIFrameOkno Frame) { this.frame = Frame;	}
		public void itemStateChanged(ItemEvent arg0) {
			String dotaz = "SELECT * FROM "+new String(vyberTabulku.getSelectedItem());		//	do Stringu ulozime nazev Tabulky
			//	nacteme nove zvolenou tabulku
			ZobrazeniDatabazi.tabulka = new TabulkaDatabaze(ZobrazeniDatabazi.sqlPripojeni,dotaz);	
			frame.zniceniTabulky();								//	odeberem tabulku z Frame
			frame.getNejnizsiPanel().nastaveniTlacitek();  			//	nastavime spodni tri tlacitka novy/uprav/smaz
			// nastavime nove data do tabulky
			ZobrazeniDatabazi.vysledkovaTabulka = new JTable(ZobrazeniDatabazi.tabulka);	
			// pridame tabulku do Frame
			frame.getTabulkaPanel().NastaveniScrollPane(ZobrazeniDatabazi.vysledkovaTabulka, frame);	}	
	}
}

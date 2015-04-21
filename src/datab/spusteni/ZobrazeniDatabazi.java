/*	Spusteci trida celeho programu
 * 
 * 
 * 
 */
package datab.spusteni;
import javax.swing.*;
import gui.frame.GUIFrameOkno;
import tabulka.TabulkaDatabaze;
import mysql.PraceDatabaze;
public class ZobrazeniDatabazi {
	public static PraceDatabaze sqlPripojeni = null;		//	objekt zajistujici obsluhu mySQL serveru - propojeni,dotaz,vysledkovouMnozinu	
	public static TabulkaDatabaze tabulka = null;			//	objekt tabulky - nutny kvuli metaDat - potomek AbstractTAble model	
	public static JTable vysledkovaTabulka =  null;			//	pomocna defaultni tabulky - kvuli desingu
	public static void main(String [] args) {
		GUIFrameOkno program = new GUIFrameOkno();
		program.setVisible(true);	}
}

package gui.panel;
import gui.frame.GUIFrameOkno;
import java.awt.*;
import javax.swing.*;
import datab.spusteni.ZobrazeniDatabazi;
public class StredniPanel extends JPanel {
	private static final long serialVersionUID = -660268840143357687L;
	private JScrollPane scrollPane;
	public StredniPanel(GUIFrameOkno frame) {
		this.setLayout(new GridLayout()); 										//	zajistime zvetseni komponenty na celou plochu
		this.setBackground(new Color((int)21,(int)228,(int)241));		//	nabarvime pozadi objektu TabulkaPanel
		ZobrazeniDatabazi.vysledkovaTabulka =  new JTable(40, 10);
		NastaveniScrollPane(ZobrazeniDatabazi.vysledkovaTabulka,frame);	}
	public void NastaveniScrollPane(JTable tabulka,GUIFrameOkno frame) {
		tabulka.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);				//	update vsech sloupcu
		tabulka.setAutoCreateRowSorter(true);									//	nastavime sortovani tabulky
		scrollPane = new JScrollPane(tabulka,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(scrollPane);		//	do stredniho panelu vlozime scroolPane
		frame.add(this);			//	objekt this = je Stredni Panel
		frame.setVisible(true);	}	//	refreshneme JFrame
	public void znicTabulku() {
		scrollPane.remove(ZobrazeniDatabazi.vysledkovaTabulka);
		this.remove(scrollPane);	}
	public Insets getInsets() {									//	metoda definujici okraje vnitrnich komponent
		return new Insets(10,10,10,10);	}						//	vrch / vlevo / spodek / vpravo ..... v pixelech
}

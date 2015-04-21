package gui.panel;
import gui.frame.GUIFrameOkno;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import tabulka.TabulkaDatabaze;
import datab.spusteni.ZobrazeniDatabazi;
public class DolniPanel extends JPanel {
	private static final long serialVersionUID = -7014412282093781871L;
	private JButton novyTlac,ulozTlac,ukonciProgram,smazTlac;
	public DolniPanel(GUIFrameOkno frame) {
		this.setBackground(new Color((float)0.55,(float)0.77,(float)0.88));
		this.setLayout(new BorderLayout());
		novyTlac = new JButton("Vlož záznam");
		ulozTlac = new JButton("Ulož databázi");
		smazTlac = new JButton("Smaž záznam");
		ukonciProgram = new JButton("Ukonèi program");
		ukonciProgram.addActionListener(new KonecProgramu(frame));
		novyTlac.setEnabled(false);
		smazTlac.setEnabled(false);
		ulozTlac.setEnabled(false);
		JPanel vlevoPanel = new JPanel();
		JPanel vpravoPanel = new JPanel();
		vlevoPanel.setLayout(new FlowLayout());
		vlevoPanel.setBackground(new Color((float)0.55,(float)0.77,(float)0.88));
		vlevoPanel.add(novyTlac);
		vlevoPanel.add(ulozTlac);
		vlevoPanel.add(smazTlac);
		this.add(vlevoPanel,BorderLayout.WEST);
		vpravoPanel.add(ukonciProgram);
		vpravoPanel.setBackground(new Color((float)0.55,(float)0.77,(float)0.88));
		this.add(vpravoPanel,BorderLayout.EAST);	}
	public void nastaveniTlacitek(){
		if(TabulkaDatabaze.isNactenaDatabaze()) {
			novyTlac.setEnabled(true);
			smazTlac.setEnabled(true);
			ulozTlac.setEnabled(true);	}
		else {
			novyTlac.setEnabled(false);
			smazTlac.setEnabled(false);
			ulozTlac.setEnabled(false);	}	}
	private class KonecProgramu implements ActionListener {
		private GUIFrameOkno frame;
		KonecProgramu(GUIFrameOkno frame){
			this.frame=frame;	}
		public void actionPerformed(ActionEvent arg0) {
			if (ZobrazeniDatabazi.sqlPripojeni != null) {	
				//System.out.println("Test");
				ZobrazeniDatabazi.sqlPripojeni.odpojeniMySQLServeru(frame); }
			System.exit(1);	}
	}
}

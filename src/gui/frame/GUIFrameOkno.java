package gui.frame;
import gui.panel.*;
import java.awt.*;
import javax.swing.*;
public class GUIFrameOkno extends JFrame{
	private static final long serialVersionUID = -4613577556545725553L;
	private HorniSpodniPanel horniSpodniPanel;							
	private HorniHorniPanel hornihorniPanel;
	private JPanel panelHorni;
	private StredniPanel tabulkaPanel;
	private DolniPanel nejnizsiPanel;
	public DolniPanel getNejnizsiPanel() 											{	return nejnizsiPanel;	}
	public void setNejnizsiPanel(DolniPanel nejnizsiPanel) 							{	this.nejnizsiPanel = nejnizsiPanel;	}
	public StredniPanel getTabulkaPanel() {
		return tabulkaPanel;
	}
	public void setTabulkaPanel(StredniPanel tabulkaPanel) {
		this.tabulkaPanel = tabulkaPanel;
	}
	private void ZmenaGUI() {
		String LOOKANDFEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel"; 	// zvolen NIMBUS
		try{		
			UIManager.setLookAndFeel(LOOKANDFEEL);
			SwingUtilities.updateComponentTreeUI(this);
			this.pack();	}
		catch(Exception e){	
			e.printStackTrace();	}	}
	public GUIFrameOkno() {								//	konstruktor - sestaveni GUI - prezentacni vrstva
		NastaveniJFrame();
		NastaveniHorniHorniPanel();
		NastaveniHorniSpodniPanel();
		NastaveníStrednihoPanel();
		NastaveniDolnihoPanel();	}
	private void NastaveniUmisteniOkna() {
		Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int sirkaObrazovky = maxBounds.width;
		int vyskaObrazovky = maxBounds.height;
		int velikostOknaX=1300;int velikostOknaY=700;
		this.setSize(velikostOknaX,velikostOknaY);
		this.setLocation((sirkaObrazovky-velikostOknaX)/2,(vyskaObrazovky-velikostOknaY)/2);	}
	private void NastaveniJFrame() {
		super.setTitle("Program pro zobrazeni databazi - funkèní pro MySQL");
		ZmenaGUI();															//	nastavime GUI typu NIMBUS
		NastaveniUmisteniOkna();											//	nastavime JFrame na stred obrazovky
		panelHorni = new JPanel();											//	je to panel do ktereho se vlozi	horniHorniPanel,horniSpodniPanel									
		this.add(panelHorni,BorderLayout.NORTH);							//	pro hlavni JFrame je nahore
		panelHorni.setLayout(new BoxLayout(panelHorni,BoxLayout.Y_AXIS)); }	//	zajisti ze subpanely budou pokladany pod sebou v ose Y
	private void NastaveniHorniHorniPanel() {
		hornihorniPanel = new HorniHorniPanel(this);
		panelHorni.add(hornihorniPanel);	}
	private void NastaveniHorniSpodniPanel() {
		horniSpodniPanel= new HorniSpodniPanel(this);
		panelHorni.add(horniSpodniPanel);	}
	private void NastaveníStrednihoPanel() {
		setTabulkaPanel(new StredniPanel(this));		
		this.add(getTabulkaPanel(),BorderLayout.CENTER);	}
	private void NastaveniDolnihoPanel() {
		setNejnizsiPanel(new DolniPanel(this));
		this.add(getNejnizsiPanel(),BorderLayout.SOUTH);	}
	public void refreshNastaveniHorniSpodniPanel() {						//	nutny refresh kvuli moznosti opet zvolit stejnou stejnou databazi
		panelHorni.removeAll();												//	po odpojeni - nejaky Java problem se ztratou ItemListener
		panelHorni.add(hornihorniPanel);
		panelHorni.add(horniSpodniPanel);	}
	public void zniceniTabulky() {										//	musime odebrat cely panel kvuli refreshi tabulky
		getTabulkaPanel().znicTabulku();
		this.remove(getTabulkaPanel());	}
}

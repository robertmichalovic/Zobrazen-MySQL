package datab;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
class PripojDatab11 {													//	trida obsluhujici pripojeni k MySQL databazi
	private String Url,User,Pass;
	protected Connection pripojeniCon=null;
	protected Statement dotazState=null;
	protected ResultSet vysledekResultSet=null;
	protected static boolean pripojen=false;
	static { 															// staticky blok pro nacteni driveru databaze MySQL
		try{ 
			Class.forName("com.mysql.jdbc.Driver").newInstance();	}
		 catch(Exception e){  											// neni k dispozici driver v path - nastavit external *.jar v libraries
			 System.out.println("Driver nenacten - Chyba driveru");
			 System.out.println("Zde je zprava :  "+e.getMessage());
			 e.printStackTrace();	}	}
	PripojDatab11(String adresa,String user,String pass) {				//	konstruktor objektu
		this.Url=adresa;this.User=user;this.Pass=pass;	}
	protected void napojeniMySQLServeru(JFrame frame) {								//	provedem pripojeni k serveru a nastavime boolean na true
		try{ 
			pripojeniCon = DriverManager.getConnection(Url,User,Pass);
			pripojen=true;	}
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
	protected void ziskaniDatabazi(JFrame frame) {									//	do resultSetuUlozime seznam databazi
		try{ 
			dotazState = pripojeniCon.createStatement();						//	vytvorime objekt dotazu
			vysledekResultSet = dotazState.executeQuery("SHOW DATABASES");	}	//	do vysledkove mnoziny ulozime seznam databazi
		catch(SQLException e){ 
			JOptionPane.showMessageDialog(frame, "Nepodaøilo se ziskat seznam databazi ","POZOR - Server nebyl pøipojen", JOptionPane.WARNING_MESSAGE );
			System.out.println("Nepodarilo se zadat dotaz serveru nebo ziskat seznam databazi");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());
			e.printStackTrace();	}	}
	protected void ziskaniTabulek(String jmenoDatabaze){				//	do resultSetu ulozime seznam tabulek
		try {
			dotazState = pripojeniCon.createStatement();										//	vytvorime objekt dotazu
			vysledekResultSet = dotazState.executeQuery("SHOW TABLES FROM "+jmenoDatabaze);	} 	//	objektu dotazu nacteme tabuky
		catch (SQLException e) {
			System.out.println("Nepodarilo se vytvorit dotaz serveru nebo ziskat seznam tabulek");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());
			e.printStackTrace();	}	}
	protected void odpojeniMySQLServeru(GUIFrameOkno frame) {		//	odpojime objekty a vymazeme a opet nastavime pripojeni na false
		try {
			if(TabulkaDatabaze02.nactenaDatabaze == true) {		//	pokud je nactena databaze je nutne provest vymazani tabulky
				frame.zniceniTabulky();							//	do defaultniho stavu
				ZobrazeniDatabazi.vysledkovaTabulka =  new JTable(40, 10);
				frame.tabulkaPanel.NastaveniScrollPane(ZobrazeniDatabazi.vysledkovaTabulka, frame);
				TabulkaDatabaze02.nactenaDatabaze = false;	}
			frame.nejnizsiPanel.nastaveniTlacitek();  			//	nastavime spodni tri tlacitka novy/uprav/smaz
			vysledekResultSet.close();
			vysledekResultSet=null;
			dotazState.close();
			dotazState=null;
			pripojeniCon.close();
			pripojeniCon=null;
			pripojen=false;	} 
		catch (SQLException e) {
			System.out.println("Nepodarilo se provest odpojeni serveru");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());
			e.printStackTrace();	}	}
	protected void odpojeniServeruPripojeniDatabaze(String jmenoDatabaze,GUIFrameOkno frame) {	//	odpojime se od serveru a prihlasime se k databazi
		try {
			if(TabulkaDatabaze02.nactenaDatabaze == true) {		//	pokud je nactena databaze je nutne provest vymazani tabulky
				frame.zniceniTabulky();							//	do defaultniho stavu
				ZobrazeniDatabazi.vysledkovaTabulka =  new JTable(40, 10);
				frame.tabulkaPanel.NastaveniScrollPane(ZobrazeniDatabazi.vysledkovaTabulka, frame);
				TabulkaDatabaze02.nactenaDatabaze = false;	}
			vysledekResultSet.close();
			vysledekResultSet=null;
			dotazState.close();
			dotazState=null;
			pripojeniCon.close();
			pripojeniCon=null;
			pripojen=false;
			pripojeniCon = DriverManager.getConnection(Url+"/"+jmenoDatabaze,User,Pass);
			pripojen=true;	} 
		catch (SQLException e) {
			System.out.println("Nepodarilo se provest pripojeni ke konkretni databazi");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());
			e.printStackTrace();	}	}
}
class TabulkaDatabaze02 extends AbstractTableModel {						//	trida obsluhujici samotnou tabulku
	private static final long serialVersionUID = 1608127932116736832L;
	private PripojDatab11 objekt;
	private Statement dotazPripojeni = null;
	private ResultSet vysledkovaMnozina = null;
	private ResultSetMetaData metaData = null;
	private int pocetRadku;
	protected static boolean nactenaDatabaze = false;
	TabulkaDatabaze02(PripojDatab11 pripojeniDatabaze,String dotaz) {		//	konstruktor
		this.objekt = pripojeniDatabaze;
		nastavDotaz(dotaz);	}
	@SuppressWarnings("static-access")
	private void nastavDotaz(String dotaz){
		if(!objekt.pripojen) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		try {
			dotazPripojeni=objekt.pripojeniCon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			vysledkovaMnozina = dotazPripojeni.executeQuery(dotaz);		//	vzneseme dotaz
			metaData = vysledkovaMnozina.getMetaData();					//	preneseme vystup z vysledkove mnoziny do metaDat
			vysledkovaMnozina.last();									//	skocime kolekci na posledni sloupce kvuli zjisteni poctu radku
			pocetRadku = vysledkovaMnozina.getRow();					//	ulozime pocet radku		....	OK
			nactenaDatabaze = true;										//	provedli jsme zmenu 
			this.fireTableStructureChanged();	}						//	tato metoda provede zmenu tabulky dle aktualniho nastaveni
		catch (SQLException e) {
			System.out.println("Nepodarilo se provest operace prevedeni tabulek do metaDat v objetku AbstractTableModel");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Zde je zprava :  "+e.getMessage());		
			e.printStackTrace();	}	}
	@SuppressWarnings("static-access")
	public int getRowCount() {																//	vrati pocet radku v modelu
		if(!objekt.pripojen) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		return pocetRadku;	}	//	objekt metaData neobsahuje metodu ziskej pocet radku a proto se to resi pres vysledkovou mnozinu
	@SuppressWarnings("static-access")
	public int getColumnCount() {															//	vraci pocet sloupcu v modelu
		if(!objekt.pripojen) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		try {
			return metaData.getColumnCount();	} 
		catch (SQLException e) {
			e.printStackTrace();	}
		return 0;	}
	@SuppressWarnings("static-access")
	public Object getValueAt(int radek, int sloupec) {								//	vraci hodnotu z tabulky
		if(!objekt.pripojen) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		try {
			vysledkovaMnozina.absolute(radek+1);											// JTable ma [0,0] a ResultSet[1,1]
			return vysledkovaMnozina.getObject(sloupec+1);	} 
		catch (SQLException e) {
			e.printStackTrace();	}
		return "";	}
	@SuppressWarnings("static-access")
	public String getColumnName(int sloupec) {												//	ziskej jmeno sloupce - pretizena
		if(!objekt.pripojen) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		try {
			return metaData.getColumnName(sloupec+1);	} 
		catch (SQLException e) {
			e.printStackTrace();	}
		return "";	}
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public Class getColumnClass (int sloupec) {												//	ziskas objekt-tridu pro sloupec
		if(!objekt.pripojen) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		try {
			String jmenoTridy = metaData.getColumnClassName(sloupec+1);
			return Class.forName(jmenoTridy);	} 
		catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();	}
		return Object.class;	}
	public boolean isCellEditable(int radek, int sloupec) {		//	pokyn pro prezentaèní vrstvu, aby této buòce dovolila zmìnu
		return true;	}
	//	vice info viz. soubor tabulky AbstracTableModel.rtf ............. šilené ale funkèní
	public void setValueAt(Object novaHodnota, int radek, int sloupec) {	//	datová vrstva provedenou zmìnu uloží do tabulky Object [][]
		Object staraHodnota = getValueAt(radek,sloupec);			//	nacteme puvodni hodnotu
		if(novaHodnota==null && staraHodnota==null) {				//	kontrola prazdnym referencim
			return ;	}
		if(staraHodnota !=null) {									//	kontrola jestli se stara nerovna nove
			if(staraHodnota.equals(novaHodnota)){
				return;	}	}
		try {
			vysledkovaMnozina.absolute(radek+1);							//	JTable ma [0,0] a ResultSet[1,1]
			updateResultSet(vysledkovaMnozina,novaHodnota,radek,sloupec);	//	volame metodu updateResultSet a ulozime hodnotu
			vysledkovaMnozina.updateRow();	}									//	ve vysledkove mnozime upravime radek
			//objekt.pripojeniCon.commit(); 	}	// ************** automaticky ulozime zmenu v pripojeni a tedy i na SQL serveru
			catch (SQLException e) {
				System.out.println("Nepodarilo se nastavit ResultSet o 1 vyssi");
				e.printStackTrace();	}					
			catch (Exception e) {
				System.out.println("Nepodarilo se prenastavit ResultSet na novou hodnotu");
				e.printStackTrace();	}
		fireTableCellUpdated(radek,sloupec);	}							//	prepiseme tabulku
	private void updateResultSet(ResultSet rs, Object aValue, int rowIndex, int columnIndex) throws Exception	{
		if ( aValue == null || aValue.toString().trim().length() == 0 )	{	//	je-li bunka prazdna vloz nulu
			rs.updateNull(columnIndex+1);	return; 	}					//	zde se uklada nula
		switch(metaData.getColumnType(columnIndex+1))	{		
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				rs.updateString(columnIndex+1, (String)aValue);return;
			case Types.INTEGER:
				rs.updateInt(columnIndex+1, ((Integer)aValue).intValue());return;
			case Types.SMALLINT:
				rs.updateShort(columnIndex+1, ((Short)aValue).shortValue());return;
			case Types.TINYINT:
				rs.updateByte(columnIndex+1, ((Byte)aValue).byteValue());return;
			case Types.FLOAT:
			case Types.DOUBLE:
				rs.updateDouble(columnIndex+1, ((Double)aValue).doubleValue());return;
			case Types.REAL:
				rs.updateFloat(columnIndex+1, ((Float)aValue).floatValue());return;
			case Types.BIT:
				rs.updateBoolean(columnIndex+1, ((Boolean)aValue).booleanValue());return;
			case Types.BIGINT:
				rs.updateLong(columnIndex+1, ((Long)aValue).longValue());return;
			case Types.NUMERIC:
			case Types.DATE:
				rs.updateDate(columnIndex+1, (java.sql.Date)aValue);return;
			/* *************** deaktivujeme tyto typy promennych ******* az budou potreba tak se aktivuji
			case Types.DECIMAL:
				rs.updateBigDecimal(columnIndex+1, (BigDecimal)aValue);	return;
			case Types.TIMESTAMP:
				if (dateTime == Types.TIMESTAMP)	{
					rs.updateTimestamp(columnIndex+1, (Timestamp)aValue);	}
				if (dateTime == Types.TIME)	{
					rs.updateTime(columnIndex+1, (Time)aValue);	}
				if (dateTime == Types.DATE)	{
					rs.updateDate(columnIndex+1, (java.sql.Date)aValue);	}	return; */
			case Types.TIME:	       
				rs.updateTime(columnIndex+1, (Time)aValue);	return;
			default:
				rs.updateObject(columnIndex+1, aValue);	return;	} 	}
}
class StredniPanel extends JPanel {
	private static final long serialVersionUID = -660268840143357687L;
	private JScrollPane scrollPane;
	StredniPanel(GUIFrameOkno frame) {
		this.setLayout(new GridLayout()); 										//	zajistime zvetseni komponenty na celou plochu
		this.setBackground(new Color((int)21,(int)228,(int)241));		//	nabarvime pozadi objektu TabulkaPanel
		ZobrazeniDatabazi.vysledkovaTabulka =  new JTable(40, 10);
		NastaveniScrollPane(ZobrazeniDatabazi.vysledkovaTabulka,frame);	}
	 protected void NastaveniScrollPane(JTable tabulka,GUIFrameOkno frame) {
		tabulka.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);				//	update vsech sloupcu
		tabulka.setAutoCreateRowSorter(true);									//	nastavime sortovani tabulky
		scrollPane = new JScrollPane(tabulka,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(scrollPane);		//	do stredniho panelu vlozime scroolPane
		frame.add(this);			//	objekt this = je Stredni Panel
		frame.setVisible(true);	}	//	refreshneme JFrame
	protected void znicTabulku() {
		scrollPane.remove(ZobrazeniDatabazi.vysledkovaTabulka);
		this.remove(scrollPane);	}
	public Insets getInsets() {									//	metoda definujici okraje vnitrnich komponent
		return new Insets(10,10,10,10);	}						//	vrch / vlevo / spodek / vpravo ..... v pixelech
}
class DolniPanel extends JPanel {
	private static final long serialVersionUID = -7014412282093781871L;
	private JButton novyTlac,ulozTlac,ukonciProgram,smazTlac;
	DolniPanel(GUIFrameOkno frame) {
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
	protected void nastaveniTlacitek(){
		if(TabulkaDatabaze02.nactenaDatabaze) {
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
class HorniSpodniPanel extends JPanel {
	protected static String jmenoDatabaze,jmenoTabulky;
	protected static Choice vyberDatab,vyberTabulku;
	private static final long serialVersionUID = 8024367216856608168L;
	HorniSpodniPanel(GUIFrameOkno frame) {
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
			if (ZobrazeniDatabazi.sqlPripojeni.pripojen)	{ 	//	pokud jsme pripojeni aktivujeme Choice a pridame tabulky 
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
				while (ZobrazeniDatabazi.sqlPripojeni.vysledekResultSet.next()) {			
					HorniSpodniPanel.vyberTabulku.add(ZobrazeniDatabazi.sqlPripojeni.vysledekResultSet.getString(1));	}	} 
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
			ZobrazeniDatabazi.tabulka = new TabulkaDatabaze02(ZobrazeniDatabazi.sqlPripojeni,dotaz);	
			frame.zniceniTabulky();								//	odeberem tabulku z Frame
			frame.nejnizsiPanel.nastaveniTlacitek();  			//	nastavime spodni tri tlacitka novy/uprav/smaz
			// nastavime nove data do tabulky
			ZobrazeniDatabazi.vysledkovaTabulka = new JTable(ZobrazeniDatabazi.tabulka);	
			// pridame tabulku do Frame
			frame.tabulkaPanel.NastaveniScrollPane(ZobrazeniDatabazi.vysledkovaTabulka, frame);	}	
	}
}
class HorniHorniPanel extends JPanel {	
	private static final long serialVersionUID = 442506149444989698L;
	private JTextField vstupAdresa,vstupUser,vstupPass;
	private JButton pripojPotvr,odpojeniPotvr;
	HorniHorniPanel(GUIFrameOkno frame){
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
			ZobrazeniDatabazi.sqlPripojeni = new PripojDatab11(vstupAdresa.getText(),vstupUser.getText(),vstupPass.getText());	//	vytvorime objekt pripojeni
			ZobrazeniDatabazi.sqlPripojeni.napojeniMySQLServeru(frame);							//	napojeni se na SQLServer
			ZobrazeniDatabazi.sqlPripojeni.ziskaniDatabazi(frame);									//	do objektu kolekce ResultSet ulozime seznam databazi
			if (ZobrazeniDatabazi.sqlPripojeni.pripojen)	{ 									//	pokud jsme pripojeni aktivujeme Choice a pridame databaze 
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
				while (ZobrazeniDatabazi.sqlPripojeni.vysledekResultSet.next()) {		// provadej dokud je co	
					HorniSpodniPanel.vyberDatab.add(ZobrazeniDatabazi.sqlPripojeni.vysledekResultSet.getString(1));	}	}	// Choice naplnime nazvy tabulek
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
class GUIFrameOkno extends JFrame{
	private static final long serialVersionUID = -4613577556545725553L;
	private HorniSpodniPanel horniSpodniPanel;							
	private HorniHorniPanel hornihorniPanel;
	protected JPanel panelHorni;
	protected StredniPanel tabulkaPanel;
	protected DolniPanel nejnizsiPanel;
	private void ZmenaGUI() {
		String LOOKANDFEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel"; 	// zvolen NIMBUS
		try{		
			UIManager.setLookAndFeel(LOOKANDFEEL);
			SwingUtilities.updateComponentTreeUI(this);
			this.pack();	}
		catch(Exception e){	
			e.printStackTrace();	}	}
	private void NastaveniUmisteniOkna() {
		Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int sirkaObrazovky = maxBounds.width;
		int vyskaObrazovky = maxBounds.height;
		int velikostOknaX=1300;int velikostOknaY=700;
		this.setSize(velikostOknaX,velikostOknaY);
		this.setLocation((sirkaObrazovky-velikostOknaX)/2,(vyskaObrazovky-velikostOknaY)/2);	}
	GUIFrameOkno() {								//	konstruktor - sestaveni GUI - prezentacni vrstva
		NastaveniJFrame();
		NastaveniHorniHorniPanel();
		NastaveniHorniSpodniPanel();
		NastaveníStrednihoPanel();
		NastaveniDolnihoPanel();	}
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
		tabulkaPanel = new StredniPanel(this);		
		this.add(tabulkaPanel,BorderLayout.CENTER);	}
	private void NastaveniDolnihoPanel() {
		nejnizsiPanel = new DolniPanel(this);
		this.add(nejnizsiPanel,BorderLayout.SOUTH);	}
	protected void refreshNastaveniHorniSpodniPanel() {						//	nutny refresh kvuli moznosti opet zvolit stejnou stejnou databazi
		panelHorni.removeAll();												//	po odpojeni - nejaky Java problem se ztratou ItemListener
		panelHorni.add(hornihorniPanel);
		panelHorni.add(horniSpodniPanel);	}
	protected void zniceniTabulky() {										//	musime odebrat cely panel kvuli refreshi tabulky
		tabulkaPanel.znicTabulku();
		this.remove(tabulkaPanel);	}
}
public class ZobrazeniDatabazi {
	protected static PripojDatab11 sqlPripojeni = null;			//	objekt zajistujici obsluhu mySQL serveru - propojeni,dotaz,vysledkovouMnozinu	
	protected static TabulkaDatabaze02 tabulka = null;			//	objekt tabulky - nutny kvuli metaDat - potomek AbstractTAble model	
	protected static JTable vysledkovaTabulka =  null;			//	pomocna defaultni tabulky - kvuli desingu
	public static void main(String [] args) {
		GUIFrameOkno program = new GUIFrameOkno();
		program.setVisible(true);	}
}

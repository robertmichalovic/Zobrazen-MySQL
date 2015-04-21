package tabulka;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import javax.swing.table.AbstractTableModel;
import mysql.PraceDatabaze;
public class TabulkaDatabaze extends AbstractTableModel {						//	trida obsluhujici samotnou tabulku
	private static final long serialVersionUID = 1608127932116736832L;
	private PraceDatabaze objekt;
	private Statement dotazPripojeni = null;
	private ResultSet vysledkovaMnozina = null;
	private ResultSetMetaData metaData = null;
	private int pocetRadku;
	private static boolean nactenaDatabaze = false;
	public static boolean isNactenaDatabaze() 						{	return nactenaDatabaze;	}
	public static void setNactenaDatabaze(boolean nactenaDatabaze) 	{	TabulkaDatabaze.nactenaDatabaze = nactenaDatabaze;	}
	public TabulkaDatabaze(PraceDatabaze pripojeniDatabaze,String dotaz) {		//	konstruktor
		this.objekt = pripojeniDatabaze;
		nastavDotaz(dotaz);	}
	@SuppressWarnings("static-access")
	private void nastavDotaz(String dotaz){
		if(!objekt.isPripojen()) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		try {
			dotazPripojeni=objekt.getPripojeniCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			vysledkovaMnozina = dotazPripojeni.executeQuery(dotaz);		//	vzneseme dotaz
			metaData = vysledkovaMnozina.getMetaData();					//	preneseme vystup z vysledkove mnoziny do metaDat
			vysledkovaMnozina.last();									//	skocime kolekci na posledni sloupce kvuli zjisteni poctu radku
			pocetRadku = vysledkovaMnozina.getRow();					//	ulozime pocet radku		....	OK
			setNactenaDatabaze(true);										//	provedli jsme zmenu 
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
		if(!objekt.isPripojen()) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		return pocetRadku;	}	//	objekt metaData neobsahuje metodu ziskej pocet radku a proto se to resi pres vysledkovou mnozinu
	@SuppressWarnings("static-access")
	public int getColumnCount() {															//	vraci pocet sloupcu v modelu
		if(!objekt.isPripojen()) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		try {
			return metaData.getColumnCount();	} 
		catch (SQLException e) {
			e.printStackTrace();	}
		return 0;	}
	@SuppressWarnings("static-access")
	public Object getValueAt(int radek, int sloupec) {								//	vraci hodnotu z tabulky
		if(!objekt.isPripojen()) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		try {
			vysledkovaMnozina.absolute(radek+1);											// JTable ma [0,0] a ResultSet[1,1]
			return vysledkovaMnozina.getObject(sloupec+1);	} 
		catch (SQLException e) {
			e.printStackTrace();	}
		return "";	}
	@SuppressWarnings("static-access")
	public String getColumnName(int sloupec) {												//	ziskej jmeno sloupce - pretizena
		if(!objekt.isPripojen()) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
		try {
			return metaData.getColumnName(sloupec+1);	} 
		catch (SQLException e) {
			e.printStackTrace();	}
		return "";	}
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public Class getColumnClass (int sloupec) {												//	ziskas objekt-tridu pro sloupec
		if(!objekt.isPripojen()) throw new IllegalStateException("Neni pripojen k databazi");	//	pokud neni pripojeni proved vyjimku
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

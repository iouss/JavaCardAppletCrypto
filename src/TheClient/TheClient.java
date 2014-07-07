package TheClient;

import java.io.*;
import opencard.core.service.*;
import opencard.core.terminal.*;
import opencard.core.util.*;
import opencard.opt.util.*;




public class TheClient {

	private PassThruCardService servClient = null;
	boolean DISPLAY = true;
	boolean loop = true;

	static final byte CLA					= (byte)0x00;
	static final byte P1					= (byte)0x00;
	static final byte P2					= (byte)0x00;
	static final byte UPDATECARDKEY				= (byte)0x14;
	static final byte UNCIPHERFILEBYCARD			= (byte)0x13;
	static final byte CIPHERFILEBYCARD			= (byte)0x12;
	static final byte CIPHERANDUNCIPHERNAMEBYCARD		= (byte)0x11;
	static final byte READFILEFROMCARD			= (byte)0x10;
	static final byte WRITEFILETOCARD			= (byte)0x09;
	static final byte UPDATEWRITEPIN			= (byte)0x08;
	static final byte UPDATEREADPIN				= (byte)0x07;
	static final byte DISPLAYPINSECURITY			= (byte)0x06;
	static final byte DESACTIVATEACTIVATEPINSECURITY	= (byte)0x05;
	static final byte ENTERREADPIN				= (byte)0x04;
	static final byte ENTERWRITEPIN				= (byte)0x03;
	static final byte READNAMEFROMCARD			= (byte)0x02;
	static final byte WRITENAMETOCARD			= (byte)0x01;


	public TheClient() {
		try {
			SmartCard.start();
			System.out.print( "Smartcard inserted?... " ); 

			CardRequest cr = new CardRequest (CardRequest.ANYCARD,null,null); 

			SmartCard sm = SmartCard.waitForCard (cr);

			if (sm != null) {
				System.out.println ("got a SmartCard object! \n");
			} else
				System.out.println( "did not get a SmartCard object! \n" );

			this.initNewCard( sm ); 

			SmartCard.shutdown();

		} catch( Exception e ) {
			System.out.println( "TheClient error: " + e.getMessage() );
		}
		java.lang.System.exit(0) ;
	}

	private ResponseAPDU sendAPDU(CommandAPDU cmd) {
		return sendAPDU(cmd, true);
	}

	private ResponseAPDU sendAPDU( CommandAPDU cmd, boolean display ) {
		ResponseAPDU result = null;
		try {
			result = this.servClient.sendCommandAPDU( cmd );
			if(display)
				displayAPDU(cmd, result);
		} catch( Exception e ) {
			System.out.println( "Exception caught in sendAPDU: " + e.getMessage() );
			java.lang.System.exit( -1 );
		}
		return result;
	}


	/************************************************
	 * *********** BEGINNING OF TOOLS ***************
	 * **********************************************/


	private String apdu2string( APDU apdu ) {
		return removeCR( HexString.hexify( apdu.getBytes() ) );
	}


	public void displayAPDU( APDU apdu ) {
		System.out.println( removeCR( HexString.hexify( apdu.getBytes() ) ) + "\n" );
	}


	public void displayAPDU( CommandAPDU termCmd, ResponseAPDU cardResp ) {
		System.out.println( "--> Term: " + removeCR( HexString.hexify( termCmd.getBytes() ) ) );
		System.out.println( "<-- Card: " + removeCR( HexString.hexify( cardResp.getBytes() ) ) );
	}


	private String removeCR( String string ) {
		return string.replace( '\n', ' ' );
	}


	/******************************************
	 * *********** END OF TOOLS ***************
	 * ****************************************/


	private boolean selectApplet() {
		boolean cardOk = false;
		try {
			CommandAPDU cmd = new CommandAPDU( new byte[] {
				(byte)0x00, (byte)0xA4, (byte)0x04, (byte)0x00, (byte)0x0A,
				    (byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x62, 
				    (byte)0x03, (byte)0x01, (byte)0x0C, (byte)0x06, (byte)0x01
			} );
			ResponseAPDU resp = this.sendAPDU( cmd );
			if( this.apdu2string( resp ).equals( "90 00" ) )
				cardOk = true;
		} catch(Exception e) {
			System.out.println( "Exception caught in selectApplet: " + e.getMessage() );
			java.lang.System.exit( -1 );
		}
		return cardOk;
	}


	private void initNewCard( SmartCard card ) {
		if( card != null )
			System.out.println( "Smartcard inserted\n" );
		else {
			System.out.println( "Did not get a smartcard" );
			System.exit( -1 );
		}

		System.out.println( "ATR: " + HexString.hexify( card.getCardID().getATR() ) + "\n");


		try {
			this.servClient = (PassThruCardService)card.getCardService( PassThruCardService.class, true );
		} catch( Exception e ) {
			System.out.println( e.getMessage() );
		}

		System.out.println("Applet selecting...");
		if( !this.selectApplet() ) {
			System.out.println( "Wrong card, no applet to select!\n" );
			System.exit( 1 );
			return;
		} else 
			System.out.println( "Applet selected" );

		mainLoop();
	}


	void updateCardKey() {
	}


	void uncipherFileByCard() {
	}


	void cipherFileByCard() {
	}


	void cipherAndUncipherNameByCard() {
	}

public void TableauDeBytesVersFichier (byte[] tableau, String fic) throws java.io.IOException {
		try {
		    java.io.FileWriter f = new java.io.FileWriter(fic);
		    for (int i = 0; i < tableau.length; i++) {
   			    f.write((int)tableau[i]);
	    	}	 
			f.close();
		}
		catch(IOException e){
			System.out.println(e+"erreur lors de la lecture du fichier");
		}
 
	}
 
 
	/**
	 * Permet de copier le contenu d'un fichier dans un tableau byte[]
	 **/ 
	public byte[] FichierVersTableauDeBytes (String fic) throws java.io.IOException {
		java.io.File fichier = new File(fic);
		int i=0;
		int ch;
		byte[] tableau;
		try {			
			FileInputStream f= new FileInputStream(fichier);
			try{
				tableau = new byte[(int)fichier.length()];				
				while((ch=f.read())!=-1){ 
					tableau[i]=(byte)ch;
					i++;						
				}
			}
			finally{
				f.close();
			}
			return tableau;
		}
		catch(FileNotFoundException ef){
			System.out.println("fichier introuvable");
			return null;
		}
		catch(IOException e){
			System.out.println(e+"erreur lors de la lecture du fichier");
			return null;			
		}		
	}
	
	void readFileFromCard() {
		System.out.println( "Lecture fichier " );
		int maxSize=127;
		int nbrpaquet;
		int tailleDernierPaquet;
		String chaine="";
		String fichier ="fileToRead.txt";
		File tmpFile;
		tmpFile = new File("fileToRead.txt");
		int tmpFileSize= (int)tmpFile.length();
		nbrpaquet=(tmpFileSize/maxSize);
		System.out.println( "nbrpaquet "+ nbrpaquet);
		tailleDernierPaquet=tmpFileSize%maxSize;
		System.out.println( "tailleDernierPaquet "+ tailleDernierPaquet);
		byte [] tab = new byte[tmpFileSize];
		try{
		tab=FichierVersTableauDeBytes(fichier);
		}
		catch(IOException e){
			System.out.println(e+"erreur lors de la lecture du fichier");		
		}		
		byte [] apdu= new byte[5+maxSize];
		byte [] apduLast= new byte[5+tailleDernierPaquet];
		for (int j=0;j<nbrpaquet;j++){
			byte [] header= {CLA,READFILEFROMCARD,(byte)(j+1),(byte)j,(byte)maxSize };
			System.arraycopy(header, (short)0,apdu,(short)0,(short)header.length);
			System.arraycopy(tab, (short)j*maxSize,apdu,(short)header.length,(short)maxSize);
			CommandAPDU cmd = new CommandAPDU( apdu );
			ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
		}	
		if	(tailleDernierPaquet!=0) {
			byte [] header= {CLA,READFILEFROMCARD,(byte)(nbrpaquet+1),(byte)127,(byte)tailleDernierPaquet };
			System.arraycopy(header,(short)0,apduLast,(short)0,(short)header.length);
			System.arraycopy(tab,(short)nbrpaquet*maxSize,apduLast,(short)header.length,(short)tailleDernierPaquet);
			CommandAPDU cmd = new CommandAPDU(apduLast);	
			ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
		}
		
		
	}


	void writeFileToCard() {
		System.out.println( "Ecriture de fichier " );
		int maxSize=127;
		int nbrpaquet;
		int tailleDernierPaquet;
		String chaine="";
		String fichier ="fileToRead.txt";
		File tmpFile;
		tmpFile = new File("fileToRead.txt");
		int tmpFileSize= (int)tmpFile.length();
		nbrpaquet=(tmpFileSize/maxSize);
		System.out.println( "nbrpaquet "+ nbrpaquet);
		tailleDernierPaquet=tmpFileSize%maxSize;
		System.out.println( "tailleDernierPaquet "+ tailleDernierPaquet);
		byte [] tab = new byte[tmpFileSize];
		try{
		tab=FichierVersTableauDeBytes(fichier);
		}
		catch(IOException e){
			System.out.println(e+"erreur lors de la lecture du fichier");		
		}		
		byte [] apdu= new byte[5+maxSize];
		byte [] apduLast= new byte[5+tailleDernierPaquet];
		/*for (int j=0;j<nbrpaquet;j++){
			byte [] header= {CLA,WRITEFILETOCARD,(byte)(j+1),(byte)j,(byte)maxSize };
			System.arraycopy(header, (short)0,apdu,(short)0,(short)header.length);
			System.arraycopy(tab, (short)j*maxSize,apdu,(short)header.length,(short)maxSize);
			CommandAPDU cmd = new CommandAPDU( apdu );
			ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
		}	
		if	(tailleDernierPaquet!=0) {
			byte [] header= {CLA,WRITEFILETOCARD,(byte)(nbrpaquet+1),(byte)127,(byte)tailleDernierPaquet };
			System.arraycopy(header,(short)0,apduLast,(short)0,(short)header.length);
			System.arraycopy(tab,(short)nbrpaquet*maxSize,apduLast,(short)header.length,(short)tailleDernierPaquet);
			CommandAPDU cmd = new CommandAPDU(apduLast);	
			ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
		}*/
		/*System.out.println( "Ecriture fichier  " );
		int maxSize=2;
		int nbrpaquet;
		int tailleDernierPaquet;
		//byte [] data= {'0','1','2','3','4'};
		FileWriter fstream; 
		BufferedWriter out;
		String message="";
		try{
			// Create file 
			fstream = new FileWriter("FileWrittenFromKeyBord.txt");
			out = new BufferedWriter(fstream);
			message= readKeyboard() ;
			out.write(message);
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		byte [] data= message.getBytes();
		nbrpaquet=(data.length/maxSize);
		System.out.println( "nbrpaquet "+ nbrpaquet);
		tailleDernierPaquet=data.length%maxSize;
		System.out.println( "tailleDernierPaquet "+ tailleDernierPaquet);
		byte [] apdu= new byte[5+maxSize];
		byte [] apduLast= new byte[5+tailleDernierPaquet];
		for (int j=0;j<nbrpaquet;j++){
			byte [] header= {CLA,WRITEFILETOCARD,(byte)(j+1),(byte)127,(byte)maxSize };
			System.arraycopy(header, (short)0,apdu,(short)0,(short)header.length);
			System.arraycopy(data, (short)j*maxSize,apdu,(short)header.length,(short)maxSize);
			CommandAPDU cmd = new CommandAPDU( apdu );
			ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
		}	
		if	(tailleDernierPaquet!=0) {
			byte [] header= {CLA,WRITEFILETOCARD,(byte)(nbrpaquet+1),(byte)127,(byte)tailleDernierPaquet };
			System.arraycopy(header,(short)0,apduLast,(short)0,(short)header.length);
			System.arraycopy(data,(short)nbrpaquet*maxSize,apduLast,(short)header.length,(short)tailleDernierPaquet);
			CommandAPDU cmd = new CommandAPDU(apduLast);	
			ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
		}*/
	}


	void updateWritePIN() {
		System.out.println( "Veuillez entrer votre nouveau code pin d'ecriture SVP " );
		String pin= readKeyboard() ;
		int lc = pin.length();
		byte [] apdu= new byte[5+lc];
		byte [] header= {CLA,UPDATEWRITEPIN,P1,P2,(byte)lc };
		byte [] data= pin.getBytes();
		System.arraycopy(header, (short)0,apdu,(short)0,(short)header.length);
		System.arraycopy(data, (short)0,apdu,(short)header.length,(short)data.length);
	    System.out.println("// Update write pin ");
        CommandAPDU cmd = new CommandAPDU( apdu );
        ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
	}


	void updateReadPIN() {
		System.out.println( "Veuillez entrer votre nouveau code pin de lecture SVP " );
		String pin= readKeyboard() ;
		int lc = pin.length();
		byte [] apdu= new byte[5+lc];
		byte [] header= {CLA,UPDATEREADPIN,P1,P2,(byte)lc };
		byte [] data= pin.getBytes();
		System.arraycopy(header, (short)0,apdu,(short)0,(short)header.length);
		System.arraycopy(data, (short)0,apdu,(short)header.length,(short)data.length);
	    System.out.println("// Write pin ");
        CommandAPDU cmd = new CommandAPDU( apdu );
        ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
	}


	void displayPINSecurity() {
		System.out.println( "display PIN Security" );
		byte [] apdu= {CLA,DISPLAYPINSECURITY,P1,P2};
		System.out.println("// display PIN Security ");
        CommandAPDU cmd = new CommandAPDU( apdu );
        ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
		byte[] buffer = resp.getBytes();
		if (buffer[0]==0x11)
			System.out.println("Desactivate  PIN Security ");
		if (buffer[0]==0x00)
			System.out.println("Activate  PIN Security ");
	
	}


	void desactivateActivatePINSecurity() {
		System.out.println( "Desactivation du pin ... " );
		byte [] apdu= {CLA,DESACTIVATEACTIVATEPINSECURITY,P1,P2};
		System.out.println("// desactivate Activate PIN Security ");
        CommandAPDU cmd = new CommandAPDU( apdu );
		ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
       		
	}


	void enterReadPIN() {
		System.out.println( "Veuillez entrer votre code pin de lecture SVP " );
		String nom= readKeyboard() ;
		int lc = nom.length();
		byte [] apdu= new byte[5+lc];
		byte [] header= {CLA,ENTERREADPIN,P1,P2,(byte)lc };
		byte [] data= nom.getBytes();
		System.arraycopy(header, (short)0,apdu,(short)0,(short)header.length);
		System.arraycopy(data, (short)0,apdu,(short)header.length,(short)data.length);
	    System.out.println("// read pin ");
        CommandAPDU cmd = new CommandAPDU( apdu );
        ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
	}


	void enterWritePIN() {
		System.out.println( "Veuillez entrer votre code pin d'ecriture SVP " );
		String nom= readKeyboard() ;
		int lc = nom.length();
		byte [] apdu= new byte[5+lc];
		byte [] header= {CLA,ENTERWRITEPIN,P1,P2,(byte)lc };
		byte [] data= nom.getBytes();
		System.arraycopy(header, (short)0,apdu,(short)0,(short)header.length);
		System.arraycopy(data, (short)0,apdu,(short)header.length,(short)data.length);
	    System.out.println("// Write pin ");
        CommandAPDU cmd = new CommandAPDU( apdu );
        ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
	}


	void readNameFromCard() {
		byte[] cmd_ = {CLA,READNAMEFROMCARD,P1,P2,0};
        CommandAPDU cmd = new CommandAPDU( cmd_ );
        ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
	    byte[] bytes = resp.getBytes();
	    String msg = "";
	    for(int i=0; i<bytes.length-2;i++)
		    msg += new StringBuffer("").append((char)bytes[i]);
	    System.out.println(msg);
	
	}


	void writeNameToCard() {
		System.out.println( "Bonjour veuillez saisir votre nom " );
		String nom= readKeyboard() ;
		int lc = nom.length();
		byte [] apdu= new byte[5+lc];
		byte [] header= {CLA,WRITENAMETOCARD,P1,P2,(byte)lc };
		byte [] data= nom.getBytes();
		System.arraycopy(header, (short)0,apdu,(short)0,(short)header.length);
		System.arraycopy(data, (short)0,apdu,(short)header.length,(short)data.length);
	    System.out.println("// Write name into the card ");
        CommandAPDU cmd = new CommandAPDU( apdu );
        ResponseAPDU resp = this.sendAPDU( cmd, DISPLAY );
				
	}


	void exit() {
		loop = false;
	}


	void runAction( int choice ) {
		switch( choice ) {
			case 14: updateCardKey(); break;
			case 13: uncipherFileByCard(); break;
			case 12: cipherFileByCard(); break;
			case 11: cipherAndUncipherNameByCard(); break;
			case 10: readFileFromCard(); break;
			case 9: writeFileToCard(); break;
			case 8: updateWritePIN(); break;
			case 7: updateReadPIN(); break;
			case 6: displayPINSecurity(); break;
			case 5: desactivateActivatePINSecurity(); break;
			case 4: enterReadPIN(); break;
			case 3: enterWritePIN(); break;
			case 2: readNameFromCard(); break;
			case 1: writeNameToCard(); break;
			case 0: exit(); break;
			default: System.out.println( "unknown choice!" );
		}
	}


	String readKeyboard() {
		String result = null;

		try {
			BufferedReader input = new BufferedReader( new InputStreamReader( System.in ) );
			result = input.readLine();
		} catch( Exception e ) {}

		return result;
	}


	int readMenuChoice() {
		int result = 0;

		try {
			String choice = readKeyboard();
			result = Integer.parseInt( choice );
		} catch( Exception e ) {}

		System.out.println( "" );

		return result;
	}


	void printMenu() {
		System.out.println( "" );
		System.out.println( "14: update the DES key within the card" );
		System.out.println( "13: uncipher a file by the card" );
		System.out.println( "12: cipher a file by the card" );
		System.out.println( "11: cipher and uncipher a name by the card" );
		System.out.println( "10: read a file from the card" );
		System.out.println( "9: write a file to the card" );
		System.out.println( "8: update WRITE_PIN" );
		System.out.println( "7: update READ_PIN" );
		System.out.println( "6: display PIN security status" );
		System.out.println( "5: desactivate/activate PIN security" );
		System.out.println( "4: enter READ_PIN" );
		System.out.println( "3: enter WRITE_PIN" );
		System.out.println( "2: read a name from the card" );
		System.out.println( "1: write a name to the card" );
		System.out.println( "0: exit" );
		System.out.print( "--> " );
	}


	void mainLoop() {
		while( loop ) {
			printMenu();
			int choice = readMenuChoice();
			runAction( choice );
		}
	}


	public static void main( String[] args ) throws InterruptedException {
		new TheClient();
	}


}

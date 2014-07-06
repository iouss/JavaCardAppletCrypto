package applet;


import javacard.framework.*;




public class TheApplet extends Applet {


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
	
	final static short NameSIZE      = (short)20;
	static byte[] Name              = new byte[NameSIZE];
	static byte[] PIN              = new byte[9];
	OwnerPIN pinr, pinw;
	final static short SW_VERIFICATION_FAILED= (short) 0x6300;
	final static short SW_PIN_VERIFICATION_REQUIRED= (short) 0x6301;
	boolean sec=true;
	final static byte codeDisplayTrue= (short) 0x00;
	final static byte codeDisplayFalse= (short) 0x11;
	


	protected TheApplet() {
		byte[] pincoder = {(byte)0x30,(byte)0x30,(byte)0x30,(byte)0x30}; // PIN read code "0000"
		pinr = new OwnerPIN((byte)3,(byte)8);  				// 3 tries 8=Max Size
		pinr.update(pincoder,(short)0,(byte)4); 

		byte[] pincodew = {(byte)0x31,(byte)0x31,(byte)0x31,(byte)0x31}; // PIN write code "1111"
		pinw = new OwnerPIN((byte)3,(byte)8);  				// 3 tries 8=Max Size
		pinw.update(pincodew,(short)0,(byte)4); 	
		
		this.register();
	}


	public static void install(byte[] bArray, short bOffset, byte bLength) throws ISOException {
		new TheApplet();
	} 


	public boolean select() {
		if ( pinr.getTriesRemaining() == 0 )
			return false;
			
		if ( pinw.getTriesRemaining() == 0 )
			return false;
		
		return true;
	} 


	public void deselect(){
	  pinr.reset();
	  pinw.reset();
	}
	
	void verify(APDU apdu, OwnerPIN pin){
		apdu.setIncomingAndReceive();
		byte[] buffer=apdu.getBuffer();
		if (!pin.check(buffer,(byte)5,buffer[4]))
			ISOException.throwIt(SW_VERIFICATION_FAILED);
	}
	
	

	public void process(APDU apdu) throws ISOException {
		if( selectingApplet() == true )
			return;

		byte[] buffer = apdu.getBuffer();

		switch( buffer[1] ) 	{
			case UPDATECARDKEY: updateCardKey( apdu ); break;
			case UNCIPHERFILEBYCARD: uncipherFileByCard( apdu ); break;
			case CIPHERFILEBYCARD: cipherFileByCard( apdu ); break;
			case CIPHERANDUNCIPHERNAMEBYCARD: cipherAndUncipherNameByCard( apdu ); break;
			case READFILEFROMCARD: readFileFromCard( apdu ); break;
			case WRITEFILETOCARD: writeFileToCard( apdu ); break;
			case UPDATEWRITEPIN: updateWritePIN( apdu ); break;
			case UPDATEREADPIN: updateReadPIN( apdu ); break;
			case DISPLAYPINSECURITY: displayPINSecurity( apdu ); break;
			case DESACTIVATEACTIVATEPINSECURITY: desactivateActivatePINSecurity( apdu ); break;
			case ENTERREADPIN: enterReadPIN( apdu ); break;
			case ENTERWRITEPIN: enterWritePIN( apdu ); break;
			case READNAMEFROMCARD: readNameFromCard( apdu ); break;
			case WRITENAMETOCARD: writeNameToCard( apdu ); break;
			default: ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}


	void updateCardKey( APDU apdu ) {
	}


	void uncipherFileByCard( APDU apdu ) {
	}


	void cipherFileByCard( APDU apdu ) {
	}


	void cipherAndUncipherNameByCard( APDU apdu ) {
	}

	
	
	void readFileFromCard( APDU apdu ) {
		byte[] buffer = apdu.getBuffer();
	
	}


	void writeFileToCard( APDU apdu ) {
		byte[] buffer = apdu.getBuffer();
		byte nbrpaquet = buffer[2];
		byte  maxsize=127;
		short fileSize=(short)(maxsize*nbrpaquet);
		byte[] tableau = new byte[fileSize];
		apdu.setIncomingAndReceive();  
		Util.arrayCopy(buffer,(short)4,tableau,(short)0,(short)(1+fileSize));
		
		
	}


	void updateWritePIN( APDU apdu ) {
		if (sec==true)
			if ( ! pinw.isValidated() )
			ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		byte[] buffer = apdu.getBuffer();
		apdu.setIncomingAndReceive();  
		pinw.update(buffer,(short)5,(byte)4); 	
	}


	void updateReadPIN( APDU apdu ) {
		if (sec==true)
			if ( ! pinr.isValidated() )
					ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		byte[] buffer = apdu.getBuffer();
		apdu.setIncomingAndReceive();  
		pinr.update(buffer,(short)5,(byte)buffer[4]); 
	}


	void displayPINSecurity( APDU apdu ) {
		byte[] buffer = apdu.getBuffer();
		buffer[0]=buffer[1]=sec?codeDisplayTrue:codeDisplayFalse;
		apdu.setOutgoingAndSend((short)0,buffer[4]);  
	}


	void desactivateActivatePINSecurity( APDU apdu ) {
		sec=!sec;
	}


	void enterReadPIN( APDU apdu ) {
		verify(apdu,pinr);
	}


	void enterWritePIN( APDU apdu ) {
		verify(apdu,pinw);
	
	}


	void readNameFromCard( APDU apdu ) {
		if (sec==true)
			if ( ! pinr.isValidated() )
					ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		byte[] buffer = apdu.getBuffer();
		Util.arrayCopy(Name,(short)1,buffer,(short)0,Name[0]);
		apdu.setOutgoingAndSend((short)0,Name[0]);  
	  
	}


	void writeNameToCard( APDU apdu ) {
		if (sec==true)
			if ( ! pinw.isValidated() )
			ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
			
		byte[] buffer = apdu.getBuffer();
		apdu.setIncomingAndReceive();  
		Util.arrayCopy(buffer,(short)4,Name,(short)0,(short)(1+buffer[4]));
	}


}

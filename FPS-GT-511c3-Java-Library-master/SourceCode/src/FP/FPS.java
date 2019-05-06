package FP;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class FPS {
	
	InputStream in;
	OutputStream out;
	CommPortIdentifier portIdentifier; 
	 CommPort commPort;
	public Boolean open (String portName) throws Exception
    {
        portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned())
        {
            System.out.println("Error: Port is currently in use");
            return false;
        }
        else
        {
            commPort = portIdentifier.open(this.getClass().getName(),2000);
            if (commPort instanceof SerialPort)
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
                
                //FPS Open
                Open();
                
                
                return true;
            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
                return false;
            }
        }
    }
	
	public void close()
	{
		if(commPort!=null)
		{
			commPort.close();
			commPort = null;
		}
		
		if(portIdentifier!=null)
			portIdentifier=null;
	
	}
	
	

	byte[] command = new byte[2];	
	byte[] Parameter = new byte[4];								// Parameter 4 bytes, changes meaning depending on command							
	
	private final char[] hexArray = "0123456789ABCDEF".toCharArray();
	public String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	
	public static class Commands
	{
		public static final short NotSet				= 0x00,		// Default value for enum. Scanner will return error if sent this.
		Open				= 0x01,		// Open Initialization
		Close				= 0x02,		// Close Termination
		UsbInternalCheck	= 0x03,		// UsbInternalCheck Check if the connected USB device is valid
		ChangeEBaudRate		= 0x04,		// ChangeBaudrate Change UART baud rate
		SetIAPMode			= 0x05,		// SetIAPMode Enter IAP Mode In this mode, FW Upgrade is available
		CmosLed				= 0x12,		// CmosLed Control CMOS LED
		GetEnrollCount		= 0x20,		// Get enrolled fingerprint count
		CheckEnrolled		= 0x21,		// Check whether the specified ID is already enrolled
		EnrollStart			= 0x22,		// Start an enrollment
		Enroll1				= 0x23,		// Make 1st template for an enrollment
		Enroll2				= 0x24,		// Make 2nd template for an enrollment
		Enroll3				= 0x25,		// Make 3rd template for an enrollment, merge three templates into one template, save merged template to the database
		IsPressFinger		= 0x26,		// Check if a finger is placed on the sensor
		DeleteID			= 0x40,		// Delete the fingerprint with the specified ID
		DeleteAll			= 0x41,		// Delete all fingerprints from the database
		Verify1_1			= 0x50,		// Verification of the capture fingerprint image with the specified ID
		Identify1_N			= 0x51,		// Identification of the capture fingerprint image with the database
		VerifyTemplate1_1	= 0x52,		// Verification of a fingerprint template with the specified ID
		IdentifyTemplate1_N	= 0x53,		// Identification of a fingerprint template with the database
		CaptureFinger		= 0x60,		// Capture a fingerprint image(256x256) from the sensor
		MakeTemplate		= 0x61,		// Make template for transmission
		GetImage			= 0x62,		// Download the captured fingerprint image(256x256)
		GetRawImage			= 0x63,		// Capture & Download raw fingerprint image(320x240)
		GetTemplate			= 0x70,		// Download the template of the specified ID
		SetTemplate			= 0x71,		// Upload the template of the specified ID
		GetDatabaseStart	= 0x72,		// Start database download, obsolete
		GetDatabaseEnd		= 0x73,		// End database download, obsolete
		UpgradeFirmware		= 0x80,		// Not supported
		UpgradeISOCDImage	= 0x81,		// Not supported
		Ack					= 0x30,		// Acknowledge.
		Nack				= 0x31;	// Non-acknowledge
	}
	
	final byte COMMAND_START_CODE_1 = 0x55;	// Static byte to mark the beginning of a command packet	-	never changes
	final byte COMMAND_START_CODE_2 = (byte)0xAA;	// Static byte to mark the beginning of a command packet	-	never changes
	final byte COMMAND_DEVICE_ID_1 = 0x01;	// Device ID Byte 1 (lesser byte)							-	theoretically never changes
	final byte COMMAND_DEVICE_ID_2 = 0x00;	// Device ID Byte 2 (greater byte)							-	theoretically never changes
	int Error;
	Boolean ACK;
	byte[] ParameterBytes = new byte[4];
	byte[] ResponseBytes = new byte[2];
	byte[] RawBytes = new byte[12];
	
	public static class ErrorCodes
	{
				public static final int NO_ERROR					= 0x0000,	// Default value. no error
				NACK_TIMEOUT				= 0x1001,	// Obsolete, capture timeout
				NACK_INVALID_BAUDRATE		= 0x1002,	// Obsolete, Invalid serial baud rate
				NACK_INVALID_POS			= 0x1003,	// The specified ID is not between 0~199
				NACK_IS_NOT_USED			= 0x1004,	// The specified ID is not used
				NACK_IS_ALREADY_USED		= 0x1005,	// The specified ID is already used
				NACK_COMM_ERR				= 0x1006,	// Communication Error
				NACK_VERIFY_FAILED			= 0x1007,	// 1:1 Verification Failure
				NACK_IDENTIFY_FAILED		= 0x1008,	// 1:N Identification Failure
				NACK_DB_IS_FULL				= 0x1009,	// The database is full
				NACK_DB_IS_EMPTY			= 0x100A,	// The database is empty
				NACK_TURN_ERR				= 0x100B,	// Obsolete, Invalid order of the enrollment (The order was not as: EnrollStart -> Enroll1 -> Enroll2 -> Enroll3)
				NACK_BAD_FINGER				= 0x100C,	// Too bad fingerprint
				NACK_ENROLL_FAILED			= 0x100D,	// Enrollment Failure
				NACK_IS_NOT_SUPPORTED		= 0x100E,	// The specified command is not supported
				NACK_DEV_ERR				= 0x100F,	// Device Error, especially if Crypto-Chip is trouble
				NACK_CAPTURE_CANCELED		= 0x1010,	// Obsolete, The capturing is canceled
				NACK_INVALID_PARAM			= 0x1011,	// Invalid parameter
				NACK_FINGER_IS_NOT_PRESSED	= 0x1012,	// Finger is not pressed
				INVALID						= 0XFFFF;	// Used when parsing fails
	}
	
	byte[] GetPacketBytes(int cmd)
	{
		byte[] packetbytes= new byte[12];

		command[0] = GetLowByte(cmd);
		command[1] = GetHighByte(cmd);

		
		packetbytes[0] = COMMAND_START_CODE_1;
		packetbytes[1] = COMMAND_START_CODE_2;
		packetbytes[2] = COMMAND_DEVICE_ID_1;
		packetbytes[3] = COMMAND_DEVICE_ID_2;
		packetbytes[4] = Parameter[0];
		packetbytes[5] = Parameter[1];
		packetbytes[6] = Parameter[2];
		packetbytes[7] = Parameter[3];
		packetbytes[8] = command[0];
		packetbytes[9] = command[1];
		
		int checksum = _CalculateChecksum();
		packetbytes[10] = GetLowByte(checksum);
		packetbytes[11] = GetHighByte(checksum);
		
		
		// input byte debug code		
		/*System.out.println("checksum:" + checksum);
		System.out.println("GetLowByte:" + packetbytes[10]);		
		System.out.println("GetHighByte:" + packetbytes[11]);

		System.out.print("Request:");
		for(int i=0;i<12;i++)
		{
			byte[] test = new byte[1];
			test[0] = packetbytes[i];
			System.out.print("0x" + bytesToHex(test) + ",");
		}
		System.out.println("");*/
		
		
		return packetbytes;
	} 
	
	int _CalculateChecksum()
	{
		int w = 0;
		w += COMMAND_START_CODE_1&0xff;
		w += COMMAND_START_CODE_2&0xff;
		w += COMMAND_DEVICE_ID_1&0xff;
		w += COMMAND_DEVICE_ID_2&0xff;
		w += Parameter[0]&0xff;
		w += Parameter[1]&0xff;
		w += Parameter[2]&0xff;
		w += Parameter[3]&0xff;
		w += command[0]&0xff;
		w += command[1]&0xff;

		return w;
	}
	
	
	byte GetHighByte(int w)
	{
		//System.out.println("High Input:" +w);
		//System.out.println("High Ouput:" + (byte) ((byte)(w>>8)&0x00FF));
		return (byte) ((byte)(w>>8)&0x00FF);
	}

	// Returns the low byte from a word
	byte GetLowByte(int w)
	{
		//System.out.println("Low Input:" + w);
		//System.out.println("Low Ouput:" + (byte) ((byte)w&0x00FF));
		return (byte) ((byte)w&0x00FF);
	}
	
	String SendToSerial(byte data[], int length)
	{
	  String res = "";
	  boolean first=true;                                                                                                                                  
	  res = res + "\"";
	  for(int i=0; i<length; i++)
	  {
		if (first) first=false; else  res = res + " "; // Serial.print(" ");
		res = res + serialPrintHex(data[i]);
	  }
	  res = res + "\"";
	  return res;
	}
	
	String serialPrintHex(byte data)
	{
	  char[] tmp = new char[16];
	  new String(tmp);
	return String.format("%.2X", data);
	}
	
	
	byte[] Response_Packet(byte[] buffer)
	{
		if (buffer[8] == 0x30)
			ACK = true; 
		else 
			ACK = false;

		int checksum = CalculateChecksum(buffer, 10);
		byte checksum_low = GetLowByte(checksum);
		byte checksum_high = GetHighByte(checksum);

		Error = ParseFromBytes(buffer[5], buffer[4]);

		ParameterBytes[0] = buffer[4];
		ParameterBytes[1] = buffer[5];
		ParameterBytes[2] = buffer[6];
		ParameterBytes[3] = buffer[7];
		ResponseBytes[0]=buffer[8];
		ResponseBytes[1]=buffer[9];
		for (int i=0; i < 12; i++)
		{
			RawBytes[i]=buffer[i];
		}
		return RawBytes;
	}
	
	int ParseFromBytes(byte high, byte low)
	{
		int e = ErrorCodes.INVALID;
		if (high == 0x00)
		{
			
		}
		else {
			switch(low)
			{
				case 0x00: e = ErrorCodes.NO_ERROR; break;
				case 0x01: e = ErrorCodes.NACK_TIMEOUT; break;
				case 0x02: e = ErrorCodes.NACK_INVALID_BAUDRATE; break;
				case 0x03: e = ErrorCodes.NACK_INVALID_POS; break;
				case 0x04: e = ErrorCodes.NACK_IS_NOT_USED; break;
				case 0x05: e = ErrorCodes.NACK_IS_ALREADY_USED; break;
				case 0x06: e = ErrorCodes.NACK_COMM_ERR; break;
				case 0x07: e = ErrorCodes.NACK_VERIFY_FAILED; break;
				case 0x08: e = ErrorCodes.NACK_IDENTIFY_FAILED; break;
				case 0x09: e = ErrorCodes.NACK_DB_IS_FULL; break;
				case 0x0A: e = ErrorCodes.NACK_DB_IS_EMPTY; break;
				case 0x0B: e = ErrorCodes.NACK_TURN_ERR; break;
				case 0x0C: e = ErrorCodes.NACK_BAD_FINGER; break;
				case 0x0D: e = ErrorCodes.NACK_ENROLL_FAILED; break;
				case 0x0E: e = ErrorCodes.NACK_IS_NOT_SUPPORTED; break;
				case 0x0F: e = ErrorCodes.NACK_DEV_ERR; break;
				case 0x10: e = ErrorCodes.NACK_CAPTURE_CANCELED; break;
				case 0x11: e = ErrorCodes.NACK_INVALID_PARAM; break;
				case 0x12: e = ErrorCodes.NACK_FINGER_IS_NOT_PRESSED; break;
			}
		}
		return e;
	}
	
	
	
	int CalculateChecksum(byte[] buffer, int length)
	{
		int checksum = 0;
		for (int i=0; i<length; i++)
		{
			checksum +=buffer[i]&0xff;
		}
		return checksum;
	}
	
	void Open() throws Exception
	{
		Parameter = new byte[]{0x00,0x00,0x00,0x00};
		SendRequest(Commands.Open);
		byte[] res = GetResponse(0);
	}
	
	void Close() throws Exception
	{
		Parameter = new byte[]{0x00,0x00,0x00,0x00};
		SendRequest(Commands.Close);
		byte[] res = GetResponse(0);
	}
	
	void purgeresponse()
	{
		byte[] temp;
		try
		{
			int c = this.in.available();
			temp = new byte[c];
			this.in.read(temp);
			this.in.reset();	
		}
		catch(Exception exp)
		{
			
		}
		finally
		{
			temp = null;
		}
	}
	
	byte[] GetResponse(int timeout) throws Exception
	{
		byte firstbyte = 0;
		Boolean done = false;
		
		if(timeout==0)
			Thread.sleep(200);
		else
			Thread.sleep(timeout);
		
		while (done == false)
		{
			firstbyte = (byte)this.in.read();
			if (firstbyte == COMMAND_START_CODE_1)
			{
				done = true;
			}
		}
		byte[] resp = new byte[12];
		resp[0] = firstbyte;
		for (int i=1; i < 12; i++)
		{
			while (this.in.available() == 0) Thread.sleep(5);
			resp[i]= (byte) this.in.read();
		}
		return Response_Packet(resp);
	}
	
	int IntFromParameter()
	{
		int retval = 0;
		retval = (retval << 8) + ParameterBytes[3];
		retval = (retval << 8) + ParameterBytes[2];
		retval = (retval << 8) + ParameterBytes[1];
		retval = (retval << 8) + ParameterBytes[0];
		return retval;
	}
	
	void ParameterFromInt(int i)
	{
		Parameter[0] = (byte) (i & 0x000000ff);
		Parameter[1] = (byte) ((i & 0x0000ff00) >> 8);
		Parameter[2] = (byte) ((i & 0x00ff0000) >> 16);
		Parameter[3] = (byte) ((i & 0xff000000) >> 24);
	}
	
	
	public void SendRequest(short cmd) throws Exception
	{
		purgeresponse();
		this.out.write(GetPacketBytes(cmd));
		this.out.write("\n".getBytes());
	}
	
	public Boolean LEDON() throws Exception
	{
		Parameter = new byte[]{0x01,0x00,0x00,0x00};
		SendRequest(Commands.CmosLed);
		byte[] res = GetResponse(0);
		return ACK;
	}
	
	public Boolean LEDOFF() throws Exception
	{
		Parameter = new byte[]{0x00,0x00,0x00,0x00};
		SendRequest(Commands.CmosLed);
		byte[] res = GetResponse(0);
		return ACK;
	}
	
	public int GetEnrollCount() throws Exception
	{
		Parameter = new byte[]{0x00,0x00,0x00,0x00};
		SendRequest(Commands.GetEnrollCount);
		byte[] res = GetResponse(0);
		return IntFromParameter();
	}
	
	public Boolean IsPressFinger() throws Exception
	{
		LEDON();
		Parameter = new byte[]{0x00,0x00,0x00,0x00};
		SendRequest(Commands.IsPressFinger);
		byte[] res = GetResponse(50);
		Boolean retval = false;
		int pval = ParameterBytes[0];
		pval += ParameterBytes[1];
		pval += ParameterBytes[2];
		pval += ParameterBytes[3];
		if (pval == 0) retval = true;
		
		
		return retval;
	}
	
	Boolean CheckEnrolled(int id) throws Exception
	{
		SendRequest(Commands.CheckEnrolled);
		ParameterFromInt(id);
		byte[] res = GetResponse(0);
		Boolean retval = false;
		retval = ACK;
		return retval;
	}
	
	
	public int EnrollStart(int id) throws Exception
	{
		ParameterFromInt(id);
		SendRequest(Commands.EnrollStart);
		byte[] res = GetResponse(0);
		int retval = 0;
		if (ACK == false)
		{
			if (Error == ErrorCodes.NACK_DB_IS_FULL) retval = 1;
			if (Error == ErrorCodes.NACK_INVALID_POS) retval = 2;
			if (Error == ErrorCodes.NACK_IS_ALREADY_USED) retval = 3;
		}
		return retval;
	}
	
	Boolean CaptureFinger(Boolean highquality) throws Exception
	{
		if (highquality)
			ParameterFromInt(1);
		else
			ParameterFromInt(0);

		SendRequest(Commands.CaptureFinger);
		byte[] res = GetResponse(0);
		Boolean retval = ACK;
		return retval;

	}
	
	
	public int Enroll1() throws Exception
	{
		SendRequest(Commands.Enroll1);
		byte[] res = GetResponse(0);
		
		int retval = IntFromParameter();
		if (retval < 200) retval = 3; else retval = 0;
		if (ACK == false)
		{
			if (Error == ErrorCodes.NACK_ENROLL_FAILED) retval = 1;
			if (Error == ErrorCodes.NACK_BAD_FINGER) retval = 2;
		}
		if (ACK) return 0; else return retval;
	}
	
	public int Enroll2() throws Exception
	{
		SendRequest(Commands.Enroll2);
		byte[] res = GetResponse(0);
		
		
		int retval = IntFromParameter();
		if (retval < 200) retval = 3; else retval = 0;
		if (ACK == false)
		{
			if (Error == ErrorCodes.NACK_ENROLL_FAILED) retval = 1;
			if (Error == ErrorCodes.NACK_BAD_FINGER) retval = 2;
		}
		if (ACK) return 0; else return retval;
	}
	
	public int Enroll3() throws Exception
	{
		SendRequest(Commands.Enroll3);
		byte[] res = GetResponse(0);
		
		int retval = IntFromParameter();
		if (retval < 200) retval = 3; else retval = 0;
		if (ACK == false)
		{
			if (Error == ErrorCodes.NACK_ENROLL_FAILED) retval = 1;
			if (Error == ErrorCodes.NACK_BAD_FINGER) retval = 2;
		}
		if (ACK) return 0; else return retval;
	}
	
	public int Identify1_N() throws Exception
	{
		SendRequest(Commands.Identify1_N);
		byte[] res = GetResponse(0);
		
		int retval = IntFromParameter();
		if (retval > 200) retval = 200;
		return retval;
	}
	
	//Custom Methods
	
	public Boolean NewEnrollment(int enrollid) throws Exception
	{
		LEDON();
		
		Boolean usedid = true;
		while (usedid == true)
		{
			usedid = CheckEnrolled(enrollid);
			if (usedid==true)
			{
				enrollid++;
				System.out.println("Request Enroll ID# was already enrolled. So Assigning New Enroll Id #" + enrollid);
			}
		}
		EnrollStart(enrollid);

		// enroll
		System.out.print("Press finger to Enroll #");
		System.out.println(enrollid);
		while(IsPressFinger() == false) Thread.sleep(100);
		Boolean bret = CaptureFinger(true);
		int iret = 0;
		if (bret != false)
		{
			System.out.println("Remove finger");
			Enroll1(); 
			while(IsPressFinger() == true) Thread.sleep(100);
			System.out.println("Press same finger again");
			while(IsPressFinger() == false) Thread.sleep(100);
			bret = CaptureFinger(true);
			if (bret != false)
			{
				System.out.println("Remove finger");
				Enroll2();
				while(IsPressFinger() == true) Thread.sleep(10);
				System.out.println("Press same finger yet again");
				while(IsPressFinger() == false) Thread.sleep(10);
				bret = CaptureFinger(true);
				if (bret != false)
				{
					System.out.println("Remove finger");
					iret = Enroll3();
					if (iret == 0)
					{
						System.out.println("Enrolling Successfull for ID #"+enrollid);
						return true;
					}
					else
					{
						System.out.println("Enrolling Failed with error code:");
						System.out.println(iret);
						return false;
					}
				}
				else 
				{
					System.out.println("Failed to capture third finger");
					return false;
				}
			}
			else 
				{
					System.out.println("Failed to capture second finger");
					return false;
				}
		}
		else 
			{
				System.out.println("Failed to capture first finger");
				return false; 
			}
	}
	
}

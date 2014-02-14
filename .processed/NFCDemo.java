import java.io.IOException;
import java.util.Date;

import javax.microedition.contactless.ContactlessException;
import javax.microedition.contactless.DiscoveryManager;
import javax.microedition.contactless.TargetListener;
import javax.microedition.contactless.TargetProperties;
import javax.microedition.contactless.TargetType;
import javax.microedition.contactless.TransactionListener;
import javax.microedition.contactless.ndef.NDEFMessage;
import javax.microedition.contactless.ndef.NDEFRecord;
import javax.microedition.contactless.ndef.NDEFRecordListener;
import javax.microedition.contactless.ndef.NDEFRecordType;
import javax.microedition.contactless.ndef.NDEFTagConnection;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.nokia.nfc.nxp.mfstd.MFStandardConnection;


public class NFCDemo extends MIDlet implements CommandListener, TargetListener, NDEFRecordListener, TransactionListener {
	
	Form form = new Form("NFC Demo di Alfredo");
	
	Command exitCmd = new Command("Exit", Command.EXIT, 1);
	Command connectCmd = new Command("Connect", Command.EXIT, 1);
	
	DiscoveryManager dm;
	
	private NDEFRecordType myType = new NDEFRecordType(NDEFRecordType.EXTERNAL_RTD, "urn:nfc:ext:ismb.it:nfc_demo");
	
	
	public NFCDemo() {
		// TODO Auto-generated constructor stub
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		form.addCommand(exitCmd);
		form.setCommandListener(this);
		
		form.addCommand(connectCmd);
		form.setCommandListener(this);
		
		Display.getDisplay(this).setCurrent(form);
		
		dm = DiscoveryManager.getInstance();
		
		try {
			dm.addTargetListener(this, TargetType.RFID_TAG);
			//dm.addNDEFRecordListener(this, myType);
			//dm.addTransactionListener(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	public void commandAction(Command command, Displayable displayable) {
		// TODO Auto-generated method stub
		if (command == exitCmd){
			exit();
		}
		else if (command == connectCmd){
			connect();
		}
			
	}

	private void connect() {
		// TODO Auto-generated method stub
		System.out.println("CONNESSO!!!");
		
		form.append("Selezionare un TARGET, prego...");
	}

	private void exit() {
		// TODO Auto-generated method stub
		try {
			destroyApp(false);
			notifyDestroyed();
			
			dm.removeTargetListener(this, TargetType.RFID_TAG);
			//dm.removeNDEFRecordListener(this, myType);
			//dm.removeTransactionListener(this);
			
		} catch (MIDletStateChangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void targetDetected(TargetProperties[] arg0) {
		// TODO Auto-generated method stub
		System.out.println("Target Detected");
		/*TargetProperties target = arg0[0];
		String url = target.getUrl();
		try {
			NDEFTagConnection conn1 = (NDEFTagConnection)Connector.open(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//SCRITTURA e LETTURA SU TAG
		for (int i=0; i<arg0.length ; i++){
			TargetProperties tp = arg0[i];
			Class[] connections = tp.getConnectionNames();
			for (int j=0;j<connections.length;j++){
				Class conn = connections[j];
				String className = conn.getName();
				if (className.endsWith("NDEFTagConnection")){
					writeNDEFTag(tp);
				} else if (className.endsWith("MFStandardConnection")){
					readMifare(tp, conn);
				}
			}
		}
		
		
		/*for (int i=0; i<arg0.length;i++){
			TargetProperties tp = arg0[i];
			Class[] connections = tp.getConnectionNames();
			for (int j=0;j<connections.length;j++){
				Class conn = connections[j];
				String className = conn.getName();
				if (className.endsWith("NDEFTagConnection")){
					System.out.println("NDEF");
					form.append("NDEF");
				}
				else{
					System.out.println("NO NDEF");
				}
			}
		}*/
		
	}

	private void readMifare(TargetProperties tp, Class conn) {
		// TODO Auto-generated method stub
		MFStandardConnection mfConn = null;
		
		try {
			mfConn = (MFStandardConnection) Connector.open(tp.getUrl(conn));
			byte[] rawData = new byte[mfConn.size()];
			mfConn.read(null, rawData, 0, 0, rawData.length);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				mfConn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void writeNDEFTag(TargetProperties tp) {
		// TODO Auto-generated method stub
		String here = "I was here at "+ new Date();
		NDEFRecord ndefRec = new NDEFRecord(myType, null, here.getBytes());
		NDEFMessage ndefMsg = new NDEFMessage(new NDEFRecord[] {ndefRec});
		
		NDEFTagConnection ndefConn = null;
		
	
		try {
			ndefConn = (NDEFTagConnection)Connector.open(tp.getUrl());
			ndefConn.writeNDEF(ndefMsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ndefConn != null){
				try {
					ndefConn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		
	}

	public void recordDetected(NDEFMessage arg0) {
		// TODO Auto-generated method stub
		System.out.println("Record Detected");
	}

	public void externalReaderDetected(byte arg0) {
		// TODO Auto-generated method stub
		System.out.println("External Reader Detected");
	}

}

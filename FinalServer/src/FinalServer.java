import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/** �Ƶ��̳� & ��ǻ�� ���� �ּ� */
// ��ǻ�� & �ȵ���̵� ���� �ּ� 

public class FinalServer implements SerialPortEventListener {
	SerialPort serialPort;
	String SERIAL;

	/** �Ƶ��̳�, ��ǻ�� ���� ���� */
	/** The port we're normally going to use. */
	private static final String PORT_NAMES[] = {"COM3"};
	/** Buffered input stream from the port */
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	
	// ��ǻ��, �ȵ���̵� ���� ���� 
	private PrintWriter out;
	private BufferedReader in;  
	ServerSocket serverSocket;
	Socket clientSocket;		// Socket I/O
	
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public void initialize()
	{
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		
		while (portEnum.hasMoreElements()) 
		{
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			
			for (String portName : PORT_NAMES) 
			{
				if (currPortId.getName().equals(portName)) 
				{
					portId = currPortId;
					break;
				}
			}
		}
		
		if (portId == null) 
		{
			System.out.println("Could not find COM port.");
			return;
		}
		
		try 
		{
			/** �Ƶ��̳� & ��ǻ�� ����. */
			serialPort = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);
			serialPort.setSerialPortParams(DATA_RATE,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			
			output = serialPort.getOutputStream();  // �ø���� ����.
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));  // �ø��󿡼� ����.
			
			// ��ǻ�Ϳ� �ȵ���̵� ����.
			serverSocket = new ServerSocket(5555);
			clientSocket = serverSocket.accept();  // ������ Ŭ���̾�Ʈ ���� ����
			
			System.out.println("Ŭ���̾�Ʈ ����");
			out = new PrintWriter(clientSocket.getOutputStream(), true);  // �ȵ���̵�� ����.
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  // �ȵ���̵忡�� ����.
			
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} 
		catch (Exception e) 
		{
			System.err.println(e.toString());
		}
	}
	
	public synchronized void close() 
	{
		if (serialPort != null) 
		{
			serialPort.removeEventListener();
			serialPort.close();
		}
	}
	
	public synchronized void serialEvent(SerialPortEvent oEvent) 
	{
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) 
		{
			try 
			{  // ��ư�� ������ ��.
				SERIAL = input.readLine();
				out.println(SERIAL);
				out.flush();
			}
			catch (Exception e) 
			{
			}	
		}
	}
	
	public static void main(String[] args) throws Exception 
	{	
		FinalServer main = new FinalServer();
		
		while(true)
		{
			main.initialize();
			
			System.out.println("Started");
			
			try
			{
				while(true)
				{
					String inputLine=main.in.readLine();
					System.out.println("Ŭ���̾�Ʈ�κ��� ���� ���ڿ�: " + inputLine);
					if(inputLine.equals("quit"))
					{
						System.out.println("������ ����˴ϴ�.");
						break;
					}
				}
				main.close();
				main.out.close();
				main.in.close();
				main.clientSocket.close();
				main.serverSocket.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

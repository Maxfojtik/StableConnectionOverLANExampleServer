import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConnection 
{
	private Socket clientSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	boolean connected = false;
	static String getClientIP() throws IOException {
		DatagramSocket socket = new DatagramSocket(4445);
		socket.setSoTimeout(5000);
       	byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        String received = null;
        while(true)
        {
	        try
	        {
	            socket.receive(packet);
		        received = new String(packet.getData(), 0, packet.getLength());
		        if(received.startsWith("iPadClient:"))
		        {
		        	received = received.replace("iPadClient:", "");
		        	break;
		        }
	        }
	        catch(Exception e)
	        {
	        }
        }
        socket.close();
        return received;
    }
	void connectToClient()
	{
		System.out.println("Waiting for client to ask us to connect to them");
		String clientIp = null;
		while(clientIp==null)
		{
			try {
				clientIp = getClientIP();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			clientSocket = new Socket(clientIp, 4446);
	        out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        try {
				send("p");
				connected = true;
			} catch (Exception e) {
				disconnect();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void send(String str) throws Exception
	{
		System.out.println("-> "+str);
		out.println(str);
		if(out.checkError())
		{
			throw new Exception("Connection Exception"); 
		}
	}
	void parse(String str)
	{
		System.out.println("<- "+str);
		String[] params = str.split(":");
	}
	void disconnect()
	{
		try {
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clientSocket = null;
	}
	long lastPing = 0;
	boolean logic()
	{
		try
		{
			if(clientSocket==null)
			{
				return false;
			}
			if(in.ready())
			{
				parse(in.readLine());
			}
			if(System.currentTimeMillis()-lastPing>1000)
			{
				lastPing = System.currentTimeMillis();
				send("p");
			}
		}
		catch(Exception e)
		{
			System.out.println("Networking error "+e.getMessage());
//			e.printStackTrace();
			disconnect();
		}
		return true;
	}
	static class RunningThread implements Runnable
	{
		TCPConnection connection;
		TCPConnection init()
		{
			connection = new TCPConnection();
			return connection;
		}
		boolean run = true;
		@Override
		public void run() 
		{
			connection.connectToClient();
			while(run)
			{
				run = connection.logic();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Connection Thread Ended");
			iPadServer.removeConnection(connection);
		}		
	}
	void startListening()
	{
		
	}
	static TCPConnection waitingConnection = null;
	static void listenForConnections()
	{
		if(waitingConnection==null)
		{
			RunningThread rt = new RunningThread();
			waitingConnection = rt.init();
			new Thread(rt).start();
		}
		if(waitingConnection.connected)
		{
			waitingConnection = null;
		}
	}
}

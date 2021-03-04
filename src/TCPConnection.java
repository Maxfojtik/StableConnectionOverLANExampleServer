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
	static private Socket clientSocket = null;
	static private PrintWriter out = null;
	static private BufferedReader in = null;
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
	static void connectToClient()
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
	static void send(String str) throws Exception
	{
		System.out.println("-> "+str);
		out.println(str);
		if(out.checkError())
		{
			throw new Exception("Connection Exception"); 
		}
	}
	static void parse(String str)
	{
		System.out.println("<- "+str);
		String[] params = str.split(":");
	}
	static void disconnect()
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
	static long lastPing = 0;
	static void logic()
	{
		try
		{
			if(clientSocket==null)
			{
				TCPConnection.connectToClient();
			}
			if(TCPConnection.in.ready())
			{
				parse(TCPConnection.in.readLine());
			}
			if(System.currentTimeMillis()-lastPing>1000)
			{
				lastPing = System.currentTimeMillis();
				send("p");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			disconnect();
		}
	}
}

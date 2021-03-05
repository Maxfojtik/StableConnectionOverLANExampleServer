import java.io.IOException;
import java.util.LinkedList;

public class iPadServer 
{
	static LinkedList<TCPConnection> connections = new LinkedList<TCPConnection>();
	static boolean paused = false;
	static void removeConnection(TCPConnection connection)
	{
		while(paused)
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		connections.remove(connection);
		System.out.println("Removed Connection, we now have "+(connections.size()-1)+" active connections");//-1 because we have a connection waiting for a connection
	}
	public static void main(String args[]) throws IOException
	{
		while(true)
		{
			TCPConnection.listenForConnections();
			paused = true;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			paused = false;
		}
	}
}

import java.io.IOException;

public class iPadServer 
{
	public static void main(String args[]) throws IOException
	{
		while(true)
		{
			TCPConnection.logic();
		}
	}
}

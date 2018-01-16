package webservices;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientInterface
{
	private static final String HOST = "192.168.56.103";

	private ServerInterfaceBase base;

	public void init()
	{
		try {
			Registry registry = LocateRegistry.getRegistry(HOST, ServerInterfaceBase.RMI_PORT);
			base = (ServerInterfaceBase) registry.lookup(ServerInterfaceBase.REGISTRY_NAME);
		}
		catch (NotBoundException e)
		{
			System.err.println("Server not available: ");
			e.printStackTrace();
			System.exit(1);	// TODO don't terminate!!!
		}
		catch (RemoteException e)
		{
			System.err.println("Failed to connect to server: ");
			e.printStackTrace();
			System.exit(1);	// TODO don't terminate!!!
		}
	}
}

package webservices;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static webservices.ServerInterfaceBase.HOST;

public class ClientInterface
{
	private static ServerInterfaceBase base;

	public static void init()
	{
		System.setProperty("java.rmi.server.hostname", HOST);

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

	public static ServerInterfaceBase getBase() {
		return base;
	}
}

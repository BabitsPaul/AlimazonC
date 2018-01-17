package ai_drones;

import misc.AIReport;
import webservices.ProcurementQueue;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ProcurementQueueImpl
	extends UnicastRemoteObject
	implements ProcurementQueue
{
	public static final String QUEUE_HOST = "localhost";
	public static final int QUEUE_PORT = 6000;
	public static final String REG_NAME = "alimazon.procurementq";

	public static final int EXIT_CODE_SERVER_FAILURE = 1;
	public static final int EXIT_CODE_SERVER_ALREADY_UP = 2;

	private static ProcurementQueueImpl instance;

	public static ProcurementQueue getInstance()
	{
		return instance;
	}

	public static void main(String[] args)
	{
		System.setProperty("java.rmi.server.hostname", QUEUE_HOST);

		try {
			instance = new ProcurementQueueImpl();

			instance.init();
		}catch (RemoteException e)
		{
			System.err.println("Failed to initialize the server");

			System.exit(EXIT_CODE_SERVER_FAILURE);
		}catch (AlreadyBoundException e)
		{
			System.err.println("Server is already running");

			System.exit(EXIT_CODE_SERVER_ALREADY_UP);
		}

		try {
			getInstance().report(new AIReport());
			getInstance().report(new AIReport());
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private BlockingQueue<AIReport> q = new LinkedBlockingDeque<>();

	private ProcurementQueueImpl()
			throws RemoteException
	{
		super(QUEUE_PORT);
	}

	public void init()
		throws RemoteException, AlreadyBoundException
	{
		Registry reg = LocateRegistry.createRegistry(QUEUE_PORT);
		reg.bind(REG_NAME, this);
	}

	@Override
	public void report(AIReport r) {
		q.offer(r);
	}

	@Override
	public AIReport retrieve()
		throws RemoteException, InterruptedException
	{
		return q.take();
	}
}

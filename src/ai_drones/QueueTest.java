package ai_drones;

import webservices.ProcurementQueue;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class QueueTest {
	public static void main(String[] args)
			throws Exception
	{
		Registry reg = LocateRegistry.getRegistry("localhost", ProcurementQueueImpl.QUEUE_PORT);
		ProcurementQueue pq = (ProcurementQueue) reg.lookup(ProcurementQueueImpl.REG_NAME);

		pq.retrieve();
		pq.retrieve();

		System.out.println("Two elements retrieved");
	}
}

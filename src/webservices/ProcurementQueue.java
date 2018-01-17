package webservices;

import misc.AIReport;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcurementQueue
	extends Remote
{
	/**
	 * Report new data from regressions or search-request-analysis
	 *
	 * @param r
	 */
	void report(AIReport r) throws RemoteException;

	AIReport retrieve() throws RemoteException, InterruptedException;
}

package webservices;

import misc.AIReport;

import java.rmi.RemoteException;

public interface ProcurementQueue
{
	/**
	 * Report new data from regressions or search-request-analysis
	 *
	 * @param r
	 */
	void report(AIReport r) throws RemoteException;

	AIReport retrieve() throws RemoteException, InterruptedException;
}

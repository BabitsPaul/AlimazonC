package webservices;

import misc.AIReport;

public interface ProcurementQueue
{
	/**
	 * Report new data from regressions or search-request-analysis
	 *
	 * @param r
	 */
	void report(AIReport r);
}

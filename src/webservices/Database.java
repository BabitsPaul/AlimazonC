package webservices;

import misc.Order;
import misc.Product;
import misc.WarehouseReport;
import misc.Request;

import java.util.List;

/**
 * basic interface for the database
 */
public interface Database
{
	/**
	 * List of products that match a certain search-request.
	 *
	 * @param request
	 * @return
	 */
	List<Product> search(Request request);

	/**
	 * List of failed requests.
	 *
	 * @param sqlrequest
	 * @return
	 */
	List<Request> failedRequests(String sqlrequest);

	/**
	 * Interface for warehouse-reporting-system
	 */
	void warehouseReport(WarehouseReport r);

	/**
	 * Lists all completed orders.
	 *
	 * Each order should only be listed once.
	 *
	 * @param sqlrequest specific search-request on the database
	 * @return
	 */
	List<Order> pastOrders(String sqlrequest);
}

package webservices;

import misc.Order;

import java.util.List;

/**
 * Orderqueue. Functions like a regular queue
 */
public interface OrderQueue
{
	/**
	 * Enqueue new order
	 *
	 * @param o
	 */
	void offer(Order o);

	/**
	 * Retrieve next order from the queue. Marks the item as ordered.
	 */
	void poll(Order o);

	/**
	 * Lists active orders
	 *
	 * @return
	 */
	List<Order> listActiveOrders();
}

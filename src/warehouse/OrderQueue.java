package warehouse;

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
	Order poll();

	/**
	 * Lists active orders
	 *
	 * @return
	 */
	List<Order> listActiveOrders();
}
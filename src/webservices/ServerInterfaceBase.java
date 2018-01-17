package webservices;

import misc.Order;
import misc.Product;
import misc.User;

import javax.swing.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ServerInterfaceBase
	extends Remote
{
	int RMI_PORT = 1099;

	String REGISTRY_NAME = "databaseif";

	String HOST = "192.168.56.103";

	//////////////////////////////////////////////////////////////////////////
	// order
	//

	/**
	 * Creates a new order
	 *
	 * @param user user that ordered the items
	 * @param order the order-object
	 * @return the oid of the order
	 * @throws RemoteException
	 */
	int addOrder(User user, Order order) throws RemoteException;

	void removeOrder(int oid) throws RemoteException;

	Order getOrder(int oid) throws RemoteException;

	/**
	 * @return a list of all active orders
	 * @throws RemoteException
	 */
	List<Order> listOrders() throws RemoteException;

	/**
	 *
	 * @return a list of orders that are pending
	 * @throws RemoteException
	 */
	List<Order> listActiveOrders() throws RemoteException;

	Map<Date, Map<Integer, Integer>> listSalesByDate() throws RemoteException;

	///////////////////////////////////////////////////////////////////////////
	// product
	//

	/**
	 * inserts a new product into the table
	 *
	 * @param name
	 * @param description
	 * @param imgBuffer
	 * @param price
	 * @return product id
	 * @throws RemoteException
	 */
	int addProduct(String name, String description, ImageIcon imgBuffer, double price) throws RemoteException;

	void removeProduct(int pid) throws RemoteException;

	Product getProduct(int pid) throws RemoteException;

	int[] getLocation(int pid) throws RemoteException;

	void setLocation(int pid, int x, int y) throws RemoteException;

	List<Product> listProducts() throws RemoteException;

	//////////////////////////////////////////////////////////////////////////
	// user
	//

	/**
	 * inserts a new user into the table
	 *
	 * @param name
	 * @return user-id
	 * @throws RemoteException
	 */
	int addUser(String name) throws RemoteException;

	void removeUser(int uid) throws RemoteException;

	User getUser(int uid) throws RemoteException;

	//////////////////////////////////////////////////////////////////////////
	// client based shutdown
	//

	void shutdownServer() throws RemoteException;
}

package webservices;

import misc.Order;
import misc.Product;
import misc.User;

import javax.swing.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterfaceBase
	extends Remote
{
	int RMI_PORT = 1099;

	String REGISTRY_NAME = "databaseif";

	String HOST = "192.168.56.103";

	//////////////////////////////////////////////////////////////////////////
	// order
	//

	void addOrder(List<Product> products) throws RemoteException;

	void removeOrder(int oid) throws RemoteException;

	Order getOrder(int oid) throws RemoteException;

	List<Order> listOrders() throws RemoteException;

	///////////////////////////////////////////////////////////////////////////
	// product
	//

	void addProduct(String name, String description, ImageIcon imgBuffer) throws RemoteException;

	void removeProduct(String name) throws RemoteException;

	Product getProduct(String name) throws RemoteException;

	int[] getLocation(String name) throws RemoteException;

	void setLocation(String name, int x, int y) throws RemoteException;

	List<String> listProducts() throws RemoteException;

	//////////////////////////////////////////////////////////////////////////
	// user
	//

	void addUser(String name) throws RemoteException;

	void removeUser(String name) throws RemoteException;

	User getUser(String name) throws RemoteException;

	//////////////////////////////////////////////////////////////////////////
	// client based shutdown
	//

	void shutdownServer() throws RemoteException;
}

package webservices;

import misc.Order;
import misc.Product;
import misc.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server
	extends UnicastRemoteObject
	implements ServerInterfaceBase
{
	private static final String SERVER_ACC = "jdbc:mysql://localhost/sweng";
	private static final String DB_USER = "root";
	private static final String USER_PW = "root";

	private static final String IMAGE_FOLDER = "imgs";

	private Registry registry;

	private Connection con;

	private PreparedStatement addOrder, removeOrder, listOrders, getOrder,
								addProduct, removeProduct, getProduct, listProducts,
								getLocation, setLocation,
								addUser, removeUser,  getUser;

	private int nextAvailableFile;

	public Server()
		throws RemoteException
	{
		super(RMI_PORT);
	}

	public static void main(String[] args)
	{
		try {
			Server sif = new Server();
			sif.initRMI();
			sif.initDB();
			sif.initIMG();
		}catch (AlreadyBoundException e)
		{
			System.err.println("Server already running");
			System.exit(1);
		}
		catch (RemoteException e)
		{
			System.err.println("Unknown failure: ");
			e.printStackTrace();
			System.exit(1);
		}
		catch (SQLException e)
		{
			System.err.println("Failed to connect to database");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void initRMI()
		throws RemoteException, AlreadyBoundException
	{
		registry = LocateRegistry.createRegistry(RMI_PORT);
		registry.bind(REGISTRY_NAME, this);
	}

	private void initDB()
		throws SQLException
	{
		con = DriverManager.getConnection(SERVER_ACC, DB_USER, USER_PW);
		addOrder = con.prepareStatement("INSERT INTO orders (list) VALUES (?)");
		removeOrder = con.prepareStatement("DELETE FROM orders WHERE id=?");
		listOrders = con.prepareStatement("SELECT * FROM product");
		getOrder = con.prepareStatement("SELECT list FROM orders WHERE id=?");
		addProduct = con.prepareStatement("INSERT INTO product (name, description, iconFile) VALUES (?, ?, ?)");
		removeProduct = con.prepareStatement("DELETE FROM product WHERE name=?");
		getProduct = con.prepareStatement("SELECT * FROM product WHERE name=?");
		listProducts = con.prepareStatement("SELECT * FROM product");
		getLocation = con.prepareStatement("SELECT xcoord, ycoord FROM product WHERE name=?");
		setLocation = con.prepareStatement("INSERT INTO location (name, xcoord, ycoord) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE xcoord=?, ycoord=?");
		addUser = con.prepareStatement("INSERT INTO user (name) VALUES (?)");
		removeUser = con.prepareStatement("DELETE FROM user WHERE name=?");
		getUser = con.prepareStatement("SELECT * FROM user WHERE name=?");
	}

	private void initIMG()
	{
		File folder = new File(IMAGE_FOLDER);

		if(!folder.exists())
		{
			nextAvailableFile = 0;

			if(!folder.mkdir())
			{
				System.err.println("Failed to create folder for image-storage");
				System.exit(1);
			}
		}
		else
		{
			nextAvailableFile = Arrays.stream(folder.list()).mapToInt(Integer::parseInt).max().orElse(-1) + 1;
		}
	}

	@Override
	public void addOrder(List<Product> products)
		throws RemoteException
	{
		try{
			addOrder.setString(1, products.stream().collect(
					StringBuilder::new,
					(sb, s)-> sb.append(", \"").append(s).append("\""),
					(a, b)->a.append(b)).
					toString());
			addOrder.execute();
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to add order", e);
		}
	}

	@Override
	public void removeOrder(int oid) throws RemoteException {
		try {
			removeOrder.setInt(1, oid);
			removeOrder.execute();
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to remove order", e);
		}
	}

	@Override
	public Order getOrder(int oid) throws RemoteException
	{
		try {
			getOrder.setInt(1, oid);

			ResultSet res = getOrder.executeQuery();

			return null;
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to retrieve order", e);
		}
	}

	@Override
	public List<Order> listOrders()
		throws RemoteException
	{
		// TODO storage of orders

		return null;
	}

	@Override
	public void addProduct(String name, String description, ImageIcon img)
		throws RemoteException
	{
		String fn = IMAGE_FOLDER + "/" + nextAvailableFile;
		nextAvailableFile++;

		try {
			Image i = img.getImage();
			BufferedImage bi = new BufferedImage(i.getWidth(null),i.getHeight(null),BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2 = bi.createGraphics();
			g2.drawImage(i, 0, 0,i.getWidth(null),i.getHeight(null), null);
			g2.dispose();

			ImageIO.write(bi, "jpg", new FileOutputStream(new File(fn)));
		}catch (IOException e)
		{
			throw new RemoteException("Failed to store image", e);
		}

		try {
			addProduct.setString(1, name);
			addProduct.setString(2, description);
			addProduct.setString(3, IMAGE_FOLDER + "/" + nextAvailableFile);
			addProduct.execute();
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to write product to database", e);
		}
	}

	@Override
	public void removeProduct(String name) throws RemoteException {
		try {
			removeProduct.setString(1, name);
			removeProduct.execute();
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to delete product", e);
		}
	}

	@Override
	public Product getProduct(String name) throws RemoteException {
		try {
			getProduct.setString(1, name);
			ResultSet res = getProduct.executeQuery();

			return new Product(res.getString(1), res.getString(2),
					new ImageIcon(ImageIO.read(new File(res.getString(3)))));
		}catch (SQLException | IOException e)
		{
			throw new RemoteException("Failed to read product", e);
		}
	}

	@Override
	public List<String> listProducts()
		throws RemoteException
	{
		try{
			ResultSet res = listProducts.executeQuery();

			List<String> prodList = new ArrayList<>();

			while(res.next())
			{
				prodList.add(res.getString(1));
			}

			return prodList;
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to read product-list", e);
		}
	}

	@Override
	public int[] getLocation(String name) throws RemoteException {
		try {
			getLocation.setString(1, name);
			ResultSet res = getLocation.executeQuery();

			return new int[]{res.getInt(1), res.getInt(2)};
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to retrieve product-location");
		}
	}

	@Override
	public void setLocation(String name, int x, int y) throws RemoteException
	{
		try {
			setLocation.setString(1, name);
			setLocation.setInt(2, x);
			setLocation.setInt(3, y);
			setLocation.setInt(4, x);
			setLocation.setInt(5, y);
			setLocation.execute();
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to update location", e);
		}
	}

	@Override
	public void addUser(String name) throws RemoteException {
		try {
			addUser.setString(1, name);
			addUser.execute();
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to insert user", e);
		}
	}

	@Override
	public void removeUser(String name) throws RemoteException {
		try {
			removeUser.setString(1, name);
			removeUser.execute();
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to remove user", e);
		}
	}

	@Override
	public User getUser(String name) throws RemoteException {
		try {
			getUser.setString(1, name);
			ResultSet res = getUser.executeQuery();

			return new User(res.getString(2), res.getInt(1));
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to get user", e);
		}
	}

	@Override
	public void shutdownServer() throws RemoteException {
		try{
			registry.unbind(REGISTRY_NAME);
		}catch (RemoteException | NotBoundException e){}

		try{
			con.close();
		}catch (SQLException e){}
	}
}

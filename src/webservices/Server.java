package webservices;

import misc.Order;
import misc.OrderElement;
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
import java.util.*;
import java.util.Date;
import java.util.List;

public class Server
	extends UnicastRemoteObject
	implements ServerInterfaceBase
{
	private static final String SERVER_ACC = "jdbc:mysql://localhost/sweng";
	private static final String DB_USER = "root";
	private static final String USER_PW = "root";

	private static final long DROP_TIME_FACTOR = 1000 * 3600 * 24;

	private static final String IMAGE_FOLDER = "imgs";

	private Registry registry;

	private Connection con;

	private PreparedStatement addOrder, addOrderProd, removeOrder, listOrders, getOrder, listActiveOrders, listSalesByDate,
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
		System.setProperty("java.rmi.server.hostname", HOST);

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
		addOrder = con.prepareStatement("INSERT INTO orders (uid) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
		addOrderProd = con.prepareStatement("INSERT INTO orderls (oid, pid, ct) VALUES (?, ?, ?)");
		removeOrder = con.prepareStatement("DELETE FROM orders WHERE id=?");
		listOrders = con.prepareStatement("SELECT orderls.oid, orderls.ct, product.* FROM orderls INNER JOIN product ON orderls.pid = product.pid");
		listActiveOrders = con.prepareStatement("SELECT orderls.oid, orderls.ct, product.* FROM " +
															"orderls INNER JOIN product ON orderls.pid = product.pid INNER JOIN orders ON orderls.oid = orders.oid " +
															"WHERE orders.status = 'pending'");
		listSalesByDate = con.prepareStatement("SELECT orderls.pid, orderls.ct, orders.created FROM orderls INNER JOIN orders ON orderls.oid = orders.oid");
		getOrder = con.prepareStatement("SELECT orderls.ct, product.* FROM orderls INNER JOIN product ON orderls.pid = product.pid WHERE orderls.oid=?");
		addProduct = con.prepareStatement("INSERT INTO product (name, description, iconFile, price) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		removeProduct = con.prepareStatement("DELETE FROM product WHERE pid=?");
		getProduct = con.prepareStatement("SELECT * FROM product WHERE pid=?");
		listProducts = con.prepareStatement("SELECT * FROM product");
		getLocation = con.prepareStatement("SELECT xcoord, ycoord FROM location WHERE pid=?");
		setLocation = con.prepareStatement("INSERT INTO location (pid, xcoord, ycoord) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE xcoord=?, ycoord=?");
		addUser = con.prepareStatement("INSERT INTO user (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
		removeUser = con.prepareStatement("DELETE FROM user WHERE uid=?");
		getUser = con.prepareStatement("SELECT * FROM user WHERE uid=?");
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
			nextAvailableFile = Arrays.stream(folder.list()).map(s -> s.substring(0, s.indexOf('.'))).
					mapToInt(Integer::parseInt).max().orElse(-1) + 1;
		}
	}

	@Override
	public int addOrder(User user, Order order)
		throws RemoteException
	{
		try{
			int oid;

			addOrder.setInt(1, (int) user.getUid());
			addOrder.execute();

			ResultSet res = addOrder.getGeneratedKeys();
			if(res.next())
				oid = res.getInt(1);
			else
				throw new RemoteException("Failed to retrieve OID");

			for(int i = 0; i < order.size(); i++)
			{
				addOrderProd.setInt(1, oid);
				addOrderProd.setInt(2, order.get(i).product.getPid());
				addOrderProd.setInt(3, order.get(i).count);
				addOrderProd.execute();
			}

			return oid;
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
			Order o = new Order();

			getOrder.setInt(1, oid);
			ResultSet res = getOrder.executeQuery();

			while(res.next())
			{
				try {
					Product p = new Product(res.getInt(2), res.getString(3), res.getString(4),
							new ImageIcon(ImageIO.read(new File(res.getString(5)))), res.getDouble(6));

					o.addOderElement(new OrderElement(p, res.getInt(1)));
				}catch (IOException e)
				{
					throw new RemoteException("Failed to retrieve product icon", e);
				}
			}

			return o;
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to retrieve order", e);
		}
	}

	@Override
	public List<Order> listOrders()
		throws RemoteException
	{
		try {
			Map<Integer, Order> orders = new HashMap<>();

			// SELECT orderls.oid, orderls.count, product.* FROM orderls INNERJOIN product ON orderls.pid = product.pid
			ResultSet res = listOrders.executeQuery();

			while(res.next())
			{
				try {
					Product p = new Product(res.getInt(3), res.getString(4), res.getString(5),
							new ImageIcon(ImageIO.read(new File(res.getString(6)))), res.getDouble(7));

					if(orders.get(res.getInt(1)) == null)
					{
						orders.put(res.getInt(1), new Order());
					}

					orders.get(res.getInt(1)).addOderElement(new OrderElement(p, res.getInt(2)));
				}catch (IOException e)
				{
					throw new RemoteException("Failed to retrieve product icon", e);
				}
			}

			return new ArrayList<>(orders.values());
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to retrieve orders", e);
		}
	}

	@Override
	public List<Order> listActiveOrders() throws RemoteException {
		try {
			Map<Integer, Order> orders = new HashMap<>();

			// SELECT orderls.oid, orderls.count, product.* FROM orderls INNERJOIN product ON orderls.pid = product.pid
			ResultSet res = listActiveOrders.executeQuery();

			while(res.next())
			{
				try {
					Product p = new Product(res.getInt(3), res.getString(4), res.getString(5),
							new ImageIcon(ImageIO.read(new File(res.getString(6)))), res.getDouble(7));

					if(orders.get(res.getInt(1)) == null)
					{
						orders.put(res.getInt(1), new Order());
					}

					orders.get(res.getInt(1)).addOderElement(new OrderElement(p, res.getInt(2)));
				}catch (IOException e)
				{
					throw new RemoteException("Failed to retrieve product icon", e);
				}
			}

			return new ArrayList<>(orders.values());
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to retrieve orders", e);
		}
	}

	@Override
	public Map<Date, Map<Integer, Integer>> listSalesByDate() throws RemoteException {
		try {
			ResultSet res = listSalesByDate.executeQuery();

			Map<Date, Map<Integer, Integer>> sales = new HashMap<>();

			while(res.next())
			{
				int pid = res.getInt(1);
				int ct = res.getInt(2);
				Date created = res.getDate(3);
				created = new Date(created.getYear(), created.getMonth(), created.getDate());

				Map<Integer, Integer> tmp = sales.get(created);

				if(tmp == null)
				{
					tmp = new HashMap<>();

					sales.put(created, tmp);
				}

				if(tmp.containsKey(pid))
					tmp.put(pid, tmp.get(pid) + ct);
				else
					tmp.put(pid, ct);
			}

			return sales;
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to retrieve orders by date", e);
		}
	}

	@Override
	public int addProduct(String name, String description, ImageIcon img, double price)
		throws RemoteException
	{
		String fn = IMAGE_FOLDER + "/" + nextAvailableFile + ".png";
		nextAvailableFile++;

		try {
			Image i = img.getImage();
			BufferedImage bi = new BufferedImage(i.getWidth(null),i.getHeight(null),BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2 = bi.createGraphics();
			g2.drawImage(i, 0, 0,i.getWidth(null),i.getHeight(null), null);
			g2.dispose();

			ImageIO.write(bi, "png", new FileOutputStream(new File(fn)));
		}catch (IOException e)
		{
			throw new RemoteException("Failed to store image", e);
		}

		try {
			addProduct.setString(1, name);
			addProduct.setString(2, description);
			addProduct.setString(3, fn);
			addProduct.setDouble(4, price);
			addProduct.execute();

			ResultSet res = addProduct.getGeneratedKeys();

			if(res.next())
				return res.getInt(1);
			else
				throw new RemoteException("Failed to retrieve PID");
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to write product to database", e);
		}
	}

	@Override
	public void removeProduct(int pid) throws RemoteException {
		try {
			removeProduct.setInt(1, pid);
			removeProduct.execute();
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to delete product", e);
		}
	}

	@Override
	public Product getProduct(int pid) throws RemoteException {
		try {
			getProduct.setInt(1, pid);
			ResultSet res = getProduct.executeQuery();

			if(!res.next())
				throw new RemoteException("No such Product");

			return new Product(res.getInt(1), res.getString(2), res.getString(3),
					new ImageIcon(ImageIO.read(new File(res.getString(4)))), res.getDouble(5));
		}catch (SQLException | IOException e)
		{
			throw new RemoteException("Failed to read product", e);
		}
	}

	@Override
	public List<Product> listProducts()
		throws RemoteException
	{
		try{
			ResultSet res = listProducts.executeQuery();

			List<Product> prodList = new ArrayList<>();

			while(res.next())
			{
				try {
					prodList.add(new Product(res.getInt(1), res.getString(2), res.getString(3),
							new ImageIcon(ImageIO.read(new File(res.getString(4)))), res.getDouble(5)));
				}catch (IOException e)
				{
					throw new RemoteException("Failed to load icon for product", e);
				}
			}

			return prodList;
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to read product-list", e);
		}
	}

	@Override
	public int[] getLocation(int pid) throws RemoteException {
		try {
			getLocation.setInt(1, pid);
			ResultSet res = getLocation.executeQuery();

			if(!res.next())
				throw new RemoteException("Entry not found");

			return new int[]{res.getInt(1), res.getInt(2)};
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to retrieve product-location");
		}
	}

	@Override
	public void setLocation(int pid, int x, int y) throws RemoteException
	{
		try {
			setLocation.setInt(1, pid);
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
	public int addUser(String name) throws RemoteException {
		try {
			addUser.setString(1, name);
			addUser.execute();

			ResultSet res = addUser.getGeneratedKeys();

			if(res.next())
				return res.getInt(1);
			else
				throw new RemoteException("Failed to retrieve UID");
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to insert user", e);
		}
	}

	@Override
	public void removeUser(int uid) throws RemoteException {
		try {
			removeUser.setInt(1, uid);
			removeUser.execute();
		}catch (SQLException e)
		{
			throw new RemoteException("Failed to remove user", e);
		}
	}

	@Override
	public User getUser(int uid) throws RemoteException {
		try {
			getUser.setInt(1, uid);
			ResultSet res = getUser.executeQuery();

			if(!res.next())
				throw new RemoteException("No such user");

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

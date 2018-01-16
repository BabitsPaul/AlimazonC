package webservices;

import misc.Order;
import misc.OrderElement;
import misc.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;

public class DBUpdateUtil {
	private static ServerInterfaceBase base;

	public static void main(String[] args)
		throws IOException
	{
		ClientInterface.init();
		base = ClientInterface.getBase();

		if(args.length > 0 && args[0].equals("shutdown"))
		{
			base.shutdownServer();
			System.out.println("Server shutdown");
			return;
		}

		if(args.length < 2)
		{
			System.out.println("Error: Too few parameters");
			System.out.println("Usage: <type> <file>");
			System.out.println("	Where <type> is one of <user>, <product>, <order> or <location>");
			System.out.println("	and <file> specifies the input-file");
			System.exit(1);
		}

		switch (args[0])
		{
			case "user": 	addUsers(args[1]); 		break;
			case "product": addProducts(args[1]);	break;
			case "location":addLocations(args[1]);	break;
			case "order":	addOrders(args[1]);		break;
			default:		System.err.println("Invalid type: " + args[0]);
							System.exit(1);	break;
		}
	}

	private static void addUsers(String fn)
		throws RemoteException
	{
		try(BufferedReader br = new BufferedReader(new FileReader(fn)))
		{
			String str;

			while((str = br.readLine()) != null)
			{
				System.out.println("User " + str + " with UID: " + base.addUser(str));
			}
		}catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void addProducts(String fn)
		throws IOException
	{
		try(BufferedReader br = new BufferedReader(new FileReader(fn)))
		{
			String str;

			while((str = br.readLine()) != null)
			{
				int idxEndProdName = str.indexOf('\"', 1);
				int idxEndDescription = str.indexOf('\"', idxEndProdName + 3);
				int idxEndImage = str.indexOf(' ', idxEndDescription + 2);

				String prodName = str.substring(1, idxEndProdName);
				String description = str.substring(idxEndProdName + 3, idxEndDescription);
				String iconStr = str.substring(idxEndDescription + 2, idxEndImage);

				int i = base.addProduct(prodName, description, new ImageIcon(ImageIO.read(new File(iconStr))),
						Double.parseDouble(str.substring(idxEndImage)));

				System.out.println(prodName + " with pid: " + i);
			}
		}catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void addLocations(String fn)
	{
		try(BufferedReader br = new BufferedReader(new FileReader(fn)))
		{
			String str;

			while((str = br.readLine()) != null)
			{
				String[] s = str.split(" ");

				base.setLocation(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			}
		}catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void addOrders(String fn)
	{
		try(BufferedReader br = new BufferedReader(new FileReader(fn)))
		{

			String str;

			while((str = br.readLine()) != null)
			{
				String[] s = str.split(" ");

				User u = base.getUser(Integer.parseInt(s[0]));
				Order o = new Order();

				for(int i = 1; i < s.length; i += 2)
				{
					o.addOderElement(new OrderElement(base.getProduct(Integer.parseInt(s[i])), Integer.parseInt(s[i + 1])));
				}

				base.addOrder(u, o);
			}
		}catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}

package webservices;

import misc.Order;

public class Test {
	public static void main(String[] args)
		throws Exception
	{
		ClientInterface.init();
		ServerInterfaceBase base = ClientInterface.getBase();

		for(Order o : base.listActiveOrders())
		{
			System.out.println();

			for(int i = 0; i < o.size(); i++)
			{
				System.out.println(o.get(i).count + " of " + o.get(i).product.getName());
			}
		}
	}
}

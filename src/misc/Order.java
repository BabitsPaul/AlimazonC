package misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order
	implements Serializable
{
	private List<OrderElement> orderElementList=new ArrayList<OrderElement>();
	
	public void addOderElement(OrderElement o)
	{
		orderElementList.add(o);
	}
	
	public void removeOrderElement(int i)
	{
		orderElementList.remove(i);
	}
	
	public void moveDown(int i)
	{
		
		
		OrderElement o=orderElementList.get(i);
		if(o.done)
			return;
		
		
		orderElementList.remove(i);
		
		
		for (int j=0; j<orderElementList.size(); j++)
		{
			if (orderElementList.get(j).done)
			{
				orderElementList.add(j, o);
				return;
			}
		}
		orderElementList.add(orderElementList.size(), o);

		
	}

	public OrderElement get(int i)
	{
		return orderElementList.get(i);
	}

	public void switchState(int i)
	{
		OrderElement o=orderElementList.get(i);
		o.done=!o.done;
		orderElementList.remove(i);
		if (o.done)
		{
			orderElementList.add(orderElementList.size(), o);
		}
		else
		{
			orderElementList.add(0,o);
		}
	}

	public int size()
	{
		return orderElementList.size();
	}
	
}

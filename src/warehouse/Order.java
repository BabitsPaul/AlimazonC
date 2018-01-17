package warehouse;

import java.util.ArrayList;
import java.util.List;
/**
 * basically contains a list or OrderElements, but provides additional functions to sort and change the status (todo/done) or an element
 * 
 * @author stefan
 *
 */
public class Order {
	
	int startX=-1, startY=-1; 
	
	/**
	 * set the position where the warehouse worker has to return with the items - important to calculate the distance correctly
	 * 
	 * @param x
	 * @param y
	 */
	public void setStartKoords(int x, int y)
	{
		this.startX=x;
		this.startY=y;
	}
	
	/**
	 * sorts the elements in the OrderElement list so that the whole path from a starting position and returning to it is a short as possible
	 * 
	 * @param x
	 * @param y
	 */
	public void sort(int x, int y)
	{
		if(x>1000)
			x=1000;
		else if(x<0)
			x=0;
		
		if(y>1000)
			y=1000;
		else if(y<0)
			y=0;
		
		List<OrderElement> listToSort=new ArrayList<OrderElement>();
		List<OrderElement> alreadyDone=new ArrayList<OrderElement>();

		for (OrderElement oe:orderElementList)
		{
			if(oe.done)
				alreadyDone.add(oe);
			else
				listToSort.add(oe);
		}
		
		if (listToSort.size()<1)
		{
			return;
		}
		
		List<OrderElement> sorted=sort(x,y,listToSort,x,y).o2;
		
		orderElementList.clear();
		
		for (OrderElement oe:sorted)
		{
			orderElementList.add(oe);
		}
		
		for (OrderElement oe:alreadyDone)
		{
			orderElementList.add(oe);
		}
	}
	
	/**
	 * internal recursive algorithm - tries out all possible products as next one from the previous one and compares all possible outcomes
	 * to find the shortest path
	 * 
	 * @param startX
	 * @param startY
	 * @param openElements
	 * @param lastX
	 * @param lastY
	 * @return
	 */
	private Pair<Integer, List<OrderElement>> sort(int startX, int startY, List<OrderElement> openElements, int lastX, int lastY)
	{
		if(openElements.size()==1)
		{
			int dx=Math.abs(lastX-openElements.get(0).product.getPosX())+Math.abs(startX-openElements.get(0).product.getPosX());
			int dy=Math.abs(lastY-openElements.get(0).product.getPosY())+Math.abs(startY-openElements.get(0).product.getPosY());

			return new Pair<Integer, List<OrderElement>>(new Integer((int)Math.sqrt(dx*dx+dy*dy)), openElements);
			
		}
		
		int min=999999999;
		List<OrderElement> minList=null;
		int minIndex=-1;
		
		for(int i=0; i<openElements.size(); i++)
		{
			List<OrderElement> current=new ArrayList<OrderElement>();
			for(int j=0; j<openElements.size(); j++)
			{
				if(i!=j)
				{
					current.add(openElements.get(j));
				}
			}
			
			Pair<Integer, List<OrderElement>> currentPair=sort(openElements.get(i).product.getPosX(),openElements.get(i).product.getPosY(),
					current, lastX, lastY);
			
			int dx=Math.abs(openElements.get(i).product.getPosX()-startX);
			int dy=Math.abs(openElements.get(i).product.getPosY()-startY);
			int distance=(int)Math.sqrt(dx*dx+dy*dy);
			
			
			if(currentPair.o1.intValue()+distance<min)
			{
				min=currentPair.o1.intValue()+distance;
				minList=currentPair.o2;
				minIndex=i;
			}
		}
		
		minList.add(0, openElements.get(minIndex));
		
		return new Pair<Integer, List<OrderElement>>(new Integer(min), minList);
		
	}
	
	private List<OrderElement> orderElementList=new ArrayList<OrderElement>();
	
	/**
	 * add an element to the list
	 * 
	 * @param o
	 */
	public void addOderElement(OrderElement o)
	{
		orderElementList.add(o);
		if(startX>-1&&startY>-1)
			sort(startX, startY);
	}
	
	/**
	 * remove an element from the list
	 * 
	 * @param i
	 */
	public void removeOrderElement(int i)
	{
		orderElementList.remove(i);
		if(startX>-1&&startY>-1)
			sort(startX, startY);
	}
	
	/**
	 * allows the worker to skip an element in case it is currently not available, or he just does not find it and wants to ask his
	 * co-worker later. Just moves down an still active (OPEN-State) order to that it is on the bottom of the ACTIVE orders
	 * 
	 * @param i
	 */
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
	/**
	 * returns an element from the list
	 * 
	 * @param i
	 * @return
	 */
	public OrderElement get(int i)
	{
		return orderElementList.get(i);
	}
	
	/**
	 * 
	 * switches the state of an orderElement from done to open or the other way round
	 * @param i
	 */
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
		
		if(startX>-1&&startY>-1&&!o.done)
			sort(startX, startY);
	}
	
	/**
	 * 
	 * current size of the list
	 * @return
	 */
	public int size()
	{
		return orderElementList.size();
	}
	
	/**
	 * removes all order elements from the list
	 * 
	 */
	public void clear()
	{
		orderElementList.clear();
		
	}
	
}

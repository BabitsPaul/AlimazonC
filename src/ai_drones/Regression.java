package ai_drones;

import webservices.ClientInterface;
import webservices.ServerInterfaceBase;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.trees.m5.PreConstructedLinearModel;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Regression
{
	public static void main(String[] args)
		throws Exception
	{
		Regression r = new Regression();
		r.init();
		r.loadData();
	}

	private ServerInterfaceBase si;

	private Map<Integer, Map<Date, Integer>> sales;

	public Regression()
	{
		sales = new HashMap<>();
	}

	public void init()
		throws RemoteException
	{
		ClientInterface.init();
		si = ClientInterface.getBase();
	}

	/**
	 * Generates a Map from Products to sales per day
	 */
	public void loadData()
		throws RemoteException
	{
		Map<Date, Map<Integer, Integer>> sales = si.listSalesByDate();

		for(Date d : sales.keySet())
		{
			Map<Integer, Integer> dailySales = sales.get(d);

			for(int pid : dailySales.keySet())
			{
				Map<Date, Integer> m = this.sales.get(pid);

				if(m == null)
				{
					m = new HashMap<>();
					this.sales.put(pid, m);
				}

				m.put(d, dailySales.get(pid));
			}
		}

		System.out.println(sales);
	}

	public LinearRegression genLinearRegression(int pid)
		throws Exception
	{
		Attribute dateAttr = new Attribute("date", new SimpleDateFormat().toLocalizedPattern());
		Attribute countAttr = new Attribute("sold");

		ArrayList<Attribute> attributes = new ArrayList<>();
		attributes.add(dateAttr);	// date-attribute
		attributes.add(countAttr);	// numeric attribute

		Instances inst = new Instances("linear reg - " + pid, attributes, sales.get(pid).size());
		Map<Date, Integer> sold = sales.get(pid);
		DateFormat df = new SimpleDateFormat();
		for(Date d : sold.keySet())
		{
			DenseInstance denseInstance = new DenseInstance(attributes.size());
			denseInstance.setValue(dateAttr, df.format(d));
			denseInstance.setValue(countAttr, sold.get(d));
			inst.add(denseInstance);
		}

		// build a linear regression for the training data using the
		// tested attributes

		LinearRegression reg = new LinearRegression();

		LinearRegression temp = new LinearRegression();
		temp.buildClassifier(inst);

		return temp;
	}
}

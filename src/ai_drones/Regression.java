package ai_drones;

import webservices.ClientInterface;
import webservices.ServerInterfaceBase;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Regression
{
	private ServerInterfaceBase si;

	private Map<Integer, Map<Date, Integer>> sales;

	private Map<Integer, Instances> data;

	public Regression(ServerInterfaceBase si)
	{
		sales = new HashMap<>();
		this.si = si;
	}

	/**
	 * Generates a Map from Products to sales per day
	 */
	public void loadData()
		throws RemoteException
	{
		data = new HashMap<>();
		this.sales = new HashMap<>();

		Map<Date, Map<Integer, Integer>> salesPerDate = si.listSalesByDate();

		for(Date d : salesPerDate.keySet())
		{
			Map<Integer, Integer> dailySales = salesPerDate.get(d);

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
	}

	public LinearRegression genLinearRegression(int pid)
		throws Exception
	{
		LinearRegression temp = new LinearRegression();
		temp.buildClassifier(getInstance(pid));

		return temp;
	}

	public MultilayerPerceptron getNonLinearRegression(int pid)
		throws Exception
	{
		MultilayerPerceptron tmp = new MultilayerPerceptron();
		tmp.buildClassifier(getInstance(pid));

		return tmp;
	}

	private Instances getInstance(int pid)
	{
		Instances inst;

		if(data.containsKey(pid))
		{
			inst = data.get(pid);
		}
		else {
			Attribute dateAttr = new Attribute("date", new SimpleDateFormat().toLocalizedPattern());
			Attribute countAttr = new Attribute("sold");

			ArrayList<Attribute> attributes = new ArrayList<>();
			attributes.add(dateAttr);    // date-attribute
			attributes.add(countAttr);    // numeric attribute

			inst = new Instances("linear reg - " + pid, attributes, sales.get(pid).size());
			Map<Date, Integer> sold = sales.get(pid);
			DateFormat df = new SimpleDateFormat();
			for (Date d : sold.keySet()) {
				DenseInstance denseInstance = new DenseInstance(attributes.size());
				denseInstance.setValue(dateAttr, df.format(d));
				denseInstance.setValue(countAttr, sold.get(d));
				inst.add(denseInstance);
			}

			inst.setClass(countAttr);
		}

		return inst;
	}

	public Set<Integer> listAvailableProductIDs()
	{
		return sales.keySet();
	}

	public Map<Date, Integer> getProductSales(int pid)
	{
		return sales.get(pid);
	}
}

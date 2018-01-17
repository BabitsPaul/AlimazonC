package ai_drones;

import misc.Product;
import webservices.ClientInterface;
import webservices.ServerInterfaceBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.stream.Collectors;

public class RegressionUI
{
	public static void main(String[] args)
	{
		new RegressionUI().init();
	}

	private Regression r;

	private JFrame frame;

	private ServerInterfaceBase si;

	private java.util.List<Product> products;

	private DataRenderer renderer;

	public RegressionUI()
	{

	}

	public void init()
	{
		ClientInterface.init();
		si = ClientInterface.getBase();

		r = new Regression(si);

		try{
			r.loadData();
		}catch (RemoteException e)
		{
			System.err.println("Failed to load data");
			e.printStackTrace();

			return;
		}

		products = r.listAvailableProductIDs().stream().
				map(pid -> { try{ return si.getProduct(pid); }catch (RemoteException e) { return null; }} ).
				filter(prod -> prod != null).
				collect(Collectors.toList());

		JComboBox<Product> jcb = new JComboBox<>(new Vector<>(products));

		JButton run = new JButton("Generate regression");
		run.addActionListener(e -> {});

		JPanel left = new JPanel();
		left.add(jcb);
		left.add(run);

		renderer = new DataRenderer();

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, renderer);

		frame = new JFrame("Regression");
		frame.setContentPane(split);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}

	private class DataRenderer
		extends JPanel
	{
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);


		}
	}
}

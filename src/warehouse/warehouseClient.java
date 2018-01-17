package warehouse;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JTable;

import webservices.ClientInterface;
import webservices.ServerInterfaceBase;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class warehouseClient {

	private static ServerInterfaceBase base;

	
	private JFrame frame;
	private JTable orderTable;
	private OrderTableModel orderTableModel;
	private Order ord;
	private JScrollPane sp;
	private int currentOrderIndex=0;
		
	private ProductMap productMap;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					warehouseClient window = new warehouseClient();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public warehouseClient() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1594, 877);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton btnReceiveOrder = new JButton("receive order");
		btnReceiveOrder.setBounds(12, 792, 770, 25);
		frame.getContentPane().add(btnReceiveOrder);



		
		ord = new Order();
		
		ord.setStartKoords(570, 700);

		orderTableModel = new OrderTableModel(ord);
		orderTable = new JTable(orderTableModel);
		
		//orderTable.setBounds(12, 13, 770, 766);
		
		sp=new JScrollPane(orderTable);
		sp.setBounds(12, 13, 770, 766);
		frame.getContentPane().add(sp);

		orderTable.setAutoCreateColumnsFromModel(true);
		// frame.getContentPane().add(new JScrollPane(orderTable));
		orderTable.setFont(new Font("SansSerif", Font.PLAIN, 20));
		orderTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 20));
		orderTable.setRowHeight(30);

		orderTable.getColumnModel().getColumn(0).setPreferredWidth(500);
		orderTable.getColumnModel().getColumn(1).setPreferredWidth(20);
		orderTable.getColumnModel().getColumn(2).setPreferredWidth(30);
		orderTable.getColumnModel().getColumn(3).setPreferredWidth(30);

		try {
		productMap=new ProductMap("C:\\Users\\stefan\\softwareEngineering\\src\\softwareEngineering\\warehouse2.jpg", ord, 800, 14, 765, 765);
		frame.add(productMap);
		
		}
		catch(Exception e)
		{
			System.err.println("Unfortunately no map of the warehouse available. Check if background image is in the correct folder");
			
		}

		ClientInterface.init();
		base = ClientInterface.getBase();
		
		orderTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = orderTable.rowAtPoint(evt.getPoint());
				int col = orderTable.columnAtPoint(evt.getPoint());

				orderTableModel.manageEvent(row, col, orderTable);
				productMap.refresh();
			}

		});
		
		productMap.refresh();

		
		btnReceiveOrder.addActionListener(new ActionListener() {
			
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ord.clear();

				try {
					getOrderFromServer();
					
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				frame.getContentPane().remove(sp);
				frame.getContentPane().add(sp);

				productMap.refresh();
				
				
				
//				ord.clear();
//				// testdata - can be used to test the warehouse-client if no server connection is available
//				Product p0 = new Product("Indiana Jerky Pork Original 200g", 10.82f, 0, 318, 367);
//				Product p1 = new Product("Huawei P10", 405.0f, 1, 448, 465);
//				Product p2 = new Product("Black Diamond Chalk 300g", 9.45f, 2, 615, 219);
//				Product p3 = new Product("Alimazon Alexandra Echo Dot", 100.0f, 3, 615, 426);
//				Product p4 = new Product("MOVIT Teleskop Klimmzugstange", 15.9f, 4, 818, 143);
//				
//				
//
//				OrderElement o0 = new OrderElement(p0, 5);
//				OrderElement o1 = new OrderElement(p1, 1);
//				OrderElement o2 = new OrderElement(p2, 1);
//				OrderElement o3 = new OrderElement(p3, 1);
//				OrderElement o4 = new OrderElement(p4, 1);
//				
//				ord.addOderElement(o0);
//				ord.addOderElement(o1);
//				ord.addOderElement(o2);
//				ord.addOderElement(o3);
//				ord.addOderElement(o4);
//				
//				
//				frame.getContentPane().remove(sp);
//				frame.getContentPane().add(sp);
//
//				productMap.refresh();
//

			}
		});


	}

	
	/**
	 * 
	 * gets Data about orders from the server
	 * @throws IOException 
	 */
	private void getOrderFromServer() throws IOException {
		
		misc.Order mo=null;
		
		for(;currentOrderIndex<10000;currentOrderIndex++)
		{
			mo=base.getOrder(currentOrderIndex);
			
			if(mo.size()>0)
				break;
			
			
			
		}
		if(mo!=null)
		{
			if(mo.size()>0)
			{
				importMiscOrderToOrd(mo);
			}
		}
		
	}
	
	/**
	 * converts an order as it is stored in the database to a warehouseclient order element
	 * the object structure is very similar and mainly differs in functionality
	 * 
	 * 
	 * @param mo
	 */
	private void importMiscOrderToOrd(misc.Order mo)
	{
		for (int i=0; i<mo.size(); i++)
		{
			int c=mo.get(i).count;
			misc.Product mp=mo.get(i).product;
			
			int[] location=null;
			try {
				location=base.getLocation(mp.getPid());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Product wp=new Product(mp.getName(), mp.getPrice(), mp.getPid(), location==null?0:location[0], location==null?0:location[1]);
			
			OrderElement o = new OrderElement(wp, c);
			
			ord.addOderElement(o);
		}
		
	}
	
	
}

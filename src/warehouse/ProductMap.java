package warehouse;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * JPanel which takes a background image as map of the warehouse and an Order element. The active elements
 * of the order object are added to the map. The first element is marked green, all others red
 * 
 * @author stefan
 *
 */
class ProductMap extends JPanel {

	  private Image img;
	  private Order order;
	  
	  private List<JLabel> labels=new ArrayList<JLabel>();

	  public ProductMap(String img, Order order, int x, int y, int w, int h) throws IOException {
	    this(ImageIO.read(new File(img)), order, x, y, w, h);
	  }

	  public ProductMap(Image img, Order order, int x, int y, int w, int h) {
		this.order=order;
		super.setBounds(x,y,w,h);
	    this.img = img.getScaledInstance(super.getWidth(), super.getHeight(), Image.SCALE_SMOOTH);
	    Dimension size = new Dimension(super.getWidth(), super.getHeight());//(img.getWidth(null), img.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    setLayout(null);
	  }

	  public void paintComponent(Graphics g) {
	    g.drawImage(img, 0, 0, null);
	  }
	  
	  
/**
 * 
 * updates the markers on the map - you should call this function when changing the order of the orderelements
 */
	public void refresh() {
		
		for(JLabel l:labels)
		{
			l.setVisible(false);
			this.remove(l);
		}
			
		this.repaint();
		
		labels.clear();
		
		for(int i=0; i<order.size(); i++)
		{
			if (!order.get(i).done)
			{
				int x=order.get(i).product.getPosX();
				int y=order.get(i).product.getPosY();

				
				  JLabel l=new JLabel();
				  //l.setText("X");
				  l.setText("X  "+order.get(i).product.name);
				  l.setForeground(i==0?Color.GREEN:Color.RED);
				  l.setBounds((int)((double)(x)/1000.0*(double)this.getWidth()), (int)((double)(y)/1000.0*(double)this.getHeight()), 100, 10);
				  
				  this.add(l);
				  labels.add(l);
			}
		}
		
	}
	  
	  
	  

	}

package warehouse;

/**
 * 
 * this class represents a product
 * @author stefan
 *
 */
public class Product {
	/**
	 * a product needs a name, a price and an id
	 * 
	 * @param name
	 * @param price
	 * @param id
	 */
	public Product(String name, double price, int id) {
		super();
		this.name = name;
		this.id = id;
		this.price=price;
	}
	
	/**
	 * same as above with additional coordinates for the position in the warehouse - the position goes from 0 to 1000, other values
	 * are not possible
	 * 
	 * @param name
	 * @param d
	 * @param id
	 * @param x
	 * @param y
	 */
	public Product(String name, double d, int id, int x, int y)
	{
		this(name, d, id);
		this.setPosX(x);
		this.setPosY(y);
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		if(posX>1000)
			posX=1000;
		else if(posX<0)
			posX=0;
		this.posX = posX;
	}
	public int getPosY() {
		if(posX>1000)
			posX=1000;
		else if(posX<0)
			posX=0;
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	public String name;
	public int id;
	public double price;
	private int posX=0, posY=0;
	
	
}

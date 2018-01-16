package misc;

import java.io.Serializable;

public class OrderElement
	implements Serializable
{
	public OrderElement(Product product, int count) {
		super();
		this.product = product;
		this.count = count;
		this.done = false;
	}

	public Product product;

	public int count;

	public boolean done;
}

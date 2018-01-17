package misc;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Product
	implements Serializable
{
	private int pid;

	private ImageIcon img;

	private String name;

	private String description;

	private double price;

	public Product(int pid, String name, String description, ImageIcon icon, double price) {
		this.name = name;
		this.description = description;
		this.img = icon;
		this.pid = pid;
		this.price = price;
	}

	public ImageIcon getImg() {
		return img;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getPid() {
		return pid;
	}

	public double getPrice() {
		return price;
	}

	public String toString()
	{
		return name;
	}
}

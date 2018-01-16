package misc;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Product
	implements Serializable
{
	private ImageIcon img;

	private String name;

	private String description;

	public Product(String name, String description, ImageIcon icon) {
		this.name = name;
		this.description = description;
		this.img = icon;
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
}

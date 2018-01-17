package misc;

import java.io.Serializable;

/**
 * Holds data from either regressions or search-request-analysis about
 * whether or what amount of a certain product should be ordered.
 */
public class AIReport
	implements Serializable
{
	private Product product;

	/**
	 * Expected sales per week
	 */
	private int[] expectedSales;

	public AIReport(Product product, int[] expectedSales)
	{
		this.product = product;
		this.expectedSales = expectedSales;
	}

	public Product getProduct() {
		return product;
	}

	public int[] getExpectedSales() {
		return expectedSales;
	}
}

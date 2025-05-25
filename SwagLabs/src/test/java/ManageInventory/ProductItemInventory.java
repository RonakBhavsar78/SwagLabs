package ManageInventory;

import java.io.IOException;
import org.testng.annotations.Test;
import BaseFile.CommonAnnotation;
import Inventory.ProductInventory;

public class ProductItemInventory extends CommonAnnotation {
	
	@Test(priority=1)
	public void testProductItem() throws IOException, InterruptedException
	{
		ProductInventory.ProductsortNamesDescending (propertyFileName);
	}
	
	@Test(priority=2)
	public void testProductPrice() throws IOException, InterruptedException
	{
		ProductInventory.ProductPriceHighLow(propertyFileName);
	}
	
	@Test(priority=3)
	public void testProductAddtoCart() throws IOException, InterruptedException
	{
		ProductInventory.ProductAddtoCart(propertyFileName);
	}
	
	@Test(priority=4)
	public void testCheckout() throws IOException, InterruptedException
	{
		ProductInventory.Checkout(propertyFileName);
	}
	
}

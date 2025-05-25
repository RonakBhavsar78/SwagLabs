package Inventory;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.microsoft.playwright.Locator;

import Infrastructure.AccessibilityUtil;
import Infrastructure.BrowserConfiguration;
import Infrastructure.LoggerUtil;
import Infrastructure.VisualTestUtil;
import Infrastructure.WebControls;
import Infrastructure.WriteData;


public class ProductInventory {
	
	public static Double subtotalValue;
	
	/**
	* Below code sorts product names in descending order (Z to A),
	* validates the product names are correctly sorted,
	* checks the 'src' attribute of product images,
	* and adds the first product to the cart.
	*/
	public static void ProductsortNamesDescending(String PropertyFileName) {
		
		try {
			//Handle Accesiblity of product inventory page.
			AccessibilityUtil.handleAccessibility(BrowserConfiguration.page, "Productinventory");
			//Take screenshot of product inventory page for visual test.
			VisualTestUtil.runVisualTest(BrowserConfiguration.page, "ProductInventory", "Productinventory");
			WebControls.selectValueByOption(PropertyFileName, "ProductSorting", "za");
			BrowserConfiguration.page.waitForTimeout(1000);
			String ProductName = BrowserConfiguration.webControl.getpropertyValues(PropertyFileName, "ProductName");
			List<String> actual = BrowserConfiguration.page.locator(ProductName).allInnerTexts();
			List<String> expected = new ArrayList<>(actual); 
			expected.sort(Comparator.reverseOrder());
				if (expected.equals(actual)) {
					WriteData.actualResult = "Product item is sorted in descending order as expected. ";
					LoggerUtil.log(WriteData.actualResult);
				} else {
					WriteData.actualResult = "Product item is not sorted in descending order.";
					LoggerUtil.log(WriteData.actualResult);
				}
				assertEquals(expected, actual);
				WebControls.handleProductImages(PropertyFileName,BrowserConfiguration.page);
				String AddtoCart = BrowserConfiguration.webControl.getpropertyValues(PropertyFileName,"AddtoCart");
				BrowserConfiguration.page.locator(AddtoCart).first().click();
				} catch (Exception e) {
						e.printStackTrace();
				}
		}
	
	/**
	* Below code sorts product names in high to low,
	* validates the product price are correctly sorted, 
	* and adds the first product to the cart.
	*/

	public static void ProductPriceHighLow(String PropertyFileName) {
		
		try {
			BrowserConfiguration.page.waitForTimeout(50);
			WebControls.selectValueByOption(PropertyFileName,"ProductSorting", "hilo");
			BrowserConfiguration.page.waitForTimeout(1000);
			String ProductPrice = BrowserConfiguration.webControl.getpropertyValues(PropertyFileName,"ProductPrice");
			List<String> priceStrings = BrowserConfiguration.page.locator(ProductPrice).allInnerTexts();
	
			// Convert to numbers (remove currency symbols, commas etc.)
			List<Double> actualPrices = priceStrings.stream()
			// keep digits and dot only
			.map(s -> s.replaceAll("[^\\d.]", ""))
			.map(Double::parseDouble)
			.collect(Collectors.toList());
		
			List<Double> expectedPrices = new ArrayList<>(actualPrices); 
			expectedPrices.sort(Comparator.reverseOrder());
			if (expectedPrices.equals(actualPrices)) {
				WriteData.actualResult = "Product price is sorted in High to low order as expected.";
				LoggerUtil.log(WriteData.actualResult);
			} else {
				WriteData.actualResult = "Product price is not high sorted in high to low.";
				LoggerUtil.log(WriteData.actualResult);
			}
			assertEquals(expectedPrices, actualPrices);
			String AddtoCart = BrowserConfiguration.webControl.getpropertyValues(PropertyFileName,"AddtoCart");
			BrowserConfiguration.page.locator(AddtoCart).first().click();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	
	/**
	* Below code adds the first product to the cart,
	* verifies the URL via assertion,
	* adds the second product to the cart,
	* then clicks the shopping cart icon and proceeds to the checkout page.
	*/
	public static void ProductAddtoCart(String PropertyFileName) {
		try {
			String AddtoCart = BrowserConfiguration.webControl.getpropertyValues(PropertyFileName, "AddtoCart");
			BrowserConfiguration.page.locator(AddtoCart).first().click();
			WebControls.clickonButton(PropertyFileName, "ShoppingCartIcon");
			WebControls.assertByURL(".*/cart");
			AccessibilityUtil.handleAccessibility(BrowserConfiguration.page, "Cart");
			BrowserConfiguration.page.waitForTimeout(10);
			VisualTestUtil.runVisualTest(BrowserConfiguration.page, "Cart", "Cart");
			WebControls.clickonButton(PropertyFileName,"ContinueShopping");
			BrowserConfiguration.page.locator(AddtoCart).nth(1).click();
			WebControls.clickonButton(PropertyFileName, "ShoppingCartIcon");
			WebControls.clickonButton(PropertyFileName, "Checkout");
			WebControls.assertByURL(".*/checkout-step-one");
			WriteData.actualResult = "Checkout completed successfully.";
			LoggerUtil.log(WriteData.actualResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Below code will verifies the URL via assertion,
	* Fill the details of user after verify the product individual price with subtotal 
	* and verify the Final Amount with subtotal including tax price.
	*/
	public static void Checkout(String PropertyFileName) {
		try {
			WebControls.assertByURL(".*/checkout-step-one");
			AccessibilityUtil.handleAccessibility(BrowserConfiguration.page, "Checkout");
			VisualTestUtil.runVisualTest(BrowserConfiguration.page, "Checkout", "Checkout");
			WebControls.setvalueForTextbox(PropertyFileName, "FirstName", "Ronak");
			WebControls.setvalueForTextbox(PropertyFileName,"LastName", "Bhavsar");
			WebControls.setvalueForTextbox(PropertyFileName,"ZipCode", "382418");
			WebControls.clickonButton(PropertyFileName, "ContinueCheckout");
			WebControls.loginValidation();
			BrowserConfiguration.page.waitForTimeout(1000);
			CompareSubtotalwithProductPrice(PropertyFileName);
			CompareTotalPrice(PropertyFileName);
			WebControls.clickonButton(PropertyFileName, "CompleteOrder");
			WebControls.assertByURL(".*/checkout-complete");
			AccessibilityUtil.handleAccessibility(BrowserConfiguration.page, "Ordersummary");
			VisualTestUtil.runVisualTest(BrowserConfiguration.page, "Ordersummary", "Ordersummary");
			BrowserConfiguration.page.waitForTimeout(1000);
			WebControls.clickonButton(PropertyFileName,"BackToHome");
			WriteData.actualResult = "Order has been placed sucessfully";
			LoggerUtil.log(WriteData.actualResult);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void CompareSubtotalwithProductPrice(String PropertyFileName) {
		try {
		// Below code will Locate all individual item price.
		String InventoryItemPrice = BrowserConfiguration.webControl.getpropertyValues(PropertyFileName, "InventoryItemPrice");
		List<Locator> itemPrices = BrowserConfiguration.page.locator(InventoryItemPrice).all();
		
		// Below code will addition of all individual Product price.
		double total = 0.0;
		for (Locator pricelocator : itemPrices) {
		String priceText = pricelocator.innerText().replace("$", "").trim();
		total += Double.parseDouble(priceText);
		}
	
		// Below code will get the sub total value.
		String Subtotal = BrowserConfiguration.webControl.getpropertyValues(PropertyFileName,"Subtotal");
		String SubtotalText = BrowserConfiguration.page.locator(Subtotal).innerText();
		String subtotalValueText = SubtotalText.replace("Item total: $", "").trim();
		subtotalValue = Double.parseDouble(subtotalValueText);
		if (total == subtotalValue) {
			LoggerUtil.log("individual Price is same as subtotal price");
		} else {
			WriteData.actualResult = "individual Price is not same as subtotal price";
			LoggerUtil.log(WriteData.actualResult);
		}
		// Compare both subtotal, price and individual Product price.
		assertEquals(total, subtotalValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void CompareTotalPrice(String PropertyFileName) {
	try {
	// Below code will get the tax value.
	String tax = BrowserConfiguration.webControl.getpropertyValues(PropertyFileName, "Tax");
	String totalText = BrowserConfiguration.page.locator(tax).innerText();
	String totalValueText = totalText.replace("Tax: $", "").trim();
	double taxtotalValue = Double.parseDouble(totalValueText);
	
	double totalValue = subtotalValue + taxtotalValue;
	totalValue = Math.round(totalValue * 100.0) / 100.0;
	
	// Below code will get the Total Amount
	String totalAmount = BrowserConfiguration.webControl.getpropertyValues(PropertyFileName, "TotalAmount");
	String totalAmountText = BrowserConfiguration.page.locator(totalAmount).innerText();
	String totalAmountValueText = totalAmountText.replace("Total: $","").trim();
	double FinalAmountValue = Double.parseDouble(totalAmountValueText);
		if (totalValue == FinalAmountValue) {
			LoggerUtil.log("Product Price is same as Final Amount");
		} else {
			WriteData.actualResult = "Product Price is not same as Final Amount.";
			LoggerUtil.log(WriteData.actualResult);
			// Compare both Price total including tax and Final Amount. 
			assertEquals(totalValue, FinalAmountValue);
			} 
		}catch (Exception e) {
		e.printStackTrace();
		}
	}
}

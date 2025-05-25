# SwagLabs

## Swag Labs Automation Suite

This project automates the testing of [Swag Labs](https://www.saucedemo.com/), a demo e-commerce web application.  
The suite is built using **Java + Playwright** and includes:

- Functional (Feature) Testing  
- Visual Testing (Baseline vs Actual Screenshots)  
- Accessibility Testing (WCAG compliance using axe-core)  

---

## Tech Stack

| Tool         | Purpose                          |
|--------------|----------------------------------|
| Java 16+     | Programming language             |
| Playwright   | Browser automation & testing     |
| TestNG       | Test execution and reporting     |
| Apache POI   | Excel report generation          |
| axe-core     | Accessibility scanning (via JS)  |
| Maven        | Build and dependency management  |

---

## Test Coverage

This suite automates the following user flows and scenarios:

- Login with valid/invalid credentials (data stored in `TestData/LoginDetails.xlsx`)
- Product sorting by:
  - "Z to A" and verifying product names in descending order
  - "High to Low" and verifying price of the top item
- Adding items to the cart and validating the **Continue Shopping** flow
- Completing checkout with:
  - Step 1: Customer details input
  - Step 2: Verifying subtotal vs individual item prices
  - Step 3: Final total including tax
- Assertions handled using URL validation from every page
- Generated Reports:
  - ✅ `TestResult/Result-log.txt`
  - ✅ `TestResult/saucedemo.xlsx`
  - ✅ `test-output/emailable-report.html` (TestNG default)

---

## Visual Testing

- Captures full-page screenshots of key pages
- Compares **Actual** vs **Baseline** using pixel comparison
- Highlights UI differences in red
- Output includes:
  - `Baseline_<Page>.png`
  - `Actual_<Page>.png`
  - `Diff_<Page>.png`
  - Summary report: `TestResult/saucevisual.xlsx`

---

## Accessibility Testing

- Uses `axe.min.js` injected via Playwright
- Scans pages for WCAG 2.1 violations
- Outputs:
  - Human-readable: `TestResult/Accesiblitylog/<Page>.txt`
  - Structured Excel: `TestResult/sauceaccesiblity.xlsx`

---

## How to Run Tests

### Prerequisites

- Eclipse IDE
- Java 16+
- Maven installed
- TestNG plugin installed
- Internet connection to access Swag Labs

---

## Browser Configuration

The Playwright browser mode (headless or headed) is controlled from:

To switch between modes, update:
-Infrastructure/BrowserConfiguration.java
	-.setHeadless(true)  // Runs in background without opening a browser window
	-.setHeadless(false) // Opens the browser visibly for debugging

---

### Run All Tests

Run the main suite file:

```bash
testng `Saucedemo.xml`

Run the Individual Test Modules:

testng
- `XMLFiles/UserLogin.xml`  
- `XMLFiles/ProductItemInventory.xml`  

# Amazon Inventory Repricer
Repricing software for your inventory on Amazon.

This helps increase your sales by monitoring and automatically updating your inventory prices according to the current market. This saves countless hours of manually re-pricing hundreds, if not thousands, of listings.

### Features
* Finds the lowest price listing with equivalent or better condition, and either matches or beats the price by $0.01
* Calculates profit earned from the listed price after shipping, tax, and Amazon fees
* Sets price floor to a desired minimum profit
* Sets price ceiling to prevent offer from being delisted (happens when price is too high)
* Filters listings based on condition and seller rating

### How to Use
Create a text file in the `Amazon` directory with all of your product ASIN/ISBN-10 numbers and their condition. The condition value corresponds to the following table (see `books.txt` for an example):

| **Condition:**           | **Value:** |
|--------------------------|---------|
| New                      | 9       |
| Used - Like New          | 8       |
| Used - Very Good         | 7       |
| Used - Good              | 6       |
| Used - Acceptable        | 5       |
| Collectible - Like New   | 4       |
| Collectible - Very Good  | 3       |
| Collectible - Good       | 2       |
| Collectible - Acceptable | 1       |

After running the program, there will be a file inside the `Amazon` directory titled `<FileName>_out.txt`. The text file will contain an inventory list of all your products that met your minimum desired profit and their new prices.

For each product, the console (System.out) will display the name, the new price, and the net profit. If there are product(s) that did not meet your minimum desired profit, the console will also display a list of those product names at the end.

Invalid product ASIN or ISBN-10 numbers will be printed to System.out. Problems with network connection will throw an exception to System.err and terminate the program.

### Prerequisites
* Java JDK 1.7 or higher  
For convenience, you should add the `/bin` directory to the `PATH` environment variable.
* jsoup 1.8.2 or higher  
[jsoup](http://jsoup.org/) is a Java library for extracting and manipulating HTML data.


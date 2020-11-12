package com.bill.zografos;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ProductManager manager = new ProductManager(5);
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        boolean done = false;

        while (!done) {
            System.out.print(
                    "\nSelect an action: \n"+
                            "\t0. get product's data, \n\t1. for adding 1 product, \n\t2. for fetching the prices of a product, \n\t3. for adding many products, \n\t4. get products, \n\t5. find products by category, \n\t6. find prodcuts categories, \n\t7. exit\n"
            );

            System.out.print("Your action: ");
            String input = scanner.nextLine();

            // check if user wants to find a product
            if (Integer.decode(input) == 0) {
                System.out.print("Give product's name: ");

                String name = scanner.nextLine();
                String result = manager.findProduct(name);
                System.out.print(result);
            }

            // check if user wants to add a product
            if (Integer.decode(input) == 1) {
                System.out.print("Give product's name: ");
                String name = scanner.nextLine();

                System.out.print("Give product's barcode: ");
                String barcode = scanner.nextLine();

                System.out.print("Give product's price: ");
                float price = Float.valueOf(scanner.nextLine());

                System.out.print("Give product's category: ");
                String category = scanner.nextLine();

                System.out.print("Give product's description: ");
                String description = scanner.nextLine();

                System.out.print("\n");

                Product product = new Product(name, barcode, price, category, description);
                manager.addProduct(product);
                System.out.print("Your product was added!\n");
            }

            // check if user wants to get product prices
            if (Integer.decode(input) == 2) {
                System.out.print("Give the product's name: \n");
                String name = scanner.nextLine();

                String prices = manager.pricesOfProduct(name);
                System.out.print("\n"+name+"-prices:"+prices);
            }

            // check if user wants to many products
            if (Integer.decode(input) == 3) {
                List<Product> products = new ArrayList<>();

                System.out.print("Give the number products: ");
                int N = Integer.decode(scanner.nextLine());
                for (int i = 0; i < N; i++) {
                    System.out.print("Give product["+i+"] name: ");
                    String name = scanner.nextLine();

                    System.out.print("Give product["+i+"] barcode: ");
                    String barcode = scanner.nextLine();

                    System.out.print("Give product["+i+"] price: ");
                    float price = Float.valueOf(scanner.nextLine());

                    System.out.print("Give product["+i+"] category: ");
                    String category = scanner.nextLine();

                    System.out.print("Give product["+i+"] description: ");
                    String description = scanner.nextLine();

                    System.out.print("\n");
                    Product product = new Product(name, barcode, price, category, description);
                    products.add(product);
                }

                manager.addProducts(products);
                System.out.print("Your products were added!\n");
            }

            // check if user wants to find products
            if (Integer.decode(input) == 4) {
                System.out.print("Products: "+manager.findProducts()+"\n");
            }

            // check if user wants to find products by category
            if (Integer.decode(input) == 5) {
                System.out.print("Type the products category: ");
                String category = scanner.nextLine();
                System.out.print("Products by category: "+manager.findProductsByCategory(category)+"\n");
            }

            // check if user wants to find products categories
            if (Integer.decode(input) == 6) {
                System.out.print("Products categories: "+manager.getCategories()+"\n");
            }

            // check if user wants to exit
            if (Integer.decode(input) == 7) {
                System.out.print("Exit");
                done = true;
            }
        }
    }
}

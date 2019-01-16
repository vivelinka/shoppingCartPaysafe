package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import driverJNA.RawConsoleInput;

public class Main {


    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to \"VelMag\"!");
        System.out.println("Here is a list of our available products:");

        Map<String, Double> productWithPrices = new HashMap<>();
        Map<String, Integer> productQuantityList =new HashMap<>();

        Map<String, Integer> cartOfClient = new HashMap<>();
        double totalPrice = 0.0;

        fillProductData(productWithPrices, productQuantityList);
        showAllProductsInStock(productWithPrices, productQuantityList);

        String input = sc.nextLine();

        label:
        while (!input.equals("Exit")) {

            switch (input) {
                case "My Cart":
                    showClientCart(cartOfClient, productWithPrices);
                    String option = sc.nextLine();
                    if (option.equals("Check Out")) {
                        //TODO call the method for CheckOUt actions

                        break label;
                    } else if (option.equals("Remove")) {
                        System.out.println();
                        System.out.println("Please type the name of the product you want to remove from Your Cart\n");
                        System.out.println("Product:");
                        String productToRemove = sc.nextLine();
                      //  System.out.println("Quantity");

                        while (!cartOfClient.keySet().contains(productToRemove)) {
                            System.out.println("Sorry, no such product in your cart :( Please try again!");
                            productToRemove = sc.nextLine();
                        }
                        System.out.println("Quantity:");
                        String strQuantity = sc.nextLine();
                        int qty = Integer.parseInt(strQuantity);
                        int currentQuanity = cartOfClient.get(productToRemove);
                        if (qty >= currentQuanity) {
                            qty = currentQuanity;
                            cartOfClient.remove(productToRemove);
                        }

                        totalPrice -= productWithPrices.get(productToRemove) * qty;

                        int currentQuantity = productQuantityList.get(productToRemove);
                        int updatedQuantity = currentQuantity + qty;
                        productQuantityList.put(productToRemove, updatedQuantity);

                        if (cartOfClient.keySet().contains(productToRemove)){
                         cartOfClient.put(productToRemove,currentQuanity - qty);
                        }
                        showAllProductsInStock(productWithPrices, productQuantityList);
                        input = sc.nextLine();
                        continue;
                    } else if (option.equals("Back")) {
                        showAllProductsInStock(productWithPrices, productQuantityList);
                        break;
                    }else {
                        System.out.println("Sorry, no such option :( Please try again!");
                        input = sc.nextLine();
                    }
                    break;

                case "Add":
                    System.out.println();
                    System.out.println("Please type the name of the product to add to Your Cart\n");

                    String nameOfProduct = sc.nextLine();

                    while (!productWithPrices.containsKey(nameOfProduct)) {
                        System.out.println("Sorry, no such product :( Please try again!");
                        nameOfProduct = sc.nextLine();
                    }


                        System.out.print("Number of products you want to buy:\t");
                        //TODO try catch block for int  - check the value if it is real int
                        String strQuantity = sc.nextLine();
                        int qty = Integer.parseInt(strQuantity);

                        int quantityInStock = productQuantityList.get(nameOfProduct);
                        if (quantityInStock - qty < 0 ) {
                            System.out.printf("Not enough in stock! We have only %d of this product.\n", quantityInStock);
                            System.out.printf("Do you want to take %d %s? Please type \"y\" for yes and \"n\" for NO\n", quantityInStock, nameOfProduct);
                            String answer = sc.nextLine();
                            while (!answer.equals("y") && !answer.equals("n")) {
                                answer = sc.nextLine();
                            }
                            if (answer.equals("y")) {
                                cartOfClient.put(nameOfProduct, quantityInStock);
                                productQuantityList.put(nameOfProduct,0);
                            }else {
                                showAllProductsInStock(productWithPrices, productQuantityList);
                                continue;
                            }
                        }
                        if (cartOfClient.keySet().contains(nameOfProduct)) {
                            int currentQuantity = cartOfClient.get(nameOfProduct);
                            cartOfClient.put(nameOfProduct, currentQuantity + qty);
                        }else {
                            cartOfClient.put(nameOfProduct, qty);
                        }
                        double currentPrice = productWithPrices.get(nameOfProduct) * qty;
                        totalPrice += currentPrice;
                        productQuantityList.put(nameOfProduct, quantityInStock - qty);

                        System.out.printf("***** You have added %s to your cart! *****\n\n", nameOfProduct);

                        System.out.println("Press Enter to continue...");
                        int abv = RawConsoleInput.read(false);
                    System.out.println(abv);

                        showAllProductsInStock(productWithPrices, productQuantityList);
                        input = sc.nextLine();
                        continue;

                case "Check Out":
                    break label;
                case "Exit":
                    //TODO checkOut stuff
                    break;
                default:
                    System.out.println("Sorry, no such option :( Please try again!");
                    break;
            }

            input = sc.nextLine();
        }
        //TODO smth if cart is empty
        if (cartOfClient.isEmpty()) {
            //TODO show first page with checkOut???
        }

        //TODO move to CheckOut section
        System.out.println("You have paid successfully!\n");
        System.out.println("************* :)(: *************");
        System.out.println("Thank you and have a lovely day ahead!\n");
        //TODO Total sum to pay and Options Pay and Thank you message + Exit + pause
        //  Cancel - See you again!
    }



    public static void fillProductData
            (Map<String, Double> productWithPrices, Map <String, Integer> productQuantityList) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("listOfProducts.txt"));
        String line = "";
        while ((line = in.readLine()) != null) {
            String tokens[] = line.split("\\s+");
            double currentPrice = Double.parseDouble(tokens[1]);
            int currentQuantity = Integer.parseInt(tokens[2]);
            productWithPrices.put(tokens[0], currentPrice);
            productQuantityList.put(tokens[0], currentQuantity);
        }
        in.close();
    }


    public static void showClientCart(Map<String, Integer> currentCart, Map<String, Double> products) {

        double totalPrice = 0.0;

        System.out.println();
        System.out.println("==== My Cart ===");
        if (currentCart.size() != 1) {
            System.out.printf("You have %d items in Your Cart \n", currentCart.size());
        } else {
            System.out.println("You have 1 item in Your Cart \n");
        }
        System.out.println();
        for (String item : currentCart.keySet()) {
            String name = item;
            double currentPrice = products.get(item) * currentCart.get(item);
            totalPrice+= currentPrice;

            System.out.printf("*** Item: %s \t\t QTY: %d \tPrice: %.2f $\n", name, currentCart.get(item), currentPrice);
        }



        System.out.println("--------------------------------------------------------------");
        System.out.printf("*** Total price: \t\t %.2f $\n\n", totalPrice);
        System.out.println("Please choose one of the following options:");
        System.out.println("\t - \"Remove\" to remove an item from Your Cart");
        System.out.println("\t - \"Check Out\" to finish your order and pay");
        System.out.println("\t - \"Back\" to return to product list");

    }

    public static void showAllProductsInStock(Map<String, Double> productsWithPrices, Map<String, Integer> quantityList) {
        System.out.println("=======================***====================");
        for (String item : productsWithPrices.keySet()) {
            int currentQuantity = quantityList.get(item);
            if (currentQuantity > 10) {
                System.out.printf("%s\t %s %-10.2f $\t (In Stock.)\n", item, "Price", productsWithPrices.get(item));
            } else if (currentQuantity > 0){
                System.out.printf("%s\t %-10s %6.2f $\t (Only %-10d in stock!)\n", item, "Price", productsWithPrices.get(item),currentQuantity);
            }else if (currentQuantity == 0) {
                System.out.printf("%s\t %s %-10.2f $\t (Out of Stock!)\n", item, "Price", productsWithPrices.get(item));
            }
        }


        System.out.println("=======================***====================");
        System.out.println();
        System.out.println("Please choose one of the following options:");
        System.out.println("\t - \"Add\" to add to Your Cart");
        System.out.println("\t - \"My Cart\" to review your ordered products");
        System.out.println("\t - \"Check Out\" to review your order and pay!");
        System.out.println("\t - \"Exit\" if you have finished shopping");
    }

}
import java.util.ArrayList;
import java.util.List;

public class App {
    // Global array to store all products in the system
    public static List<Product> productCatalog = new ArrayList<>();

    // Method to initialize the product catalog with some sample products
    public static void initializeProductCatalog() {
        productCatalog.add(new Product("Laptop", 1000.0, 10, false, true, 2.5));
        productCatalog.add(new Product("Smartphone", 500.0, 20, false, true, 0.3));
        productCatalog.add(new Product("Book", 20.0, 50, false, false, null));
        productCatalog.add(new Product("Milk", 2.0, 30, true, false, null));
    }

    public void testCase1() {
        // Test case 1: Create a customer and add products to the cart
        Customer customer = new Customer("Alice", 2000.0);
        Cart cart = new Cart();
        cart.add("Laptop", 1);
        cart.add("Smartphone", 2);

        // Perform checkout
        try {
            Checkout checkout = new Checkout(cart, customer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        // Initialize the product catalog
        initializeProductCatalog();

        // Create an instance of the App class
        App app = new App();

        // Run test case 1
        app.testCase1();
    }
}

class Customer {
    private String name;
    private double balance;

    public Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

class Product {
    private String name;
    private double price;
    private int quantity;
    private boolean isExpired;
    private boolean isShippable;
    private Double weight; // Optional field, only used for shippable items

    public Product(String name, double price, int quantity, boolean isExpired, boolean isShippable, Double weight) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.isExpired = isExpired;
        this.isShippable = isShippable;
        this.weight = isShippable ? weight : null; // Ensure weight is only set for shippable items
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

    public boolean isShippable() {
        return isShippable;
    }

    public void setShippable(boolean isShippable) {
        this.isShippable = isShippable;
        if (!isShippable) {
            this.weight = null; // Clear weight if the item is not shippable
        }
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        if (isShippable) {
            this.weight = weight;
        } else {
            throw new IllegalStateException("Weight can only be set for shippable items.");
        }
    }
}

class Cart {
    private List<Product> products;
    private List<Product> shippableProducts;

    public Cart() {
        this.products = new ArrayList<>();
    }

    public void add(String productName, int quantity) {
        for (Product product : App.productCatalog) {
            // Check if the product exists and has enough quantity
            if (product.getName().equalsIgnoreCase(productName) && product.getQuantity() >= quantity) {
                // Add the product to the cart
                for (int i = 0; i < quantity; i++) {
                    products.add(product);
                }
                // Reduce the product quantity in the catalog
                product.setQuantity(product.getQuantity() - quantity);
                return;
            }

            // If the product is shippable, add it to the shippable products list
            if (product.isShippable() && product.getName().equalsIgnoreCase(productName)) {
                if (shippableProducts == null) {
                    shippableProducts = new ArrayList<>();
                }
                for (int i = 0; i < quantity; i++) {
                    shippableProducts.add(product);
                }
                product.setQuantity(product.getQuantity() - quantity);
                return;
            }
        }

        // If the product is not found or not enough quantity, throw an exception
        throw new IllegalArgumentException("Product not found or insufficient quantity: " + productName);
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Product> getShippableProducts() {
        return shippableProducts != null ? shippableProducts : new ArrayList<>();
    }
}

class Checkout {
    private Cart cart;
    private Customer customer;

    public Checkout(Cart cart, Customer customer) {
        this.cart = cart;
        this.customer = customer;
        process();
    }

    public void process() {

        double customerBalance = customer.getBalance();
        double totalCost = 0.0;

        // Check if the cart is empty
        if (cart == null || cart.getProducts().isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot proceed to checkout.");
        }

        // Check if the product is expired or out of stock
        for (Product product : cart.getProducts()) {
            if (product.isExpired()) {
                throw new IllegalArgumentException("Cannot purchase expired product: " + product.getName());
            }
            if (product.getQuantity() <= 0) {
                throw new IllegalArgumentException("Product out of stock: " + product.getName());
            }
            totalCost += product.getPrice();
        }

        // Check if the customer has enough balance
        if (customerBalance < totalCost) {
            throw new IllegalArgumentException("Insufficient balance for customer: " + customer.getName());
        }

        // Deduct the total cost from the customer's balance
        customer.setBalance(customerBalance - totalCost);

        System.out.println(" ** Shipment notice ** ");
        List<Product> shippableProducts = cart.getShippableProducts();
        double totalWeight = 0.0;
        double shippingFees = 0.0;
        // Loop through shippable products and print their details
        for (Product product : shippableProducts) {
            double weight = product.getWeight() != null ? product.getWeight() : 0.0;
            System.out.println(product.getQuantity() + "x " + product.getName() + "     " + weight + "g");
            totalWeight += weight * product.getQuantity();
            shippingFees += 10.0; // Assuming a constant shipping fee of 10.0 per shippable product
        }
        System.out.println("Total package weight: " + totalWeight + "g");

        System.out.println(" ** Checkout receipt ** ");
        for (Product product : cart.getProducts()) {
            System.out.println(product.getQuantity() + "x " + product.getName() + "     " + product.getPrice());
        }
        System.out.println("----------------------");
        System.out.println("Subtotal     " + totalCost);
        System.out.println("Shipping fees     " + shippingFees);
        System.out.println("Amount     " + (totalCost + shippingFees));
    }
}
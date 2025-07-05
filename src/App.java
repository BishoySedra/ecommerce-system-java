import java.util.ArrayList;
import java.util.List;

public class App {
    // Global list to store all products in the system
    public static List<Product> productCatalog = new ArrayList<>();

    // Initialize the product catalog with sample products
    public static void initializeProductCatalog() {
        productCatalog.add(new Product("Laptop", 1000.0, 10, false, true, 2.5));
        productCatalog.add(new Product("Smartphone", 500.0, 20, false, true, 0.3));
        productCatalog.add(new Product("Book", 20.0, 50, false, false, null));
        productCatalog.add(new Product("Milk", 2.0, 30, true, false, null));
    }

    // Test case 1: Successful checkout with multiple products
    public void testCase1() {
        Customer customer = new Customer("Alice", 2000.0);
        Cart cart = new Cart();
        cart.add("Laptop", 1);
        cart.add("Smartphone", 2);

        try {
            Checkout checkout = new Checkout(cart, customer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Test case 2: Attempt to checkout with insufficient balance
    public void testCase2() {
        Customer customer = new Customer("Bob", 100.0);
        Cart cart = new Cart();
        cart.add("Laptop", 1);

        try {
            Checkout checkout = new Checkout(cart, customer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Test case 3: Attempt to add expired product to the cart
    public void testCase3() {
        Customer customer = new Customer("Charlie", 500.0);
        Cart cart = new Cart();
        Product expiredMilk = new Product("Milk", 2.0, 10, true, false, null);
        productCatalog.add(expiredMilk);

        try {
            cart.add("Milk", 1);
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

        // Run test cases
        app.testCase1();
        app.testCase2();
        app.testCase3();
    }
}

interface ShippableItem {
    String getName();

    double getWeight();
}

class ShippingService {
    // Process shippable items and display shipment details
    public static void processShippableItems(List<ShippableItem> items) {
        System.out.println(" ** Shipment notice ** ");
        double totalWeight = 0.0;

        for (ShippableItem item : items) {
            System.out.println("Item: " + item.getName() + ", Weight: " + item.getWeight() + "kg");
            totalWeight += item.getWeight();
        }

        System.out.println("Total package weight: " + totalWeight + "kg");
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
    private Double weight;

    public Product(String name, double price, int quantity, boolean isExpired, boolean isShippable, Double weight) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.isExpired = isExpired;
        this.isShippable = isShippable;
        this.weight = isShippable ? weight : null;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public boolean isShippable() {
        return isShippable;
    }

    public Double getWeight() {
        return weight;
    }
}

class Cart {
    private List<Product> products = new ArrayList<>();
    private List<Product> shippableProducts = new ArrayList<>();

    // Add a product to the cart
    public void add(String productName, int quantity) {
        for (Product product : App.productCatalog) {
            if (product.getName().equalsIgnoreCase(productName)) {
                if (product.isExpired()) {
                    throw new IllegalArgumentException("Cannot add expired product: " + productName);
                }
                if (product.getQuantity() < quantity) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + productName);
                }

                products.add(product);
                if (product.isShippable()) {
                    shippableProducts.add(product);
                }
                product.setQuantity(product.getQuantity() - quantity);
                return;
            }
        }
        throw new IllegalArgumentException("Product not found: " + productName);
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Product> getShippableProducts() {
        return shippableProducts;
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

    // Process the checkout
    public void process() {
        if (cart.getProducts().isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot proceed to checkout.");
        }

        double subtotal = 0.0;
        double shippingFees = 0.0;

        for (Product product : cart.getProducts()) {
            subtotal += product.getPrice();
        }

        List<ShippableItem> shippableItems = new ArrayList<>();
        for (Product product : cart.getShippableProducts()) {
            shippableItems.add(new ShippableItem() {
                @Override
                public String getName() {
                    return product.getName();
                }

                @Override
                public double getWeight() {
                    return product.getWeight();
                }
            });
        }

        if (!shippableItems.isEmpty()) {
            shippingFees = shippableItems.size() * 10.0; // Example shipping fee calculation
            ShippingService.processShippableItems(shippableItems);
        }

        double total = subtotal + shippingFees;
        if (customer.getBalance() < total) {
            throw new IllegalArgumentException("Insufficient balance for customer: " + customer.getName());
        }

        customer.setBalance(customer.getBalance() - total);

        System.out.println(" ** Checkout receipt ** ");
        System.out.println("Subtotal " + subtotal);
        System.out.println("Shipping " + shippingFees);
        System.out.println("Amount " + total);
        System.out.println("Remaining balance: " + customer.getBalance());
    }
}
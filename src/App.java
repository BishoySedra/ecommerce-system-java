import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class App {
    public static void checkout(Customer customer, Cart cart) {
        // Check if the cart is empty
        if (cart.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot proceed to checkout.");
        }

        double subtotal = cart.getSubtotal();
        double shippingFees = cart.getShippingFees();
        double total = subtotal + shippingFees;

        // Check if the customer can afford the total amount
        if (!customer.canAfford(total)) {
            throw new RuntimeException("Customer cannot afford the total amount: " + total);
        }

        List<Shippable> shippableItems = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        // Process each item in the cart
        for (CartItem item : cart.getItems()) {
            item.product.reduceStock(item.quantity);
            if (item.requireShipping() && item.product instanceof Shippable) {
                shippableItems.add((Shippable) item.product);
                quantities.add(item.quantity);
            }
        }

        // If there are shippable items, call the shipping service
        if (!shippableItems.isEmpty()) {
            ShippingService.ship(shippableItems, quantities);
        }

        // Deduct the total amount from the customer's balance
        customer.deductBalance(total);

        // Print the receipt
        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.getItems()) {
            System.out.println(item.quantity + "x " + item.product.getName() + "\t$" + item.getTotalPrice());
        }
        System.out.println("----------------------");
        System.out.println("Subtotal: $" + subtotal);
        System.out.println("Shipping Fees: $" + shippingFees);
        System.out.println("Amount: $" + total);
        System.out.println("Balance left: $" + customer.getBalance());
    }

    public static void main(String[] args) throws Exception {
        // Run test cases
        testCase1(); // Normal checkout with valid products
        testCase2(); // Product is expired
        testCase3(); // Quantity exceeds stock
        testCase4(); // Empty cart
        testCase5(); // Insufficient balance
        testCase6(); // Digital-only products, no shipping required
        testCase7(); // Multiple shippable items with weight
        testCase8(); // Stock reduced after successful checkout
    }

    // Start Test Cases

    // Test Case 1: Normal checkout with valid products
    public static void testCase1() {
        System.out.println("=== Test Case 1: Normal checkout with valid products ===");
        try {
            Product cheese = new PerishableProduct("Cheese", 100, 5,
                    new Date(System.currentTimeMillis() + 86400000),
                    0.2);
            Product tv = new NonPerishableProduct("TV", 300, 2, true, 5);
            Product scratchCard = new NonPerishableProduct("Scratch Card", 50, 10, false, 0);
            Customer customer = new Customer("Ahmed", 1000);

            Cart cart = new Cart();
            cart.add(cheese, 2);
            cart.add(tv, 1);
            cart.add(scratchCard, 3);

            checkout(customer, cart);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    // Test Case 2: Product is expired
    public static void testCase2() {
        System.out.println("\n=== Test Case 2: Product is expired ===");
        try {
            Product cheese = new PerishableProduct("Cheese", 100, 5, new Date(System.currentTimeMillis() - 1000),
                    0.2);
            Customer customer = new Customer("Laila", 500);
            Cart cart = new Cart();
            cart.add(cheese, 1);
            checkout(customer, cart);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage()); // Expected: Product is expired
        }
    }

    // Test Case 3: Quantity exceeds stock
    public static void testCase3() {
        System.out.println("\n=== Test Case 3: Quantity exceeds stock ===");
        try {
            Product tv = new NonPerishableProduct("TV", 300, 2, true, 5);
            Customer customer = new Customer("Omar", 1000);
            Cart cart = new Cart();
            cart.add(tv, 3); // exceeds available quantity
            checkout(customer, cart);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage()); // Expected: Quantity exceeds stock
        }
    }

    // Test Case 4: Empty cart
    public static void testCase4() {
        System.out.println("\n=== Test Case 4: Empty cart ===");
        try {
            Customer customer = new Customer("Sara", 200);
            Cart cart = new Cart();
            checkout(customer, cart);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage()); // Expected: Cart is empty
        }
    }

    // Test Case 5: Insufficient balance
    public static void testCase5() {
        System.out.println("\n=== Test Case 5: Insufficient balance ===");
        try {
            Product laptop = new NonPerishableProduct("Laptop", 900, 2, true, 3);
            Customer customer = new Customer("Mona", 500); // not enough
            Cart cart = new Cart();
            cart.add(laptop, 1);
            checkout(customer, cart);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage()); // Expected: Insufficient balance
        }
    }

    // Test Case 6: Digital-only products, no shipping required
    public static void testCase6() {
        System.out.println("\n=== Test Case 6: Digital-only products, no shipping required ===");
        try {
            Product scratchCard = new NonPerishableProduct("Scratch Card", 50, 20, false, 0);
            Customer customer = new Customer("Nour", 500);
            Cart cart = new Cart();
            cart.add(scratchCard, 5);

            checkout(customer, cart); // No shipping fee should apply
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    // Test Case 7: Multiple shippable items with weight
    public static void testCase7() {
        System.out.println("\n=== Test Case 7: Multiple shippable items with weight ===");
        try {
            Product biscuits = new PerishableProduct("Biscuits", 150, 5,
                    new Date(System.currentTimeMillis() + 86400000), 0.7);
            Product tv = new NonPerishableProduct("TV", 300, 3, true, 5);
            Customer customer = new Customer("Karim", 2000);

            Cart cart = new Cart();
            cart.add(biscuits, 2); // 1.4 kg
            cart.add(tv, 1); // +5 kg = 6.4kg total

            checkout(customer, cart);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    // Test Case 8: Stock reduced after successful checkout
    public static void testCase8() {
        System.out.println("\n=== Test Case 8: Stock reduced after successful checkout ===");
        try {
            Product cheese = new PerishableProduct("Cheese", 100, 3,
                    new Date(System.currentTimeMillis() + 86400000),
                    0.2);
            Customer customer1 = new Customer("Ali", 1000);
            Customer customer2 = new Customer("Salma", 1000);

            Cart cart1 = new Cart();
            cart1.add(cheese, 2);
            checkout(customer1, cart1);

            Cart cart2 = new Cart();
            cart2.add(cheese, 2); // only 1 left, should fail
            checkout(customer2, cart2);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage()); // Expected: Quantity exceeds stock
        }
    }

    // End Test Cases
}

// Start Product Classes

interface Shippable {
    String getName();

    double getWeight();
}

abstract class Product {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
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

    public void reduceStock(int quantity) {
        if (this.quantity < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + name);
        }
        this.quantity -= quantity;
    }

    public abstract boolean isExpired();

    public abstract boolean isShippable();
}

class PerishableProduct extends Product implements Shippable {
    private Date expiryDate;
    private double weight;

    public PerishableProduct(String name, double price, int quantity, Date expiryDate, double weight) {
        super(name, price, quantity);
        this.expiryDate = expiryDate;
        this.weight = weight;
    }

    @Override
    public boolean isExpired() {
        return new Date().after(expiryDate);
    }

    @Override
    public boolean isShippable() {
        return true;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public double getWeight() {
        return weight;
    }

}

class NonPerishableProduct extends Product implements Shippable {
    private boolean shippingRequired;
    private double weight;

    public NonPerishableProduct(String name, double price, int quantity, boolean shippingRequired, double weight) {
        super(name, price, quantity);
        this.shippingRequired = shippingRequired;
        this.weight = weight;
    }

    @Override
    public boolean isExpired() {
        return false; // Non-perishable products are never expired
    }

    @Override
    public boolean isShippable() {
        return shippingRequired; // Non-perishable products can always be shipped
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public double getWeight() {
        return weight;
    }
}

// End Product Classes

// Start Customer Class

class Customer {
    private String name;
    private double balance;

    public Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public boolean canAfford(double amount) {
        return balance >= amount;
    }

    public void deductBalance(double amount) {
        if (!canAfford(amount)) {
            throw new RuntimeException("Insufficient balance for customer: " + name);
        }
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}

// End Customer Class

// Start Cart Classes

class CartItem {
    Product product;
    int quantity;

    public CartItem(Product product, int quantity) {
        // Check if the product is expired before adding to the cart
        if (product.isExpired()) {
            throw new RuntimeException("Cannot add expired product to cart: " + product.getName());
        }

        // Check if the product quantity is available
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        this.product = product;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    public boolean requireShipping() {
        return product.isShippable();
    }
}

class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void add(Product product, int quantity) {
        items.add(new CartItem(product, quantity));
    }

    public List<CartItem> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getSubtotal() {
        double subtotal = 0.0;
        for (CartItem item : items) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    public double getShippingFees() {
        double shippingFees = 0.0;
        for (CartItem item : items) {
            if (item.requireShipping()) {
                shippingFees += 10.0 * item.quantity; // Flat rate shipping fee of $10 per item
            }
        }
        return shippingFees;
    }
}

// End Cart Classes

// Start Shipping Service

class ShippingService {
    public static void ship(List<Shippable> items, List<Integer> quantities) {
        System.out.println("** Shipment notice **");
        double totalWeight = 0.0;
        int itemsCount = items.size();
        for (int i = 0; i < itemsCount; i++) {
            Shippable item = items.get(i);
            int quantity = quantities.get(i);
            double weight = item.getWeight() * quantity;
            totalWeight += weight;
            System.out.println(quantity + "x " + item.getName() + "\t" + (int) (weight * 1000) + "g");
        }
        System.out.println("Total package weight " + totalWeight + "kg");
    }
}

// End Shipping Service
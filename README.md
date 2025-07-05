# 🛒 E-Commerce System

A console-based Java implementation of an e-commerce system. This project demonstrates strong object-oriented design, and error handling.

---

## 🚀 Features

- 🧾 **Product Management**  
  - Perishable and Non-Perishable product types  
  - Shipping-required and digital products  
  - Expiry tracking, weight, and stock management

- 🛒 **Shopping Cart Functionality**  
  - Add products with quantity validation  
  - Supports multiple product types  
  - Handles subtotal and shipping calculations

- 💳 **Checkout Process**  
  - Console-based receipt  
  - Balance deduction and validation  
  - Shipping integration with total package weight

- 🛑 **Error Handling**  
  - Expired products  
  - Out-of-stock errors  
  - Insufficient balance  
  - Empty cart

- 📦 **Shipping Service**  
  - Collects names and weights  
  - Outputs readable shipping slip

---

## ✅ Test Coverage

All functionalities and edge cases are fully tested through console output:
- ✔️ Normal checkout flow
- ❌ Expired product
- ❌ Insufficient balance
- ❌ Out of stock
- ❌ Empty cart
- ✔️ Digital products with no shipping
- ✔️ Weight aggregation across shippable items
- ✔️ Stock update across repeated checkouts

> See `src/App.java` for organized test cases (`testCase1()` to `testCase8()`).

---

## 📁 Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

### Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

---

## 📦 Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies.  
More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

---

## 🛠️ How to Run

1. Open project in **VS Code**
2. Make sure Java extension is installed
3. Run `Main.java` from the `src` folder
4. Observe output in the terminal window

---

## 📌 Notes & Assumptions

- Shipping fee is **fixed at 10** for each shippable item.
- Product expiry is based on system time (`Date.now()`).
- Stock updates only after successful checkout.

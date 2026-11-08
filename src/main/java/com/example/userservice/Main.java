package com.example.userservice;

import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.UserDaoImpl;
import com.example.userservice.entity.User;
import com.example.userservice.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            while (true) {
                printMenu();
                int choice = readInt("Choose option: ");
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> findUserById();
                    case 3 -> findAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> {
                        System.out.println("Goodbye!");
                        HibernateUtil.shutdown();
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            }
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void printMenu() {
        System.out.println("\n=== User Service Menu ===");
        System.out.println("1. Create User");
        System.out.println("2. Find User by ID");
        System.out.println("3. Find All Users");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");
        System.out.println("0. Exit");
    }

    private static void createUser() {
        System.out.println("--- Create User ---");
        String name = readString("Enter name: ");
        String email = readString("Enter email: ");
        int age = readInt("Enter age: ");
        try {
            User user = new User(name, email, age);
            User saved = userDao.save(user);
            System.out.println("User created: " + saved);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void findUserById() {
        long id = readInt("Enter user ID: ");
        try {
            Optional<User> user = userDao.findById(id);
            user.ifPresentOrElse(
                    u -> System.out.println("Found: " + u),
                    () -> System.out.println("User not found with ID: " + id)
            );
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void findAllUsers() {
        try {
            List<User> users = userDao.findAll();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                users.forEach(System.out::println);
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateUser() {
        long id = readInt("Enter user ID to update: ");
        try {
            Optional<User> existing = userDao.findById(id);
            if (existing.isEmpty()) {
                System.out.println("User not found.");
                return;
            }
            User user = existing.get();
            System.out.print("New name (" + user.getName() + "): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) user.setName(name);
            System.out.print("New email (" + user.getEmail() + "): ");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) user.setEmail(email);
            System.out.print("New age (" + user.getAge() + "): ");
            String ageStr = scanner.nextLine().trim();
            if (!ageStr.isEmpty()) user.setAge(Integer.parseInt(ageStr));

            User updated = userDao.update(user);
            System.out.println("User updated: " + updated);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        long id = readInt("Enter user ID to delete: ");
        try {
            userDao.deleteById(id);
            System.out.println("Delete operation completed.");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
package com.example;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class ATM {
    private int id;
    private double balance;
    private ReentrantLock lock;

    public ATM(int id, double initialBalance) {
        this.id = id;
        this.balance = initialBalance;
        this.lock = new ReentrantLock(true);} 

    public int getId() {
        return id;
    }

    public void deposit(double amount, String customerName) {
        lock.lock();
        try {
            balance += amount;
            System.out.println(customerName + " deposited " + amount + " in acc " + id + ". Balance: " + balance);
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(double amount, String customerName) {
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                System.out.println(customerName+" withdrew " +amount+" in acc "+id+". Balance: " +balance);
            } else {
                System.out.println("Insufficient funds, "+customerName+", " +amount+",acc "+id);
            }
        } finally {
            lock.unlock();
        }
    }

    public void checkBalance(String customerName) {
        lock.lock();
        try {
            System.out.println(customerName + " checked balance for acc" + id +":"+  balance);
        } finally {
            lock.unlock();
        }
    }
}

class Customer extends Thread {
    private String name;
    private List<ATM> atms;
    private Random random = new Random();

    public Customer(String name, List<ATM> atms) {
        this.name = name;
        this.atms = atms;
    }
    public void run() {
        for (int i = 0; i < 3; i++) { 
            ATM selectedATM = atms.get(random.nextInt(atms.size())); 
            double amount = 50 + random.nextInt(100);
            int action = random.nextInt(3);

            switch (action) {
                case 0:
                    selectedATM.deposit(amount, name);
                    break;
                case 1:
                    selectedATM.withdraw(amount, name);
                    break;
                case 2:
                    selectedATM.checkBalance(name);
                    break;
            }

            try {
                Thread.sleep(random.nextInt(500));
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}

public class ATMSystem {
    public static void main(String[] args) {
        int numATM = 3; 
        int numCust = 5;

        List<ATM> atms = new ArrayList<>();
        for (int i = 1; i <= numATM; i++) {
            atms.add(new ATM(i, 1000)); 
        }

        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= numCust; i++) {
            customers.add(new Customer("Customer" + i, atms));
        }

        for (Customer customer : customers) {
            customer.start(); 
        }

        for (Customer customer : customers) {
            try {
                customer.join();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        System.out.println("All transactions completed.");
    }
}
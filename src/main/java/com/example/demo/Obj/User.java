package com.example.demo.Obj;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class User {
    @JsonProperty("id")
    private int id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("email")
    private String email;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("role")
    private int role;
    @JsonProperty("balances")
    private List<Balance> balances;
    @JsonProperty("totalBalance")
    private String totalBalance;
    @JsonProperty("timeRegister")
    private String timeRegister;

    // Constructors, getters, and setters
    public User(int id, String username, String email, String displayName, int role, List<Balance> balances, String totalBalance, String timeRegister) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.balances = balances;
        this.totalBalance = totalBalance;
        this.timeRegister = timeRegister;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getTimeRegister() {
        return timeRegister;
    }

    public void setTimeRegister(String timeRegister) {
        this.timeRegister = timeRegister;
    }

    // Nested class for Balance
    public static class Balance {
        private int idServer;
        private int balance;
        private long withdrawalLimit;

        // Constructors, getters, and setters
        public Balance(int idServer, int balance, long withdrawalLimit) {
            this.idServer = idServer;
            this.balance = balance;
            this.withdrawalLimit = withdrawalLimit;
        }

        public int getIdServer() {
            return idServer;
        }

        public void setIdServer(int idServer) {
            this.idServer = idServer;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public long getWithdrawalLimit() {
            return withdrawalLimit;
        }

        public void setWithdrawalLimit(long withdrawalLimit) {
            this.withdrawalLimit = withdrawalLimit;
        }
    }
}

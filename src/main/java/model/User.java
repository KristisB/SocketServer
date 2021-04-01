package model;

public class User {
    private int userId;
    private String userName;
    private String symbol;

    public User(int userId, String userName, String symbol) {
        this.userId = userId;
        this.userName = userName;
        this.symbol = symbol;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    //comparing 2 users.
    // Users are equal if their IDs are the same AND names are the same
    @Override
    public boolean equals(Object obj) {
        System.out.println("two User objects compared: "+ this.userId+" and " +obj);

        if (this == obj) return true;
        if (obj == null) return false;
        User user = (User) obj;
        if ((user.getUserId() == userId) && (user.getUserName().equals(userName))) return true;
        return false;
    }

}

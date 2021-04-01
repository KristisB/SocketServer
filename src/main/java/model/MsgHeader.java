package model;

public class MsgHeader {
    private User user;
    private int address;

    public MsgHeader(User user) {
        this.user=user;
    }
    public MsgHeader(User user, int address) {
        this.user=user;
        this.address=address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}

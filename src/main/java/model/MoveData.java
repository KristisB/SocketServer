package model;

import com.google.gson.annotations.SerializedName;

public class MoveData {
    @SerializedName("user")
    public User user;

    @SerializedName("col")
    public int col;

    @SerializedName("row")
    public int row;

//    @SerializedName("symbol")
//    public String symbol;

}

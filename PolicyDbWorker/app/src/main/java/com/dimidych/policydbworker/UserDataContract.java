package com.dimidych.policydbworker;

public class UserDataContract {

    public long UserId;
    public String UserLastName;
    public String UserFirstName;
    public String UserMiddleName;

    public UserDataContract(){}

    public UserDataContract(long userId,String userLastName,String userFirstName, String userMiddleName){
        UserId=userId;
        UserLastName=userLastName;
        UserFirstName=userFirstName;
        UserMiddleName=userMiddleName;
    }
}

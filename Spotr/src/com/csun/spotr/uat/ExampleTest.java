package com.csun.spotr.uat;

import com.csun.spotr.core.User;

import fit.ColumnFixture;

public class ExampleTest extends ColumnFixture {
    public int x;
    public int y;
    public User user;
    
    public void setUp() {
	user = new User.Builder(1, "chan", "123").build();
    }
    
    public String getPassword() {
	setUp();
	return  user.getPassword();
    }
    
    
}

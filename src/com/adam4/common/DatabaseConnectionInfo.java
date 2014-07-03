package com.adam4.common;

public class DatabaseConnectionInfo
{
	public String url, user, password;
	public int connections;
	public DatabaseConnectionInfo(String url, String user, String password)
	{
		this.url = url;
		this.user = user;
		this.password = password;
	}
}
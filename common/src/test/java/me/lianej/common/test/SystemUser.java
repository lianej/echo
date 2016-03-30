package me.lianej.common.test;

import java.util.Date;

public class SystemUser {
	private String username;
	private String password;
	private Date loginTime;
	private String loginIp;
	private Integer role;
	
	public SystemUser(String username, String password, Date loginTime, String loginIp, Integer role) {
		super();
		this.username = username;
		this.password = password;
		this.loginTime = loginTime;
		this.loginIp = loginIp;
		this.role = role;
	}
	public SystemUser(){}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "SystemUser [username=" + username + ", password=" + password + ", loginTime=" + loginTime + ", loginIp="
				+ loginIp + ", role=" + role + "]";
	}
	
}

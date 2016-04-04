package me.lianej.common.test;

import java.util.Date;

public class SystemUser {
	private String username;
	private String password;
	private Date loginTime;
	private String loginIp;
	private SystemRole role;
	private Long id;
	
	public SystemUser(String username, String password, Date loginTime, String loginIp) {
		super();
		this.username = username;
		this.password = password;
		this.loginTime = loginTime;
		this.loginIp = loginIp;
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
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public SystemRole getRole() {
		return role;
	}
	public void setRole(SystemRole role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "SystemUser [username=" + username + ", password=" + password + ", loginTime=" + loginTime + ", loginIp="
				+ loginIp + ", role=" + role + ", id=" + id + "]";
	}
	
}

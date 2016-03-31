package me.lianej.common.test;

import java.util.Date;

public class SAPUser {
	private Long id;
	private String name;
	private String pwd;
	private Integer role;
	private Date createDate;
	private Date lastLoginDate;
	
	public SAPUser(String name, String pwd, Integer role, Date createDate, Date lastLoginDate) {
		super();
		this.name = name;
		this.pwd = pwd;
		this.role = role;
		this.createDate = createDate;
		this.lastLoginDate = lastLoginDate;
	}
	
	public SAPUser() {
		super();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "SAPUser [name=" + name + ", pwd=" + pwd + ", role=" + role + ", createDate=" + createDate
				+ ", lastLoginDate=" + lastLoginDate + "]";
	}
	
}

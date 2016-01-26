package me.lianej.echo.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class UserGroup extends BaseBean{

	private static final long serialVersionUID = -7191120911029168730L;

	@ManyToMany
	private List<User> users;
	
	private String groupName;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
}

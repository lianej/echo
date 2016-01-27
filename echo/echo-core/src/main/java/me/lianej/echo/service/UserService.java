package me.lianej.echo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.lianej.echo.dao.IUserDAO;
import me.lianej.echo.domain.User;

//@Service
//@Transactional
public class UserService extends BaseService{
	
//	@Autowired
	private IUserDAO userDAO;
//	@Autowired
//	private IUserGroupDAO userGroupDAO;
	
	
	public void save(User user){
		userDAO.save(user);
	}

	public List<User> list(){
		return userDAO.list();
	}
	
	public void delete(Long id){
		userDAO.delete(id);
	}
	
	public void update(User user){
		userDAO.update(user);
	}
	
}

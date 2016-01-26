package me.lianej.echo.dao.impl;

import org.springframework.stereotype.Repository;

import me.lianej.echo.dao.IUserDAO;
import me.lianej.echo.domain.User;

@Repository
public class UserDAOImpl extends AbstractBaseDAO<User> implements IUserDAO {

}

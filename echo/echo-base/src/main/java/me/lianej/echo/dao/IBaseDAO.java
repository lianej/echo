package me.lianej.echo.dao;

import java.util.List;

import me.lianej.echo.page.Paging;

public interface IBaseDAO<T> {

	void save(T entity);
	void update(T entity);
	T get(Long id);
	void delete(Long id);
	List<T> list();
	List<T> list(Paging paging);
	
}

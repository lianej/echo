package me.lianej.echo.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import me.lianej.echo.dao.IBaseDAO;
import me.lianej.echo.page.Paging;
import me.lianej.echo.page.Sortord;

public abstract class AbstractBaseDAO<T> implements IBaseDAO<T>{

	protected Log log = LogFactory.getLog(getClass());
	
	private Class<T> beanClz;
	
	/**
	 * 只读库
	 */
	@Autowired
	private SessionFactory readSessionFactory;
	/**
	 * 读写库
	 */
	@Autowired
	private SessionFactory readAndWriteSessionFactory;
	
	@SuppressWarnings("unchecked")
	protected AbstractBaseDAO(){
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		if(genericSuperclass instanceof ParameterizedType){
			beanClz = (Class<T>) ((ParameterizedType)genericSuperclass).getActualTypeArguments()[0];
		}else{
			throw new IllegalStateException("DAO必须要有泛型");
		}
	}
	
	
	@Override
	public void save(T entity) {
		readAndWriteSessionFactory.getCurrentSession().save(entity);
	}

	@Override
	public void update(T entity) {
		readAndWriteSessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public T get(Long id) {
		return readSessionFactory.getCurrentSession().get(beanClz, id);
	}

	@Override
	public void delete(Long id) {
		T entity = get(id);
		readAndWriteSessionFactory.getCurrentSession().delete(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list() {
		return readSessionFactory.getCurrentSession().createCriteria(beanClz).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(Paging paging) {
		Criteria criteria = readSessionFactory.getCurrentSession().createCriteria(beanClz);
		switch (paging.getPageMode()) {
		case Paging.PAGE_MODE_ID:
			criteria.addOrder(Order.desc("id"));
			criteria.add(Restrictions.lt("id", paging.getPagingId()));
			criteria.setFirstResult(0);
			criteria.setMaxResults(paging.getPageSize());
			break;
		case Paging.PAGE_MODE_PAGINATION:
			Sortord sortord = paging.getSortord();
			Order order = new MyOrder(sortord.getOrderby(),sortord.isAscending());
			criteria.addOrder(order);
			Integer pageSize = paging.getPageSize();
			Integer pageNumber = paging.getPagination();
			int firstResult = (pageSize*pageNumber-1)/pageSize;
			criteria.setFirstResult(firstResult);
			criteria.setMaxResults(pageSize);
			break;
		default:
			throw new IllegalArgumentException("page mode = "+paging.getPageMode());
		}
		return criteria.list();
	}

	private static class MyOrder extends Order{
		private static final long serialVersionUID = 1550219396514255807L;
		public MyOrder(String propertyName, boolean ascending) {
			super(propertyName, ascending);
		}
	}
	
}

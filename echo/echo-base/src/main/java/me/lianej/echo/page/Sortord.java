package me.lianej.echo.page;

public class Sortord {
	private String orderby;
	private boolean ascending;
	public Sortord(String orderby, boolean ascending) {
		super();
		this.orderby = orderby;
		this.ascending = ascending;
	}
	public String getOrderby() {
		return orderby;
	}
	public boolean isAscending() {
		return ascending;
	}
	
}

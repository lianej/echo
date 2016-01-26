package me.lianej.echo.page;

import org.springframework.util.Assert;

public class Paging {

	public static final int PAGE_MODE_ID = 0;//ID分页
	public static final int PAGE_MODE_PAGINATION = 1;//
	
	private Integer pageSize;
	private Integer pagination;
	private Long pagingId;
	private int pageMode;
	private Sortord sortord;
	public Paging(Integer pageSize, Long pagingId) {
		super();
		this.pageSize = pageSize;
		this.pagingId = pagingId;
		pageMode = PAGE_MODE_ID;
	}
	public Paging(Integer pageSize, Integer pagination) {
		super();
		this.pageSize = pageSize;
		this.pagination = pagination;
		pageMode = PAGE_MODE_PAGINATION;
		sortord = new Sortord("id",false);
	}
	public Paging(Integer pageSize, Integer pagination, Sortord sortord) {
		this(pageSize,pagination);
		this.sortord = sortord;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public Integer getPagination() {
		return pagination;
	}
	public Long getPagingId() {
		return pagingId;
	}
	public int getPageMode() {
		return pageMode;
	}
	public Sortord getSortord() {
		Assert.notNull(sortord,"排序方式不能为空,或者错误的使用了这个属性");
		return sortord;
	}
	
}

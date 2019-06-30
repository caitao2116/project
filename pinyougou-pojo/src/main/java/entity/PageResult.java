package entity;

import java.io.Serializable;
import java.util.List;

/**
 * 查询返回结果实体类
 * @author cai
 *
 */
public class PageResult implements Serializable {
	
	private Long total;//总记录数
	private List rows;
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	public PageResult(Long total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	
	
	

}

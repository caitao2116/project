package entity;
/**
 *增删改返回结果实体类
 * @author cai
 *
 */

import java.io.Serializable;

public class Result implements Serializable{

	private boolean success;
	private String message;
	
	
	
	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}

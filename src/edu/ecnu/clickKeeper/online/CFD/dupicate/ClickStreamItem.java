package edu.ecnu.clickKeeper.online.CFD.dupicate;

import java.util.List;

/** 
 * @description: 点击流中的每条记录
 * @author: Song Leyi  2012-12-30
 * @version: 1.0
 * @modify: 
 * @Copyright: 华东师范大学软件学院版权所有
 */
public class ClickStreamItem {
	//User标识
	private String user_id;
	//属性列表
	private List<String> attrs;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public List<String> getAttrs() {
		return attrs;
	}
	public void setAttrs(List<String> attrs) {
		this.attrs = attrs;
	}
}

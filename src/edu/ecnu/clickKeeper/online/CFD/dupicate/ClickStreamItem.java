package edu.ecnu.clickKeeper.online.CFD.dupicate;

import java.util.List;

/** 
 * @description: ������е�ÿ����¼
 * @author: Song Leyi  2012-12-30
 * @version: 1.0
 * @modify: 
 * @Copyright: ����ʦ����ѧ���ѧԺ��Ȩ����
 */
public class ClickStreamItem {
	//User��ʶ
	private String user_id;
	//�����б�
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

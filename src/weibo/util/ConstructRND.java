package weibo.util;

import java.util.Date;


/**
 * @author coderwang
 * 2014/1/9
 * ���������
 * */
public class ConstructRND {
	
	public ConstructRND(){}
	
	
	/**
	 * @return ����ʱ��� ��Ϊ��ҳץȡת������ʱ��url�����һ��������һ����
	 * ���� ��&_rnd=1389252369559��
	 * */
	public static long getTimeStamp(){
		return new Date().getTime();
	}
	

}

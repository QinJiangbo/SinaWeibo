package weibo.util;

/**
 * 
 * 将long类型的微博mid转换成微博url中的id
 * eg:
 * http://weibo.com/2480531040/z8ElgBLeQ
 * 3520617028999724->z8ElgBLeQ
 *
 * @author coderwang
 *
 */
public class GetWeiboUrlId {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String id ="3520617028999724"; 
		System.out.println(Id2Mid(id));
	}
	
	

 
    public static String Id2Mid(String id)
    {
        String mid = "", strTemp;
        int startIdex, len;
        int length=id.length();//id长度
        //System.out.println("id length : "+length);
        for (int i = length - 7; i > -7; i = i - 7) //从最后往前以7字节为一组读取mid
        {
            startIdex = i < 0 ? 0 : i;
            len = i < 0 ? length % 7 : 7;
            //System.out.println("start index: "+startIdex+" len : "+len);
            strTemp = id.substring(startIdex, startIdex+len);
            //System.out.println(strTemp);
            mid = IntToStr62(Integer.parseInt(strTemp)) + mid;
        }
        return mid;
    }


    //10进制转成62进制
    public static String IntToStr62(int int10)
    {
        String s62 = "";
        int r = 0;
        while (int10 != 0)
        {
            r = int10 % 62;
            s62 = Get62key(r) + s62;
            int10 = int10 / 62;
        }
        return s62;
    }
    // 62进制字典
    private static String str62keys = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVTXYZ";
    

    //获取62进制整数对应的key
    private static String Get62key(int int10)
    {
        if (int10 < 0 || int10 > 61)
            return "";
        return str62keys.substring(int10, int10+1);
    }

}

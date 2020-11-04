package cn.rhyme.utils.http;



import cn.rhyme.utils.MyCollectionUtils;
import cn.rhyme.utils.MyEncryptionUtils;
import cn.rhyme.utils.MyStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * HTTP工具类
 * @author LiuZhi
 * @Date 2020-08-31 15:00
 * @Version V1.0
 */
public class MyHttpUtils {

    /**
     * 获取短连接
     * @param longUrl 需要缩短的长链接
     * @return 返回缩短后的链接
     */
    public static String longUrlToShorUrl(String longUrl)
    {
        // 可以自定义生成 MD5 加密字符传前的混合 KEY
        String key = "LiuZhi" ;
        // 要使用生成 URL 的字符
        String[] chars = new String[] { "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h" ,
                "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
                "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" ,
                "6" , "7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" ,
                "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
                "U" , "V" , "W" , "X" , "Y" , "Z"
        };
        // 对传入网址进行 MD5 加密
        String sMD5EncryptResult = (MyEncryptionUtils.MD5Encode32(key + longUrl));
        String hex = sMD5EncryptResult;
        String[] resUrl = new String[4];
        //得到 4组短链接字符串
        for ( int i = 0; i < 4; i++) {
            // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);
            // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong (sTempSubString, 16);
            String outChars = "" ;
            //循环获得每组6位的字符串
            for ( int j = 0; j < 6; j++) {
                // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                //(具体需要看chars数组的长度   以防下标溢出，注意起点为0)
                long index = 0x0000003D & lHexLong;
                // 把取得的字符相加
                outChars += chars[( int ) index];
                // 每次循环按位右移 5 位
                lHexLong = lHexLong >> 5;
            }
            // 把字符串存入对应索引的输出数组
            resUrl[i] = outChars;
        }
        return resUrl[1];
    }

    /**
     * 获取url 的 HOST地址
     * @param url 需要获取的连接
     * @return 返回url的host
     */
    public static String getUrlHost(String url) {
        if (url == null) {
            throw new RuntimeException("需要获取的URL地址不能为NULL");
        }
        url = url.toLowerCase();
        String host;
        if (url.contains("https://")) {
            host = "https://" +  MyStringUtils.getTextMiddle(url, "https://", "/");
        } else if (url.contains("http://")) {
            host = "http://" + MyStringUtils.getTextMiddle(url, "http://", "/");
        } else {
            host = MyStringUtils.getTextLeft(url, "/");
        }
        return host;
    }

    /**
     * 获取url的参数信息
     * @param url 需要获取的连接
     * @return 返回url的host
     */
    public static Map<String,String> getUrlParam(String url) {
        if (url == null) {
            throw new RuntimeException("需要获取的URL地址不能为NULL");
        }
        url = url.toLowerCase();
        String host;
        if (url.contains("?")) {
            host = MyStringUtils.getTextRight(url, "?");
            List<String> param = MyStringUtils.splitToList(host,"&");
            if(MyCollectionUtils.isNotEmpty(param)){
                return param.stream().collect(Collectors.toMap(s->MyStringUtils.splitToList(s,"=").get(0),s->{
                    String str;
                    try {
                        return MyStringUtils.splitToList(s,"=").get(1);
                    }catch (Exception e){
                        return "";
                    }
                }));
            }
        }
        return null;

    }
}
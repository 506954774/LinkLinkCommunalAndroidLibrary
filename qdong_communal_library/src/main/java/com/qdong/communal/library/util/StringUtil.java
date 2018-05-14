/**
 *
 */
package com.qdong.communal.library.util;

/**
 * 字符串操作工具类
 *
 * @author yyh
 */
public class StringUtil {

    public static final String EMPTY = "";

    public static boolean isEmpty(String text) {
        if (null == text || "".equals(text) || " ".equals(text) || "null".equals(text))
            return true;
        return false;
    }

    /**
     * @param : [str]
     * @return type: java.lang.String
     *
     * @method name: substring
     * @des: 截取价格类型字符串后面的.00, 例如11.00截取后返回11
     * @date 创建时间：2015/11/23 10:36
     */
    public static String substring(String str) {
        String s = "";
        if (!android.text.TextUtils.isEmpty(str)) {
            if (str.indexOf(".") != -1) {
                s = str.substring(0, str.indexOf("."));
            }
        }
        return s;
    }
}

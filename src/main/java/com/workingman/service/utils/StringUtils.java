package com.workingman.service.utils;

public class StringUtils {
    public static String getRandomString(int stringLength) {
        String string = "abcdefghijklmnopqrstuvwxyz";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < stringLength; i++) {
            int index = (int) Math.floor(Math.random() * string.length());//向下取整0-25
            sb.append(string.charAt(index));
        }
        return sb.toString();
    }

    public static String addZero(String code){
        char[] codeStr=code.toCharArray();
        if(codeStr.length>6){
            return code;
        }
        char[] str=new char[6];
        int j=5;
        for(int i=codeStr.length-1;i>=0;i--,j--){
            str[j]=codeStr[i];
        }
        for(;j>=0;j--){
            str[j]='0';
        }
        code= String.valueOf(str);
        return code;
    }
}

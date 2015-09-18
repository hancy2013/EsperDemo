package com.imperio;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxmimperio on 2015/9/18.
 */
public class DataFactory {
    private DataFactory() {}

    public static DataFactory dataFactory = new DataFactory();

    public static List<String> getInitJsonData() {
        List<String> list = new ArrayList<String>();
        String data1 = JsonUtil.obj2JsonData(new PhoneBean("wxm","男",20,4603));
        String data2 = JsonUtil.obj2JsonData(new PhoneBean("imperio","男",25,123456));
        String data3 = JsonUtil.obj2JsonData(new PhoneBean("wxmimperio","女",21,8888));

        list.add(data1);
        list.add(data2);
        list.add(data3);

        return list;
    }
}

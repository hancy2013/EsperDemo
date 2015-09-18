package com.imperio;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * Created by wxmimperio on 2015/9/18.
 */
public class JsonUtil {
    public static String obj2JsonData(PhoneBean phoneBean) {
        String jsonData = null;
        try {
            //使用XContentBuilder创建json数据
            XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
            jsonBuild.startObject()
                    .field("name",phoneBean.getName())
                    .field("gender",phoneBean.getGender())
                    .field("age",phoneBean.getAge())
                    .field("phone",phoneBean.getPhone())
                    .endObject();
            jsonData = jsonBuild.string();
            System.out.println(jsonData);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }
}

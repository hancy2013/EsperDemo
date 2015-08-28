package epl.learning.seventhEPL;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by wxmimperio on 2015/8/28.
 * <p/>
 * 1. 返回Map类型的外部数据
 * 2. 如果方法的返回类型是Map或者Map数组，则Map的key-value定义固定为<String, Object>
 */
public class InvocationMethodMap {
    public static Map<String, Object> getMapObject() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", String.class);
        map.put("size", int.class);

        return map;
    }

    public static Map<String, Class> getMapObjectMetadata() {

        Map<String, Class> map = new HashMap<String, Class>();
        map.put("name", String.class);
        map.put("size", int.class);

        return map;
    }
}

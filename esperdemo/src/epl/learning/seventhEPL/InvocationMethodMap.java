package epl.learning.seventhEPL;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by wxmimperio on 2015/8/28.
 *
 * 1. 返回Map类型的外部数据
 * 2. 如果方法的返回类型是Map或者Map数组，则Map的key-value定义固定为<String, Object>
 * 3. 如果返回的数据是Map或者Map数组，除了定义返回数据的方法外，还要定义返回元数据的方法（这个元数据针对返回的数据）
 * 4. 方法是公共静态方法，且必须是无参数方法。方法返回类型为Map<String, Class>
 *   4.1 String表示返回的数据的名称
 *   4.2 Class表示返回的数据的类型
 * 5. 返回元数据的方法名称=返回数据的方法名称+Metadata
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

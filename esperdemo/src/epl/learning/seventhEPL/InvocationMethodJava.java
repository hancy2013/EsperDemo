package epl.learning.seventhEPL;

/**
 * Created by wxmimperio on 2015/8/28.
 * <p/>
 * 1. 如果返回一条数据或无返回数据，则方法的返回类型可以是Java类或者Map类型数据
 * 2. 如果返回多条数据（包括一条），则方法返回类型必须是Java类的数组或者Map数组
 * 3. 如果方法的返回类型是Java类或者Java类数组，则Java的类定义必须包含针对属性的get方法
 */

class JavaObject {
    private String name;
    private int size;

    public JavaObject(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "JavaObject{ name: " + name + ", size: " + size + " }";
    }
}

public class InvocationMethodJava {
    public static JavaObject[] getJavaObject(int times) {
        JavaObject[] javaObjects = new JavaObject[2];

        JavaObject javaObject1 = new JavaObject("javaObject1", 1 * times);

        JavaObject javaObject2 = new JavaObject("javaObject2", 2 * times);

        javaObjects[0] = javaObject1;
        javaObjects[1] = javaObject2;

        return javaObjects;
    }
}

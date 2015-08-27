package second.esper;

/**
 * Created by wxmimperio on 2015/8/24.
 */
public class MyEvent {

    private int id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //构造函数初始化
    public MyEvent(int id, String name) {
        this.name = name;
        this.id = id;
    }
}

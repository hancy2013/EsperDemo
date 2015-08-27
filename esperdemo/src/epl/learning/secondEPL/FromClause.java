package epl.learning.secondEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/26.
 *
 * 1. 要过滤的属性只能是数字和字符串。
 * 2. 过滤表达式中不能使用聚合函数。
 * 3. "prev"和"prior"函数不能用于过滤表达式
 */
class Student2 {
    private int age;
    private int id;

    public Student2(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student2(int age, int id) {
        this.age = age;
        this.id = id;
    }
}

class FromClauseListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("student's id: " + eb.get("id"));
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class FromClause {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("student2",Student2.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        //String epl1 = "select id from student2(age > 10) as s";
        //String epl1 = "select id from student2(age in (0:50])";
        //String epl1 = "select id from student2(age>=1 and age<=20)";
        //String epl1 = "select id from student2(age between 1 and 20)";
        String epl1 = "select id from student2(age in [10:20])";

        EPStatement statement = admin.createEPL(epl1);
        statement.addListener(new FromClauseListener());

        EPRuntime er = esProvider.getEPRuntime();

        Student2 student1 = new Student2(10,1);
        er.sendEvent(student1);

    }
}

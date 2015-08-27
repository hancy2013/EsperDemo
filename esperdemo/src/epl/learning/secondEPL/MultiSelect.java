package epl.learning.secondEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/26.
 */
class Student1 {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Student1(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

class Teacher1 {
    private String name;
    private int age;

    public Teacher1(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

class MultiSelectListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("student'name: " + eb.get("student.name") + " student'age: " + eb.get("student.age"));
                System.out.println("teacher'name: " + eb.get("teacher.name") + " teacher'age: " + eb.get("teacher.age"));
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class MultiSelect {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("Student",Student1.class);
        config.addEventType("Teacher",Teacher1.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        String epl1 = "select s.* as student, t.* as teacher from Student.win:time(10) as s, Teacher.win:time(10) as t";

        EPStatement statement = admin.createEPL(epl1);

        statement.addListener(new MultiSelectListener());

        EPRuntime er = esProvider.getEPRuntime();

        Student1 student1 = new Student1("wxm",23);
        er.sendEvent(student1);

        Teacher1 teacher1 = new Teacher1("imperio",50);
        er.sendEvent(teacher1);
    }
}

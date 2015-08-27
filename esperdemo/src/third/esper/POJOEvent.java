package third.esper;

import java.util.List;
import java.util.Map;

/**
 * Created by wxmimperio on 2015/8/24.
 */

//总体事件的类进行封装
public class POJOEvent {
    String name;
    int age;
    List<Child> children;
    Map<String, Integer> phones;
    Address address;


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public Map<String, Integer> getPhones() {
        return phones;
    }

    public void setPhones(String name, Integer number) {
        phones.put(name, number);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public POJOEvent(String name, int age, List<Child> children, Map<String, Integer> phones, Address address) {
        this.name = name;
        this.age = age;
        this.children = children;
        this.phones = phones;
        this.address = address;
    }
}


//孩子的类进行封装
class Child {
    String name;
    String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Child(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }
}

//地址的类进行封装
class Address {
    String road;
    String street;
    int houseNo;

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(int houseNo) {
        this.houseNo = houseNo;
    }

    public Address(String road, String street, int houseNo) {
        this.road = road;
        this.street = street;
        this.houseNo = houseNo;
    }
}

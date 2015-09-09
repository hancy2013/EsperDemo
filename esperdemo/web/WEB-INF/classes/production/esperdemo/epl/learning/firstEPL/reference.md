##1.EPL Syntax (语法模版)
```
[annotations]
[expression_declarations]
[context context_name]
[insert into insert_into_def]
select select_list
from stream_def [as name] [, stream_def [as name]] [,...]
[where search_conditions]
[group by grouping_expression_list]
[having grouping_search_conditions]
[output output_specification]
[order by order_by_expression_list]
[limit num_rows]
```

##2.Time Periods (时间周期模版)
```
time-period : [year-part] [month-part] [week-part] [day-part] [hour-part]
[minute-part] [seconds-part] [milliseconds-part]
year-part : (number|variable_name) ("years" | "year")
month-part : (number|variable_name) ("months" | "month")
week-part : (number|variable_name) ("weeks" | "week")
day-part : (number|variable_name) ("days" | "day")
hour-part : (number|variable_name) ("hours" | "hour")
minute-part : (number|variable_name) ("minutes" | "minute" | "min")
seconds-part : (number|variable_name) ("seconds" | "second" | "sec")
milliseconds-part : (number|variable_name) ("milliseconds" | "millisecond" | "msec")
```
- 时间表达式
```
// 计算过去的5分3秒中进入改语句的Fruit事件的平均price
select avg(price) from Fruit.win:time(5 minute 3 sec)
// 每一天输出一次用户的账户总额
select sum(account) from User output every 1 day
```

##3.Comments (注释)
- 只能//单行注释，而/**/可以多行注释
```
a.单行注释
// This is MyEvent
select * from MyEvent

b.多行注释
/*
*This is OtherEvent
*/
select * from OtherEvent 
```

```
```
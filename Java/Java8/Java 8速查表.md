# Java 8速查表

### Java8的常用函数式接口：

|     函数式接口     |   函数式描述   |                         原始类型特化                         |
| :----------------: | :------------: | :----------------------------------------------------------: |
|   Predicate\<T>    |   T->boolean   |         IntPredicate, LongPredicate, DoublePredicate         |
|    Consumer\<T>    |    T->void     |          IntConsumer, LongConsumer, DoubleConsumer           |
|   Function<T,R>    |      T->R      | IntFunction\<R>,  IntToDoubleFunction,  IntToLongFunction, LongFunction\<R>,  LongToDoubleFunction, LongToIntFunction, DoubleFunction\<R>, ToIntFunction\<T>,  ToDoubleFunction\<T>,  ToLongFunction\<T> |
|    Supplier\<T>    |     ()->T      | BooleanSupplier, IntSupplier, LongSupplier,  DoubleSupplier  |
| UnaryOperator\<T>  |      T->T      |   IntUnaryOperator, LongUnaryOperator, DoubleUnaryOperator   |
| BinaryOperator\<T> |    (T,T)->T    | IntBinaryOperator,  LongBinaryOperator, DoubleBinaryOperator |
|  BiPredicate<L,R>  | (L,R)->boolean |                                                              |
|  BiConsumer<T,U>   |  (T,U)->void   | ObjIntConsumer\<T>, ObjLongConsumer\<T>,  ObjDoubleConsumer\<T> |
| BiFunction<T,U,R>  |    (T,U)->R    | ToIntBiFunction<T,U>,  ToLongBiFunction<T,U>,  ToDoubleBiFunction<T,U> |

### Lambda复合：

| Lambda复合 |                  应用                  |
| :--------: | :------------------------------------: |
| 比较器复合 | 逆序`reversed`,比较器链`thenComparing` |
|  谓词复合  |      与或非`and`，`or`，`negate`       |
|  函数复合  |   g(f(x))`andThen`，f(g(x))`compose`   |

### 流的中间操作和终端操作：

| 操作     | 类型 | 返回类型   | 操作参数                | 函数描述符      |
| -------- | ---- | ---------- | ----------------------- | --------------- |
| filter   | 中间 | Stream\<T> | Predicate\<T>           | T -> boolean    |
| map      | 中间 | Stream\<T> | Function\<T>            | T -> R          |
| limit    | 中间 | Stream\<T> |                         |                 |
| sorted   | 中间 | Stream\<T> | Comparator\<T>          | (T,T) -> int    |
| distinct | 中间 | Stream\<T> |                         |                 |
| skip     | 中间 | Stream\<T> | long                    |                 |
| flatmap  | 中间 | Stream\<R> | Function<T, Stream\<R>> | T -> Stream\<R> |

| 操作      | 类型 | 返回类型     | 操作参数           | 函数描述符   |
| --------- | ---- | ------------ | ------------------ | ------------ |
| forEach   | 终端 | void         | Consumer\<T>       | T -> void    |
| count     | 终端 | long         |                    |              |
| collet    | 终端 | R            | Collector<T, A, R> |              |
| anyMatch  | 终端 | boolean      | Predicate\<T>      | T -> boolean |
| allMatch  | 终端 | boolean      | Predicate\<T>      | T -> boolean |
| noneMatch | 终端 | boolean      | Predicate\<T>      | T -> boolean |
| findAny   | 终端 | Optional\<T> |                    |              |
| findFirst | 终端 | Optional\<T> |                    |              |
| reduce    | 终端 | Optional\<T> | BinaryOperator\<T> | (T, T) -> T  |

### Stream\<T>上的收集器：

| 工厂方法          | 返回类型              | 用于                                                         | 使用示例                                                     |
| ----------------- | --------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| toList            | List\<T>              | 把流中所有项目收集到一个List中                               | List\<Dish> dishes=menuStream.collect(toList());             |
| toSet             | Set\<T>               | 把流中所有项目收集到一个Set中，删除重复项                    | Set\<Dish> dishes=menuStream.collect(toSet());               |
| toCollection      | Collection\<T>        | 把流中所有项目收集到给定的供应源创建的集合                   | Collection\<DIsh> dishes=menuStream.collect(toCollection(),ArrayList::new); |
| counting          | Long                  | 计算流中元素的个数                                           | long howManyDishes=menuStream.collect(counting);             |
| summingInt        | Integer               | 对流中项目的一个整数属性求和                                 | int totalCalories menuStream=collect(summingInt(Dish::getCalories)); |
| averagingInt      | Double                | 计算流中项目Integer属性的平均值                              | doubel avgCalories menuStream=collect(averagingInt(Dish::getCalories)); |
| summarizingInt    | IntSummaryStatistics  | 收集关于流中项目Integer属性的统计值，例如最大，最小，总和与平均值 | IntSummaryStatistics menuStatistics=menuStream.collect(summarizingInt(Dish::getCalories)); |
| joining           | String                | 连接对流中每个项目调用toString方法所生成的字符串             | String shortMenu=menuStream.map(Dish::getName).collect(joining(",")); |
| maxBy             | Optional\<T>          | 一个包裹了流中按照给定比较器选出的最大元素的Optional，或如果流为空则为Optional.empty() | Optional\<Dish> fattest=menuStream.collect(maxBy(comparingInt(Dish::getCalories))); |
| minBy             | Optional\<T>          | 一个包裹了流中按照给定比较器选出的最小元素的Optional，或如果流为空则为Optional.empty() | Optional\<Dish> lightest=menuStream.collect(minBy(comparingInt(Dish::getCalories))); |
| reducing          | 归约操作产生的类型    | 从一个作为累加器的初始值开始，利用BinaryOperator与流中的元素逐个结合，从未将流归约到单个值 | int totalCalories=menuStream.collect(0,Dish::getCalories,Integer::sum) |
| CollectingAndThen | 转换函数返回的类型    | 包裹另一个收集器，对结果应用转换函数                         | int howManyDishes=menuStream.collect(CollectingAndThen(toList(),List::size)); |
| groupingBy        | Map<K,List\<T>>       | 根据项目的一个属性的值对流中的项目做分组，并将属性值作为结果Map的键 | Map<Dish.Type,List\<Dish>> dishByType=menuStream.collect(groupingBy(Dish::getType)); |
| partitioningBy    | Map<Boolean,List\<T>> | 根据对流中每个项目应用谓词的结果来对项目进行分区             | Map<Boolean,List\<Dish>> vegetarianDish=menuStream.collect(partitioningBy(Dish::isVegetarian)); |

### 解决多继承冲突的规则

1. **类中的方法优先级最高。类或父类中声明的方法的优先级高于任何声明为默认方法的优先级。**
2. **如果无法依据第一条判断，那么子接口的优先级更高：函数签名相同时，优先选择拥有最具体实现的默认方法的接口，即如果B继承了A，那么B就比A更具体。**
3. **最后，如果还是无法判断，继承了多个接口的类必须通过显式覆盖和调用期望的方法，显式地选择使用哪一个默认方法的实现。**

### Optional类的方法：

| 方法        | 描述                                                         |
| ----------- | ------------------------------------------------------------ |
| empty       | 返回一个空的Optional实例                                     |
| filter      | 如果值存在并且满足提供的谓词，就返回该值的Optional对象，否则返回一个空的Optional对象 |
| flatMap     | 如果值存在，就对该值执行提供的mapping函数调用，返回一个Optional类型的值，否则就返回一个空的Optional对象 |
| get         | 如果值存在，将该值用Optional封装返回，否则抛出一个NoSuchElementException异常 |
| ifPresent   | 如果该值存在，就执行使用该值的方法调用，否则什么都不做       |
| isPresent   | 如果值存在就返回true，否则返回false                          |
| map         | 如果值存在，就对该值执行提供的mapping函数调用                |
| of          | 将指定值用Optional封装之后返回，如果该值为null，则抛出一个NullPointerException |
| ofNullable  | 将指定值用Optional封装之后返回，如果该值为null，则返回一个空的Optional对象 |
| orElse      | 如果有值则将其返回，否则返回一个默认值                       |
| orElseGet   | 如果有值则将其返回，否则返回一个由指定的Supplier接口生成的值 |
| orElseThrow | 如果有值则将其返回，否则抛出一个由指定的Supplier接口生成的异常 |
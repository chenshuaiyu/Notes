# Java集合框架

### 一、Java集合类简介：

- Set：无序、不可重复的集合。
- List：有序、可重复的集合。
- Queue：代表一种队列集合实现。
- Map：具有映射关系的集合。

#### 1.Java集合和数组的区别：

1. 数组长度在初始化时指定，只能保存定长的数据。而集合可以保存数量不确定的数据。同时可以保存具有映射关系的数据。
2. 数组关系即可以是基本类型的值，也可以是对象。集合里只能保存对象（实际上只能保存对象的引用变量），基本数据类型的变量要转换成对应的包装类才能放入集合类中。

#### 2.Java集合类之间的继承关系

Collection和Map是Java集合框架的根接口。

![集合框架继承](https://github.com/chenshuaiyu/Notes/blob/master/Java/Java/Java进阶/assets/Collection继承体系.png)

ArrayList，LinkedList，HashSet，TreeSet是经常会用到的集合类。

![Map继承体系](https://github.com/chenshuaiyu/Notes/blob/master/Java/Java/Java进阶/assets/Map继承体系.png)

Map实现类用于保存具有映射关系的数据。HashMap，TreeMap是经常会用到的集合类。

### 二、Collection接口：

#### 1.简介

使用Iterator（迭代器）对集合元素进行迭代时，把集合元素的值传给了迭代变量（就如同参数传递是值传递，**基本数据类型传递的是值，引用类型传递的仅仅是对象的引用变量**）。

#### 2.Set集合

Set不允许包含相同元素。

#### 3.List集合

元素有序，可重复的集合。

#### 4.Queue集合

通常，队列不允许随机访问队列中的元素。

### 三、Map集合

#### 1.简介

key不允许重复，即同一个Map对象的任何两个key通过equals方法比较总是返回false。

#### 2.Map集合与Set集合，List集合的关系

1. 与Set集合的关系：如果把Map集合里的key放在一起看，就组成了一个Set集合，实际上Map确实包含了一个keySet方法，用户返回Map里所有key组成的Set集合。
2. 与List集合的关系：如果把Map集合里的value放在一起看，又非常类似于一个List，元素可以重复，可以根据索引来查找，只是Map中索引不再使用整数值，而是以另外一个对象作为索引。

Map内部包括一个Entry，封装了一个key-value对。

# ArrayList

### 一、概述

ArrayList是一个相对来说比较简单的数据结构，最重要的一点就是它的**自动扩容**，可以认为是“动态数组”。

1. 实现了List、RandomAccess、Cloneable, java.io.Serializable接口，可以插入空数据。
2. 以数组实现。节约空间，但数组有容量限制。超出限制会增加50%容量，用System.arraycopy()复制到新的数组，因此最好能给出数组大小的预估值。**默认第一次插入数组时创建大小为10的数组**。
3. 按数组下标访问元素——get(i)/set(i, e)的性能很高，这是数组的基本优势。
4. 直接在数组末尾加入元素，add(e)的性能也高，但如果按下标插入，删除元素——add(i,e),remove(i),remove(e)，则要用System.arraycopy()来移动部分受影响的元素，性能变差，这是基本劣势。

RandomAccess，Cloneable, java.io.Serializable接口都是**标记接口**。

实现了RandomAccess接口表示随机访问，使用foreach比Iterator要快。否则，使用Iterator比foreach要快。

**序列化：**

Java序列化和反序列化：序列化就是以一种形式保持，比如存放到硬盘或用于传输。反序列化是序列化逆过程。

ArrayList是基于动态数组实现的，并不是所有的空间都被使用。

`transient`关键字：通常将某个列实现`Serilizable`接口即可实现序列化，但是希望此类中的某些属性不被序列化时，可使用`transient`修饰该属性。

```java
transient Object[] elementData;
```

ArrayList自定义序列化和反序列化：

```java
private void writeObject(java.io.ObjectOutputStream s)
    throws java.io.IOException{
    // Write out element count, and any hidden stuff
    int expectedModCount = modCount;
    s.defaultWriteObject();

    // Write out size as capacity for behavioural compatibility with clone()
    s.writeInt(size);

    // Write out all elements in the proper order.
    for (int i=0; i<size; i++) {
        s.writeObject(elementData[i]);
    }

    if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
    }
}

private void readObject(java.io.ObjectInputStream s)
    throws java.io.IOException, ClassNotFoundException {
    elementData = EMPTY_ELEMENTDATA;

    // Read in size, and any hidden stuff
    s.defaultReadObject();

    // Read in capacity
    s.readInt(); // ignored

    if (size > 0) {
        // be like clone(), allocate array based upon size not capacity
        ensureCapacityInternal(size);

        Object[] a = elementData;
        // Read in all elements in the proper order.
        for (int i=0; i<size; i++) {
            a[i] = s.readObject();
        }
    }
}
```

当对象中自定义了`writeObject`和`readObject`方法时，JVM 会调用这两个自定义方法来实现序列化与反序列化。 

从`for`中的界限是`size`而不是`elementData.length `，说明序列化时，只是序列化那些实际存储的值，而不是整个数组。

### 二、add函数

```java
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}
```

`ensureCapacityInternal()`函数就是自动扩容机制的核心。

```java
private void ensureCapacityInternal(int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }

    ensureExplicitCapacity(minCapacity);
}

private void ensureExplicitCapacity(int minCapacity) {
    modCount++;

    // overflow-conscious code
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}

private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    //拓展为原来的1.5倍
    int newCapacity = oldCapacity + (oldCapacity >> 1);、
    //如果拓展后仍不满足需求，直接拓展为需求值
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // minCapacity is usually close to size, so this is a win:
    //使用Arrays.copyOf()函数拓展数组
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

### 三、set和get函数

```java
public E set(int index, E e) {
    rangeCheck(index);
    checkForComodification();
    E oldValue = ArrayList.this.elementData(offset + index);
    ArrayList.this.elementData[offset + index] = e;
    return oldValue;
}

public E get(int index) {
    rangeCheck(index);
    return elementData(index);
}
```

### 四、remove函数

```java
public E remove(int index) {
    rangeCheck(index);
    modCount++;
    E oldValue = elementData(index);

    int numMoved = size - index - 1;
    if (numMoved > 0)
        System.arraycopy(elementData, index+1, elementData, index,
                         numMoved);
    elementData[--size] = null; // clear to let GC do its work

    return oldValue;
}
```

### 五、Vector

### Vector

也实现了`List`，`RandomAccess`接口，底层数据结构和ArrayList类似，也是动态数组。

`add`方法使用`synchronized`  进行同步写数据，但是开销较大，所以vector是一个同步容器而不是一个并发容器。

`add(E e)`方法：

```java
public synchronized boolean add(E e) {
    modCount++;
    ensureCapacityHelper(elementCount + 1);
    elementData[elementCount++] = e;
    return true;
}
```

`add(int index, E element)`方法：

```java
public void add(int index, E element) {
    insertElementAt(element, index);
}
public synchronized void insertElementAt(E obj, int index) {
    modCount++;
    if (index > elementCount) {
        throw new ArrayIndexOutOfBoundsException(index
                                                 + " > " + elementCount);
    }
    ensureCapacityHelper(elementCount + 1);
    System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
    elementData[index] = obj;
    elementCount++;
}
```

# LinkedList

### 一、概述

1. 以双向链表实现。链表无容量限制，但双向链表本身使用了更多空间，也需要额外的链表指针操作。
2. 按下标访问元素—get(i)/set(i,e)要悲剧的遍历链表将指针移动到位（如果i>数组大小的一半，会从末尾移起）。
3. 当插入、删除元素是修改前后结点的指针即可，但还是要遍历部分链表的指针才能移动到下标所指的位置，只有在链表两头的操作—add()，addFirst()，removeLast()或用Iterator上的remove()能省掉指针的移动。

基于双向链表实现的。

### 二、set和get函数

```java
public E set(int index, E element) {
    checkElementIndex(index);
    Node<E> x = node(index);
    E oldVal = x.item;
    x.item = element;
    return oldVal;
}

public E get(int index) {
    checkElementIndex(index);
    return node(index).item;
}
```

这两个函数都调用了node函数，该函数会以O(n/2)的性能去获取一个结点。

```java
Node<E> node(int index) {
    // assert isElementIndex(index);
    if (index < (size >> 1)) {
        Node<E> x = first;
        for (int i = 0; i < index; i++)
            x = x.next;
        return x;
    } else {
        Node<E> x = last;
        for (int i = size - 1; i > index; i--)
            x = x.prev;
        return x;
    }
}
```

### 三、add和linkedLast函数

```java
public boolean add(E e) {
    linkLast(e);
    return true;
}

void linkLast(E e) {
    final Node<E> l = last;
    final Node<E> newNode = new Node<>(l, e, null);
    last = newNode;
    if (l == null)
        first = newNode;
    else
        l.next = newNode;
    size++;
    modCount++;
}
```

每次插入都是移动指针，和ArrayList的拷贝数组来说效率要高上不少。

# HashTable

- 底层数组+链表实现，key/value都不能为null，线程安全，实现线程安全的方式是在修改数据时锁住整个HashTable，效率低，ConcurrentHashMap做了相关优化
- 初始size为**11**，扩容：newsize = oldsize*2+1
- 计算index的方法：index = (hash & 0x7FFFFFFF) % tab.length

# HashMap

### 一、概述

- 基于Map接口，允许null键/值，非同步，无序。
- 初始size为16，扩容：newsize = oldsize*2，size一定为2的n次幂
- 扩容针对整个Map，每次扩容时，原来数组中的元素依次重新计算存放位置，并重新插入
- 插入元素后才判断该不该扩容，有可能无效扩容（插入后如果扩容，如果没有再次插入，就会产生无效扩容）
- 当Map中元素总数超过Entry数组的75%，触发扩容操作，为了减少链表长度，元素分配更均匀
- 计算index方法：index = hash & (tab.length – 1)

二、两个重要参数

容量（Capacity），负载因子（Load factor）

Capacity是bucket的大小，Load factor是bucket填满程度的最大比例。如果对迭代性能的要求很高的话，不要把Capacity设置过大，也不要把Load factor设置过小。

当bucket中的entries的数目大于`Capacity × Load factor`时，就需要调整bucket的大小为当前的2倍。

### 三、put函数的实现

1. 对key的hashCode()做hash()，然后再计算index
2. 如果没碰撞直接放到bucket里
3. 如果碰撞了，以链表的形式存在buckets后
4. 如果碰撞导致链表过长（大于等于TREEIFY_THRESHOLD=8），就把链表转换成红黑树
5. 如果节点已经存在就替换old value（保证key的唯一性）
6. 如果bucket满了（超过`load factor × current capacity`），就要resize。

```java
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}

final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
    //Node实现了Map接口的Entry<K,V>
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    //如果table为null或长度为0，对tab用resize函数进行扩容
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    //计算Index，并对null做处理
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {//发生Hash冲突
        Node<K,V> e; K k;
        //节点存在
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        //该链为树
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        //该链为链表
        else {
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        //写入
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}
```

### 四、get函数的实现

1. bucket里的第一个节点，直接命中；
2. 如果有冲突，则通过key.equals(k)去查找对应的entry，若为树，则在树中通过key.equals(k)查找，O(logn)；若为链表，则在链表中通过key.equals(k)去查找，O(n)。

```java
public V get(Object key) {
    Node<K,V> e;
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}

final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        //直接命中
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        //未命中
        if ((e = first.next) != null) {
            //在树中get
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            //在表中get
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```

### 五、hash函数的实现

在get和put的过程中，计算下标时，先对hashCode进行hash操作，然后再通过hash值进一步计算下标，如下图所示：

![hash函数的实现](https://github.com/chenshuaiyu/Notes/blob/master/Java/Java/Java进阶/assets/hash函数的实现.png)

```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

高16bit不变，低16bit和高16bit做了一个异或。

在Java8之前的实现中是用链表解决冲突的，在产生碰撞的情况下，进行get时，两步的时间复杂度是O(1)+O(n)。因此，当碰撞的很厉害的时候n很大，O(n)的速度显然是影响速度的。

在Java8中。利用红黑树替换链表，这样复杂度就成了O(1)+O(logn)了，这样n在很大的时候，能够比较理想的解决这个问题。

### 六、RESIZE的实现

当put时，如果超过比例，就会发生resize。在resize的过程中，简单的说就是把bucket扩充为2倍，之后重新计算index，把节点放到新的bucket中。

扩充HashMap时，不需要重新计算hash，只需要看看原来的hash值新增的那个bit是1还是0，是0的话索引不变，是1的话索引直接变成原索引+oldCap。

```java
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
        //超过最大值就不在扩充了，就只好随你去碰撞
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        //没超过最大值，就扩充为原来的2倍
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // double threshold
    }
    else if (oldThr > 0) // initial capacity was placed in threshold
        newCap = oldThr;
    else {               // zero initial threshold signifies using defaults
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    //计算新的resize上限
    if (newThr == 0) {
        float ft = (float)newCap * loadFactor;
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                  (int)ft : Integer.MAX_VALUE);
    }
    threshold = newThr;
    @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    table = newTab;
    if (oldTab != null) {
        //把每个bucket都移动到新的buckets中
        for (int j = 0; j < oldCap; ++j) {
            Node<K,V> e;
            if ((e = oldTab[j]) != null) {
                oldTab[j] = null;
                if (e.next == null)
                    newTab[e.hash & (newCap - 1)] = e;
                else if (e instanceof TreeNode)
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                else { // preserve order
                    Node<K,V> loHead = null, loTail = null;
                    Node<K,V> hiHead = null, hiTail = null;
                    Node<K,V> next;
                    do {
                        next = e.next;
                        //原索引
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                        //原索引+oldCap
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    //原索引+oldCap放到bucket里
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```

### 七、并发

在并发环境下使用HashMap容易出现死循环，所以HashMap只能在单线程中使用，多线程场景下推荐使用ConcurrentHashMap。

### 八、总结

1.什么时候会使用HashMap？他有什么特点？

是基于Map接口的实现，存储键值对时，它可以接收null的键值，是非同步的，HashMap存储着Entry(hash, key, value, next)对象。

2.你知道HashMap的工作原理吗？

通过hash方法，通过put和get存储和获取对象。存储对象时，我们将K/V传给put方法时，它调用hashCode()计算hash从而得到bucket的值，进一步存储，HashMap会根据当前的bucket来自动调整容量（超过Load Factor，则resize为原来的2倍）。获取对象时，我们将K传给get，它调用hashCode()计算hash从而得到bucket位置，并进一步调用equals方法确定键值对。如果发生碰撞的时候，HashMap通过链表将产生碰撞冲突的元素组织起来，在Java8中，如果一个bucket中碰撞冲突的元素超过某个限制（默认是8），则使用红黑树来替换链表，从而提高速度。

3.你知道get和put的原理吗？equals()和hashCode()的都有什么作用？

通过key的hashCode()进行hashing，并计算下标`((n-1) & hash)`，从而获得buckets的位置。如果产生碰撞，则利用key.equals()方法去链表或树中查找对应的节点。

4.你知道hash的实现吗？为什么要这样实现？

在Java8的实现中，是通过hashCode()的高16位异或低16位实现的：`(h = k.hashCode()) ^ (h >>> 16)`，主要从速度、功效、质量来考虑的，这么做可以在bucket比较小的时候，也能保证考虑到高低bit都参与到hash的计算中，同时不会有太大的开销。

5.如果HashMap的大小超过了负载因子（load factor）定义的容量，怎么办？

如果超过了负载因子（默认是0.75），则会resize一个原来长度两倍的HashMap，并且会重新调用hash方法。

# ConcurrentHashMap







# LinkedHashMap

### 一、概述

是`HashMap`+`双向链表`的实现，并且依靠着双向链表保证了迭代顺序是插入的顺序。

LinkedHashMap的排序方式有两种：

- 根据写入顺序排序
- 根据访问顺序排序（通过accessOrder控制）

accessOrder默认为false，默认按照插入顺序排序，可以调用

```java
public LinkedHashMap(int initialCapacity,
                     float loadFactor,
                     boolean accessOrder) {
    super(initialCapacity, loadFactor);
    this.accessOrder = accessOrder;
}
```

来按照访问顺序排序，

### 二、三个重点实现的函数

```java
void afterNodeAccess(Node<K,V> e) { }
void afterNodeInsertion(boolean evict) { }
void afterNodeRemoval(Node<K,V> e) { }
```

LinkedHashMap继承与HashMap，因此也重新实现了这3个函数，在accessOrder为true时调用这些函数，函数作用是节点访问后，节点插入后，节点移除后做一些事情。

```java
void afterNodeAccess(Node<K,V> e) { // move node to last
    LinkedHashMap.Entry<K,V> last;
    //如果定义了accessOrder，那么就保证最近访节点放到最后
    if (accessOrder && (last = tail) != e) {
        LinkedHashMap.Entry<K,V> p =
            (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
        p.after = null;
        if (b == null)
            head = a;
        else
            b.after = a;
        if (a != null)
            a.before = b;
        else
            last = b;
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
        tail = p;
        ++modCount;
    }
}
```

进行put只有就算是对节点的访问了，那么这个时候就会更新链表，把最近访问的放到最后。

```java
void afterNodeInsertion(boolean evict) { // possibly remove eldest
    LinkedHashMap.Entry<K,V> first;
    //如果定义了溢出规则，则执行相应的溢出。
    if (evict && (first = head) != null && removeEldestEntry(first)) {
        K key = first.key;
        removeNode(hash(key), key, null, false, true);
    }
}
```

如果定义了removeEldestEntry的规则，那么便可以执行相应的溢出操作。

```java
void afterNodeRemoval(Node<K,V> e) { // unlink
    //从链表中移除节点
    LinkedHashMap.Entry<K,V> p =
        (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
    p.before = p.after = null;
    if (b == null)
        head = a;
    else
        b.after = a;
    if (a == null)
        tail = b;
    else
        a.before = b;
}
```

这个函数是在移除节点后调用的，就是将节点从双向链表中删除。

### 三、put和get函数

put函数在LinkedHashMap中未重新实现，只是实现了afterNodeAccess和afterNodeInsertion两个回调函数。

为什么会使用HashMap的put函数？（自己的理解）

虽然HashMap是散列表存储的，但是LinkedHashMap的Node是在HashMap的Node的基础上加入了after和before引用。同时通过回调实现双向链表的连接。

get函数的具体实现：

```java
public V get(Object key) {
    Node<K,V> e;
    if ((e = getNode(hash(key), key)) == null)
        return null;
    if (accessOrder)
        afterNodeAccess(e);
    return e.value;
}
```

在accessOrder模式下，只要执行get或者put等操作的时候，就会产生structural modification。

# TreeMap

### 一、概述

保持key的大小顺序。

使用红黑树的好处是能够树具有不错的平衡性，这样操作的速度就可以达到log(n)的水平。

### 二、put函数

如果存在的话，old value被替换，如果不存在的话，则新添一个节点，然后对做红黑树的平衡操作。

```java
public V put(K key, V value) {
    Entry<K,V> t = root;
    if (t == null) {
        compare(key, key); // type (and possibly null) check

        root = new Entry<>(key, value, null);
        size = 1;	
        modCount++;
        return null;
    }
    int cmp;
    Entry<K,V> parent;
    // split comparator and comparable paths
    Comparator<? super K> cpr = comparator;
    //如果该节点存在，直接返回替换值
    if (cpr != null) {//存在比较器
        do {
            parent = t;
            cmp = cpr.compare(key, t.key);
            if (cmp < 0)
                t = t.left;
            else if (cmp > 0)
                t = t.right;
            else
                return t.setValue(value);
        } while (t != null);
    }
    else {
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
        do {
            parent = t;
            cmp = k.compareTo(t.key);
            if (cmp < 0)
                t = t.left;
            else if (cmp > 0)
                t = t.right;
            else
                return t.setValue(value);
        } while (t != null);
    }
    //如果该节点未存在，则新建
    Entry<K,V> e = new Entry<>(key, value, parent);
    if (cmp < 0)
        parent.left = e;
    else
        parent.right = e;
    //红黑树平衡调整
    fixAfterInsertion(e);
    size++;
    modCount++;
    return null;
}
```

### 三、get函数

以log(n)的复杂度进行get。

```java
public V get(Object key) {
    Entry<K,V> p = getEntry(key);
    return (p==null ? null : p.value);
}

final Entry<K,V> getEntry(Object key) {
    // Offload comparator-based version for sake of performance
    if (comparator != null)
        return getEntryUsingComparator(key);
    if (key == null)
        throw new NullPointerException();
    @SuppressWarnings("unchecked")
    Comparable<? super K> k = (Comparable<? super K>) key;
    Entry<K,V> p = root;
    //按照二叉树搜索方式进行搜索
    while (p != null) {
        int cmp = k.compareTo(p.key);
        if (cmp < 0)
            p = p.left;
        else if (cmp > 0)
            p = p.right;
        else
            return p;
    }
    return null;
}
```

### 四、successor后继

TreeMap输出相当于树的中序遍历（LDR）。

```java
static <K,V> TreeMap.Entry<K,V> successor(Entry<K,V> t) {
    if (t == null)
        return null;
    else if (t.right != null) {
        //有右子树的节点，后继节点就是右子树的最左节点
        //因为最左子树是右子树的最小节点
        Entry<K,V> p = t.right;
        while (p.left != null)
            p = p.left;
        return p;
    } else {
        //如果右子树为空，则寻找当前节点所在左子树的第一个祖先节点
        //因为左子树找完了，根据LDR该D了
        Entry<K,V> p = t.parent;
        Entry<K,V> ch = t;
        while (p != null && ch == p.right) {
            ch = p;
            p = p.parent;
        }
        return p;
    }
}
```

# HashSet

### 一、概述

无序，元素不可重复。

### 二、成员变量

```java
private transient HashMap<E,Object> map;

// Dummy value to associate with an Object in the backing Map
private static final Object PRESENT = new Object();
```

- map：用于存放最终数据
- PRESENT：所有写入map的value值。

### 三、构造函数

```java
public HashSet() {
    map = new HashMap<>();
}

public HashSet(Collection<? extends E> c) {
    map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
    addAll(c);
}

public HashSet(int initialCapacity, float loadFactor) {
    map = new HashMap<>(initialCapacity, loadFactor);
}

public HashSet(int initialCapacity) {
    map = new HashMap<>(initialCapacity);
}

HashSet(int initialCapacity, float loadFactor, boolean dummy) {
    map = new LinkedHashMap<>(initialCapacity, loadFactor);
}
```

### 四、add函数

```java
public boolean add(E e) {
    return map.put(e, PRESENT)==null;
}
```

将存放的对象当做了HashMap的键，value都是相同的PRESENT。

### 五、总结

HashSet就是记住HashMap来实现的。

# PriorityQueue

优先级队列，一个基于优先级堆的无界优先级队列。 
package Set;

public interface Set<T> {
    // 添加元素
    void add(T t);

    // 删除元素
    void remove(T t);

    // 是否包含此元素
    boolean contains(T t);

    // 是否为空
    boolean isEmpty();

    // 元素数目
    int getSize();
}

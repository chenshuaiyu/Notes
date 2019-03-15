package Queue;

public interface Queue<T> {

    // 入队
    void enqueue(T t);

    // 出队
    T dequeue();

    // 获取队首元素
    T getFront();

    // 队列是否为空
    boolean isEmpty();

    // 队列内元素数目
    int getSize();
}

package Queue;

import ArrayList.ArrayList;

/**
 * 顺序表队列的简单实现
 *
 * @param <T>
 */
public class ArrayQueue<T> implements Queue<T> {

    private ArrayList<T> data;

    public ArrayQueue(int size) {
        data = new ArrayList<T>(size);
    }

    public ArrayQueue() {
        this(10);
    }

    @Override
    public void enqueue(T t) {
        data.addLast(t);
    }

    @Override
    public T dequeue() {
        return data.removeFirst();
    }

    @Override
    public T getFront() {
        return data.getFirst();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public int getSize() {
        return data.getSize();
    }

    @Override
    public String toString() {
        return data.toString();
    }
}

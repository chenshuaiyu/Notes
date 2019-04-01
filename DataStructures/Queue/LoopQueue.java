package Queue;

import java.util.Arrays;

/**
 * 循环队列的简单实现
 *
 * @param <T>
 */
public class LoopQueue<T> implements Queue<T> {
    private T[] data;
    private int front, tail;
    private int size;

    public LoopQueue() {
        this(10);
    }

    public LoopQueue(int capacity) {
        data = (T[]) new Object[capacity + 1];
        this.size = 0;
        this.front = 0;
        this.tail = 0;
    }

    @Override
    public void enqueue(T t) {
        if ((tail + 1) % data.length == front)
            ensureCapacity();
        data[tail] = t;
        tail = (tail + 1) % data.length;
        size++;
    }

    @Override
    public T dequeue() {
        if (isEmpty())
            throw new RuntimeException("队列为空");
        T ret = data[front];
        front = (front + 1) % data.length;
        size--;
        return ret;
    }

    @Override
    public T getFront() {
        if (isEmpty())
            throw new RuntimeException("队列为空");
        return data[front];
    }

    @Override
    public boolean isEmpty() {
        return front == tail;
    }

    @Override
    public int getSize() {
        return size;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            data = Arrays.copyOf(data, size * 2);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (int i = front; i < size; i = (i + 1) % data.length) {
            if (i != size - 1)
                s.append(data[i] + ",");
            else
                s.append(data[i] + "]");
        }
        return s.toString();
    }
}

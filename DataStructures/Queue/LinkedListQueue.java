package Queue;

import LinkedList.LinkedList;

/**
 * 链表队列的简单实现
 *
 * @param <T>
 */
public class LinkedListQueue<T> implements Queue<T> {
    private LinkedList<T> data;

    public LinkedListQueue() {
        data = new LinkedList<T>();
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

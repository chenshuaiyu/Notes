package Stack;

import LinkedList.LinkedList;

public class LinkedListStack<T> implements Stack<T> {
    private LinkedList<T> data;

    public LinkedListStack() {
        data = new LinkedList<T>();
    }

    @Override
    public void push(T t) {
        data.addLast(t);
    }

    @Override
    public void pop() {
        data.removeLast();
    }

    @Override
    public T peek() {
        return data.getLast();
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

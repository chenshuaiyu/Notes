package Stack;

import ArrayList.ArrayList;

/**
 * 顺序栈的简单实现
 * @param <T>
 */
public class ArrayStack<T> implements Stack<T> {
    private ArrayList<T> data;

    public ArrayStack(int size) {
        data = new ArrayList<T>(size);
    }

    public ArrayStack() {
        this(10);
    }

    @Override
    public void push(T t) {
        data.addLast(t);
    }

    @Override
    public void pop() {
        if (isEmpty())
            throw new RuntimeException("栈为空");
        data.removeLast();
    }

    @Override
    public T peek() {
        if (isEmpty())
            throw new RuntimeException("栈为空");
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

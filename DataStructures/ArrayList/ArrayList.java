package ArrayList;

import java.util.Arrays;

/**
 * 顺序表的简单实现
 *
 * @param <T>
 */
public class ArrayList<T> {
    private T[] data;
    private int size;

    public ArrayList(int size) {
        if (size < 0)
            throw new RuntimeException("容量不可小于0");
        data = (T[]) new Object[size];
        this.size = 0;
    }

    public ArrayList() {
        this(10);
    }

    public ArrayList(T[] arr) {
        this.data = (T[]) new Object[arr.length];
        this.size = arr.length;
        for (int i = 0; i < size; i++) {
            this.data[i] = arr[i];
        }
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return data.length;
    }

    public void addFirst(T t) {
        insert(0, t);
    }

    public void addLast(T t) {
        insert(size, t);
    }

    public void insert(int index, T t) {
        if (isEmpty())
            throw new RuntimeException("顺序表为空");
        if (index > size || index < 0)
            throw new RuntimeException("下标越界");
        ensureCapacity();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = t;
        size++;
    }

    public T removeFirst() {
        return remove(0);
    }

    public T removeLast() {
        return remove(size - 1);
    }

    public T remove(int index) {
        if (isEmpty())
            throw new RuntimeException("顺序表为空");
        if (index >= size || index < 0)
            throw new RuntimeException("下标越界");
        T ret = data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[--size] = null;
        return ret;
    }

    public void set(int index, T t) {
        if (isEmpty())
            throw new RuntimeException("顺序表为空");
        if (index >= size || index < 0)
            throw new RuntimeException("下标越界");
        data[index] = t;
    }

    public T getFirst() {
        return get(0);
    }

    public T getLast() {
        return get(size - 1);
    }

    public T get(int index) {
        if (isEmpty())
            throw new RuntimeException("顺序表为空");
        if (index >= size || index < 0)
            throw new RuntimeException("下标越界");
        return data[index];
    }

    private void ensureCapacity() {
        if (size == data.length) {
            data = Arrays.copyOf(data, size * 2);
        }
    }

    public boolean isEmpty() {
        return data == null || size == 0;
    }

    public boolean isFull() {
        return size == data.length;
    }

    public boolean contains(T t) {
        return find(t) != -1;
    }

    public int find(T t) {
        for (int i = 0; i < size; i++) {
            if (t == data[i])
                return i;
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i != size - 1)
                s.append(data[i] + ",");
            else
                s.append(data[i] + "]");
        }
        return s.toString();
    }
}

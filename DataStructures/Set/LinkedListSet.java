package Set;

import LinkedList.LinkedList;

public class LinkedListSet<T> implements Set<T> {
    private LinkedList<T> data;

    public LinkedListSet() {
        data = new LinkedList<T>();
    }

    @Override
    public void add(T t) {
        if (!data.contains(t))
            data.addLast(t);
    }

    @Override
    public void remove(T t) {
        data.remove(t);
    }

    @Override
    public boolean contains(T t) {
        return data.contains(t);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public int getSize() {
        return data.getSize();
    }
}

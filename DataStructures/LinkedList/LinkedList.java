package LinkedList;

public class LinkedList<T> {
    private class Node {
        public T t;
        public Node next;

        public Node(T t, Node next) {
            this.t = t;
            this.next = next;
        }

        public Node() {
            this(null, null);
        }

        public Node(T t) {
            this(t, null);
        }

        @Override
        public String toString() {
            return t + "";
        }
    }

    private Node head;
    private int size;


    public LinkedList() {
        head = new Node();
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }


    public void addFirst(T t) {
        insert(0, t);
    }

    public void addLast(T t) {
        insert(size, t);
    }

    public void insert(int index, T t) {
        if (index > size || index < 0)
            throw new RuntimeException("下标越界");
        Node node = head;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        node.next = new Node(t, node.next);
        size++;
    }

    public T get(int index) {
        if (index >= size || index < 0)
            throw new RuntimeException("下标越界");
        Node node = head;
        for (int i = 0; i <= index; i++) {
            node = node.next;
        }
        return node.t;
    }

    public T getFirst() {
        return get(0);
    }

    public T getLast() {
        return get(size - 1);
    }

    public void set(int index, T t) {
        if (index >= size || index < 0)
            throw new RuntimeException("下标越界");
        Node node = head;
        for (int i = 0; i <= index; i++) {
            node = node.next;
        }
        node.t = t;
    }

    public boolean contains(T t) {
        Node cur = head.next;
        for (int i = 0; i < size; i++) {
            if (cur.t == t)
                return true;
            cur = cur.next;
        }
        return false;
    }

    public T removeFirst() {
        return remove(0);
    }

    public T removeLast() {
        return remove(size - 1);
    }

    public T remove(int index) {
        if (index >= size || index < 0)
            throw new RuntimeException("下标越界");
        Node cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        Node node = cur.next;
        cur.next = node.next;
        node.next = null;
        size--;
        return cur.t;
    }

    public void remove(T t) {
        Node cur = head.next;
        Node pre = head;
        for (int i = 0; i < size; i++) {
            if (cur.t == t) {
                pre.next = cur.next;
                cur = null;
            }
            pre = cur;
            cur = cur.next;
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        Node cur = head.next;
        for (int i = 0; i < size; i++) {
            s.append(cur + " -> ");
            cur = cur.next;
        }
        s.append("NULL");
        return s.toString();
    }
}

package Stack;

public interface Stack<T> {
    // 入栈
    void push(T t);

    // 出栈
    void pop();

    // 查看栈顶元素
    T peek();

    // 栈是否为空
    boolean isEmpty();

    // 栈内元素数目
    int getSize();
}

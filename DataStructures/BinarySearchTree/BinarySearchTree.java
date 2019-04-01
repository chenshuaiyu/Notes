package BinarySearchTree;

/**
 * 二叉搜索树的简单实现
 */
public class BinarySearchTree<E extends Comparable<E>> {
    private class Node{
        public E e;
        public Node left, right;

        public Node(E e) {
            this.e = e;
            left = null;
            right = null;
        }
    }

    private Node root;
    private int size;

    public BinarySearchTree() {
        root = null;
        size = 0;
    }

    public Node root() {
        return root;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

}

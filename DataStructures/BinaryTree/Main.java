package BinaryTree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Main {
    public BinaryTree genBinaryTree(Integer[] nodes, int index) {
        BinaryTree tree = null;
        if (index < nodes.length && nodes[index] != null) {
            tree = new BinaryTree(nodes[index]);
            tree.left = genBinaryTree(nodes, index * 2 + 1);
            tree.right = genBinaryTree(nodes, index * 2 + 2);
        }
        return tree;
    }

    //层级遍历
    public void levelOrderTraversal(BinaryTree root) {
        if (root == null) return;
        Queue<BinaryTree> queue = new LinkedList<BinaryTree>();
        queue.add(root);
        while (!queue.isEmpty()) {
            BinaryTree tree = queue.poll();
            System.out.print(tree.val + " ");
            if (tree.left != null) queue.add(tree.left);
            if (tree.right != null) queue.add(tree.right);
        }
    }

    //前序遍历-递归
    public void preorderTraversalByRecursion(BinaryTree root) {
        if (root == null) return;
        System.out.print(root.val + " ");
        preorderTraversalByRecursion(root.left);
        preorderTraversalByRecursion(root.right);
    }

    //前序遍历
    public void preorderTraversal(BinaryTree root) {
        Stack<BinaryTree> stack = new Stack<>();
        while (root != null || !stack.empty()) {
            while (root != null) {
                System.out.print(root.val + " ");
                stack.push(root);
                root = root.left;
            }
            root = stack.pop().right;
        }
    }

    //中序遍历-递归
    public void inorderTraversalByRecursion(BinaryTree root) {
        if (root == null) return;
        inorderTraversalByRecursion(root.left);
        System.out.print(root.val + " ");
        inorderTraversalByRecursion(root.right);
    }

    //中序遍历
    public void inorderTraversal(BinaryTree root) {
        Stack<BinaryTree> stack = new Stack<>();
        while (root != null || !stack.empty()) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            System.out.print(root.val + " ");
            root = root.right;
        }
    }

    //后序遍历-递归
    public void postorderTraversalByRecursion(BinaryTree root) {
        if (root == null) return;
        postorderTraversalByRecursion(root.left);
        postorderTraversalByRecursion(root.right);
        System.out.print(root.val + " ");
    }

    //后序遍历
    public void postorderTraversal(BinaryTree root) {
        Stack<BinaryTree> stack = new Stack<>();
        BinaryTree node = root;
        BinaryTree pre = null;
        while (node != null || !stack.empty()) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
            node = stack.peek();
            if (node.right == null || node.right == pre) {
                pre = stack.pop();
                System.out.print(node.val + " ");
                node = null;
            } else
                node = node.right;
        }
    }
}
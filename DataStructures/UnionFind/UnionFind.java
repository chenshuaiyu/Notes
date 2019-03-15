package UnionFind;

public class UnionFind {
    private int count;
    private int[] parent;
    // rank[i] 表示以 i 为根的集合中所表示的树的深度
    private int[] rank;

    public UnionFind(int count) {
        this.count = count;
        parent = new int[count];
        rank = new int[count];
        for (int i = 0; i < count; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }

    public int find(int p) {
        while (p != parent[p]) {
            p = parent[p];
        }
        return p;
    }

    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;
        if (rank[rootP] < rank[rootQ]) {
            parent[rootP] = rootQ;
        } else if (rank[rootP] > rank[rootQ]) {
            parent[rootQ] = rootP;
        } else {
            parent[rootP] = rootQ;
            rank[rootP]++;
        }
    }

    public boolean isConnected(int p, int q) {
        return find(p) == find(q);
    }
}

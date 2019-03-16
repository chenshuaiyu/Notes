package Graph;


public class Prim {
    class Edge {
        int beg;
        int en;
        int len;
    }

    public void prim(Graph g) {
        Edge[] tree = new Edge[g.vertexNum - 1];
        for (int i = 0; i < tree.length; i++) {
            tree[i] = new Edge();
        }

        for (int i = 1; i < g.vertexNum; i++) {
            tree[i - 1].beg = 0;
            tree[i - 1].en = i;
            tree[i - 1].len = g.edges[0][i];
        }
        int min, index;
        for (int i = 0; i < tree.length; i++) {
            min = tree[0].len;
            index = i;
            for (int j = i + 1; j < tree.length; j++) {
                if (tree[j].len < min) {
                    min = tree[j].len;
                    index = j;
                }
            }

            int v = tree[index].en;
            Edge t = tree[index];
            tree[index] = tree[i];
            tree[i] = t;

            for (int j = i + 1; j < tree.length; j++) {
                int d = g.edges[v][tree[j].en];
                if (d < tree[j].len) {
                    tree[j].len = d;
                    tree[j].beg = v;
                }
            }
        }

        for (int i = 0; i < tree.length; i++) {
            System.out.println(tree[i].beg + " -> " + tree[i].en + " 权值: " + g.edges[tree[i].beg][tree[i].en]);
        }
    }

    public static void main(String[] args) {
        int INF = Integer.MAX_VALUE;

        Prim prim = new Prim();
        Graph g = new Graph(6, 10,
                new int[][]{
                        {0, 10, 12, INF, 15, INF},
                        {10, 0, 7, 5, INF, 6},
                        {12, 7, 0, INF, 12, 8},
                        {INF, 5, INF, 0, INF, 6},
                        {15, INF, 12, INF, 0, 10},
                        {INF, 6, 8, 6, 10, 0}
                }
        );
        prim.prim(g);

    }
}

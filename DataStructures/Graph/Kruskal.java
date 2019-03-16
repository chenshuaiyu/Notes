package Graph;


public class Kruskal {
    class Edge {
        int beg;
        int en;
        int len;
    }

    public void kruskal(Graph g) {
        Edge[] edges = new Edge[g.vertexNum * g.vertexNum];
        Edge[] tree = new Edge[g.edgeNum];
    }

    public static void main(String[] args) {
        Kruskal kruskal = new Kruskal();
        kruskal.kruskal(null);
    }
}

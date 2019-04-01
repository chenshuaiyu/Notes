package Graph;

/**
 * 图的实体类
 */
public class Graph {
    int vertexNum; //节点数
    int edgeNum; //边数
    int[][] edges; //边的权值

    public Graph(int vertexNum, int edgeNum, int[][] edges) {
        this.vertexNum = vertexNum;
        this.edgeNum = edgeNum;
        this.edges = edges;
    }
}

package Graph;

public class DFS {
    public void dfsTraverse(Graph g) {
        int[] visited = new int[g.vertexNum];
        for (int i = 0; i < visited.length; i++)
            visited[i] = 0;
        for (int i = 0; i < g.vertexNum; i++) {
            if (visited[i] == 0)
                dfs(g, visited, i);
        }
    }

    private void dfs(Graph g, int[] visited, int i) {
        System.out.print(i + " ");
        visited[i] = 1;
        for (int j = 0; j < g.vertexNum; j++) {
            if (visited[i] == 0 && g.edges[i][j] == 1)
                dfs(g, visited, j);
        }
    }

    public static void main(String[] args) {
        DFS dfs = new DFS();
        Graph g = new Graph(6, 7,
                new int[][]{
                        {0, 1, 1, 0, 0, 0},
                        {1, 0, 0, 1, 1, 0},
                        {1, 0, 0, 0, 0, 1},
                        {0, 1, 0, 0, 1, 0},
                        {0, 1, 0, 1, 0, 1},
                        {0, 0, 1, 0, 1, 0}
                }
        );
        dfs.dfsTraverse(g);
    }
}

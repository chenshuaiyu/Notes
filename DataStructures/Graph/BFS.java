package Graph;

import java.util.LinkedList;
import java.util.Queue;

public class BFS {
    public void bfsTraverse(Graph g) {
        int count = 0;
        int[] visited = new int[g.vertexNum];
        for (int i = 0; i < visited.length; i++)
            visited[i] = 0;
        for (int i = 0; i < g.vertexNum; i++) {
            if (visited[i] == 0) {
                count++;
                bfs(g, visited, i);
            }
        }
        System.out.println("\n共 " + count + " 个连通分量");
    }

    private void bfs(Graph g, int[] visited, int i) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(i);
        visited[i] = 1;
        while (!queue.isEmpty()) {
            Integer node = queue.poll();
            System.out.print(node + " ");
            for (int j = g.vertexNum - 1; j >= 0; j--) {
                if (visited[j] == 0 && g.edges[node][j] == 1) {
                    queue.add(j);
                    visited[j] = 1;
                }
            }
        }
    }

    public static void main(String[] args) {
        BFS bfs = new BFS();
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
        bfs.bfsTraverse(g);
    }
}

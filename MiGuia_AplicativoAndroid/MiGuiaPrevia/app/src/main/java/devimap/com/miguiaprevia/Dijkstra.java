package devimap.com.miguiaprevia;

/**
 * Created by nossopc on 23/09/16.
 */
public class Dijkstra {

    // A utility function to find the vertex with minimum distance value,
    // from the set of vertices not yet included in shortest path tree
    int V;
    int pai[];
    int path[];
    int dist[];
    int graph[][];

    Dijkstra (int V, int graph[][]) {
        this.V = V;
        this.pai = new int[V];
        this.path = new int[V];
        this.dist = new int[V];
        this.graph = graph;
    }

    int minDistance(int dist[], boolean sptSet[])
    {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index=-1;

        for (int v = 0; v < V; v++)
            if (sptSet[v] == false && dist[v] <= min)
            {
                min = dist[v]; //min = 0
                min_index = v;   //0
            }

        return min_index;
    }

    // A utility function to print the constructed distance array
    void printSolution(int dist[], int n)
    {
        System.out.println("Vertex   Distance from Source");
        for (int i = 0; i < V; i++)
            System.out.println(i+" \t\t "+dist[i]);
    }

    void printCaminho (int src, int dest) {
        int i = dest;
        System.out.print(i);
        while (pai[i]!=-1 && i!=src) { // AQUI TEM QUE SER SRC
            i = pai[i];
            System.out.print(" <-- " + i);
        }
        System.out.println();
    }

    public int[] returnPath (int src, int dest) {
        int i = dest;
        int j;
        j = dist[dest];
        path[j] = i;
        while (pai[i]!=-1 && i!=src) { // AQUI TEM QUE SER SRC
            i = pai[i];
            j--;
            path[j] = i;
        }

        System.out.print("Caminho: ");
        for (int k=0;k<dist[dest]+1;k++) {
            System.out.print(path[k] + ", ");
        }
        System.out.println();
        return path;
    }

    // Funtion that implements Dijkstra's single source shortest path
    // algorithm for a graph represented using adjacency matrix
    // representation
    public int[] dijkstra(int src, int dest)
    {
        // dist[V]: The output array. dist[i] will hold
        // the shortest distance from src to i

        // sptSet[i] will true if vertex i is included in shortest
        // path tree or shortest distance from src to i is finalized
        boolean sptSet[] = new boolean[V];

        // Initialize all distances as INFINITE, stpSet[] as false, pai[] and caminho[] as -1
        for (int i = 0; i < V; i++)
        {
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
            pai[i] = -1;
            path[i] = -1;
        }

        // Distance of source vertex from itself is always 0
        dist[src] = 0;

        // Find shortest path for all vertices
        for (int count = 0; count < V-1; count++)
        {
            // Pick the minimum distance vertex from the set of vertices
            // not yet processed. u is always equal to src in first
            // iteration.
            int u = minDistance(dist, sptSet);

            // Mark the picked vertex as processed
            sptSet[u] = true;

            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (int v = 0; v < V; v++) {
                /*Log.i("Debug", "u: " + u + ", v: " + v);
                Log.i("Debug", "sptSet[v]: " + sptSet[v]);
                Log.i("Debug", "dist[u]: " + dist[u]);
                Log.i("Debug", "dist[v]: " + dist[v]);*/
                // Update dist[v] only if is not in sptSet, there is an
                // edge from u to v, and total weight of path from src to
                // v through u is smaller than current value of dist[v]
                if (!sptSet[v] && graph[u][v]!=0 &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u]+graph[u][v] < dist[v]){
                    dist[v] = dist[u] + graph[u][v];
                    pai[v] = u;
                }
            }

        }

        // print the constructed distance array
        //printSolution(dist, V);

        return returnPath(src, dest);
    }
}

import java.io.*;
import java.util.*;

public class Main {
    static class Node {
        int r;
        int c;
        int moved;
        Node(int r, int c) {this.r=r;this.c=c;this.moved = 0;}
        Node(int r, int c, int moved) {this.r=r;this.c=c;this.moved=moved;}
        public String getString() {
            return new String(String.valueOf(r) + "," + String.valueOf(c));
        }

        @Override
        public String toString() {
            return String.format("[%d, %d]", r, c);
        }
    }
    static class Edge {
        int from;
        int to;
        int c;
        Edge(int from, int to, int c) { this.from=from; this.to=to; this.c=c;}
    }

    static final int[] ROWS = {0,0,1,-1};
    static final int[] COLS = {1,-1,0,0};

    static int[] parent;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][] grid = new int[n][n];
        
        Node start = null;
        ArrayList<Node> deactivated = new ArrayList<>();
        ArrayList<Node> bases  = new ArrayList<>();
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = sc.nextInt();
                if (grid[i][j] == 1) {
                    start = new Node(i, j);
                    bases.add(start);
                }
                else if (grid[i][j] == 2) {
                    Node deac = new Node(i,j);
                    deactivated.add(deac);
                    bases.add(deac);
                }
            }
        }

        if (start == null)
            return;

        Node[] arr = new Node[1 + deactivated.size()];
        HashMap<String, Integer> rcToIdx = new HashMap<>();
        rcToIdx.put(start.getString(), 0);
        arr[0] = start;
        int idx = 1;
        for (Node node : deactivated) {
            arr[idx] = node;
            rcToIdx.put(node.getString(), idx++);
        }
        
        PriorityQueue<Edge> pq = new PriorityQueue<>(
            (a,b) -> {
                return a.c - b.c;
            }
        );
        

        for (Node node : bases) {
            ArrayDeque<Node> dq = new ArrayDeque<>();

            dq.add(node);
            boolean[][] visited = new boolean[n][n];
            Edge e = null;

            while (!dq.isEmpty()) {
                Node s = dq.poll();

                if (visited[s.r][s.c])
                    continue;
                visited[s.r][s.c] = true;

                if ( !(s.r == node.r && s.c == node.c) && ((grid[s.r][s.c] == 1) || (grid[s.r][s.c] == 2))) {
                    e = new Edge(rcToIdx.get(node.getString()), rcToIdx.get(s.getString()), s.moved);   
                    pq.add(e);
                }

                for (int d = 0; d < 4; d++) {
                    int nextR = s.r + ROWS[d];
                    int nextC = s.c + COLS[d];

                    if (nextR < 0 || nextC < 0 || nextR == n || nextC == n)
                        continue;
                    
                    if (visited[nextR][nextC])
                        continue;

                    if (grid[nextR][nextC] == -1)
                        continue;
                    dq.add(new Node(nextR, nextC, s.moved + 1));
                }   
            }
        }

        parent = new int[1 + deactivated.size()];
        for (int i = 0; i < parent.length; i++)
            parent[i] = i;

        int minCost = 0;
        while (!pq.isEmpty()) {
            Edge e = pq.poll();
            int fromIdx = e.from;
            int toIdx = e.to;
            
            int pa = find(fromIdx);
            int pb = find(toIdx);

            if (pa == pb)
                continue;

            parent[pa] = Math.min(pa ,pb);
            parent[pb] = Math.min(pa, pb);
            minCost += e.c;
        }

        HashSet<Integer> s = new HashSet<>();
        for (int i = 0; i < parent.length; i++)
            s.add(find(parent[i]));

        if (s.size() != 1)
            System.out.println(-1);
        else
            System.out.println(minCost);
    }

    public static int find(int x) {
        if (parent[x] == x)
            return x;

        return parent[x] = find(parent[x]);
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package overordnatsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author oskst764
 */
public class OptPlan {

    private List<Vertex> nodes;
    private List<Edge> edges;
    private DataStore ds;

    public OptPlan(DataStore ds) {
        this.ds = ds;
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        int diff;

        for (int i = 0; i < ds.nodes; i++) {
            Vertex lovation = new Vertex("" + (i + 1), "Nod #" + (i + 1));
            //System.out.println("lovation.getId()" + lovation.getId());
            nodes.add(lovation);
        }

        for (int i = 0; i < ds.arcs; i++) {
            diff = (int) Math.max(Math.abs(ds.nodeY[ds.arcStart[i] - 1] - ds.nodeY[ds.arcEnd[i] - 1]), Math.abs(ds.nodeX[ds.arcStart[i] - 1] - ds.nodeX[ds.arcEnd[i] - 1]));
            Edge lane = new Edge("" + (i + 1), nodes.get(ds.arcStart[i] - 1), nodes.get(ds.arcEnd[i] - 1), diff);
            edges.add(lane);
            //System.out.println("ds.arcStart[i] " + ds.arcStart[i]);
        }
    }

    public LinkedList<Vertex> createPlan(int start, int stop, int color, boolean intekrock) {
        LinkedList<Vertex> path;
        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph, ds);
        // Compute shortest path
        dijkstra.execute(nodes.get(start - 1));
        path = dijkstra.getPath(nodes.get(stop - 1));
        //System.out.println("nodes.get(start) " + nodes.get(start-1));
        //System.out.println("nodes.get(stop) " + nodes.get(stop-1));

        Arrays.fill(ds.notoknumber, 0);
        // Get shortest path
        if (intekrock) {
            for (int i = 0; i < path.size(); i++) {
                ds.notoknumber[i] = Integer.parseInt(path.get(i).getId());
                System.out.println("ds.notoknumber[i] " + ds.notoknumber[i]);
            }
        }

        // Arcs in the shortest path
        for (int i = 0; i < path.size() - 1; i++) {
            for (int j = 0; j < ds.arcs; j++) {
                if (ds.arcStart[j] == Integer.parseInt(path.get(i).getId())
                        && ds.arcEnd[j]
                        == Integer.parseInt(path.get(i + 1).getId())) {
                    //System.out.println("Arc: " + j);
                    ds.arcColor[j] = color;
                }
            }
        }

        return path;
    }
}
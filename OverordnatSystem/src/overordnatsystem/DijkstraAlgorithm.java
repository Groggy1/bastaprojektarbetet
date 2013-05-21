/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package overordnatsystem;

/**
 *
 * @author oskst764
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraAlgorithm {

    private final List<Vertex> nodes;
    private final List<Edge> edges;
    private Set<Vertex> settledNodes;
    private Set<Vertex> unSettledNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Integer> distance;
    private DataStore ds;

    public DijkstraAlgorithm(Graph graph, DataStore ds) {
        // Create a copy of the array so that we can operate on this array
        this.nodes = new ArrayList<Vertex>(graph.getVertexes());
        this.edges = new ArrayList<Edge>(graph.getEdges());
        this.ds = ds;
    }

    public void execute(Vertex source) {
        //System.out.println("source.toString() " + source.toString());
        settledNodes = new HashSet<Vertex>();
        unSettledNodes = new HashSet<Vertex>();
        distance = new HashMap<Vertex, Integer>();
        predecessors = new HashMap<Vertex, Vertex>();
        distance.put(source, 0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            Vertex node = getMinimum(unSettledNodes);
            //System.out.println("node.toString() " + node.toString());
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Vertex node) {
        List<Vertex> adjacentNodes = getNeighbors(node);
        Vertex lasttarget = null;
        for (Vertex target : adjacentNodes) {
            System.out.println("target.toString() " + target.toString());

            if (getShortestDistance(target) > getShortestDistance(node)
                    + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node)
                        + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
            //System.out.println("predecessors.get(target) " + predecessors.get(target));
            //System.out.println("predecessors.get(predecessors.get(target)) " + predecessors.get(predecessors.get(target)));
            for (int i = 0; i < ds.notoknumber.length - 1; i++) {
                //System.out.println("ds.notoknumber[i] " + ds.notoknumber[i] + " ds.notoknumber[i + 1] " + ds.notoknumber[i+1]);
                if (predecessors.get(target) != null) {
                    if (ds.notoknumber[i + 1] == Integer.parseInt(predecessors.get(target).getId()) && ds.notoknumber[i] == Integer.parseInt(target.getId())) {
                        predecessors.remove(target);
                        predecessors.remove(lasttarget);
                        distance.remove(target);
                        distance.remove(lasttarget);
                    }
                }
            }
            lasttarget = target;
            //System.out.println("predecessors.get(target) " + predecessors.get(target));
            //System.out.println("predecessors.get(predecessors.get(target) " + predecessors.get(predecessors.get(target)));
            //System.out.println("predecessors.get(predecessors.get(predecessors.get(target))) " + predecessors.get(predecessors.get(predecessors.get(target))) + "\n");
        }

    }

    private int getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    private List<Vertex> getNeighbors(Vertex node) {
        List<Vertex> neighbors = new ArrayList<Vertex>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && !isSettled(edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }

    private Vertex getMinimum(Set<Vertex> vertexes) {
        Vertex minimum = null;
        for (Vertex vertex : vertexes) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Vertex vertex) {
        return settledNodes.contains(vertex);
    }

    private int getShortestDistance(Vertex destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Vertex> getPath(Vertex target) {
        LinkedList<Vertex> path = new LinkedList<Vertex>();
        Vertex step = target;
        // Check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }
}

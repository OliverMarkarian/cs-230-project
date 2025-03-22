import javafx.util.Pair;

import java.util.*;
import java.lang.Math;

/**
 * Represents a node in a graph used for pathfinding algorithms such as A*.
 * Each node corresponds to a position in a grid and contains information about
 * its edges, costs, and position.
 *
 * @author Sam
 */
class Node {
    public ArrayList<Pair<Integer, Integer>> edges;
    public float cost;
    public float estimatedCost;
    public Node previous;
    public Pair<Integer, Integer> position;
}

/**
 * A comparator for comparing two {@link Node} objects based on their estimated cost.
 * This comparator is used in pathfinding algorithms like A* to prioritize nodes with
 * the lowest estimated cost (i.e., the sum of the cost to reach the node and the heuristic estimate to the goal).
 *
 * @author Sam
 */
class NodeComparator implements Comparator<Node> {
    /**
     * Compares two nodes based on their estimated total cost.
     * The comparison is done by first checking the estimated cost of the nodes.
     * This is used by the A* algorithm to prioritize nodes with the lowest estimated cost.
     *
     * @param left  the first node to be compared.
     * @param right the second node to be compared.
     * @return a negative integer if the estimated cost of the left node is less than that of the right node,
     *         zero if they are equal, or a positive integer if the estimated cost of the left node is greater.
     */
    @Override
    public int compare(Node left, Node right) {
        if (left.estimatedCost < right.estimatedCost) {
            return -1;
        } else if (left.estimatedCost == right.estimatedCost) {
            return 0;
        } else {
            return 1;
        }
        //Magic numbers but specified in the interface's docs.
    }
}

/**
 * A class that implements the A* pathfinding algorithm. It calculates the shortest path
 * from a starting point to an endpoint in a grid, considering walkable tiles and obstacles.
 *
 * The algorithm uses a heuristic function (Euclidean distance) to estimate the cost to the goal
 * and combines it with the actual cost of traveling to a node (BFS cost).
 *
 * @author Sam
 */
public class Pathfinder {
    /**
     * Finds the shortest path from a starting point to an endpoint using A* pathfinding algorithm.
     *
     * @param startX The x-coordinate of the starting point.
     * @param startY The y-coordinate of the starting point.
     * @param endX The x-coordinate of the endpoint.
     * @param endY The y-coordinate of the endpoint.
     * @param grid The grid representation of the environment.
     * @param requester The Actor who is requesting the path.
     * @return A queue containing the sequence of coordinates representing the shortest path from start to end.
     */
    Queue<Pair<Integer, Integer>> ShortestPath(int startX, int startY, int endX, int endY, Grid grid, Actor requester) {

        Pair<Integer, Integer> startCoord = new Pair<>(startX, startY);
        Pair<Integer, Integer> endCoord = new Pair<>(endX, endY);

        HashMap<Pair<Integer, Integer>, Node> graph = convertToGraphRepresentation(requester, grid, endCoord);

        graph.get(startCoord).cost = 0;
        graph.get(startCoord).estimatedCost = 0;


        return pathfind(graph, startCoord, endCoord);
    }

    /**
     * Performs the A* pathfinding algorithm to find the shortest path between a start and end node.
     *
     * @param graph The graph representation of the environment.
     * @param start The starting point in the graph.
     * @param end The endpoint in the graph.
     * @return A queue containing the sequence of coordinates representing the shortest path from start to end.
     */
    private Queue<Pair<Integer, Integer>> pathfind(final HashMap<Pair<Integer, Integer>, Node> graph, Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        NodeComparator comparator = new NodeComparator();
        PriorityQueue<Node> queue = new PriorityQueue<>(comparator);

        for (Node node : graph.values()) {
            queue.offer(node);
        }

        //Citation: https://en.wikipedia.org/wiki/A*_search_algorithm
        while (!queue.isEmpty()) {
            Node front = queue.poll();

            for (Pair<Integer, Integer> neighbour : front.edges) {
                Node node = graph.get(neighbour);

                final boolean shorterPath = front.cost + 1 < node.cost + 1;


                if (shorterPath) {
                    node.cost = front.cost + 1;
                    node.estimatedCost = node.cost + euclideanHeuristic(node.position, end);
                    node.previous = front;

                    if (queue.contains(node)) {
                        queue.remove(node);
                        queue.add(node); //Priority queue doesn't update upon priority change, work-around.
                    }
                }
            }

        }

        return backtrack(start, end, graph);
    }

    /**
     * Backtracks from the end node to the start node to construct the path taken.
     *
     * @param startNode The starting coordinate.
     * @param endNode The ending coordinate.
     * @param graph The graph representing the environment.
     * @return A queue containing the path from start to end, in order.
     */
    private Queue<Pair<Integer, Integer>> backtrack(Pair<Integer, Integer> startNode, Pair<Integer, Integer> endNode, final HashMap<Pair<Integer, Integer>, Node> graph) {
        //Citation: https://en.wikipedia.org/wiki/A*_search_algorithm
        LinkedList<Pair<Integer, Integer>> linkedList = new LinkedList<>();
        Node currentNode = graph.get(endNode);
        if (currentNode.estimatedCost == Float.POSITIVE_INFINITY) {
            return linkedList; //No path, avoids deadlock below.
        }
        while (currentNode != null && currentNode.position != startNode) {
            linkedList.add(currentNode.position);
            currentNode = currentNode.previous;
        }
        return linkedList.reversed();
    }

    /**
     * Converts the grid into a graph representation, excluding any non-walkable tiles or tiles
     * where the Actor cannot walk (e.g., tiles occupied by enemies or obstacles).
     *
     * @param requester The Actor requesting the path.
     * @param grid The grid to convert to a graph.
     * @param endCoord The coordinates of the destination tile.
     * @return A map of coordinates to their respective Node objects.
     */
    private HashMap<Pair<Integer, Integer>, Node> convertToGraphRepresentation(Actor requester, Grid grid, Pair<Integer, Integer> endCoord) {
        HashMap<Pair<Integer, Integer>, Node> graph = new HashMap<>();


        final Actor target = grid.getTile(endCoord.getKey(), endCoord.getValue()).getOccupier();


        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {

                Pair<Integer, Integer> coord = new Pair<>(x, y);
                Node node = new Node();

                node.edges = new ArrayList<>();
                node.cost = Float.POSITIVE_INFINITY;
                node.estimatedCost = Float.POSITIVE_INFINITY;
                node.previous = null;
                node.position = coord;

                //Add edges

                grid.getAdjacentTiles(grid.getTile(coord.getKey(), coord.getValue()))
                        .stream()
                        //Take only edges where either they are walkable OR (special case) they are the final destination
                        //final destination won't be walkable cuz the player is on it.
                        .filter((tile) -> (tile.getX() == endCoord.getKey() && tile.getY() == endCoord.getValue())
                                || (tile.actorCanWalkOn(requester) && !tile.hasOccupier()))
                        .forEach(tile -> node.edges.add(new Pair<>(tile.getX(), tile.getY())));

                graph.put(coord, node);
            }
        }
        return graph;
    }

    /**
     * Calculates the Euclidean distance between two coordinates as a heuristic for A* pathfinding.
     *
     * @param node The current node's position.
     * @param target The target node's position.
     * @return The Euclidean distance between the node and the target.
     */
    private float euclideanHeuristic(Pair<Integer, Integer> node, Pair<Integer, Integer> target) {
        return (float) Math.sqrt(Math.pow(target.getKey() - node.getKey(), 2) + Math.pow(target.getValue() - node.getValue(), 2)); //Minor issues with ints
    }
}

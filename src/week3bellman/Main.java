package week3bellman;

import java.util.*;

class Edge {
	String src;
	String dst;
	int cost;

	public Edge(String s, String d, int c) {
		src = s; dst = d; cost = c;
	}
}

class Graph {
	public ArrayList<Node> nodes;
	public Edge[] edges;

	class Adjacency {
		Node node;
		int cost;
		int newcost;
		Node nextHop;
	}

	class Node {
		String name;
		ArrayList<Adjacency> neighbourList;
		ArrayList<Adjacency> adjacencies;
		public Node(String name) {
			this.name = name;
			neighbourList = new ArrayList<Adjacency>();
			adjacencies = new ArrayList<Adjacency>();
		}

		public void addNeighbour(String s, int cost) {
			Node n = getNode(s);
			if (n != null) {
				Adjacency neighbour = new Adjacency();
				neighbour.node = n; neighbour.cost = cost;
				neighbourList.add(neighbour);
			}
		}

		public void initialiseCosts() {
			for (Node n : nodes) {
				Adjacency adj = new Adjacency();
				adj.node = n; adj.cost = 255;
				if (n.equals(this)) {
					adj.cost = 0;
					adj.nextHop = this;
				}
				this.adjacencies.add(adj);
			}
		}

		public int getCost(Node fromNode) {
			for (Adjacency a : this.adjacencies) {
				if (a.node.equals(fromNode)) {
					return a.cost;
				}
			}
			return 255;
		}
	}

	Graph(Edge[] e) {
		edges = e;
		nodes = new ArrayList<Node>();
		HashSet<String> nodelist = new HashSet<String>();
		for (Edge ed : edges) {
			nodelist.add(ed.src);
			nodelist.add(ed.dst);
		}
		for (String s : nodelist) {
			nodes.add(new Node(s));
		}
		for (Node n : nodes) {
			for (Edge ed : edges) {
				if (ed.src.equals(n.name)) n.addNeighbour(ed.dst, ed.cost);
			}
			n.initialiseCosts();
		}
	}

	public Node getNode(String s) {
		for (Node n : nodes) {
			if (n.name == s) {
				return n;
			}
		}
		return null;
	}

	public void printNetwork() {
		for (Node n : nodes) {
			System.out.println(n.name);
			for (Adjacency a : n.neighbourList) {
				System.out.println("-> " + a.node.name + " " + Integer.toString(a.cost, 10));
			}
		}
	}

	public void printCosts(String s) {
		Node n = getNode(s);
		System.out.println("Showing current cost to all nodes from node "+s);
		for (Adjacency adj : n.adjacencies) {
			String nHop = "";
			if (adj.cost != 255) nHop = adj.nextHop.name;
			System.out.println(adj.node.name + ": " + adj.cost + " Next hop: " + nHop);
		}
	}

	public void BellMannFordStep() {
		Node source_node = getNode("A");
		for (Node n : nodes) {
			System.out.println(n.name);
			for (Adjacency a : n.neighbourList) {
				System.out.println(source_node.getCost(n));

			}
		}
	}
}

public class Main {
	public static void main(String[] args) {
		String[][] bidirgraf = {{"A","B","3"},{"A","C","5"},{"C","D","2"},{"B","D","5"},{"B","E","2"},{"D","E","1"},{"D","F","3"},{"C","F","6"}};

		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (String[] o : bidirgraf) {
			edges.add(new Edge(o[0],o[1],Integer.parseInt(o[2])));
			edges.add(new Edge(o[1],o[0],Integer.parseInt(o[2])));
		}
		
		Edge[] edgelist = edges.toArray(new Edge[edges.size()]);
		Graph g = new Graph(edgelist);
		g.printNetwork();
		String source = "A";
		for (int i = 0; i < 1; i++) {
			//g.printCosts(source);
			g.BellMannFordStep();
		}
		g.printCosts(source);
	}
}

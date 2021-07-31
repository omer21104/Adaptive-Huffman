package tree.utils;

import java.util.HashMap;
import java.util.LinkedList;

import utilities.Symbol;

public class HuffmanTree {

	private int id;
	private static final boolean LEFT_CHILD = true;
	
	private Node root, currentNYT;
	private HashMap<Symbol, Node> leaves;
	private Node nodes[];
//	private Vector<PriorityQueue<Node>> blocks;
	
	public HuffmanTree(int numberOfSymbols) {
		id = 2 * numberOfSymbols - 1;
		
//		blocks = new Vector<>(2);
//		blocks.add(0, new PriorityQueue<>(Collections.reverseOrder()));
//		blocks.add(1, new PriorityQueue<>(Collections.reverseOrder()));
		
		
		
		nodes = new Node[2 * numberOfSymbols];
		
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = null;
		}
		
		leaves = new HashMap<Symbol, Node>();

		root = new Node(id, 0, null, null);
		currentNYT = root;
		
		// add current nyt node to 0 block heap
//		blocks.get(0).add(currentNYT);
		
		// decrement id here for the next node
		nodes[id--] = root;
	}
	
	/**
	 * Add a node to the huffman tree. if the byte associated with that node already exists
	 * then only increment its weight.
	 * @param node the node to be added to the tree
	 */
	public void addNewSymbolNode(Symbol symbol) {

		// create and update new node, and NYT node
		// and insert them to nodes array
		Node newChild = new Node(id, 1, symbol, currentNYT);
		
		// add to 1 block
//		addNodeToBlockHeap(newChild);
		
		nodes[id--] = newChild;
		
		Node newNYT = new Node(id, 0, null, currentNYT);
		nodes[id--] = newNYT;
		
		leaves.put(symbol, newChild);
		
		// set children
		currentNYT.setLeft(newNYT);
		currentNYT.setRight(newChild);
		
//		// move old nyt up from 0 block
//		blocks.get(0).remove(currentNYT);
//		blocks.get(1).add(currentNYT);
		
		// set new nyt node
		currentNYT = newNYT;
		
		// add new NYT to 0 block heap
//		addNodeToBlockHeap(currentNYT);

		// update tree
		this.updateTree(currentNYT.getParent());
	
	}
	
	/**
	 * update the tree if needed, starting from a given node
	 * @param node the node to start updating from
	 */
	public void updateTree(Node node) {
		
		Node parentNode = node.getParent();
		if (parentNode == null) {
			return;
		}
		
		if (parentNode != null) {
			if (parentNode.equals(node)) {
				return;
			}
		}
		
		if (node.equals(this.root)) {
//			removeNodeFromBlockHeap(node);
			
			node.incrementWeight();

//			addNodeToBlockHeap(node);
			return;			
		}
		
		// swap node with the node with highest id number in its block
//		Node nodeWithHighestIdInBlock = this.getHighestNodeInBlockV3(node);
		Node nodeWithHighestIdInBlock = this.getNodeWithHighestIdInBlockScanWholeTree(node);
//		Node nodeWithHighestIdInBlock = this.getHighestIdNodeInBlock(node);
		
//		System.out.println("[*] High: " + nodeWithHighestIdInBlock);
//		System.out.println("[*] This: " + node);
		
		if (!node.equals(nodeWithHighestIdInBlock)) {
			 node.swapWith(nodeWithHighestIdInBlock);
			 
			 nodes[node.getId()] = nodeWithHighestIdInBlock;
			 nodes[nodeWithHighestIdInBlock.getId()] = node;
//			 System.out.println("[*] After switch nodes[node.id]: " + nodes[node.getId()]);
//			 System.out.println("[*] After switch nodes[high.id]: " + nodes[nodeWithHighestIdInBlock.getId()]);
		}
		
		// remove node from its current block and place it in the next one
//		removeNodeFromBlockHeap(node);
		
		node.incrementWeight();

//		addNodeToBlockHeap(node);
		
		// update the tree
		this.updateTree(parentNode);

	}
	
//	public void createNewBlockHeap(int blockIndex) {
//		
//	
//		blocks.add(blockIndex, new PriorityQueue<>(Collections.reverseOrder()));
//		
//	}
	
//	public void addNodeToBlockHeap(Node nodeToAdd) {
//		try {
//			blocks.get(nodeToAdd.getWeight());
//		} catch(ArrayIndexOutOfBoundsException e) {
//			createNewBlockHeap(nodeToAdd.getWeight());
//		}
//		
//		
//		// add the node
//		blocks.get(nodeToAdd.getWeight()).add(nodeToAdd);
//	}
//	
//	public void removeNodeFromBlockHeap(Node nodeToRemove) {
//		blocks.get(nodeToRemove.getWeight()).remove(nodeToRemove);
//	}
	
	/**
	 * find and return the node with the highest id in the block of {@code node} 
	 * @param node the node to start searching from
	 * @return the node with the highest id in {@code node}'s block
	 */
	public Node getHighestIdNodeInBlock(Node node) {
		// nodes are inserted in order to nodes array
		// once a node with greater weight is encountered search is finished
		if (node == null) {
			return null;
		}
		
		int index = node.getId();
		Node highestNode = node;
		
		for (int i = node.getId(); i < nodes.length; i++) {
			if (nodes[i].getId() > highestNode.getId()) {
				// make sure its not the nodes parent
				if (nodes[i] == highestNode.getParent()) {
					continue;
				}
				else {
					if (nodes[i].getWeight() == highestNode.getWeight()) {
//						System.out.println("[88}");
						highestNode = nodes[i];
					}
				}
			}
			
			
		}
		
		return highestNode;
	}

	/**
	 * check if the tree contains a node for specified byte b
	 * @param symbol the byte to look for
	 * @return the node in the tree for that byte, if it exists, {@code null} otherwise.
	 */
	public Node containsSymbol(Symbol symbol) {
		if (leaves.containsKey(symbol))
			return leaves.get(symbol);
		else 
			return null;
	}

	public Node getRoot() {
		return root;
	}

	public Node getCurrentNYT() {
		return currentNYT;
//		return blocks.get(0).peek();
	}
	
	public HashMap<Symbol, Node> getLeaves() {
		return leaves;
	}
	
	// test
	public Node getNodeWithHighestIdInBlockScanWholeTree(Node node) {
		LinkedList<Node> Q = new LinkedList<Node>();
		Node parentOfStartingNode = node.getParent();
		
		Node nodeToReturn = node;
		
		while (parentOfStartingNode != null) {
			Q.add(parentOfStartingNode.getLeft());
			Q.add(parentOfStartingNode.getRight());
			
			parentOfStartingNode = parentOfStartingNode.getParent();
		}
		
		while (!Q.isEmpty()) {
			Node nodeToCheck = Q.poll();
			
			if (nodeToCheck.getId() > nodeToReturn.getId()) {
				if (nodeToCheck.getWeight() == nodeToReturn.getWeight()) {
					if (!nodeToCheck.equals(nodeToReturn.getParent())) {
						nodeToReturn = nodeToCheck;
					}
				}
			}
			
		}
		return nodeToReturn;
	}
	
	// test with heaps
//	public Node getHighestNodeInBlockV3(Node nodeToCompareTo) {
//		int blockIndex = nodeToCompareTo.getWeight();
//		
//		Node nodeWithHighestIdInBlock = blocks.get(blockIndex).peek();
//		if (nodeWithHighestIdInBlock.equals(nodeToCompareTo.getParent())) {
//			return nodeToCompareTo;
//		}
//		return nodeWithHighestIdInBlock;
//	}
}

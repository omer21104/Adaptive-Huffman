package tree.utils;

import java.util.Stack;

import utilities.Symbol;

/**
 * This class represents a node in Huffman Tree structure
 */
public class Node implements Comparable<Node>
{
	public final static boolean LEFT_CHILD = false, RIGHT_CHILD = true; // false for 0 true for 1 bit
	
	private int id, weight;
	private Symbol symbol;
	private Node left, right, parent;
//	private String pathToNode;
	private Stack<Boolean> pathToThisNode;

	public Node() 
	{
		
	}
	
	/**
	 * constructor for huffman tree node.
	 * @param id unique number of the node
	 * @param weight the weight (frequency) of that node
	 * @param val the Byte value of the node
	 * @param parent parent node
	 * @param nyt boolean flag to indicate if the node is NYT
	 */
	public Node(int id, int weight, Symbol val, Node parent) 
	{
		this.parent = parent;
		this.id = id;
		this.weight = weight;
		this.symbol = val;
		this.left = this.right = null;
//		this.pathToNode = "";
		this.pathToThisNode = null;
	}
	
	/**
	 * Swap two nodes with each other.
	 * @param otherNode node to be swapped with this node
	 */
	public void swapWith(Node otherNode) 
	{
		if (otherNode.getParent() == null || this.getParent() == null) 
		{
			return;
		}
			
		boolean isOtherNodeLeftChild = otherNode.getParent().getLeft().equals(otherNode);
		boolean isThisNodeLeftChild = this.getParent().getLeft().equals(this);
		
		Node tmpNode = this;
		if (isThisNodeLeftChild) 
		{
			this.getParent().setLeft(otherNode);
		}
		else 
		{
			this.getParent().setRight(otherNode);
		}
		
		if (isOtherNodeLeftChild) 
		{
			otherNode.getParent().setLeft(tmpNode);
		}
		else 
		{
			otherNode.getParent().setRight(tmpNode);
		}
		
		// swap id's
		int thisNodeId = this.getId();
		this.setId(otherNode.getId());
		otherNode.setId(thisNodeId);
		
		// swap parents if needed
		if (!this.getParent().equals(otherNode.getParent())) 
		{
			Node tmpParent = this.getParent();
			this.setParent(otherNode.getParent());
			otherNode.setParent(tmpParent);
		}
	}
	
	public Stack<Boolean> getPathToThisNode() 
	{
		this.pathToThisNode = new Stack<>();
		Node parent = this.getParent();
		Node childNode = this;
		
		while (parent != null && childNode != null) 
		{		
			if (parent.getLeft().equals(childNode)) 
			{
				this.pathToThisNode.push(LEFT_CHILD);
			}
			else 
			{
				this.pathToThisNode.push(RIGHT_CHILD);
			}
			
			childNode = parent;
			parent = parent.getParent();
			
			if (parent != null) 
			{
				if (parent.equals(childNode)) 
				{
					break;
				}
			}
		}
	
		return pathToThisNode;
	}
	
	/**
	 * check if this node is NYT
	 * @return True if and only if this node is NYT - weight == 0, false otherwise.
	 */
	public boolean isNYT() 
	{
		return this.getWeight() == 0;
	}
	
	public boolean isLeaf() 
	{
		return this.left == null && this.right == null;
	}

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public int getWeight() 
	{
		return weight;
	}

	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	public Symbol getSymbol() 
	{
		return symbol;
	}

	public void setSymbol(Symbol symbol) 
	{
		this.symbol = symbol;
	}

	public Node getLeft() 
	{
		return left;
	}

	public void setLeft(Node left) 
	{
		this.left = left;
	}

	public Node getRight() 
	{
		return right;
	}

	public void setRight(Node right) 
	{
		this.right = right;
	}

	public Node getParent() 
	{
		return parent;
	}

	public void setParent(Node parent) 
	{
		this.parent = parent;
	}
	
	public void incrementWeight() 
	{
		weight++;
	}
	
//	public void setPathToNode(String path) {
//		this.pathToNode = path;
//	}
//	
//	public String getPathToNode() {
//		return this.pathToNode;
//	}
	
//	public void updatePathToNode(boolean isLeftChild) {
//		if (isLeftChild) {
//			this.setPathToNode(this.getPathToNode() + '0');
//		}
//		else {			
//			this.setPathToNode(this.getPathToNode() + '1');
//		}
//	}
	
	public String toString() 
	{
		return String.format("{char: %s id: %d  weight: %d}" , this.symbol, this.id, this.weight);
	}

	@Override
	public int compareTo(Node otherNode) 
	{
		if (this.getId() > otherNode.getId()) 
		{
			return 1;
		}
		else if (this.getId() == otherNode.getId()) 
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
	
	public boolean equals(Node otherNode) 
	{
		return this.id == otherNode.id && 
			   this.weight == otherNode.weight &&
			   this.parent == otherNode.parent;
	}
}

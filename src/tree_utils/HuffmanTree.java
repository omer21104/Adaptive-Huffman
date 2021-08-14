package tree_utils;

import java.util.HashMap;
import java.util.LinkedList;

import utilities.Symbol;

public class HuffmanTree 
{
	private int id;
	
	private Node root, currentNYT;
	private HashMap<Symbol, Node> leaves;
	
	public HuffmanTree(int numberOfSymbols) 
	{
		id = 2 * numberOfSymbols - 1;
		leaves = new HashMap<Symbol, Node>();
		root = new Node(id, 0, null, null);
		currentNYT = root;
		
		// decrement id here for the next node
		id--;
	}
	
	/**
	 * Add a node to the huffman tree. if the byte associated with that node already exists
	 * then only increment its weight.
	 * @param node the node to be added to the tree
	 */
	public void addNewSymbolNode(Symbol symbol) 
	{
		// create and update new node, and NYT node
		Node newChild = new Node(id, 1, symbol, currentNYT);
		id--;
		
		Node newNYT = new Node(id, 0, null, currentNYT);
		id--;
		
		leaves.put(symbol, newChild);
		
		currentNYT.setLeft(newNYT);
		currentNYT.setRight(newChild);
		
		currentNYT = newNYT;

		// update tree
		this.updateTree(currentNYT.getParent());
	}
	
	/**
	 * update the tree if needed, starting from a given node
	 * @param node the node to start updating from
	 */
	public void updateTree(Node node) 
	{
		Node parentNode = node.getParent();
		if (parentNode == null) 
		{
			return;
		}
		
		if (parentNode != null) 
		{
			if (parentNode.equals(node))
			{
				return;
			}
		}
		
		if (node.equals(this.root)) 
		{	
			node.incrementWeight();
			return;			
		}
		
		// swap node with the node with highest id number in its block
		Node nodeWithHighestIdInBlock = this.getNodeWithHighestIdInBlock(node);
		
		if (!node.equals(nodeWithHighestIdInBlock)) 
		{
			 node.swapWith(nodeWithHighestIdInBlock);
		}
		
		node.incrementWeight();

		// update the tree
		this.updateTree(parentNode);
	}

	/**
	 * check if the tree contains a node for specified byte b
	 * @param symbol the byte to look for
	 * @return the node in the tree for that byte, if it exists, {@code null} otherwise.
	 */
	public Node containsSymbol(Symbol symbol) 
	{
		if (leaves.containsKey(symbol))
		{
			return leaves.get(symbol);
		}
		else 
		{
			return null;
		}
	}

	public Node getRoot() 
	{
		return root;
	}

	public Node getCurrentNYT() 
	{
		return currentNYT;
	}
	
	/**
	 * find the node with the highest id in {@code nodeToCompare} block
	 * @param nodeToCompare node to compare other nodes to
	 * @return the node with the highest id in the block relative to {@code nodeToCompare}
	 */
	public Node getNodeWithHighestIdInBlock(Node nodeToCompare) 
	{
		LinkedList<Node> Q = new LinkedList<Node>();
		Node parentOfStartingNode = nodeToCompare.getParent();
		
		Node nodeToReturn = nodeToCompare;
		
		while (parentOfStartingNode != null) 
		{
			Q.add(parentOfStartingNode.getLeft());
			Q.add(parentOfStartingNode.getRight());
			
			parentOfStartingNode = parentOfStartingNode.getParent();
		}
		
		while (!Q.isEmpty()) 
		{
			Node nodeToCheck = Q.poll();
			
			if (nodeToCheck.getId() > nodeToReturn.getId()) 
			{
				if (nodeToCheck.getWeight() == nodeToReturn.getWeight()) 
				{
					if (!nodeToCheck.equals(nodeToReturn.getParent())) 
					{
						nodeToReturn = nodeToCheck;
					}
				}
			}
		}
		
		return nodeToReturn;
	}
}

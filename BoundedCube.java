package comp3506.assn1.adts;

import java.util.Iterator;

/**
 * A three-dimensional data structure that holds items in a positional relationship to each other.
 * Each cell in the data structure can hold multiple items.
 * A bounded cube has a specified maximum size in each dimension.
 * The root of each dimension is indexed from zero.
 * 
 * Memory Efficiency: O(n) (NOTE: n = number of items in the bounded cube  = number of tree nodes/positions/cells)
 * 						   (NOTE: p = number of planes)
 * 						   (NOTE: q = number of planes in one position)
 * 
 * NOTE: For all memory and time complexities in this class: 
 * 		n = number of items in the bounded cube  = number of tree nodes/positions
 * 		p = number of planes
 * 		q = number of planes in one position
 * 	
 * 
 * @author Peter Baldry
 * @param <T> The type of element held in the data structure.
 */
public class BoundedCube<T> implements Cube<T> {
	private TreeNode currentNode;
	private TreeNode rootNode;
	private int cubeLength;
	private int cubeBreadth;
	private int cubeHeight;

	/**
	 * Private inner class representing a node on a tree
	 *  @author Peter Baldry
	 */
	private class TreeNode {
		TreeNode leftNode;
		TreeNode rightNode;
		int x;
		int y;
		int z;
		IterableQueue<T> nodeQueue = new TraversableQueue<T>();
		
		/**
		 * TreeNode constructor
		 * @param xCo the x coordinate of the node
		 * @param yCo the y coordinate of the node
		 * @param zCo the z coordinate of the node
		 * @param leftNodeNext the left branched node of this node
		 * @param rightNodeNext the right branched node of this node
		 * @param parentNodeNext the parent node of this node
		 */
		public TreeNode(int xCo, int yCo, int zCo, TreeNode leftNodeNext, TreeNode rightNodeNext, TreeNode parentNodeNext) {
			this.x = xCo;
			this.y = yCo; 
			this.z = zCo; 
			this.leftNode = leftNodeNext;
			this.rightNode = rightNodeNext;	
		}
		
		/**
		 * Determines if input coordinates are equal to the coordinates of the current node
		 * Run-time complexity: O(1)
		 * @param x x coordinate
		 * @param y y coordinate
		 * @param z z coordinate
		 * @return true if coordinates match, false if not
		 */
		public boolean isEquals(int x, int y, int z) {
			return ((this.x == x) && (this.y == y) && (this.z == z));
			//constant
		}
		
	}
	
	/**
	 * BoundedCube Constructor
	 * @param length  Maximum size in the 'x' dimension.
	 * @param breadth Maximum size in the 'y' dimension.
	 * @param height  Maximum size in the 'z' dimension.
	 * @throws IllegalArgumentException if provided dimension sizes are not positive.
	 */ 
	public BoundedCube(int length, int breadth, int height) throws IllegalArgumentException {
		if ((length <= 0) || (breadth <= 0) || (height <=0)) {
			throw new IllegalArgumentException();
		}
		cubeLength = length;
		cubeBreadth = breadth;
		cubeHeight = height;
		/* we want the tree to be as balanced as possible => take the root Node as the floor of 
		 * the median of each of the cube inputs (approx. the middle of the cube) in the hope that 
		 * the distribution of planes are even across the entire cube. If this is the case, optimum
		 * time complexities exist. Worst case all the planes are added on the lower side of median 
		 * or all planes are added on the higher side of median => closer to worst case time 
		 * complexities (see analysis and corresponding methods for more).
		 */
		rootNode = new TreeNode(length/2,breadth/2,height/2, null, null, null);
		currentNode = rootNode;
	}
	
	/**
	 * Private helper method for getting the value to be compared at each node.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param depth the depth of the tree
	 * @return value to be compared at that depth.
	 */
	private int getSplittingValueByDepth(int x, int y, int z, int depth) {
		/*level is the indicator of which coordinate to compare:
		*                                   3D Binary Search Tree
		* depth0 => level0 => compare x               *
		*                                           /   \
		* depth1 => level1 => compare y            *     *
		*                                        /  \   /  \ 
		* depth2 => level2 => compare z         *    * *    *
		*                                     /  \  /\ /\  / \
		* depth3 => level0 => compare x      *    **  ** **   *   
		*             .
		*             .
		*             . 
		*/
		int level = depth % 3;
		if (level == 0) return x;
		else if (level == 1) return y;
		else return z;
	}

	
	/**
	 * Private helper method for adding an element to tree.
	 * @param x x coordinate input
	 * @param y y coordinate input
	 * @param z z coordinate input
	 * @param element element to place
	 * @param currentNode the currentNode (usually parentNode)
	 * @param depth the starting depth (usually 0)
	 */
	private void placeOnTree(int x, int y, int z, T element, TreeNode currentNode, int depth) {
		//traverses tree - o(logn) time for reasonably balanced tree, o(n) time for completely unbalanced tree
		while (currentNode != null) {
			
			// if we have a direct match - add it to the queue
			if (currentNode.isEquals(x, y, z)) {
				currentNode.nodeQueue.enqueue(element);
				break;
			}
			
			/* otherwise we need to move down the tree splitting on a 
			  plane (mathematics plane, not airplane!) normal to either x, y, z*/
			int treeNodeCompareValue = getSplittingValueByDepth(
					currentNode.x,
					currentNode.y,
					currentNode.z,
					depth
				);
			int inputCompareValue = getSplittingValueByDepth(x, y, z, depth);
			
			if (inputCompareValue <= treeNodeCompareValue) {
				if (currentNode.leftNode == null) {
					// setup left node with new element
					currentNode.leftNode = new TreeNode(x, y, z, null, null, currentNode);
					currentNode.leftNode.nodeQueue.enqueue(element);
					
					break;
				} else {
					currentNode = currentNode.leftNode;
				}
			} else {
				if (currentNode.rightNode == null) {
					// setup right node with new element
					currentNode.rightNode = new TreeNode(x, y, z, null, null, currentNode);
					currentNode.rightNode.nodeQueue.enqueue(element);
					
					break;
				} else {
					currentNode = currentNode.rightNode;
				}
			}
				
			depth += 1;
		}
	}
	
	/**
	 * Private helper method, determines if valid input coordinates
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @throws IndexOutOfBoundsException if coordinates are negative or outside cube.
	 */
	private void indexBoundException(int x, int y, int z) {
		if ((x < 0) || (y < 0) || (z < 0 )) {
			throw new IndexOutOfBoundsException();
		} else if ((x >= cubeLength) || (y >= cubeBreadth) || (z >= cubeHeight)) {
			throw new IndexOutOfBoundsException();
		} 
	}
	
	/**
	 * Adds an element to a specified position.
	 * Run-time complexity: Dominantly O(logn) [1], worst O(n) - if no elements are in the tree yet or tree is completely one sided
	 * Calls placeOnTree which is O(logn) complexity.
	 * 
	 *  Normal Balanced Tree O(logn) time complexity    |       Unbalanced Tree (o(n)) time worst case
	 *               *                                  |                  *
	 *             /   \                                |                 /
   	 *            *     *                               |                * 
	 *          /  \   /  \                             |               /
	 *         *    * *    *                            |              *
	 *       /  \  /\ /\  / \                           |             / 
	 *      *    **  ** **   *                          |            *
	 * See Analysis for more
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param element plane to be added
	 * @throws IndexOutOfBoundsException if coordinates are negative or outside cube.
	 */
	@Override
	public void add(int x, int y, int z, T element) throws IndexOutOfBoundsException {
		indexBoundException(x,y,z);
		currentNode = rootNode;
		// O(logn) complexity
		placeOnTree(x, y, z, element, currentNode, 0);
	}
	
	/**
	 * Gets the oldest element (plane) from specified position.
	 * Run-time complexity: Dominantly O(logn) [1], worst O(n) - if no elements are in the tree yet or tree is completely one sided
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @throws IndexOutOfBoundsException
	 * 
	 */
	@Override
	public T get(int x, int y, int z) throws IndexOutOfBoundsException {
		indexBoundException(x,y,z);
		int depth = 0;
		T element = null;
		currentNode = rootNode;
		//traverses tree - o(logn) time for reasonably balanced tree, o(n) time for completely unbalanced tree
		while (currentNode != null) {
			int treeNodeCompareValue = getSplittingValueByDepth(
					currentNode.x,
					currentNode.y,
					currentNode.z,
					depth
				);
			int inputCompareValue = getSplittingValueByDepth(x, y, z, depth);
			if (currentNode.isEquals(x, y, z)) {
				//constant, gets oldest element, one iteration always
				return currentNode.nodeQueue.iterator().next();
			}
			if (inputCompareValue <= treeNodeCompareValue) {
				currentNode = currentNode.leftNode;
			} else {
				currentNode = currentNode.rightNode;
			} 
			depth += 1;
		}
		return element;
	}
	
	/**
	 * Gets all the elements (in the form of an iterablequeue) at a specified position.
	 * Run-time complexity: Dominantly O(logn) [1], worst O(n) - if no elements are in the tree yet or tree is completely one sided
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @throws IndexOutOfBoundsException if coordinates are out of cube bounds
	 */
	@Override
	public IterableQueue<T> getAll(int x, int y, int z) throws IndexOutOfBoundsException {
		indexBoundException(x,y,z);
		int depth = 0;
		currentNode = rootNode;
		//traverses tree - o(logn) time for reasonably balanced tree, o(n) time for completely unbalanced tree
		while (currentNode != null) {
			int treeNodeCompareValue = getSplittingValueByDepth(
					currentNode.x,
					currentNode.y,
					currentNode.z,
					depth
				);
			int inputCompareValue = getSplittingValueByDepth(x, y, z, depth);
			if (currentNode.isEquals(x, y, z)) {
				//constant
				return currentNode.nodeQueue;
					
			}
			if (inputCompareValue <= treeNodeCompareValue) {
				currentNode = currentNode.leftNode;
			} else {
				currentNode = currentNode.rightNode;
			} 
			depth += 1;
		}
		return currentNode.nodeQueue;
	}
	
	/**
	 * Determines if there are multiple elements at a particular position.
	 * Run-time complexity: Dominantly O(logn) [1], worst O(n) - if no elements are in the tree yet or tree is completely one sided
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @return true if multiple elements at position, false if 0 or 1 elements at position
	 * @throws IndexOutOfBoundsException if invalid coordinates.
	 */
	@Override
	public boolean isMultipleElementsAt(int x, int y, int z) throws IndexOutOfBoundsException {
		indexBoundException(x,y,z);
		int depth = 0;
		currentNode = rootNode;
		//traverses tree - o(logn) time for reasonably balanced tree, o(n) time for completely unbalanced tree
		while (currentNode != null) {
			int treeNodeCompareValue = getSplittingValueByDepth(
					currentNode.x,
					currentNode.y,
					currentNode.z,
					depth
				); 
			int inputCompareValue = getSplittingValueByDepth(x, y, z, depth);
			if (currentNode.isEquals(x, y, z)) {
				//constant query
				if (currentNode.nodeQueue.size() > 1) {
					return true;
				} else {
					return false;
				}
			}
			if (inputCompareValue <= treeNodeCompareValue) {
				currentNode = currentNode.leftNode;
			} else if (inputCompareValue > treeNodeCompareValue) {
				currentNode = currentNode.rightNode;
			} 
			depth += 1;
		}
		return false;
	}
		
	/**
	 * Removes element/plane from specified position in cube
	 * Run-time complexity: Dominantly O(logn) [1], worst O(n) - if no elements are in the tree yet or tree is completely one sided
	 * 						NOTE: We then search through at worst q elements in the queue at the position as well, 
	 * 							  which is O(q) complexity. 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param element plane to be removed
	 * @throws IndexOutOfBoundsException if invalid coordinates.
	 * @return true if removed correctly, false if not
	 */
	@Override
	public boolean remove(int x, int y, int z, T element) throws IndexOutOfBoundsException {
		indexBoundException(x,y,z);
		int depth = 0;
		currentNode = rootNode;
		//traverses tree - o(logn) time for reasonably balanced tree, o(n) time for completely unbalanced tree
		while (currentNode != null) {
			int treeNodeCompareValue = getSplittingValueByDepth(
					currentNode.x,
					currentNode.y,
					currentNode.z,
					depth
				);
			int inputCompareValue = getSplittingValueByDepth(x, y, z, depth);
			if (currentNode.isEquals(x, y, z)) {
				Iterator<T> nodeIterator = currentNode.nodeQueue.iterator();
				//at worst O(q) time complexity
				while (nodeIterator.hasNext()) {
					T nodeElement = nodeIterator.next();
					if (nodeElement.hashCode() == element.hashCode()) {
						nodeIterator.remove();
						return true; 
					}
				}
					
			}
			if (inputCompareValue <= treeNodeCompareValue) {
				currentNode = currentNode.leftNode;
			} else if (inputCompareValue > treeNodeCompareValue) {
				currentNode = currentNode.rightNode;
			} 
			depth += 1;
		}
		
		
		return false;
	}
	
	/**
	 * Removes all planes from a specified position in cube
	 * Run-time complexity: Dominantly O(logn) [1], worst O(n) - if no elements are in the tree yet or tree is completely one sided
	 * 						NOTE: We then search through q elements in the queue at the found position as well, 
	 * 							  which is O(q) complexity. 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @throws IndexOutOfBoundsException if invalid coordinates.
	 */
	@Override
	public void removeAll(int x, int y, int z) throws IndexOutOfBoundsException {
		indexBoundException(x,y,z);
		indexBoundException(x,y,z);
		int depth = 0;
		currentNode = rootNode;
		//traverses tree - o(logn) time for reasonably balanced tree, o(n) time for completely unbalanced tree
		while (currentNode != null) {
			int treeNodeCompareValue = getSplittingValueByDepth(
					currentNode.x,
					currentNode.y,
					currentNode.z,
					depth
				);
			int inputCompareValue = getSplittingValueByDepth(x, y, z, depth);
			if (currentNode.isEquals(x, y, z)) {
				Iterator<T> nodeIterator = currentNode.nodeQueue.iterator();
				//O(q) time complexity
				while (nodeIterator.hasNext()) {
					nodeIterator.next();
					nodeIterator.remove();
				}
					
			}
			if (inputCompareValue <= treeNodeCompareValue) {
				currentNode = currentNode.leftNode;
			} else if (inputCompareValue > treeNodeCompareValue) {
				currentNode = currentNode.rightNode;
			} 
			depth += 1;
		}
		
	}
	
	/**
	 * Clears all the planes from the cube (tree).
	 * Run-time complexity: O(1);
	 */
	@Override
	public void clear() {
		//constant
		rootNode = null;
	}
		
	
}
/**
 * Analysis & Justification:
 * For the following analysis, consider 'n' to be n = number of elements in the structure (ie. positions added).
 * 'p' = number of planes
 * 'q' = number of planes in a single queue/position.
 * BoundedCube was implemented using a 3D binary search tree (formally a kd-tree, ie. 3D-tree).
 *  
 * 		3D Binary Search Tree:  | Example: cubedim = (5,5,5); add(1, 2, 3); 
 *       General form:          |
 *                              | 	Now =====>	     add (1,3,4):
 * 					*           |   => compare x  |  1 <= 2 => go left    |      (2,2,2)
 * 				   / \          |                 |                       |      /       
 * 				  *	  *         |   => compare y  |  3 > 2 => go right    |  (1,2,3)
 * 				 / \ / \        |                 |                       |      \
 *              *   **  *	    |   => compare z  |  Found a null position|  (1,3,4) 
 * 		                        |                 |   add here            |  
 * 
 * 		- Space Complexity/ Memory efficiency
 * 			The OneSky application indicates that the relatively large airspace will be sparsely populated by planes, meaning there will
 * 			be many cells unoccupied. So to minimize the memory used and to have optimal memory efficiency, the data structure 
 * 			would hold only the cells required, ie. cells where at least one plane exists. There is no need to store the other 
 * 			cells. A 3D binary search tree only stores added elements, ie added cells where planes exist, and therefore has a minimal 
 * 			memory usage, O(n) [1]. Further, where p = number of planes, the tree stores at most O(p) planes. The worst case scenario is when 
 * 			n = p, ie. every cell has only one plane. To consider a practical memory problem with the OneSky applications, take a 20 * 20 * 20
 * 		    BoundedCube with only 1 plane added. This would only store 1 element in a 3D binary search tree. In a 3D array, you would have 
 * 			to store 20*20*20 = 8000 elements. This is a huge difference and scaled to larger cubes (like the Australian airspace), the 
 * 			tree remains memory efficient.
 * 
 * 			Further, a traversable queue was used to hold the planes at a particular cell mainly due to its memory efficiency. It is able
 * 			to be efficiently dynamic by only having links between elements, which is, for q = number of planes in a queue, O(q) space
 * 			complexity.
 * 
 * 		- Time complexity
 * 			The small trade off is time complexity for some of the methods. A 3D array would implement constant add and get methods 
 * 			by simply indexing the coordinates. A 3d binary search tree usually has O(logn) time complexity for these methods and 
 * 			o(n) worst case [1]. However, considering the time complexity is usually O(logn) for most methods, this is still efficient
 * 			enough. In the remove and removeAll methods, there is an extra time complexity after there is a positional match. Because a
 * 			traversable queue was used at each cell (to hold multiple planes in one cell), the get method is constant (one iteration of 
 * 			traversable queue iterator), but the remove methods have possibly O(q) time complexity on top of cell search time, as you 
 * 			must loop through the queue after the correct cell is found.
 * 
 *  	- Unusual cases
 *  		There are some edge cases which make the 3D binary search tree have worst case method time complexity O(n) [2]. In the constructor of 
 *  		BoundedCube, the root node is set as a position which is approximately the middle of the cube. If all the added planes are 
 *  		somewhat evenly spread across the cube (ie. half one side of the middle, half the other), the time complexity is optimal. [*] 
 *  		However, if the added planes are all on one side of a tree (ie on a (500, 500, 50) cube, adding lots of x<250, y<250, z<25
 *  		will result in an unbalanced tree. The very worst case is if each node is unbalanced such that every node only has a left node
 *  		(this is very rare on normal sized trees). However, even in this case, the time complexity can only reach O(n) worst case, which
 *  		is still not terrible.
 *  
 * 		- Similar simulations and extensions
 * 			A kd tree is well known for its fast nearest neighbour search O(logn) [3]. Although this function is not applicable in the OneSky 
 * 			application, it is reasonable to suggest that similar and existing plane radar applications would require this functionality
 * 			and require it fast for safety concerns (ie. the main priority). This is where a hashmap would NOT be useful at all and further
 * 			why it wasn't used as the data structure for this bounded cube class.
 *  	
 * Several other data structures were considered:
 * 
 * 		- 3D array
 * 			A 3D array has a terrible space complexity, as the array must be of fixed size length* breadth*height. For a large airspace, this
 * 			is far too great and will likely result in a sparse matrix (lots of empty coordinates - not very efficient). The only positive
 * 			for this implementation is constant add, remove and get methods, however the space required is far too great.
 * 
 * 		- Linked List
 * 			Has a much better space complexity compared to 3D array, requiring the minimum n elements stored - O(n). Of course, this is the 
 * 			worst case scenario, as if there are multiple planes in one cell, it will be less then n. 
 * 
 * 		- HashMap
 * 			A hashmap, implemented correctly, has great time complexity. It implements constant lookup and remove time. However, the best case 
 * 			scenario in terms of space complexity is O(n). This is only achieved when a perfect implementation is achieved. This is difficult
 * 			to achieve without importing java libraries optimized to achieve this due to the difficulty of creating a good hash function. 
 * 			Further, in a practical sense, a nearest neighbour search would be useful, and essentially cannot be done using a hashmap.[4]
 * 
 * Conclusion:
 * 		The two ideal implementations were the kd tree and a hashmap due to their memory efficiency. Considering the practical implementation
 * 		of plane radar systems, a hashmap is not reliable due to its inability to efficiently perform a nearest neighbour search. So, despite 
 * 		the small time efficiency trade off, a kd tree was used essentially due to its ability to perform a nearest neighbour search efficiently 
 * 		in a practical sense while remaining memory efficient and relatively time efficient. If the implementation wasn't too difficult, 
 * 		a TreeMap would be the ideal solution.[5]
 * 
 * 
 * References:
 * [1] A. Božinovski, G. Tanev, B. Stojčevska, V. Pačovski and N. Ackovska, "Space Complexity Analysis of the Binary Tree Roll Algorithm", JITA - Journal of Information Technology and Applications (Banja Luka) - APEIRON, vol. 13, no. 1, 2017 [Online]. Available: https://www.researchgate.net/publication/321064259_Space_Complexity_Analysis_of_the_Binary_Tree_Roll_Algorithm. [Accessed: 20- Aug- 2018]
 * [2] "6.2 BinarySearchTree: An Unbalanced Binary Search Tree", Opendatastructures.org, 2018. [Online]. Available: http://opendatastructures.org/versions/edition-0.1d/ods-java/node37.html. [Accessed: 21- Aug- 2018]
 * [3] D. Lowe and M. Muja, Scalable Nearest Neighbor Algorithms for High Dimensional Data. 2014 [Online]. Available: https://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=6809191. [Accessed: 22- Aug- 2018]
 * [4] "HashMap (Java Platform SE 8 )", Docs.oracle.com, 2018. [Online]. Available: https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html. [Accessed: 24- Aug- 2018]
 * [5] "TreeMap (Java Platform SE 7 )", Docs.oracle.com, 2018. [Online]. Available: https://docs.oracle.com/javase/7/docs/api/java/util/TreeMap.html. [Accessed: 24- Aug- 2018]
 */

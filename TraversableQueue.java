package comp3506.assn1.adts;

import java.util.Iterator;

/**
 * A TraversablQueue
 * Memory Usage Efficiency: O(n) (NOTE: n = elements in the queue = number of nodes in the linked list)
 * @author Peter Baldry
 * @param <T> The type of element held in the Queue.
 */
public class TraversableQueue<T> implements IterableQueue<T> {
	private int size = 0;
	private Node head, tail;
	
	/**
	 * A node of the queue / linked list
	 * @author Peter Baldry
	 */
	private class Node {
		public T nodeElement;
		public Node nextNode;
	}
	
	/**
	 * A TraversabelQueueIterator 
	 * @author Peter Baldry
	 * @param <G> The type of element
	 */
	private class TraversableQueueIterator<G> implements Iterator<T> {
		private Node currentNode = null;
		private Node previousNode = null;
		
		/**
		 * Determines whether the next node exists
		 * Run-time complexity: O(1)
		 * @return true if next element exists
		 */
		@Override
		public boolean hasNext() {
			if (currentNode == tail) {
				return false;
			} else {
				return true;
			}
			//all constant time complexity
		}
		
		/**
		 * Gets the next element if it exists
		 * Run-time complexity: O(1)
		 * @return T the next element
		 */
		@Override
		public T next() {
			T element;
			
			if (currentNode == null) {
				currentNode = head;
			} else {
				if (currentNode.nextNode == null) {
					// out of bounds exception
					currentNode = null;
				} else {
					previousNode = currentNode;
					currentNode = currentNode.nextNode;
				}
			}
			
			if (currentNode != null) {
				element = currentNode.nodeElement;
			} else element = null;
			
			return element;
			//all constant time complexity
		}
		
		/**
		 * Removes current element.
		 * Run-time complexity: 0(1) (since this remove method
		 * 		simply removes the current element)
		 */
		@Override
		public void remove() {
			if (previousNode != null) {
				// not the first element
				if (currentNode.nextNode != null) {
					// not the last element
					previousNode.nextNode = currentNode.nextNode;
				} else {
					previousNode.nextNode = null;
					tail = previousNode;
				}
			} else {
				// first element
				if (size > 1) {
					head = currentNode.nextNode;
					currentNode = null;
				} else {
					head = null;
					tail = null;
				}
			}
			size -= 1;
			//all constant time complexity
		}
		
	}
	
	/**
	 * Creates a traversableQueueIterator
	 * Run-time complexity: O(1)
	 * @return a TraversableQueue iterator
	 */
	@Override
	public Iterator<T> iterator() {
		return new TraversableQueueIterator<T>();
		//constant time (since we aren't using the iterator yet)
	}
	
	/**
	 * Enqueues an element to the queue (adds to 'end' of queue)
	 * Run-time complexity: O(1)
	 * @param T the element to enqueue
	 * @throws IllegalStateException if queue size is incorrect
	 */
	@Override
	public void enqueue(T element) throws IllegalStateException {
		Node newNode = new Node();
		newNode.nodeElement = element;
		
		if (size > 0) {
			tail.nextNode = newNode;
		}
		
		tail = newNode;
		
		if (size == 0) {
			head = newNode;
		}
		
		size += 1;
		//all constant time complexity
	}
	
	/**
	 * Dequeues (removes) an element from the queue
	 * Run-time complexity: O(1)
	 * @return T the element dequeued. 
	 * @throws IndexOutOfBoundsException if size = 0
	 */
	@Override
	public T dequeue() throws IndexOutOfBoundsException {
		if (size == 0) { 
			throw new IndexOutOfBoundsException();
		}
		else {
			T nodeElement = head.nodeElement;
			head = head.nextNode;
			size -= 1;
			if (size == 0) {
				tail = null;
			}
			return nodeElement;
		}
		//all constant time complexity
	}
	
		
	
	/**
	 * Finds the size of the queue
	 * Run-time complexity: O(1)
	 * @return the size of the queue
	 */
	@Override
	public int size() {
		//constant time complexity
		return size; 
	}

}

/**
 * Analysis & Justification of Design Choices:
 * 
 * 		A Linked List was chosen to implement TraversableQueue. A linked list provided 
 * 		an efficient time complexity for the methods required (all o(1) time) and 
 * 		also provided a minimal space complexity of O(n), n = number of elements in the list.
 * 		Further, a linked list is simple to implement and only requires links between each
 * 		node, meaning the size of queue is 'automatically' dynamic. This is much more efficient
 * 	    compared to allocating a fixed size of the queue from the beginning.[1]
 * 
 * 		More specifically, a linked list solution provides more than sufficient time and memory 
 * 		efficiency in the context of the air traffic management simulation, the OneSky simulation. 
 * 		Common requests such as adding, removing and getting planes are simple operations which 
 * 		are time efficient. One small inefficiency is removing a plane/element from the centre of
 * 		the queue as it requires iteration over the queue and a check for equality (search) 
 * 		before applying the constant remove method on that plane/element (see above). In the worst 
 * 		case, the search could require n iterations, causing this request to be o(n) time complexity. 
 * 		This inefficiency is discussed further in the BoundedCube implementation as this is where 
 * 		the iterator is actually used and the time complexity is compromised. 
 * 		
 * 		Other data structures considered:
 * 
 * 			- Dynamic Style Array solution:
 * 			  A dynamic array implementation was considered. Dequeueing time is constant as we know 
 * 			  the index of the array which corresponds to the oldest element (array[0]). However,
 * 			  enqueueing may cause memory problems. For instance, once the original array is full,
 * 			  an enqueue operation would require creating a new array with a greater size to 
 * 			  allow for further enqueue operations.[2] This could be done by doubling the current size
 * 			  of the queue or adding a constant amount of size. Both methods can be inefficient.
 * 			  If you are doubling a 1 million sized array just to add one more element this is 
 * 			  inefficient memory wise. If you add a constant amount (say 20), but you wish to add
 * 			  1 million more elements, this is inefficient because you are continually creating new 
 * 			  arrays.
 * 		
 * 			- Dynamic Style Array Wrapping solution:
 * 			  This case is much the same as general array style, however, the elements wrap around 
 * 			  from the tail to the head until the array is full. It is more efficient than the 
 * 			  general array style solution, however still requires reallocating the size of arrays.[3]
 * 			  
 * 	
 * 		Overall, both the memory and time efficiencies of the linked list were (possibly) 
 * 		the most efficient of all the solutions for a TraversableQueue for the OneSky simulation 
 * 		considered. The implementation is fairly straightforward considering this and was therefore
 * 		considered the best solution. 
 * 
 * 
 * 		References:
 *  	[1] J. McCallum, "Linked Lists", Mccalljt.io, 2015. [Online]. Available: https://mccalljt.io/blog/2015/08/linked-lists/. [Accessed: 20- Aug- 2018]
 *  	[2] "Linked List vs Array - GeeksforGeeks", GeeksforGeeks, 2018. [Online]. Available: https://www.geeksforgeeks.org/linked-list-vs-array/. [Accessed: 21- Aug- 2018]
 *  	[3] R. Pitts, "Queue - Array Implementation - Types", Cs.bu.edu, 2000. [Online]. Available: https://www.cs.bu.edu/teaching/c/queue/array/types.html. [Accessed: 21- Aug- 2018]
 *  
 * 			  
 */

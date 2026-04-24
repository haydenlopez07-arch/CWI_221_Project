import java.util.*;

/**
 * Single-linked node implementation of IndexedUnsortedList.
 * An Iterator with working remove() method is implemented, but
 * ListIterator is unsupported.
 * 
 * @author
 * 
 * @param <E> type to store
 */
public class IUSingleLinkedList<E> implements IndexedUnsortedList<E> {
	private LinearNode<E> front, rear;
	private int count;
	private int modCount;

	/** Creates an empty list */
	public IUSingleLinkedList() {
		front = rear = null;
		count = 0;
		modCount = 0;
	}

	@Override
	public void addToFront(E element) {
		LinearNode<E> newNode = new LinearNode<>(element);
		newNode.setNext(front);
		front = newNode;

		if (count == 0) {
			rear = front;
		}

		count++;
		modCount++;
	}

	@Override
	public void addToRear(E element) {
		LinearNode<E> newNode = new LinearNode<>(element);
		if (isEmpty()) {
			front = rear = newNode;
		} else {
			rear.setNext(newNode);
			rear = newNode;
			;
		}

		count++;
		modCount++;
	}

	@Override
	public void add(E element) {
		addToRear(element);
	}

	@Override
	public void addAfter(E element, E target) {
		if (!contains(target)) {
			throw new NoSuchElementException();
		}

		LinearNode<E> current = front;
		while (current != null && !current.getElement().equals(target)) {
			current = current.getNext();
		}

		LinearNode<E> newNode = new LinearNode<>(element);
		newNode.setNext(current.getNext());
		current.setNext(newNode);

		if (current == rear) {
			rear = newNode;
		}

		count++;
		modCount++;
	}

	@Override
	public void add(int index, E element) {
		if (index < 0 || index > count) {
			throw new IndexOutOfBoundsException();
		}

		if (index == 0) {
			addToFront(element);
		} else if (index == count) {
			addToRear(element);
		} else {
			LinearNode<E> current = front, previous = null;
			for (int i = 0; i < index; i++) {
				previous = current;
				current = current.getNext();
			}

			LinearNode<E> newNode = new LinearNode<>(element);
			newNode.setNext(current);
			previous.setNext(newNode);

			count++;
			modCount++;
		}
	}

	@Override
	public E removeFirst() {
		return remove(first());
	}

	@Override
	public E removeLast() {
		return remove(last());
	}

	@Override
	public E remove(E element) {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		LinearNode<E> current = front, previous = null;
		while (current != null && !current.getElement().equals(element)) {
			previous = current;
			current = current.getNext();
		}
		// Matching element not found
		if (current == null) {
			throw new NoSuchElementException();
		}
		return removeElement(previous, current);
	}

	@Override
	public E remove(int index) {
		if (index < 0 || index >= count) {
			throw new IndexOutOfBoundsException();
		}

		LinearNode<E> current = front, previous = null;
		for (int i = 0; i < index; i++) {
			previous = current;
			current = current.getNext();
		}

		return removeElement(previous, current);
	}

	@Override
	public void set(int index, E element) {
		if (index < 0 || index >= count) {
			throw new IndexOutOfBoundsException();
		}

		LinearNode<E> current = front;
		for (int i = 0; i < index; i++) {
			current = current.getNext();
		}

		current.setElement(element);

		modCount++;
	}

	@Override
	public E get(int index) {
		if (index < 0 || index >= count) {
			throw new IndexOutOfBoundsException();
		}

		LinearNode<E> current = front;
		for (int i = 0; i < index; i++) {
			current = current.getNext();
		}

		return current.getElement();
	}

	@Override
	public int indexOf(E element) {
		LinearNode<E> current = front;
		int index = 0;
		while (current != null) {
			if (current.getElement().equals(element)) {
				return index;
			}

			current = current.getNext();
			index++;
		}

		// Element not found
		return -1;
	}

	@Override
	public E first() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}

		return front.getElement();
	}

	@Override
	public E last() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}

		return rear.getElement();
	}

	@Override
	public boolean contains(E target) {
		return indexOf(target) != -1;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		LinearNode<E> current = front;
		while (current != null) {
			result.append(current.getElement());
			if (current.getNext() != null) {
				result.append(", ");
			}

			current = current.getNext();
		}

		result.append("]");
		return result.toString();
	}

	private E removeElement(LinearNode<E> previous, LinearNode<E> current) {
		// Grab element
		E result = current.getElement();
		// If not the first element in the list
		if (previous != null) {
			previous.setNext(current.getNext());
		} else { // If the first element in the list
			front = current.getNext();
		}
		// If the last element in the list
		if (current.getNext() == null) {
			rear = previous;
		}
		count--;
		modCount++;

		return result;
	}

	@Override
	public Iterator<E> iterator() {
		return new SLLIterator();
	}

	/** Iterator for IUSingleLinkedList */
	private class SLLIterator implements Iterator<E> {
		private LinearNode<E> previous;
		private LinearNode<E> current;
		private LinearNode<E> next;
		private int iterModCount;
		private boolean canRemove;

		/** Creates a new iterator for the list */
		public SLLIterator() {
			previous = null;
			current = null;
			next = front;
			iterModCount = modCount;
			canRemove = false;
		}

		@Override
		public boolean hasNext() {
			if (iterModCount != modCount) {
				throw new ConcurrentModificationException();
			}

			return next != null;
		}

		@Override
		public E next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			previous = current;
			current = next;
			next = next.getNext();
			canRemove = true;
			return current.getElement();
		}

		@Override
		public void remove() {
			if (iterModCount != modCount) {
				throw new ConcurrentModificationException();
			}

			if (!canRemove) {
				throw new IllegalStateException();
			}

			if (current == front) {
				front = front.getNext();
				if (front == null) {
					rear = null;
				}

				current = null;
			} else {
				LinearNode<E> tempNode = front;
				while (tempNode != null && tempNode.getNext() != current) {
					tempNode = tempNode.getNext();
				}

				tempNode.setNext(current.getNext());
				if (current == rear) {
					rear = tempNode;
				}

				current = previous;
			}

			count--;
			iterModCount++;
			modCount++;
		}
	}

	// IGNORE THE FOLLOWING CODE
	// DON'T DELETE ME, HOWEVER!!!
	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<E> listIterator(int startingIndex) {
		throw new UnsupportedOperationException();
	}
}

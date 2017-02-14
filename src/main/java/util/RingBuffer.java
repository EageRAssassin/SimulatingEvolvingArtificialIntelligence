package util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RingBuffer<T> implements BlockingQueue<T> {

	private T[] buffer;
	/* The write pointer, represented as an offset into the array. */
	private Pointer ReadPointer = new Pointer(0), WritePointer = new Pointer(0);

	public RingBuffer(int capacity) {
		// validate capacity larger than 0
		if (capacity <= 0)
			throw new IllegalArgumentException("Ring Buffer capacity must be positive.");
		buffer = (T[]) new Object[capacity + 1];
	}

	/*
	 * To check if the ring buffer is not able to insert anything
	 */
	public synchronized boolean isFull() {
		return (WritePointer.pos + 1) % buffer.length == ReadPointer.pos;
	}

	@Override
	public T remove() {
		// validate capacity larger than 0
		T result = null;
		synchronized (ReadPointer) {
			if (isEmpty())
				throw new IllegalArgumentException("The Ring Buffer is empty.");
			result = buffer[ReadPointer.pos];
			ReadPointer.pos = (ReadPointer.pos + 1) % buffer.length;
		}
		synchronized (WritePointer) {
			WritePointer.notifyAll();
		}
		return result;
	}

	@Override
	public T poll() {
		T result = null;
		synchronized (ReadPointer) {
			// validate capacity larger than 0
			if (isEmpty())
				return null;
			result = buffer[ReadPointer.pos];
			ReadPointer.pos = (ReadPointer.pos + 1) % buffer.length;
		}
		synchronized (WritePointer) {
			WritePointer.notifyAll();
		}
		return result;
	}

	@Override
	public T element() {
		// validate capacity larger than 0
		if (isEmpty())
			throw new IllegalArgumentException("The Ring Buffer is empty.");
		T result = buffer[ReadPointer.pos];
		return result;
	}

	@Override
	public T peek() {
		synchronized (ReadPointer) {
			if (isEmpty())
				return null;
			return buffer[ReadPointer.pos];
		}
	}

	@Override
	public synchronized int size() {
		int size;
		if (isEmpty())
			size = 0;
		else if (isFull())
			size = buffer.length - 1;
		else
			size = (WritePointer.pos - ReadPointer.pos + buffer.length) % buffer.length;
		if (size > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return size;
	}

	@Override
	public synchronized boolean isEmpty() {
		return WritePointer.pos == ReadPointer.pos;
	}

	@Override
	public Iterator<T> iterator() {
		Iterator<T> it = new Iterator<T>() {
			private int currentIndex = ReadPointer.pos;

			@Override
			public boolean hasNext() {
				return (currentIndex + 1) % buffer.length != WritePointer.pos && buffer[currentIndex] != null;
			}

			@Override
			public T next() {
				T t = buffer[currentIndex];
				currentIndex = (currentIndex + 1) % buffer.length;
				return t;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}

	@Override
	public boolean add(T e) {
		synchronized (WritePointer) {
			if (isFull())
				throw new IllegalStateException("Ring Buffer Full.");
			buffer[WritePointer.pos] = e;
			WritePointer.pos = (WritePointer.pos + 1) % buffer.length;
			return true;
		}
	}

	@Override
	public boolean offer(T e) {
		synchronized (WritePointer) {
			if (isFull())
				return false;
			buffer[WritePointer.pos] = e;
			WritePointer.pos = (WritePointer.pos + 1) % buffer.length;
			return true;
		}
	}

	@Override
	public void put(T e) throws InterruptedException {
		synchronized (WritePointer) {
			while (isFull())
				WritePointer.wait();
			buffer[WritePointer.pos] = e;
			WritePointer.pos = (WritePointer.pos + 1) % buffer.length;
		}
		synchronized (ReadPointer) {
			ReadPointer.notifyAll();
		}
	}

	@Override
	public T take() throws InterruptedException {
		T result = null;
		synchronized (ReadPointer) {
			// validate capacity larger than 0
			while (isEmpty())
				ReadPointer.wait();
			result = buffer[ReadPointer.pos];
			ReadPointer.pos = (ReadPointer.pos + 1) % buffer.length;
		}
		synchronized (WritePointer) {
			WritePointer.notifyAll();
		}
		return result;
	}

	@Override
	public boolean contains(Object o) {
		try {
			for (int i = 0; i < buffer.length; i++)
				if (((T) o).equals(buffer[i]))
					return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public boolean equals(RingBuffer<T> t) {
		for (int i = 0; i < buffer.length; i++) {
			try {
				if (buffer[i] != t.buffer[i]) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		if (ReadPointer.pos != t.ReadPointer.pos || WritePointer.pos != t.WritePointer.pos) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		return ReadPointer.pos + WritePointer.pos;
	}

	@Override
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offer(Object e, long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int remainingCapacity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection c, int maxElements) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public T[] toArray(Object[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	private class Pointer {
		int pos;

		public Pointer(int pos) {
			this.pos = pos;
		}
	}
}

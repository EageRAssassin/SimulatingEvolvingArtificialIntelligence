package JunitTest;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import util.RingBuffer;
import util.RingBufferFactory;

public class RingBufferTest {

	@Test
	/*
	 * To test if the ring buffer adds, removes and counts the size of elements
	 * properly
	 */
	public void testAddRemoveElementSize() {
		RingBufferFactory<String> f = new RingBufferFactory<String>();
		RingBuffer<String> test = (RingBuffer<String>) f.getSynchronizedBuffer(100);
		test.add("aaa");
		test.add("bbb");
		test.add("ccc");
		test.add("ddd");
		assertEquals(test.remove(), "aaa");
		assertEquals(test.element(), "bbb");
		assertEquals(test.remove(), "bbb");
		assertEquals(test.size(), 2);
	}

	@Test
	/*
	 * To test if the ring buffer adds, removes and counts the size of elements
	 * properly
	 */
	public void testOfferPollPeek() {
		RingBufferFactory<String> f = new RingBufferFactory<String>();
		RingBuffer<String> test = (RingBuffer<String>) f.getSynchronizedBuffer(5);
		assertTrue(test.offer("aaa"));
		assertTrue(test.offer("bbb"));
		assertTrue(test.offer("ccc"));
		assertTrue(test.offer("ddd"));
		assertTrue(test.offer("eee"));
		assertFalse(test.offer("fff"));// Not enough capacity
		assertEquals(test.remove(), "aaa");
		assertEquals(test.peek(), "bbb");
		assertEquals(test.poll(), "bbb");
		assertEquals(test.poll(), "ccc");
		assertEquals(test.poll(), "ddd");
		assertEquals(test.poll(), "eee");
		assertEquals(test.poll(), null);// Nothing in the queue
		assertEquals(test.peek(), null);// Nothing in the queue
	}

	@Test
	/*
	 * To test if the ring buffer puts, remove and count the size of elements
	 * properly
	 */
	public void testisFull() {
		RingBufferFactory<Integer> f = new RingBufferFactory<Integer>();
		RingBuffer<Integer> test = (RingBuffer<Integer>) f.getSynchronizedBuffer(100);
		for (int i = 0; i < 100; i++)
			test.add(i);
		assertEquals(test.size(), 100);
		assertTrue(test.isFull());
	}

	@Test
	/*
	 * To test if the ring buffer contains elements' function worked properly
	 */
	public void testcontains() {
		RingBufferFactory<Integer> f = new RingBufferFactory<Integer>();
		RingBuffer<Integer> test = (RingBuffer<Integer>) f.getSynchronizedBuffer(100);
		for (int i = 0; i < 100; i++)
			test.add(i);
		for (int i = 99; i >= 0; i--)
			assertTrue(test.contains(i));
	}

	@Test
	/*
	 * To test if the ring buffer contains elements' function worked properly
	 */
	public void testisEmpty() {
		RingBufferFactory<Integer> f = new RingBufferFactory<Integer>();
		RingBuffer<Integer> test = (RingBuffer<Integer>) f.getSynchronizedBuffer(100);
		for (int i = 0; i < 100; i++)
			test.add(i);
		for (int i = 0; i < 100; i++)
			test.remove();
		assertTrue(test.isEmpty());
	}

	@Test
	/*
	 * To test if the ring buffer equals function worked properly
	 */
	public void testequals() {
		RingBufferFactory<Integer> f = new RingBufferFactory<Integer>();
		RingBuffer<Integer> test1 = (RingBuffer<Integer>) f.getSynchronizedBuffer(100);
		for (int i = 0; i < 100; i++)
			test1.add(i);
		test1.remove();
		RingBuffer<Integer> test2 = (RingBuffer<Integer>) f.getSynchronizedBuffer(100);
		for (int i = 0; i < 100; i++)
			test2.add(i);
		test2.remove();
		assertTrue(test1.equals(test2));
	}

	@Test
	/*
	 * To test if the ring buffer synchronizes properly
	 */
	public void testSynchronization() throws InterruptedException {
		RingBufferFactory<String> f = new RingBufferFactory<String>();
		RingBuffer<String> test = (RingBuffer<String>) f.getSynchronizedBuffer(10);
		test.add("a");
		assertTrue(test.size() == 1);
		Rput r1 = new Rput(test);
		Rtake r2 = new Rtake(test);
		for (int i = 0; i < 9; i++) {
			Thread t = new Thread(r1);
			try {
				t.sleep((long) (Math.random() * 100));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			t.run();
		}
		for (int i = 0; i < 9; i++) {
			Thread t = new Thread(r2);
			try {
				t.sleep((long) (Math.random() * 100));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			t.run();
		}
		assertTrue(test.size() == 1);
	}

	class Rput implements Runnable {
		RingBuffer<String> test;

		public Rput(RingBuffer<String> test) {
			this.test = test;
		}

		public void run() {
			try {
				test.put("AAA");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	class Rtake implements Runnable {
		RingBuffer<String> test;

		public Rtake(RingBuffer<String> test) {
			this.test = test;
		}

		public void run() {
			try {
				test.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

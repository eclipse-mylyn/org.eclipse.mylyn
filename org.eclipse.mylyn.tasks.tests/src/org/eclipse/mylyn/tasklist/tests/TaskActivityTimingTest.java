package org.eclipse.mylar.tasklist.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.internal.TaskActivityTimer;
import org.eclipse.mylar.tasklist.internal.TaskListManager;

/**
 * @author Mik Kersten (rewrite)
 */
public class TaskActivityTimingTest extends TestCase {

	private static final int TIMEOUT = 30;

	private static final int SLEEP = TIMEOUT * 10;
	
	protected TaskListManager manager = MylarTaskListPlugin.getTaskListManager();

	protected Task task1 = null;

	protected int originalActivityTimeout = -1;

	protected void setUp() throws Exception {
		super.setUp();
		task1 = new Task("t1", "t1", true);
		originalActivityTimeout = MylarPlugin.getContextManager().getInactivityTimeout();
		MylarPlugin.getContextManager().setInactivityTimeout(TIMEOUT);
	}

	public void tearDown() {
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		MylarPlugin.getContextManager().setInactivityTimeout(originalActivityTimeout);
	}

	public void testDeactivation() throws InterruptedException {
		assertEquals(0, task1.getElapsedTime());
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		assertEquals(0, task1.getElapsedTime());
		
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		Thread.sleep(SLEEP); 
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		assertTrue("elapsed: " + task1.getElapsedTime(), task1.getElapsedTime() >= SLEEP);
	}
	
	public void testTimerMap() throws InterruptedException {
		Task task0 = new Task("t0", "t0", true);
		manager.activateTask(task0);
		assertEquals(1, manager.getTimerMap().values().size());
		TaskActivityTimer timer0 = manager.getTimerMap().get(task0);
		assertTrue(timer0.isStarted());
		
		long elapsed = task1.getElapsedTime();
		assertEquals(0, elapsed);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		TaskActivityTimer timer1 = manager.getTimerMap().get(task1);
		assertEquals(1, manager.getTimerMap().values().size()); // previous task was deactivated
		assertTrue(timer1.isStarted());
		Thread.sleep(SLEEP); 
				
		elapsed = task1.getElapsedTime();
		assertTrue("should be bigger than timeout", elapsed > TIMEOUT);

		manager.deactivateTask(task1);
		assertFalse(timer1.isStarted());
		assertEquals(0, manager.getTimerMap().values().size());
		
		Thread.sleep(SLEEP); 
		long elapsedAfterDeactivation =  task1.getElapsedTime();
		assertTrue("should have accumulated some time: ", elapsedAfterDeactivation > elapsed);

		Thread.sleep(SLEEP); 
		Thread.sleep(SLEEP); 
		long elapsedAfterInactivity =  task1.getElapsedTime();
		assertEquals("no accumulation if task inactive", elapsedAfterDeactivation, elapsedAfterInactivity);
		
		manager.deactivateTask(task0);
		assertFalse(timer0.isStarted());
		assertEquals(0, manager.getTimerMap().values().size());
	}
	
	public void testElapsedTimeCapture() throws InterruptedException {
		long elapsed = task1.getElapsedTime();
		assertEquals(0, elapsed);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		Thread.sleep(SLEEP); 
		
		elapsed = task1.getElapsedTime();
		assertTrue("should be bigger than timeout", elapsed > TIMEOUT);
		
		Thread.sleep(SLEEP);
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		long elapsedAfterDeactivation =  task1.getElapsedTime();
		assertTrue("" + elapsedAfterDeactivation, elapsedAfterDeactivation > elapsed);

		Thread.sleep(SLEEP); 
		long elapsedAfterInactivity =  task1.getElapsedTime();
		assertEquals("no accumulation if task inactive", elapsedAfterDeactivation, elapsedAfterInactivity);
		
		MylarPlugin.getContextManager().setInactivityTimeout(SLEEP*2);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		Thread.sleep(SLEEP);
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		long elpasedAfterReactivation = task1.getElapsedTime();
		
		assertTrue("time: " + (elpasedAfterReactivation - elapsedAfterDeactivation), 
				elpasedAfterReactivation - elapsedAfterDeactivation >= SLEEP);
	}

	protected void mockInteraction() {
		MylarPlugin.getDefault().notifyInteractionObserved(
				new InteractionEvent(InteractionEvent.Kind.EDIT, "java", "A.java", "mock-id"));
	}
}

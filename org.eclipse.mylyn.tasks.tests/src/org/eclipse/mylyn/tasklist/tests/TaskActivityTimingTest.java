package org.eclipse.mylar.tasklist.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.internal.TaskListManager;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;

/**
 * @author Mik Kersten (rewrite)
 */
public class TaskActivityTimingTest extends TestCase {

	private static final int SLEEP = 250;

	private static final int TIMOUT = 50;

	protected TaskListManager manager = MylarTaskListPlugin.getTaskListManager();

//	protected TaskListView taskView = null;

	protected Task task1 = null;

	protected int originalActivityTimeout = -1;

	protected void setUp() throws Exception {
		super.setUp();
//		TaskListView.openInActivePerspective();
		assertNotNull(TaskListView.getDefault());
		task1 = new Task("t1", "t1", true);
		originalActivityTimeout = MylarPlugin.getContextManager().getInactivityTimeout();
		MylarPlugin.getContextManager().setInactivityTimeout(TIMOUT);
	}

	public void tearDown() {
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		MylarPlugin.getContextManager().setInactivityTimeout(originalActivityTimeout);
	}
	
	public void testElapsedTimeCapture() throws InterruptedException {
		long elapsed = task1.getElapsedTime();
		assertEquals(0, elapsed);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		Thread.sleep(SLEEP); 
		
		elapsed = task1.getElapsedTime();
		assertTrue("should be bigger than timeout", elapsed > TIMOUT);

		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		Thread.sleep(SLEEP); 
		long elapsedAfterDeactivation =  task1.getElapsedTime();
		assertTrue("should have accumulated some itme", elapsedAfterDeactivation > elapsed);

		Thread.sleep(SLEEP); 
		long elapsedAfterInactivity =  task1.getElapsedTime();
		assertEquals("no accumulation if task inactive", elapsedAfterDeactivation, elapsedAfterInactivity);
		
		MylarPlugin.getContextManager().setInactivityTimeout(SLEEP*2);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		Thread.sleep(250);
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		long elpasedAfterReactivation = task1.getElapsedTime();
		
		assertTrue("time: " + (elpasedAfterReactivation - elapsedAfterDeactivation), 
				elpasedAfterReactivation - elapsedAfterDeactivation >= 250);
	}

	protected void mockInteraction() {
		MylarPlugin.getDefault().notifyInteractionObserved(
				new InteractionEvent(InteractionEvent.Kind.EDIT, "java", "A.java", "mock-id"));
	}
}

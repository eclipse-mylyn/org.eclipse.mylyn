package org.eclipse.mylar.monitor.tests;

import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.mylar.tasklist.TaskListManager;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasklist.ui.actions.TaskDeactivateAction;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.PartInitException;

/**
 * Checks that task active time figures are within reasonable bounds.
 * 
 * @author Wesley Coelho
 */
public class TaskTimerTest extends TestCase {

	protected TaskListManager manager = MylarTasklistPlugin.getTaskListManager(); 
	protected TaskListView taskView = null;
	protected Task task1 = null;
	protected int originalActivityTimeout = -1;
		
	protected void setUp() throws Exception {
		super.setUp();
		
		try {
			MylarTasklistPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.mylar.tasks.ui.views.TaskListView");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("View not initialized");
		}
		
		assertNotNull(TaskListView.getDefault());  
		taskView = TaskListView.getDefault();
		
		originalActivityTimeout = MylarPlugin.getContextManager().getActivityTimeoutSeconds();
		
		//Must be set before the task is created
		MylarPlugin.getContextManager().setActivityTimeoutSeconds(1);
		
		task1 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 1", true);
		manager.addRootTask(task1);
		
		
		
	}
		
	
	public void testTaskTime(){
	
		//This set of parameters runs quickly but has a relatively high fudge factor
		//See parameters below for a longer test that can have a low fudge factor
		
		//Note: ActivityTimeout is set to 1 second in setup()
		long fudgeFactor = 2000; //Maximum time it takes for the inactivity timer to notice plus a fudge factor for computation time
		long sleepTime = 500; //Must be less than ActivityTimeout
		long activityTimeoutTime = 1500; //Should be X * activity timeout
		long notCountedTime = 1000; 
		
		
		//Here's a slower test with higher times so that the fudge factor
		//can be relatively small compared to the desired task time
		//Set ActivityTimeoutSeconds to 10 in setup() above to use these parameters

//		//Note: ActivityTimeout is set to be set to 10 seconds in setup() above
//		long fudgeFactor = 2000; //Maximum time it takes for the inactivity timer to notice plus a fudge factor for computation time
//		long sleepTime = 4000; //Must be less than ActivityTimeout
//		long activityTimeoutTime = 11000; //Should be around 1.X * activity timeout
//		long notCountedTime = 20000; 
		
		long startTime = GregorianCalendar.getInstance().getTimeInMillis();
		(new TaskActivateAction()).run(task1);
		
		try {
			Thread.sleep(sleepTime); //this time should be counted
			
			assertTrue(task1.getElapsedTimeLong() >= sleepTime);
			
			mockInteraction();

			Thread.sleep(activityTimeoutTime); //should not be counted

			Thread.sleep(notCountedTime); //should not be counted
			
			//At this point the task should have "stalled," causing the activityTimeoutTime
			//to be subtracted.
			
			long time1 = task1.getElapsedTimeLong();
			assertTrue(time1 >= sleepTime);
			assertTrue(time1 < sleepTime + fudgeFactor);
						
			mockInteraction();
			
			Thread.sleep(sleepTime); //This time should be counted
			
			assertTrue(task1.getElapsedTimeLong() >= sleepTime + sleepTime);
			assertTrue(task1.getElapsedTimeLong() < sleepTime + sleepTime + fudgeFactor);
	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		(new TaskDeactivateAction()).run(task1);
		long endTime = GregorianCalendar.getInstance().getTimeInMillis();
		
		//Sleep after the task has been deactivated to make sure it isn't still counting
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long totalReportedTime = task1.getElapsedTimeLong();

		//Check that the reported time is less than the timestamp upper bound times
		assertTrue(totalReportedTime <= endTime - startTime);
		
		//Check that reported time is greater than the wait times that should be added
		assertTrue(totalReportedTime >= sleepTime + sleepTime);
		
		//Check that it didn't count too much time
		assertTrue(totalReportedTime < sleepTime + sleepTime + fudgeFactor);

	}
	
	protected void mockInteraction(){
		MylarPlugin.getDefault().notifyInteractionObserved(
				new InteractionEvent(InteractionEvent.Kind.EDIT, "java", "A.java", JavaUI.ID_PACKAGES)
    	);
	}
	
	
	public void tearDown(){
		MylarPlugin.getContextManager().setActivityTimeoutSeconds(this.originalActivityTimeout);
	}
	
}

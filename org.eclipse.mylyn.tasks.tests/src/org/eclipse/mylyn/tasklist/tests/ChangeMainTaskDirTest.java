package org.eclipse.mylar.tasklist.tests;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.internal.TaskListManager;

/**
 * Tests changes to the main mylar data directory location.
 * 
 * @author Wesley Coelho
 */
public class ChangeMainTaskDirTest extends TestCase{

	private File newMainDataDir = null;
	private String originalMainDataDir = null;
	private TaskListManager manager = MylarTaskListPlugin.getTaskListManager(); 
	
	protected void setUp() throws Exception {
		super.setUp();
		
		//Get the original main data directory so that it can be reset later
		originalMainDataDir = MylarPlugin.getDefault().getDataDirectory();
		
		//Create test main data directories to use
		newMainDataDir = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator + "TestMainDir1");
		newMainDataDir.mkdir();
		assertTrue(newMainDataDir.exists());	
		
	}
	
	/**
	 * Tests moving the main mylar data directory to another location
	 * (Without copying existing data to the new directory)
	 */
	public void testChangeMainDataDir(){

		//Create a task  in the main dir and context with an interaction event to be saved
		ITask mainDataDirTask = createAndSaveTask("Main Task");
		
		//Switch task directory
		switchMainTaskDirectory(newMainDataDir.getPath());
		
		//Check that the main data dir task isn't in the list or the folder
		File taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator + mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
		assertFalse(taskFile.exists());
		assertNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(), false));
		
		//Check that a newly created task appears in the right place (method will check)
		ITask newDataDirTask = createAndSaveTask("New Data Dir");
		taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator + newDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
		assertTrue(taskFile.exists());
		
		//Check for other the tasklist file in the new dir
		File destTaskListFile =  new File(MylarPlugin.getDefault().getDataDirectory() + File.separator + MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE);	
		assertTrue(destTaskListFile.exists());
		
		//Switch back to the main task directory
		switchMainTaskDirectory(originalMainDataDir);
		
		//Check that the previously created main dir task is in the task list and its file exists
		assertNotNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(), false));
		taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator + mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
		assertTrue(taskFile.exists());
		
	}
	
	
	/**
	 * Creates a task with an interaction event and checks that it has
	 * been properly saved in the currently active data directory
	 */
	protected ITask createAndSaveTask(String taskName){
		
		//Create the task and add it to the root of the task list
		ITask newTask = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), taskName, true);
		manager.moveToRoot(newTask);
		MylarContext mockContext = MylarPlugin.getContextManager().loadContext(newTask.getHandleIdentifier(), newTask.getContextPath());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT,"structureKind","handle","originId");
		mockContext.parseEvent(event);
		MylarPlugin.getContextManager().contextActivated(mockContext);

		//Save the context file and check that it exists
		MylarPlugin.getContextManager().saveContext(mockContext.getId(), newTask.getContextPath());
		File taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator + newTask.getContextPath() + MylarContextManager.FILE_EXTENSION);
		assertTrue(MylarPlugin.getContextManager().hasContext(newTask.getContextPath()));
		assertTrue(taskFile.exists());			
		
		return newTask;
	}
	
	
	/** Copy (almost) of code that changes the task directory (MylarTaskListPreferencePage) */
	protected void switchMainTaskDirectory(String newDir){
		//Order matters:
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
		MylarPlugin.getDefault().getPreferenceStore().setValue(MylarPlugin.PREF_DATA_DIR, newDir);
		MylarTaskListPlugin.getDefault().setDataDirectory(MylarPlugin.getDefault().getDataDirectory());
	}
	
	protected void tearDown() throws Exception{
		
		//Delete the test destination folder
		
		File[] files = newMainDataDir.listFiles();
		for (File file : files) {
			file.delete();
		}
		
		newMainDataDir.delete();
		assertFalse(newMainDataDir.exists());
		
		super.tearDown();
	}
	
}

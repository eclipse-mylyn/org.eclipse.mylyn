package org.eclipse.mylar.tasklist.tests;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.mylar.tasklist.TaskListManager;
import org.eclipse.mylar.tasklist.ui.wizards.TaskDataExportWizard;
import org.eclipse.mylar.tasklist.ui.wizards.TaskDataExportWizardPage;
import org.eclipse.swt.widgets.Shell;

/**
 * Test case for the Task Export Wizard
 * 
 * @author Wesley Coelho
 */
public class TaskDataExportTest extends TestCase{

	private TaskDataExportWizard wizard = null;
	private TaskDataExportWizardPage wizardPage = null;
	private File destinationDir = null;
	private ITask task1 = null;
	private TaskListManager manager = MylarTasklistPlugin.getTaskListManager(); 
	
	protected void setUp() throws Exception {
		super.setUp();
		
		//Create the export wizard
		wizard = new TaskDataExportWizard();
		wizard.addPages();
		wizard.createPageControls(new Shell());
		wizardPage = (TaskDataExportWizardPage) wizard.getPage(TaskDataExportWizardPage.PAGE_NAME);
		assertNotNull(wizardPage);
		
		//Create test export destination directory
		destinationDir = new File(MylarPlugin.getDefault().getMylarDataDirectory() + File.separator + "TestDir");
		destinationDir.mkdir();
		assertTrue(destinationDir.exists());
		
		//Create a task and context with an interaction event to be saved
		task1 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "Export Test Task", true);
		manager.addRootTask(task1);
		MylarContext mockContext = MylarPlugin.getContextManager().loadContext(task1.getHandleIdentifier(), task1.getPath());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT,"structureKind","handle","originId");
		mockContext.parseEvent(event);
		MylarPlugin.getContextManager().contextActivated(mockContext);

		//Save the context file and check that it exists
		MylarPlugin.getContextManager().saveContext(mockContext.getId(), task1.getPath());
		File taskFile = new File(MylarPlugin.getDefault().getMylarDataDirectory() + File.separator + task1.getPath() + MylarContextManager.FILE_EXTENSION);
		assertTrue(MylarPlugin.getContextManager().hasContext(task1.getPath()));
		assertTrue(taskFile.exists());
	}

	/** Tests the wizard when it has been asked to export all task data to a zip file*/
	public void testExportAllToZip(){
		
		//Set parameters in the wizard to simulate a user setting them and clicking "Finish"
		wizardPage.setParameters(true, true, true, true, true, destinationDir.getPath());
		wizard.performFinish();
		
		//Check that the task list file was exported
		File destZipFile =  new File(destinationDir + File.separator
				+ TaskDataExportWizard.ZIP_FILE_NAME);	
		assertTrue(destZipFile.exists());
	}

	/** Tests the wizard when it has been asked to export all task data */
	public void testExportAll(){
		
		//Set parameters in the wizard to simulate a user setting them and clicking "Finish"
		wizardPage.setParameters(true, true, true, true, false, destinationDir.getPath());
		wizard.performFinish();
		
		//Check that the task list file was exported
		File destTaskListFile =  new File(destinationDir + File.separator
				+ MylarTasklistPlugin.DEFAULT_TASK_LIST_FILE);	
		assertTrue(destTaskListFile.exists());
		
		//Check that the activity history file was exported
		File destActivationHistoryFile = new File(destinationDir
				+ File.separator
				+ MylarContextManager.CONTEXT_HISTORY_FILE_NAME
				+ MylarContextManager.FILE_EXTENSION);
		assertTrue(destActivationHistoryFile.exists());
		
		//Check that the task context file created in setUp() was exported
		File destTaskContextFile = new File(destinationDir + File.separator + task1.getPath() + MylarContextManager.FILE_EXTENSION);
		assertTrue(destTaskContextFile.exists());
	}
	
	protected void tearDown() throws Exception{
		
		//Delete the test destination folder
		
		File[] files = destinationDir.listFiles();
		for (File file : files) {
			file.delete();
		}
		
		destinationDir.delete();
		assertFalse(destinationDir.exists());
		
		super.tearDown();
	}
}

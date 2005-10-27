package org.eclipse.mylar.tasklist.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListCategory;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.internal.TaskList;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for exporting tasklist data files to the file system. This wizard uses
 * a single page: TaskDataExportWizardPage
 * 
 * @author Wesley Coelho
 */
public class TaskDataExportWizard extends Wizard implements IExportWizard {

	/**
	 * The name of the dialog store's section associated with the task data
	 * export wizard
	 */
	private final static String SETTINGS_SECTION = "org.eclipse.mylar.tasklist.ui.exportWizard";

	private final static String WINDOW_TITLE = "Export";

	private TaskDataExportWizardPage exportPage = null;

	public TaskDataExportWizard() {
		IDialogSettings masterSettings = MylarTasklistPlugin.getDefault()
				.getDialogSettings();
		setDialogSettings(getSettingsSection(masterSettings));
		setNeedsProgressMonitor(true); // ?
		setWindowTitle(WINDOW_TITLE);
	}

	/**
	 * Finds or creates a dialog settings section that is used to make the
	 * dialog control settings persistent
	 */
	public IDialogSettings getSettingsSection(IDialogSettings master) {
		IDialogSettings settings = master.getSection(SETTINGS_SECTION);
		if (settings == null) {
			settings = master.addNewSection(SETTINGS_SECTION);
		}
		return settings;
	}

	public void addPages() {
		exportPage = new TaskDataExportWizardPage();
		exportPage.setWizard(this);
		addPage(exportPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no initialization needed
	}

	public boolean canFinish() {
		return exportPage.isPageComplete();
	}

	/**
	 * Called when the user clicks finish. Saves the task data.
	 * Waits until all overwrite decisions have been made before
	 * starting to save files. If any overwrite is canceled, no
	 * files are saved and the user must adjust the dialog.
	 */
	public boolean performFinish() {
		boolean overwrite = exportPage.overwrite();
		
		//Get file paths to check for existence
		String destDir = exportPage.getDestinationDirectory();
		File destTaskListFile = new File(destDir + File.separator
				+ MylarTasklistPlugin.DEFAULT_TASK_LIST_FILE);		
		File destActivationHistoryFile = new File(destDir
				+ File.separator
				+ MylarContextManager.CONTEXT_HISTORY_FILE_NAME
				+ MylarContextManager.FILE_EXTENSION);
		
		
		//Prompt the user to confirm if ANY of the save operations will cause an overwrite
		if(!overwrite){
			if (exportPage.exportTaskList() && destTaskListFile.exists()){
				if (!MessageDialog.openConfirm(getShell(), "Confirm File Replace", "The task list file " + destTaskListFile.getPath() + " already exists. Do you want to overwrite it?")){
					return false;
				}
			}
			
			if (exportPage.exportActivationHistory() && destActivationHistoryFile.exists()){
				if (!MessageDialog.openConfirm(getShell(), "Confirm File Replace", "The task activation history file " + destActivationHistoryFile.getPath() + " already exists. Do you want to overwrite it?")){
					return false;
				}		
			}
			
			if (exportPage.exportTaskContexts()) {
				 for(ITask task : getAllTasks())
				 {
					File destTaskFile = new File(destDir + File.separator + task.getPath() + MylarContextManager.FILE_EXTENSION);
					if (destTaskFile.exists()){
						if(!MessageDialog.openConfirm(getShell(), "Confirm File Replace", "Task context files already exist in " + destDir + ". Do you want to overwrite them?")){
							return false;
						}
						else{
							break;
						}
					}
				}
			}			
		}
		
		//Save the files
		
		if (exportPage.exportTaskList()) {
			MylarTasklistPlugin.getTaskListManager().saveTaskList();
			
			String sourceTaskListPath = MylarPlugin.getDefault()
					.getMylarDataDirectory()
					+ File.separator
					+ MylarTasklistPlugin.DEFAULT_TASK_LIST_FILE;
			File sourceTaskListFile = new File(sourceTaskListPath);

			if (!copy(sourceTaskListFile, destTaskListFile)) {
				MylarPlugin.fail(new Exception("Export Exception"), "Could not export task list file.", false);
			}	
		}

		if (exportPage.exportActivationHistory()) {
			try {
				MylarPlugin.getContextManager().saveActivityHistoryContextFileCopy(destActivationHistoryFile);
			} catch (RuntimeException e) {
				MylarPlugin.fail(e, "Could not export activity history context file", true);
			}
		}

		if (exportPage.exportTaskContexts()) {
			boolean errorDisplayed = false; //Prevent many repeated error messages
			 for(ITask task : getAllTasks())
			 {
				
				if (!MylarPlugin.getContextManager().hasContext(task.getPath())){
					continue; //Tasks without a context have no file to copy
				}
				 
				File destTaskFile = new File(destDir + File.separator + task.getPath() + MylarContextManager.FILE_EXTENSION);
				File sourceTaskFile = new File(MylarPlugin.getDefault().getMylarDataDirectory() + File.separator + task.getPath() + MylarContextManager.FILE_EXTENSION);
				if (!copy(sourceTaskFile, destTaskFile) && !errorDisplayed){
					MylarPlugin.fail(new Exception("Export Exception: " + sourceTaskFile.getPath() + " -> " + destTaskFile.getPath()), "Could not export one or more task context files.", true);
					errorDisplayed = true;
				}
			}
		}

		exportPage.saveSettings();
		return true;
	}
	
	/** Returns all tasks in the task list root or a category in the task list */
	protected List<ITask> getAllTasks(){
		List<ITask> allTasks = new ArrayList<ITask>();
		TaskList taskList = MylarTasklistPlugin.getTaskListManager().getTaskList();

		allTasks.addAll(taskList.getRootTasks());
		
		for (ITaskListCategory category : taskList.getCategories()){
			allTasks.addAll(category.getChildren());
		}
		
		return allTasks;
	}
	
	// Note: Copied from MylarTaskListPlugin
	private boolean copy(File src, File dst) {
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}

}

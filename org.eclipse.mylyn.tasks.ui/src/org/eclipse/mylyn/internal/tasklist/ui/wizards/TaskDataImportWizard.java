/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Rob Elves 
 * Some code leveraged from TaskDataExportWizard
 */
public class TaskDataImportWizard extends Wizard implements IImportWizard {

	private final static String SETTINGS_SECTION = "org.eclipse.mylar.tasklist.ui.importWizard";

	private final static String WINDOW_TITLE = "Import";

	private TaskDataImportWizardPage importPage = null;

	public TaskDataImportWizard() {
		super();
		IDialogSettings masterSettings = MylarTaskListPlugin.getDefault().getDialogSettings();
		setDialogSettings(getSettingsSection(masterSettings));
		setNeedsProgressMonitor(true);
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
		importPage = new TaskDataImportWizardPage();
		importPage.setWizard(this);
		addPage(importPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no initialization needed
	}

	public boolean canFinish() {
		return importPage.isPageComplete();
	}

	/**
	 * Called when the user clicks finish. Saves the task data. Waits until all
	 * overwrite decisions have been made before starting to save files. If any
	 * overwrite is canceled, no files are saved and the user must adjust the
	 * dialog.
	 */
	public boolean performFinish() {
		
		MylarTaskListPlugin.getTaskListManager().deactivateTask(MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTask());
		
		File sourceDirFile = null;
		File sourceZipFile = null;
		File sourceTaskListFile = null;
		File sourceActivationHistoryFile = null;
		List<File> contextFiles = new ArrayList<File>();
		List<String> zipFilesToExtract = new ArrayList<String>();
		boolean overwrite = importPage.overwrite();
		boolean zip = importPage.zip();

		if (zip) {

			String sourceZip = importPage.getSourceZipFile();
			sourceZipFile = new File(sourceZip);

			if (!sourceZipFile.exists()) {
				MessageDialog
						.openError(getShell(), "File not found", sourceZipFile.toString() + " could not be found.");
				return false;
			}

			Enumeration entries;
			ZipFile zipFile;

			try {
				zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
				entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = (ZipEntry) entries.nextElement();

					if (entry.isDirectory()) {
						// ignore directories (shouldn't be any)
						continue;
					}
					if (!importPage.importTaskList()
							&& entry.getName().endsWith(MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE)) {
						continue;
					}
					if (!importPage.importActivationHistory()
							&& entry.getName().endsWith(
									MylarContextManager.CONTEXT_HISTORY_FILE_NAME
											+ MylarContextManager.CONTEXT_FILE_EXTENSION)) {
						continue;
					}
					if (!importPage.importTaskContexts()
							&& entry.getName().matches(".*-\\d*" + MylarContextManager.CONTEXT_FILE_EXTENSION + "$")) {
						continue;
					}

					File destContextFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
							+ entry.getName());

					if (!overwrite && destContextFile.exists()) {
						if (MessageDialog.openConfirm(getShell(), "File exists!", "Overwrite existing file?\n"
								+ destContextFile.getName())) {
							zipFilesToExtract.add(entry.toString());
						} else {
							// no overwrite
						}
					} else {
						zipFilesToExtract.add(entry.toString());
					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// Get file paths to check for existence
			String sourceDir = importPage.getSourceDirectory();
			sourceDirFile = new File(sourceDir);
			if (!sourceDirFile.exists() || !sourceDirFile.isDirectory()) {		
				MessageDialog.openError(getShell(), "Location not found", sourceZipFile.toString() + " could not be found or is not a folder.");		
				return false;
			}

			// make sure selected files for import are there
			sourceTaskListFile = new File(sourceDir + File.separator + MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE);
			sourceActivationHistoryFile = new File(sourceDir + File.separator
					+ MylarContextManager.CONTEXT_HISTORY_FILE_NAME + MylarContextManager.CONTEXT_FILE_EXTENSION);

			File[] children = sourceDirFile.listFiles();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getAbsolutePath().matches(".*-\\d*" + MylarContextManager.CONTEXT_FILE_EXTENSION + "$")) {

					File destContextFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
							+ children[i].getName());

					if (!overwrite && destContextFile.exists()) {
						if (MessageDialog.openConfirm(getShell(), "Context exists!",
								"Overwrite existing task context?\n" + destContextFile.getName())) {
							contextFiles.add(children[i]);
						} else {
							// no overwrite
						}
					} else {
						contextFiles.add(children[i]);
					}
				}

			}

			if (importPage.importTaskList() && !sourceTaskListFile.exists()) {
				MessageDialog.openError(getShell(), "File not found", sourceTaskListFile.toString() + " not found.");
				return false;
			} else if (importPage.importActivationHistory() && !sourceActivationHistoryFile.exists()) {
				MessageDialog.openError(getShell(), "File not found", sourceActivationHistoryFile.toString()
						+ " not found.");
				return false;
			}

		}

		FileCopyJob job = new FileCopyJob(sourceDirFile, sourceZipFile, sourceTaskListFile,
				sourceActivationHistoryFile, contextFiles, zipFilesToExtract);

		IProgressService service = PlatformUI.getWorkbench().getProgressService();

		try {
			service.run(true, false, job);
		} catch (InvocationTargetException e) {
			MylarStatusHandler.fail(e, "Could not import files", true);
		} catch (InterruptedException e) {
			MylarStatusHandler.fail(e, "Could not import files", true);
		}

		importPage.saveSettings();
		return true;
	}

	/** Job that performs the file copying and zipping */
	class FileCopyJob implements IRunnableWithProgress {

		private static final String JOB_LABEL = "Importing Data";

		private File sourceZipFile = null;

		private File sourceTaskListFile = null;

		private File sourceActivationHistoryFile = null;

		private boolean zip;

		private boolean importTaskList;

		private boolean importActivationHistory;

		private boolean importTaskContexts;

		private List<File> sourceContextFiles;

		private List<String> zipFilesToExtract;

		public FileCopyJob(File sourceFolder, File sourceZipFile, File sourceTaskListFile,
				File sourceActivationHistoryFile, List<File> contextFiles, List<String> zipFiles) {

			this.sourceZipFile = sourceZipFile;
			this.sourceTaskListFile = sourceTaskListFile;
			this.sourceActivationHistoryFile = sourceActivationHistoryFile;
			this.sourceContextFiles = contextFiles;
			this.zipFilesToExtract = zipFiles;

			// Get parameters here to avoid accessing the UI thread
			this.zip = importPage.zip();
			this.importTaskList = importPage.importTaskList();
			this.importActivationHistory = importPage.importActivationHistory();
			this.importTaskContexts = importPage.importTaskContexts();

		}

		public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

			if (zip) {
				monitor.beginTask(JOB_LABEL, zipFilesToExtract.size() + 2);
				ZipFile zipFile;

				try {
					zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);

					for (String zipFileStr : zipFilesToExtract) {
						ZipEntry entry = zipFile.getEntry(zipFileStr);
						if (entry == null) {
							MylarStatusHandler.fail(new Exception("Import Exception"),
									"Problem occured extracting from zip file.", true);
							return;
						}

						File destinationFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
								+ entry.getName());
						if (destinationFile.exists()) {
							destinationFile.delete();
						}

						copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(
								destinationFile)));
						monitor.worked(1);

					}
					zipFile.close();
				} catch (IOException ioe) {
					MylarStatusHandler.fail(new Exception("Import Exception"),
							"Problem occured extracting from zip file.", true);
					return;
				}
				readTaskList();
				monitor.done();
				return;
			}

			if (importTaskList) {
				monitor.beginTask(JOB_LABEL, sourceContextFiles.size() + 2);
				String destTaskListPath = MylarPlugin.getDefault().getDataDirectory() + File.separator
						+ MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE;
				File destTaskListFile = new File(destTaskListPath);

				if (destTaskListFile.exists()) {
					destTaskListFile.delete();
				}

				if (!copy(sourceTaskListFile, destTaskListFile)) {
					MylarStatusHandler
							.fail(new Exception("Import Exception"), "Could not import task list file.", true);
				}
				monitor.worked(1);

			}

			if (importActivationHistory) {
				try {
					File destActivationHistoryFile = new File(MylarPlugin.getDefault().getDataDirectory()
							+ File.separator + MylarContextManager.CONTEXT_HISTORY_FILE_NAME
							+ MylarContextManager.CONTEXT_FILE_EXTENSION);

					if (destActivationHistoryFile.exists()) {
						destActivationHistoryFile.delete();
					}

					copy(sourceActivationHistoryFile, destActivationHistoryFile);
					monitor.worked(1);

				} catch (RuntimeException e) {
					MylarStatusHandler.fail(e, "Could not import activity history context file", true);
				}
			}

			if (importTaskContexts) {
				boolean errorDisplayed = false;
				for (File sourceContextFile : sourceContextFiles) {

					File destContextFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
							+ sourceContextFile.getName());

					if (destContextFile.exists()) {
						destContextFile.delete();
					}

					if (!copy(sourceContextFile, destContextFile) && !errorDisplayed) {
						MylarStatusHandler.fail(new Exception("Import Exception: " + sourceContextFile.getPath()
								+ " -> " + destContextFile.getPath()),
								"Could not import one or more task context files.", true);
						errorDisplayed = true;
					}
					monitor.worked(1);
				}
			}
			readTaskList();
			monitor.done();
		}
	}

	/** Returns all tasks in the task list root or a category in the task list */
	protected List<ITask> getAllTasks() {
		List<ITask> allTasks = new ArrayList<ITask>();
		TaskList taskList = MylarTaskListPlugin.getTaskListManager().getTaskList();

		allTasks.addAll(taskList.getRootTasks());

		for (AbstractTaskContainer category : taskList.getCategories()) {
			allTasks.addAll(category.getChildren());
		}

		return allTasks;
	}

	private boolean copy(File src, File dst) {

		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			return copyInputStream(in, new BufferedOutputStream(out));
		} catch (FileNotFoundException e) {
			return false;
		}

	}

	private boolean copyInputStream(InputStream inputStream, BufferedOutputStream stream) {
		try {
			InputStream in = inputStream;
			OutputStream out = stream;

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

	private void readTaskList() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				MylarTaskListPlugin.getTaskListManager().readExistingOrCreateNewList();
			}
		});
	}

}

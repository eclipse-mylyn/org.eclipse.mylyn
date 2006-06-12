/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasklist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * Class to persist the data for the offline reports list
 */
public class OfflineTaskManager {

	/** The file that the offline reports are written to */
	private File file;

	/** A list of offline reports */
	private ArrayList<RepositoryTaskData> list = new ArrayList<RepositoryTaskData>();

	/** The bug id of the most recently created offline report. */
	protected int latestNewBugId = 0;

	//private boolean updateLocalCopy = false;

	/**
	 * Constructor that reads the offline reports data persisted in the plugin's
	 * state directory, if it exists.
	 * 
	 * @param file
	 *            The file where the offline reports are persisted
	 * @throws IOException
	 *             Error opening or closing the offline reports file
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 *             Error deserializing objects from the offline reports file
	 */
	public OfflineTaskManager(File file, boolean read) throws IOException, ClassNotFoundException {
		this.file = file;
		if (read && file.exists()) {
			readFile();
		}
	}
	
	
	/**
	 * Add a RepositoryTaskData to the offline reports file
	 * 
	 * @param entry
	 *            The bug to add
	 */
	public void add(final RepositoryTaskData entry) throws CoreException {
		int index = -1;
		if ((index = find(entry.getRepositoryUrl(), entry.getId())) >= 0) {

			list.remove(index);

			list.add(entry);
			writeFile();

		} else {
			list.add(entry);
			writeFile();
		}
	}
	
	
// /**
// * Add an offline report to the offline reports list
//	 * 
//	 * @param entry
//	 *            The bug to add
//	 */
//	public RepositoryTaskSyncState add(final RepositoryTaskData entry, boolean forceSync) throws CoreException {
//
//		RepositoryTaskSyncState status = RepositoryTaskSyncState.SYNCHRONIZED;
////
////		try {
////
////			String handle = AbstractRepositoryTask.getHandle(entry.getRepositoryUrl(), entry.getId());
////			ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
////
////			if (task != null && task instanceof AbstractRepositoryTask) {
////				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
////
////				TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
////						repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());
////
////				if (repository == null) {
////					throw new Exception("No repository associated with task. Unable to retrieve timezone information.");
////				}
////
////				TimeZone repositoryTimeZone = DateUtil.getTimeZone(repository.getTimeZoneId());
//
//				int index = -1;
//				if ((index = find(entry.getRepositoryUrl(), entry.getId())) >= 0) {
//					//RepositoryTaskData oldBug = list.get(index);
//
////					if (repositoryTask.getLastSynchronized() == null
////							|| entry.getLastModified(repositoryTimeZone)
////									.compareTo(repositoryTask.getLastSynchronized()) > 0 || forceSync) {
////
////						if (oldBug.hasChanges()) {
////
////							PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
////								public void run() {
////									updateLocalCopy = MessageDialog
////											.openQuestion(
////													null,
////													"Update Local Copy",
////													"Local copy of Report "
////															+ entry.getId()
////															+ " on "
////															+ entry.getRepositoryUrl()
////															+ " has changes.\nWould you like to override local changes? \n\nNote: if you select No, only the new comment will be saved with the updated bug, all other changes will be lost.");
////								}
////							});
////
////							if (!updateLocalCopy) {
////								((RepositoryTaskData) entry).setNewComment(((RepositoryTaskData) oldBug).getNewComment());
////								((RepositoryTaskData) entry).setHasChanged(true);
////								status = RepositoryTaskSyncState.CONFLICT;
////							} else {
////								((RepositoryTaskData) entry).setHasChanged(false);
////								status = RepositoryTaskSyncState.SYNCHRONIZED;
////							}
////						} else {
////							if (forceSync) {
////								status = RepositoryTaskSyncState.SYNCHRONIZED;
////							} else {
////								status = RepositoryTaskSyncState.INCOMING;
////							}
////						}
//						list.remove(index);
////						if (entry.hasChanges() && status != RepositoryTaskSyncState.CONFLICT) {
////							status = RepositoryTaskSyncState.OUTGOING;
////						}
//						list.add(entry);
//						writeFile();
//					
//				} else {
//					// report doesn't exist in offline reports
//					list.add(entry);
//					writeFile();
//				}
//				//repositoryTask.setLastSynchronized(entry.getLastModified(repositoryTimeZone));
////	}
////		} catch (Exception e) {
////			e.printStackTrace();
////			IStatus runtimestatus = new Status(IStatus.ERROR, MylarTaskListPlugin.PLUGIN_ID, IStatus.OK,
////					"failed to add offline report", e);
////			throw new CoreException(runtimestatus);
////		}
//		return status;
//	}
	
	
	
//	/**
//	 * Add an offline report to the offline reports list
//	 * 
//	 * @param entry
//	 *            The bug to add
//	 */
//	public RepositoryTaskSyncState add(final RepositoryTaskData entry, boolean forceSync) throws CoreException {
//
//		RepositoryTaskSyncState status = RepositoryTaskSyncState.SYNCHRONIZED;
//
//		try {
//
//			String handle = AbstractRepositoryTask.getHandle(entry.getRepositoryUrl(), entry.getId());
//			ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
//
//			if (task != null && task instanceof AbstractRepositoryTask) {
//				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
//
//				TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
//						repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());
//
//				if (repository == null) {
//					throw new Exception("No repository associated with task. Unable to retrieve timezone information.");
//				}
//
//				TimeZone repositoryTimeZone = DateUtil.getTimeZone(repository.getTimeZoneId());
//
//				int index = -1;
//				if ((index = find(entry.getRepositoryUrl(), entry.getId())) >= 0) {
//					RepositoryTaskData oldBug = list.get(index);
//
//					if (repositoryTask.getLastSynchronized() == null
//							|| entry.getLastModified(repositoryTimeZone)
//									.compareTo(repositoryTask.getLastSynchronized()) > 0 || forceSync) {
//
//						if (oldBug.hasChanges()) {
//
//							PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
//								public void run() {
//									updateLocalCopy = MessageDialog
//											.openQuestion(
//													null,
//													"Update Local Copy",
//													"Local copy of Report "
//															+ entry.getId()
//															+ " on "
//															+ entry.getRepositoryUrl()
//															+ " has changes.\nWould you like to override local changes? \n\nNote: if you select No, only the new comment will be saved with the updated bug, all other changes will be lost.");
//								}
//							});
//
//							if (!updateLocalCopy) {
//								((RepositoryTaskData) entry).setNewComment(((RepositoryTaskData) oldBug).getNewComment());
//								((RepositoryTaskData) entry).setHasChanged(true);
//								status = RepositoryTaskSyncState.CONFLICT;
//							} else {
//								((RepositoryTaskData) entry).setHasChanged(false);
//								status = RepositoryTaskSyncState.SYNCHRONIZED;
//							}
//						} else {
//							if (forceSync) {
//								status = RepositoryTaskSyncState.SYNCHRONIZED;
//							} else {
//								status = RepositoryTaskSyncState.INCOMING;
//							}
//						}
//						list.remove(index);
//						if (entry.hasChanges() && status != RepositoryTaskSyncState.CONFLICT) {
//							status = RepositoryTaskSyncState.OUTGOING;
//						}
//						list.add(entry);
//						writeFile();
//					}
//				} else {
//					// report doesn't exist in offline reports
//					list.add(entry);
//					writeFile();
//				}
//				repositoryTask.setLastSynchronized(entry.getLastModified(repositoryTimeZone));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			IStatus runtimestatus = new Status(IStatus.ERROR, MylarTaskListPlugin.PLUGIN_ID, IStatus.OK,
//					"failed to add offline report", e);
//			throw new CoreException(runtimestatus);
//		}
//		return status;
//	}

	// DO NOT REMOVE
	// /**
	// * Add an offline report to the offline reports list
	// *
	// * @param entry
	// * The bug to add
	// */
	// public BugzillaOfflineStatus add(BugzillaReport entry, boolean
	// saveChosen) throws CoreException {
	//
	// BugzillaOfflineStatus status = BugzillaOfflineStatus.SAVED;
	//
	// try {
	//
	// // check for bug and do a compare
	// int index = -1;
	// if ((index = find(entry.getRepositoryUrl(), entry.getId())) >= 0) {
	// BugzillaReport oldBug = list.get(index);
	// if (oldBug instanceof BugzillaReport && entry instanceof BugzillaReport
	// && !saveChosen) {
	// CompareConfiguration config = new CompareConfiguration();
	// config.setLeftEditable(false);
	// config.setRightEditable(false);
	// config.setLeftLabel("Local Bug Report");
	// config.setRightLabel("Remote Bug Report");
	// config.setLeftImage(PlatformUI.getWorkbench().getSharedImages().getImage(
	// ISharedImages.IMG_OBJ_ELEMENT));
	// config.setRightImage(PlatformUI.getWorkbench().getSharedImages().getImage(
	// ISharedImages.IMG_OBJ_ELEMENT));
	// final BugzillaCompareInput in = new BugzillaCompareInput(config);
	// oldBug.setOfflineState(true);
	// in.setLeft((BugzillaReport) oldBug);
	// in.setRight((BugzillaReport) entry);
	// in.setTitle("Bug #" + oldBug.getId());
	//
	// try {
	// in.run(null);
	// // running in new job inside a job causes shell to popup
	// // up, losing focus
	// // PlatformUI.getWorkbench().getProgressService().run(true,
	// // true, in);
	// } catch (InterruptedException x) {
	// // cancelled by user
	// // TODO: Check how errors are handled
	// return BugzillaOfflineStatus.ERROR;
	// } catch (InvocationTargetException x) {
	// BugzillaPlugin.log(x);
	// MessageDialog.openError(null, "Compare Failed",
	// x.getTargetException().getMessage());
	// return BugzillaOfflineStatus.ERROR;
	// }
	//
	// ITask dirtyTask = getDirtyTask(oldBug);
	//					
	//					
	// if (in.getCompareResult() == null) {
	// status = BugzillaOfflineStatus.SAVED;
	// } else if (oldBug.hasChanges() || dirtyTask != null) {
	// if (!MessageDialog
	// .openQuestion(
	// null,
	// "Update Local Copy",
	// "Local copy of Report "
	// + entry.getId() + " on "+entry.getRepositoryUrl()
	// + " has changes.\nWould you like to override local changes? \n\nNote: if
	// you select No, only the new comment will be saved with the updated bug,
	// all other changes will be lost.")) {
	// ((BugzillaReport) entry).setNewComment(((BugzillaReport)
	// oldBug).getNewComment());
	// ((BugzillaReport) entry).setHasChanged(true);
	// status = BugzillaOfflineStatus.CONFLICT;
	// } else {
	// ((BugzillaReport) entry).setHasChanged(false);
	// status = BugzillaOfflineStatus.SAVED;
	// }
	// if(dirtyTask != null) {
	// TaskUiUtil.closeEditorInActivePage(dirtyTask);
	// TaskUiUtil.openEditor(dirtyTask, false);
	// }
	// } else {
	// DiffNode node = (DiffNode) in.getCompareResult();
	// IDiffElement[] children = node.getChildren();
	// if (children.length != 0) {
	// for (IDiffElement element : children) {
	// if (((DiffNode) element).getKind() == Differencer.CHANGE) {
	// if(reopenEditors(oldBug)) {
	// status = BugzillaOfflineStatus.SAVED;
	// } else {
	// status = BugzillaOfflineStatus.SAVED_WITH_INCOMMING_CHANGES;
	// }
	// break;
	// }
	// }
	// } else {
	// status = BugzillaOfflineStatus.SAVED; // do we
	// // ever get
	// // here?
	// }
	// }
	//
	// // Display.getDefault().asyncExec(new Runnable(){
	// // public void run() {
	// //
	// // CompareUI.openCompareDialog(in);
	// // }
	// // });
	// }
	// list.remove(index);
	// }
	// if (entry.hasChanges() && status != BugzillaOfflineStatus.CONFLICT) {
	// status = BugzillaOfflineStatus.SAVED_WITH_OUTGOING_CHANGES;
	// }
	// // add the entry to the list and write the file to disk
	// list.add(entry);
	// writeFile();
	// } catch (Exception e) {
	// e.printStackTrace();
	// IStatus runtimestatus = new Status(IStatus.ERROR,
	// BugzillaUiPlugin.PLUGIN_ID, IStatus.OK,
	// "failed to add offline report", e);
	// throw new CoreException(runtimestatus);
	// }
	// return status;
	// }

	/**
	 * Updates the offline reports list. Used when existing offline reports are
	 * modified and saved.
	 */
	public void update() {
		// check for bug and do a compare
		writeFile();
	}

	/**
	 * @return The id that a new offline bug should use. The value changes each
	 *         time this method is called.
	 */
	public int getNextOfflineBugId() {
		latestNewBugId++;
		return latestNewBugId;
	}

	/**
	 * Find a bug in the offline reports list.
	 * 
	 * @param id
	 *            The bug id that we are looking for
	 * @return The index of the bug in the array if it exists, else -1. Locally
	 *         created bugs are ignored.
	 */
	public int find(String repositoryUrl, int id) {
		for (int i = 0; i < list.size(); i++) {
			RepositoryTaskData currBug = list.get(i);
			if (currBug != null && currBug.getRepositoryUrl() != null
					&& (currBug.getRepositoryUrl().equals(repositoryUrl) && currBug.getId() == id)
					&& !currBug.isLocallyCreated())
				return i;
		}
		return -1;
	}

	// TODO: move to plugin
	public static RepositoryTaskData findBug(String repositoryUrl, int bugId) {
		int location = MylarTaskListPlugin.getDefault().getOfflineReportsFile().find(repositoryUrl, bugId);
		if (location != -1) {
			return MylarTaskListPlugin.getDefault().getOfflineReportsFile().elements().get(location);
		}
		return null;
	}

	/**
	 * Get the list of offline reports
	 * 
	 * @return The list of offline reports
	 */
	public ArrayList<RepositoryTaskData> elements() {
		return list;
	}

	/**
	 * Write the offline reports to disk
	 */
	private void writeFile() {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));

			// Write the size of the list so that we can read it back in easier
			out.writeInt(list.size());

			out.writeInt(latestNewBugId);

			// write each element in the array list
			for (int i = 0; i < list.size(); i++) {
				RepositoryTaskData item = list.get(i);
				out.writeObject(item);
			}
			out.close();
		} catch (IOException e) {
			// put up a message and log the error if there is a problem writing
			// to the file
			MessageDialog.openError(null, "I/O Error", "Could not write to offline reports file.");
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Read the offline reports in from the file on disk
	 * 
	 * @throws IOException
	 *             Error opening or closing the offline reports file
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 *             Error deserializing objects from the offline reports file
	 */
	private void readFile() throws IOException, ClassNotFoundException {

		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));

			// get the number of offline reports in the file
			int size = in.readInt();

			// get the bug id of the most recently created offline report
			latestNewBugId = in.readInt();

			// read in each of the offline reports in the file
			for (int nX = 0; nX < size; nX++) {
				// try {
				RepositoryTaskData item = (RepositoryTaskData) in.readObject();
				AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(item.getRepositoryKind());
				if(connector != null && connector.getOfflineTaskHandler() != null) {
					AbstractAttributeFactory factory = connector.getOfflineTaskHandler().getAttributeFactory();
					if(factory != null) {
						item.setAttributeFactory(factory);
					} 
				}
				list.add(item);				
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Remove some bugs from the offline reports list
	 * 
	 * @param indicesToRemove
	 *            An array of the indicies of the bugs to be removed
	 */
	public void remove(List<RepositoryTaskData> sel) {
		list.removeAll(sel);

		// rewrite the file so that the data is persistant
		writeFile();
	}

	/**
	 * Remove all of the items in the offline reports menu
	 */
	public void removeAll() {
		list.clear();

		// rewrite the file so that the data is persistant
		writeFile();
	}

	
}
// /**
// * Function to sort the offline reports list
// * @param sortOrder The way to sort the bugs in the offline reports list
// */
// public void sort(int sortOrder) {
// IBugzillaBug[] a = list.toArray(new IBugzillaBug[list.size()]);
//		
// // decide which sorting method to use and sort the offline reports
// switch(sortOrder) {
// case ID_SORT:
// Arrays.sort(a, new SortID());
// lastSel = ID_SORT;
// break;
// case TYPE_SORT:
// Arrays.sort(a, new SortType());
// lastSel = TYPE_SORT;
// break;
// }
//		
// // remove all of the elements from the list so that we can re-add
// // them in a sorted order
// list.clear();
//		
// // add the sorted elements to the list and the table
// for (int j = 0; j < a.length; j++) {
// add(a[j]);
// }
// }

// /**
// * Inner class to sort by bug id
// */
// private class SortID implements Comparator<IBugzillaBug> {
// public int compare(IBugzillaBug f1, IBugzillaBug f2) {
// Integer id1 = f1.getId();
// Integer id2 = f2.getId();
//
// if(id1 != null && id2 != null)
// return id1.compareTo(id2);
// else if(id1 == null && id2 != null)
// return -1;
// else if(id1 != null && id2 == null)
// return 1;
// else
// return 0;
// }
// }
//
// /**
// * Inner class to sort by bug type (locally created or from the server)
// */
// private class SortType implements Comparator<IBugzillaBug> {
// public int compare(IBugzillaBug f1, IBugzillaBug f2) {
// boolean isLocal1 = f1.isLocallyCreated();
// boolean isLocal2 = f2.isLocallyCreated();
//			
// if (isLocal1 && !isLocal2) {
// return -1;
// }
// else if (!isLocal1 && isLocal2) {
// return 1;
// }
//			
// // If they are both the same type, sort by ID
// Integer id1 = f1.getId();
// Integer id2 = f2.getId();
//
// if(id1 != null && id2 != null)
// return id1.compareTo(id2);
// else if(id1 == null && id2 != null)
// return -1;
// else if(id1 != null && id2 == null)
// return 1;
// else
// return 0;
// }
// }

// /**
// * Saves the given report to the offlineReportsFile, or, if it already
// * exists in the file, updates it.
// *
// * @param bug
// * The bug to add/update.
// */
// public static void saveOffline(IBugzillaBug bug, boolean saveChosen) throws
// CoreException {
// OfflineTaskManager file = BugzillaPlugin.getDefault().getOfflineReports();
// // If there is already an offline report for this bug, update the file.
// if (bug.isSavedOffline()) {
// file.update();
// }
// // If this bug has not been saved offline before, add it to the file.
// else {
// int index = -1;
// // If there is already an offline report with the same id, don't
// // save this report.
// if ((index = file.find(bug.getId())) >= 0) {
// removeReport(getOfflineBugs().get(index));
// // MessageDialog.openInformation(null, "Bug's Id is already
// // used.", "There is already a bug saved offline with an
// // identical id.");
// // return;
// }
// file.add(bug, saveChosen);
// bug.setOfflineState(true);
// // file.sort(OfflineTaskManager.lastSel);
// }
// }

// public static List<IBugzillaBug> getOfflineBugs() {
// OfflineTaskManager file = BugzillaPlugin.getDefault().getOfflineReports();
// return file.elements();
// }
//
// /**
// * Removes the given report from the offlineReportsFile.
// *
// * @param bug
// * The report to remove.
// */
// public static void removeReport(IBugzillaBug bug) {
// ArrayList<IBugzillaBug> bugList = new ArrayList<IBugzillaBug>();
// bugList.add(bug);
// BugzillaPlugin.getDefault().getOfflineReports().remove(bugList);
// }

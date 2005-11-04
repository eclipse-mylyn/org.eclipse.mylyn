/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core.offline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.IOfflineBugListener.BugzillaOfflineStaus;
import org.eclipse.mylar.bugzilla.core.compare.BugzillaCompareInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * Class to persist the data for the offline reports list
 */
public class OfflineReportsFile
{
	/** The file that the offline reports are written to */
	private File file;
	
	/** The directory to where the file is located */
	/** A list of offline reports */
	private ArrayList<IBugzillaBug> list = new ArrayList<IBugzillaBug>();

	
	/** Sort by bug ID */
	public static final int ID_SORT = 0;
	
	/** Sort by bug type (locally created or not) */
	public static final int TYPE_SORT = 4;
	
	/** Default sort by bug TYPE */
	public static int lastSel = TYPE_SORT;
	
	/** The bug id of the most recently created offline report. */
	protected int latestNewBugId = 0;
			
	/**
     * Constructor that reads the offline reports data persisted in the plugin's state
     * directory, if it exists.
     * 
     * @param file
     *            The file where the offline reports are persisted
     * @throws IOException
     *             Error opening or closing the offline reports file
     * @throws ClassNotFoundException
     *             Error deserializing objects from the offline reports file
     */
    public OfflineReportsFile(File file) throws IOException {
		this.file = file;
		if (file.exists()) {
			readFile();
		}
	}
	
	/**
	 * Add an offline report to the offline reports list
	 * @param entry The bug to add
	 */
	public boolean add(IBugzillaBug entry, boolean saveChosen) throws CoreException {
		try{
			BugzillaOfflineStaus status = BugzillaOfflineStaus.SAVED;
			// check for bug and do a compare
			int index = -1;
			if ((index = find(entry.getId())) >= 0) {
				IBugzillaBug oldBug = list.get(index);
				if(oldBug instanceof BugReport && entry instanceof BugReport && !saveChosen){
					CompareConfiguration config = new CompareConfiguration();
					config.setLeftEditable(false);
					config.setRightEditable(false);
					config.setLeftLabel("Local Bug Report");
					config.setRightLabel("Remote Bug Report");
					config.setLeftImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT));
					config.setRightImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT));
					final BugzillaCompareInput in = new BugzillaCompareInput(config);
					in.setLeft((BugReport)oldBug);
					in.setRight((BugReport)entry);
					in.setTitle("Bug #" + oldBug.getId());
					
					try {
						in.run(null);
						// running in new job inside a job causes shell to popup up, losing focus
//						PlatformUI.getWorkbench().getProgressService().run(true, true, in);
					} catch (InterruptedException x) {
						// cancelled by user	
						return false;
					} catch (InvocationTargetException x) {
						BugzillaPlugin.log(x);
						MessageDialog.openError(null, "Compare Failed", x.getTargetException().getMessage());
						return false;
					}
					
					if(in.getCompareResult() == null){
						return true;
					} else if(oldBug.hasChanges()){
						if(!MessageDialog.openQuestion(null, "Update Local Copy", "Local copy of Bug# " + entry.getId() + " Has Changes.\nWould you like to override local changes? Note: if you select No, your added comment will be saved with the updated bug, but all other changes will be lost.")){
							((BugReport)entry).setNewComment(((BugReport)oldBug).getNewComment());
							((BugReport)entry).setHasChanged(true);
							status = BugzillaOfflineStaus.CONFLICT;
						} else {
							((BugReport)entry).setHasChanged(false);
							status = BugzillaOfflineStaus.SAVED;
						}
						
					} else {
						DiffNode node = (DiffNode)in.getCompareResult();
						IDiffElement[] children = node.getChildren();
						if(children.length != 0){
							for (IDiffElement element : children) {
								if(((DiffNode)element).getKind() == Differencer.CHANGE){
									status = BugzillaOfflineStaus.SAVED_WITH_INCOMMING_CHANGES;
									break;
								}
							}
						} else {
							return true;
						}

					}
	//				Display.getDefault().asyncExec(new Runnable(){
	//					public void run() {
	//						
	//						CompareUI.openCompareDialog(in);	
	//					}
	//				});
				}
				list.remove(index);
			}
			if(entry.hasChanges() && status != BugzillaOfflineStaus.CONFLICT){
				status = BugzillaOfflineStaus.SAVED_WITH_OUTGOING_CHANGES;
			}
			// add the entry to the list and write the file to disk
			list.add(entry);
			writeFile();
			BugzillaPlugin.getDefault().fireOfflineStatusChanged(entry, status);
			return true;
		} catch (Exception e) {
			IStatus status = new Status(
				IStatus.ERROR,
				IBugzillaConstants.PLUGIN_ID,
				IStatus.OK,
				"failed to add of offline reort", 
				e);
			throw new CoreException(status);
		}
	}
	
	/**
	 * Updates the offline reports list. 
	 * Used when existing offline reports are modified and saved.
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
	public int find(int id) {
		for (int i = 0; i < list.size(); i++) {
			IBugzillaBug currBug = list.get(i);
			if (currBug != null && (currBug.getId() == id) && !currBug.isLocallyCreated())
				return i;
		}
		return -1;
	}

	/**
	 * Get the list of offline reports
	 * @return The list of offline reports
	 */
	public ArrayList<IBugzillaBug> elements() {
		return list;
	}

	/**
	 * Write the offline reports to disk
	 */
	private void writeFile() {
		try {
			ObjectOutputStream out =  new ObjectOutputStream(new FileOutputStream(file));
			
			// Write the size of the list so that we can read it back in easier
			out.writeInt(list.size());
			
			out.writeInt(latestNewBugId);
			
			// write each element in the array list
			for (int i = 0; i < list.size(); i++) {
				IBugzillaBug item = list.get(i);
				try{
					out.writeObject(item);
				}catch (IOException e) {
					// put up a message and log the error if there is a problem writing to the file
					BugzillaPlugin.log(new Status(Status.WARNING, IBugzillaConstants.PLUGIN_ID, Status.WARNING, "Unable to write bug object: " + item.getId(), e));
				}
			}
			out.close();
		}
		catch (IOException e) {
			// put up a message and log the error if there is a problem writing to the file
			MessageDialog.openError(null,
									"I/O Error",
									"Bugzilla could not write to offline reports file.");
			BugzillaPlugin.log(e);
		}
	}
	
	/**
	 * Read the offline reports in from the file on disk
	 * 
     * @throws IOException
     *             Error opening or closing the offline reports file
     * @throws ClassNotFoundException
     *             Error deserializing objects from the offline reports file
	 */
	private void readFile() throws IOException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));

        // get the number of offline reports in the file
        int size = in.readInt();
		
		// get the bug id of the most recently created offline report
		latestNewBugId = in.readInt();

        // read in each of the offline reports in the file
        for (int nX = 0; nX < size; nX++) {
        	try {
	            IBugzillaBug item = (IBugzillaBug) in.readObject();
	            // add the offline report to the offlineReports list
	            list.add(item);
        	} catch (ClassNotFoundException e){
        		// ignore this since we can't do anything
        		BugzillaPlugin.log(new Status(Status.ERROR, IBugzillaConstants.PLUGIN_ID, Status.ERROR, "Unable to read bug object", e));
        	}
        }
        in.close();
	}
	
	/**
	 * Remove some bugs from the offline reports list
	 * @param indicesToRemove An array of the indicies of the bugs to be removed
	 */
	public void remove(List<IBugzillaBug> sel) {
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

//	/**
//	 * Function to sort the offline reports list
//	 * @param sortOrder The way to sort the bugs in the offline reports list
//	 */
//	public void sort(int sortOrder) {
//		IBugzillaBug[] a = list.toArray(new IBugzillaBug[list.size()]);
//		
//		// decide which sorting method to use and sort the offline reports
//		switch(sortOrder) {
//			case ID_SORT:
//				Arrays.sort(a, new SortID());
//				lastSel = ID_SORT;
//				break;
//			case TYPE_SORT:
//				Arrays.sort(a, new SortType());
//				lastSel = TYPE_SORT;
//				break;
//		}
//		
//		// remove all of the elements from the list so that we can re-add
//		// them in a sorted order
//		list.clear();
//		
//		// add the sorted elements to the list and the table
//		for (int j = 0; j < a.length; j++) {
//			add(a[j]);
//		}
//	}
	
//	/**
//	 * Inner class to sort by bug id
//	 */
//	private class SortID implements Comparator<IBugzillaBug> {
//		public int compare(IBugzillaBug f1, IBugzillaBug f2) {
//			Integer id1 = f1.getId();
//			Integer id2 = f2.getId();
//
//			if(id1 != null && id2 != null)
//				return id1.compareTo(id2);
//			else if(id1 == null && id2 != null)
//				return -1;
//			else if(id1 != null && id2 == null)
//				return 1;
//			else
//				return 0;
//		}
//	}
//
//	/**
//	 * Inner class to sort by bug type (locally created or from the server)
//	 */
//	private class SortType implements Comparator<IBugzillaBug> {
//		public int compare(IBugzillaBug f1, IBugzillaBug f2) {
//			boolean isLocal1 = f1.isLocallyCreated();
//			boolean isLocal2 = f2.isLocallyCreated();
//			
//			if (isLocal1 && !isLocal2) {
//				return -1;
//			}
//			else if (!isLocal1 && isLocal2) {
//				return 1;
//			}
//			
//			// If they are both the same type, sort by ID
//			Integer id1 = f1.getId();
//			Integer id2 = f2.getId();
//
//			if(id1 != null && id2 != null)
//				return id1.compareTo(id2);
//			else if(id1 == null && id2 != null)
//				return -1;
//			else if(id1 != null && id2 == null)
//				return 1;
//			else
//				return 0;
//		}
//	}

	/**
	 * Saves the given report to the offlineReportsFile, or, if it already
	 * exists in the file, updates it.
	 * 
	 * @param bug
	 *            The bug to add/update.
	 */
	public static void saveOffline(IBugzillaBug bug, boolean saveChosen) throws CoreException {
		OfflineReportsFile file = BugzillaPlugin.getDefault().getOfflineReports();
		// If there is already an offline report for this bug, update the file.
		if (bug.isSavedOffline()) {
			file.update();
		}
		// If this bug has not been saved offline before, add it to the file.
		else {
			int index = -1;
			// If there is already an offline report with the same id, don't save this report.
			if ((index = file.find(bug.getId())) >= 0) {
				removeReport(getOfflineBugs().get(index));
//				MessageDialog.openInformation(null, "Bug's Id is already used.", "There is already a bug saved offline with an identical id.");
//				return;
			}
			file.add(bug, saveChosen);
			bug.setOfflineState(true);
//			file.sort(OfflineReportsFile.lastSel);
		}
	}
	
	public static List<IBugzillaBug> getOfflineBugs(){
		OfflineReportsFile file = BugzillaPlugin.getDefault().getOfflineReports();
		return file.elements();
	}

	/**
	 * Removes the given report from the offlineReportsFile.
	 * 
	 * @param bug
	 *            The report to remove.
	 */
	public static void removeReport(IBugzillaBug bug) {
		ArrayList<IBugzillaBug> bugList = new ArrayList<IBugzillaBug>();
		bugList.add(bug);
		BugzillaPlugin.getDefault().getOfflineReports().remove(bugList);
	}
}

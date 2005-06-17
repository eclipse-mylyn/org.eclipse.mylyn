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
package org.eclipse.mylar.bugzilla.favorites;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.IBugzillaConstants;


/**
 * Class to persist the data for the favorites list
 */
public class FavoritesFile
{
	/** The file that the favorites are written to */
	private File file;
	
	/** The directory to where the file is located */
	/** A list of favorites */
	private ArrayList<Favorite> list = new ArrayList<Favorite>();

	
	/** Sort by bug ID */
	public static final int ID_SORT = 0;
	
	/** Sort by bug priority */
	public static final int PRIORITY_SORT = 1;
	
	/** Sort by bug priority */
	public static final int SEVERITY_SORT = 2;
	
	/** Sort by bug state */
	public static final int STATE_SORT = 3;
	
	/** Default sort by bug ID */
	public static int lastSel = 0;
			
	/**
     * Constructor that reads the favorites data persisted in the plugin's state
     * directory, if it exists.
     * 
     * @param file
     *            The file where the favorites are persisted
     * @throws IOException
     *             Error opening or closing the favorites file
     * @throws ClassNotFoundException
     *             Error deserializing objects from the favorites file
     */
    public FavoritesFile(File file) throws ClassNotFoundException, IOException {
		this.file = file;
		if (file.exists()) {
			readFile();
		}
	}
	
	/**
	 * Add a favorite to the favorites list
	 * @param entry The bug to add
	 */
	public void add(Favorite entry) {
		// add the entry to the list and write the file to disk
		list.add(entry);
		writeFile();
	}
	
	/**
	 * Find a bug in the favorites list
	 * @param id The bug id that we are looking for
	 * @return The index of the bug in the array if it exists, else 0
	 */
	public int find(int id) {
		for (int i = 0; i < list.size(); i++) {
			Favorite currFav = list.get(i);
			if (currFav.getId() == id)
				return i;
		}
		return 0;
	}

	/**
	 * Get the list of favorites
	 * @return The list of favorites
	 */
	public ArrayList<Favorite> elements() {
		return list;
	}

	/**
	 * Write the favorites to disk
	 */
	private void writeFile() {
		try {
			ObjectOutputStream out =  new ObjectOutputStream(new FileOutputStream(file));
			
			// Write the size of the list so that we can read it back in easier
			out.writeInt(list.size());
			
			// write each element in the array list
			for (int i = 0; i < list.size(); i++) {
				Object item = list.get(i);
				out.writeObject(item);
			}
			out.close();
		}
		catch (IOException e) {
			// put up a message and log the error if there is a problem writing to the file
			MessageDialog.openError(null,
									"I/O Error",
									"Bugzilla could not write to favorites file.");
			BugzillaPlugin.log(e);
		}
	}
	
	/**
	 * Read the favorites in from the file on disk
	 * 
     * @throws IOException
     *             Error opening or closing the favorites file
     * @throws ClassNotFoundException
     *             Error deserializing objects from the favorites file
	 */
	private void readFile() throws ClassNotFoundException, IOException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));

        // get the number of favorites in the file
        int size = in.readInt();

        // read in each of the favorites in the file
        for (int nX = 0; nX < size; nX++) {
            Favorite item = (Favorite) in.readObject();
            // add the favorite to the favorites list
            list.add(item);
        }
        in.close();
		
		sort(lastSel);
	}
	
	/**
	 * Remove some bugs from the favorites list
	 * @param indicesToRemove An array of the indicies of the bugs to be removed
	 */
	public void remove(List<Favorite> sel) {
		list.removeAll(sel);
		
		// rewrite the file so that the data is persistant
		writeFile();
	}
	
	/**
	 * Remove all of the items in the favortes menu
	 */
	public void removeAll() {
		list.clear();
		
		// rewrite the file so that the data is persistant
		writeFile();
	}

	/**
	 * Function to sort the favorites list
	 * @param sortOrder The way to sort the bugs in the favorites list
	 */
	public void sort(int sortOrder) {
		Favorite[] a = list.toArray(new Favorite[list.size()]);
		
		// decide which sorting method to use and sort the favorites
		switch(sortOrder) {
			case ID_SORT:
				Arrays.sort(a, new SortID());
				lastSel = ID_SORT;
				break;
			case PRIORITY_SORT:
				Arrays.sort(a, new SortPriority());
				lastSel = PRIORITY_SORT;
				break;

			case SEVERITY_SORT:
				Arrays.sort(a, new SortSeverity());
				lastSel = SEVERITY_SORT;
				break;

			case STATE_SORT:
				Arrays.sort(a, new SortState());
				lastSel = STATE_SORT;
				break;
		}
		
		// remove all of the elements from the list so that we can re-add
		// them in a sorted order
		list.clear();
		
		// add the sorted elements to the list and the table
		for (int j = 0; j < a.length; j++) {
			add(a[j]);
		}
	}
	
	/**
	 * Inner class to sort by bug id
	 */
	private class SortID implements Comparator<Favorite> {
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Favorite f1, Favorite f2) {
			Integer id1 = (Integer) f1.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_ID);
			Integer id2 = (Integer) f2.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_ID);

			if(id1 != null && id2 != null)
				return id1.compareTo(id2);
			else if(id1 == null && id2 != null)
				return -1;
			else if(id1 != null && id2 == null)
				return 1;
			else
				return 0;
		}
	}

	/**
	 * Inner class to sort by priority
	 */
	private class SortPriority implements Comparator<Favorite>
	{
		/*
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Favorite f1, Favorite f2) {
			Integer pri1 = (Integer) f1.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_PRIORITY);
			Integer pri2 = (Integer) f2.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_PRIORITY);
			
			if(pri1 != null && pri2 != null)
				return pri1.compareTo(pri2);
			else if(pri1 == null && pri2 != null)
				return -1;
			else if(pri1 != null && pri2 == null)
				return 1;
			else
				return 0;
		}
	}
	
	/**
	 * Inner class to sort by severity
	 */
	private class SortSeverity implements Comparator<Favorite> {
		/*
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Favorite f1, Favorite f2) {
			Integer sev1 = (Integer) f1.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_SEVERITY);
			Integer sev2 = (Integer) f2.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_SEVERITY);
				
			if(sev1 != null && sev2 != null)
				return sev1.compareTo(sev2);
			else if(sev1 == null && sev2 != null)
				return -1;
			else if(sev1 != null && sev2 == null)
				return 1;
			else
				return 0;
		}
	}
	
	/**
	 * Inner class to sort by state
	 */
	private class SortState implements Comparator<Favorite>
	{
		/*
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Favorite f1, Favorite f2) {
			Integer sta1 = (Integer) f1.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_STATE);
			Integer sta2 = (Integer) f2.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_STATE);
						
			if(sta1 != null && sta2 != null)
			{
				int rc = sta1.compareTo(sta2);
				if(rc == 0)
				{
					Integer res1 = (Integer) f1.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_RESULT);
					Integer res2 = (Integer) f2.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_RESULT);
					
					return res1.compareTo(res2);
				}
				else
					return rc;
			}
			else if(sta1 == null && sta2 != null)
				return -1;
			else if(sta1 != null && sta2 == null)
				return 1;
			else
				return 0;
		}
	}
}

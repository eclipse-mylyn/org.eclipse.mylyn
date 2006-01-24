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

package org.eclipse.mylar.bugzilla.ui.search;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;

/**
 * This class manages accessing and persisting named Bugzilla queries.
 */
public class SavedQueryFile {
	/** The file that the queries are written to */
	private File file;

	/** The directory to where the file is located */
	private File directory;

	/** A list of remembered queries */
	private ArrayList<String> list = new ArrayList<String>();

	private ArrayList<String> nameList = new ArrayList<String>();

	private ArrayList<String> sumList = new ArrayList<String>();

	/**
	 * Constructor
	 * 
	 * @param dirPath
	 *            The path to the directory where the favorites file should be
	 *            written to
	 * @param fileName
	 *            The file that the favorites should be written to
	 */
	public SavedQueryFile(String dirPath, String fileName) {
		// create a new file and if it exists, read the data from the file
		// else create the file and directories if they dont exist
		file = new File(dirPath + fileName);
		if (file.exists()) {
			readFile();
		} else {
			directory = new File(dirPath);
			if (!directory.exists())
				directory.mkdirs();
			writeFile();
		}
	}

	/**
	 * Add a query to the list
	 * 
	 * @param entry
	 *            The query to add
	 */
	public int add(String entry, String name, String sum) {
		// add the entry to the list and write the file to disk
		int index = find(name);
		if (index == -1) {
			list.add(entry);
			nameList.add(name);
			sumList.add(sum);
			writeFile();
		} else {
			boolean isDuplicate = MessageDialog.openConfirm(BugzillaPlugin.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "Save Bugzilla Query", name
					+ " already exists. Do you want to replace it?");
			if (isDuplicate) {
				list.add(index, entry);
				nameList.add(index, name);
				sumList.add(index, sum);
				list.remove(index + 1);
				nameList.remove(index + 1);
				sumList.remove(index + 1);
				writeFile();
			}
		}

		index = find(entry);
		return index;
	}

	/**
	 * Find a bug in the query list
	 * 
	 * @param name
	 *            The name of the query that we are looking for
	 * @return The index of the query in the array if it exists, else -1
	 */
	public int find(String name) {
		for (int i = 0; i < list.size(); i++) {
			String str = nameList.get(i);
			if (name.compareTo(str) == 0)
				return i;
		}
		return -1;
	}

	/**
	 * Get the list of queries
	 * 
	 * @return The list of queries
	 */
	public ArrayList<String> elements() {
		return list;
	}

	/**
	 * Write the queries to disk
	 */
	private void writeFile() {
		try {
			OutputStream os = new FileOutputStream(file);
			DataOutputStream data = new DataOutputStream(os);

			// Write the size of the list so that we can read it back in easier
			data.writeInt(list.size());

			// write each element in the array list
			for (int i = 0; i < list.size(); i++) {
				String item = list.get(i);
				String itemName = nameList.get(i);
				String summary = sumList.get(i);

				// write the string in a machine independant manner
				data.writeUTF(item);
				data.writeUTF(itemName);
				data.writeUTF(summary);
			}

			// close the file
			data.close();
		} catch (IOException e) {
			// put up a message and log the error if there is a problem writing
			// to the file
			BugzillaPlugin.getDefault().logAndShowExceptionDetailsDialog(e,
					"occurred while saving your Bugzilla queries", "I/O Error");
		}
	}

	/**
	 * Read the queries in from the file on disk
	 */
	private void readFile() {
		try {
			InputStream is = new FileInputStream(file);
			DataInputStream data = new DataInputStream(is);

			// get the number of favorites in the file
			int size = data.readInt();

			// read in each of the favorites in the file
			for (int nX = 0; nX < size; nX++) {
				String item, name, summary;

				// get the data from disk in a machine independant way
				item = data.readUTF();
				name = data.readUTF();
				summary = data.readUTF();

				// add the favorite to the favorites list
				list.add(item);
				nameList.add(name);
				sumList.add(summary);
			}

			// close the input stream
			data.close();
		} catch (IOException e) {
			// put up a message and log the error if there is a problem reading
			// from the file
			BugzillaPlugin.getDefault().logAndShowExceptionDetailsDialog(e,
					"occurred while restoring saved Bugzilla queries.", "I/O Error");
		}
	}

	/**
	 * Remove some queries from the list
	 * 
	 * @param indicesToRemove
	 *            An array of the indicies of the queries to be removed
	 */
	public void remove(int[] indicesToRemove) {
		int timesShifted = 0;

		// remove each of the indicated items from the array
		for (int i = 0; i < indicesToRemove.length; i++) {
			list.remove(indicesToRemove[i] - timesShifted);
			nameList.remove(indicesToRemove[i] - timesShifted);
			sumList.remove(indicesToRemove[i] - timesShifted);
			timesShifted++;
		}

		// rewrite the file so that the data is persistant
		writeFile();

		// remove the items from the combo box
		timesShifted = 0;
	}

	/**
	 * Remove all of the items in the favortes menu
	 */
	public void removeAll() {
		// remove every element from the favorites list
		while (list.size() > 0) {
			list.remove(0);
			nameList.remove(0);
			sumList.remove(0);
		}

		// rewrite the file so that the data is persistant
		writeFile();
	}

	/**
	 * Get the query parameters of the currently selected remembered query
	 * 
	 * @return The query url
	 */
	public String getQueryParameters(int index) {
		return list.get(index);
	}

	/**
	 * Get the summary text of the currently selected remembered query
	 * 
	 * @return The summary text
	 */
	public String getSummaryText(int index) {
		return sumList.get(index);

	}

	public ArrayList<String> getNames() {
		return nameList;
	}
}

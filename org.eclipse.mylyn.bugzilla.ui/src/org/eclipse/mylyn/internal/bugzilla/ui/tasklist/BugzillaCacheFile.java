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

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;

/**
 * COPIED FROM
 * 
 * @see org.eclipse.mylar.bugzilla.offlineReports.OfflineReportsFile
 * 
 * @author Shawn Minto
 */
public class BugzillaCacheFile {

	private File file;

	private ArrayList<BugzillaReport> list = new ArrayList<BugzillaReport>();

	protected int latestNewBugId = 0;

	public BugzillaCacheFile(File file) throws ClassNotFoundException, IOException {
		this.file = file;
		if (file.exists()) {
			readFile();
		}
	}

	public void add(BugzillaReport entry) {
		// add the entry to the list and write the file to disk
		list.add(entry);
		writeFile();
	}

	public void update() {
		writeFile();
	}

	public int getNextOfflineBugId() {
		latestNewBugId++;
		return latestNewBugId;
	}

	public int find(int id) {
		for (int i = 0; i < list.size(); i++) {
			BugzillaReport currBug = list.get(i);
			if (currBug != null && (currBug.getId() == id) && !currBug.isLocallyCreated())
				return i;
		}
		return -1;
	}

	public ArrayList<BugzillaReport> elements() {
		return list;
	}

	private void writeFile() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

			// Write the size of the list so that we can read it back in easier
			out.writeInt(list.size());

			out.writeInt(latestNewBugId);

			// write each element in the array list
			for (int i = 0; i < list.size(); i++) {
				Object item = list.get(i);
				out.writeObject(item);
			}
			out.close();
		} catch (IOException e) {
			// put up a message and log the error if there is a problem writing
			// to the file
			MessageDialog.openError(null, "I/O Error", "Bugzilla could not write to offline reports file.");
			BugzillaPlugin.log(e);
		}
	}

	private void readFile() throws ClassNotFoundException, IOException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));

		// get the number of offline reports in the file
		int size = in.readInt();

		// get the bug id of the most recently created offline report
		latestNewBugId = in.readInt();

		// read in each of the offline reports in the file
		for (int nX = 0; nX < size; nX++) {
			BugzillaReport item = (BugzillaReport) in.readObject();
			// add the offline report to the offlineReports list
			list.add(item);
		}
		in.close();
	}

	public void remove(List<BugzillaReport> sel) {
		list.removeAll(sel);

		// rewrite the file so that the data is persistant
		writeFile();
	}

	public void removeAll() {
		list.clear();

		// rewrite the file so that the data is persistant
		writeFile();
	}
}

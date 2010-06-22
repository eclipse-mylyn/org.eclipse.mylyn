/*******************************************************************************
 * Copyright (c) 2010 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class CustomTransitionManager implements Serializable {
	private static final long serialVersionUID = 3340305752692674487L;

	private static final String defaultDuplicateStatus = "RESOLVED"; //$NON-NLS-1$

	/*
	 * Create the default Bugzilla operations
	 */

	//Indexed by the current status
	private final HashMap<String, List<AbstractBugzillaOperation>> operationMapByCurrentStatus;

	//Indexed by the status to be transitioned into
	private final HashMap<String, List<AbstractBugzillaOperation>> operationMapByEndStatus;

	//Statuses that are not marked as is_open by Bugzilla
	private final ArrayList<String> closedStatuses;

	//Whether or not customized names were used in Bugzilla
	private boolean customNames = false;

	private boolean valid = false;

	private String filePath;

	private String duplicateStatus;

	public CustomTransitionManager() {
		operationMapByCurrentStatus = new HashMap<String, List<AbstractBugzillaOperation>>();
		operationMapByEndStatus = new HashMap<String, List<AbstractBugzillaOperation>>();
		closedStatuses = new ArrayList<String>();
		this.valid = false;
		this.filePath = ""; //$NON-NLS-1$
		duplicateStatus = defaultDuplicateStatus;
	}

	/**
	 * Searches for a valid transition description file. Returns true if a file exists and was sucessfully parsed, false
	 * otherwise
	 * 
	 * @param filePath
	 * @return true if anything was changed, false otherwise.
	 * @throws CoreException
	 */
	public boolean parse(String filePath) {
		if (filePath == null || filePath.length() < 1) {
			setValid(false);
			return false;
		} else if (filePath.equals(this.filePath)) {
			//Do nothing, already parsed this file
			return false;
		}
		this.filePath = filePath;
		setValid(true);

		operationMapByCurrentStatus.clear();
		operationMapByEndStatus.clear();
		closedStatuses.clear();

		File file = new File(filePath);
		if (!file.exists() || !file.canRead()) {
			setValid(false);
			return isValid();
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s;
			boolean checkOptions = true;

			while ((s = br.readLine()) != null && isValid()) {
				if (s.equals("<transitions>")) { //$NON-NLS-1$
					checkOptions = false;
					defaultNames();
					continue;
				}

				if (checkOptions) {
					parseOptions(s);
				} else {
					parseTransitions(s);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			setValid(false);
			return valid;
		}

		return valid;
	}

	private void parseOptions(String s) throws IOException {
		String[] pieces = s.split("="); //$NON-NLS-1$
		if (pieces.length != 2) {
			throw new IOException("Invalid Bugzilla option: " + s);
		}

		String name = pieces[0];
		String value = pieces[1];

		if (name.equals("CustomStatusNames")) { //$NON-NLS-1$
			if (value.equals("true")) { //$NON-NLS-1$
				customNames = true;
			} else {
				customNames = false;
			}
		} else if (name.equals("DuplicateStatus")) { //$NON-NLS-1$
			duplicateStatus = value;
		} else if (name.equals("ClosedCustomStatus")) { //$NON-NLS-1$
			closedStatuses.add(value);
		}
	}

	private void parseTransitions(String s) throws IOException {
		String[] pieces = s.split(":"); //$NON-NLS-1$
		if (pieces.length < 4) {
			throw new IOException("Invalid Bugzilla transition: " + s);
		}
		String status = pieces[1];
		String[] endStatuses = pieces[3].split(","); //$NON-NLS-1$
		ArrayList<AbstractBugzillaOperation> validOps = new ArrayList<AbstractBugzillaOperation>();

		for (String s1 : endStatuses) {
			if (status.equals(s1)) {
				continue;
			}

			//Special case: Unconfirmed and reopened both lead to adding the reopen operation
			if (!customNames && (status.equals("REOPENED") && s1.equals("UNCONFIRMED") || s1.equals("REOPENED") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					&& status.equals("REOPENED"))) { //$NON-NLS-1$
				continue;
			}

			if (customNames && operationMapByEndStatus.get(s1) == null) {
				//Create a custom open bug_status
				if (!closedStatuses.contains(s1)) {
					ArrayList<AbstractBugzillaOperation> list = new ArrayList<AbstractBugzillaOperation>();
					list.add(new BugzillaOperation(AbstractBugzillaOperation.DEFAULT_LABEL_PREFIX + s1));
					operationMapByEndStatus.put(s1, list);
				} else {
					ArrayList<AbstractBugzillaOperation> list = new ArrayList<AbstractBugzillaOperation>();
					list.add(new BugzillaOperation(AbstractBugzillaOperation.DEFAULT_LABEL_PREFIX + s1, "resolution", //$NON-NLS-1$
							TaskAttribute.TYPE_SINGLE_SELECT, s1));
					operationMapByEndStatus.put(s1, list);
				}
			}

			//Encountered an end status that we have no valid transitions for.
			if (operationMapByEndStatus.get(s1) == null) {
				throw new IOException("Encountered status with no valid transitions: " + s1);
			}

			validOps.addAll(operationMapByEndStatus.get(s1));
		}

		operationMapByCurrentStatus.put(status, validOps);
	}

	private void defaultNames() {

		/*
		 * Add the default operations if we are not using custom names
		 */

		//Step 1: Make custom operations for the default operations
		//This is necessary so we can add verifiers to these operations later
		if (operationMapByEndStatus.size() == 0) {

			ArrayList<AbstractBugzillaOperation> list = new ArrayList<AbstractBugzillaOperation>();

			//NONE is currently not used from the map, included for completeness
			list.add(BugzillaOperation.none);
			operationMapByEndStatus.put("NONE", list); //$NON-NLS-1$

			list = new ArrayList<AbstractBugzillaOperation>();
			list.add(BugzillaOperation.accept);
			operationMapByEndStatus.put("ASSIGNED", list); //$NON-NLS-1$

			list = new ArrayList<AbstractBugzillaOperation>();
			list.add(BugzillaOperation.resolve);
			if (duplicateStatus.equals("RESOLVED")) { //$NON-NLS-1$
				list.add(BugzillaOperation.duplicate);
			}
			operationMapByEndStatus.put("RESOLVED", list); //$NON-NLS-1$

			list = new ArrayList<AbstractBugzillaOperation>();
			list.add(BugzillaOperation.reopen);
			operationMapByEndStatus.put("REOPENED", list); //$NON-NLS-1$

			list = new ArrayList<AbstractBugzillaOperation>();
			list.add(BugzillaOperation.reopen);
			operationMapByEndStatus.put("UNCONFIRMED", list); //$NON-NLS-1$

			list = new ArrayList<AbstractBugzillaOperation>();
			list.add(BugzillaOperation.verify);
			if (duplicateStatus.equals("VERIFIED")) { //$NON-NLS-1$
				list.add(BugzillaOperation.duplicate);
			}
			operationMapByEndStatus.put("VERIFIED", list); //$NON-NLS-1$

			list = new ArrayList<AbstractBugzillaOperation>();
			list.add(BugzillaOperation.close);
			list.add(BugzillaOperation.close_with_resolution);
			if (duplicateStatus.equals("CLOSED")) { //$NON-NLS-1$
				list.add(BugzillaOperation.duplicate);
			}
			operationMapByEndStatus.put("CLOSED", list); //$NON-NLS-1$

			list = new ArrayList<AbstractBugzillaOperation>();
			list.add(BugzillaOperation.markNew);
			operationMapByEndStatus.put("NEW", list); //$NON-NLS-1$
		}

	}

	public List<AbstractBugzillaOperation> getOperation(String id) {
		return operationMapByEndStatus.get(id);
	}

	public List<AbstractBugzillaOperation> getValidTransitions(String key) {
		return operationMapByCurrentStatus.get(key);
	}

	/**
	 * Sets whether or not this class is valid. If set to false, the filePath will be set to "" so that subsequent calls
	 * to parse(filePath) will work.
	 * 
	 * @param val
	 */
	public void setValid(boolean val) {
		if (val == false) {
			this.filePath = ""; //$NON-NLS-1$
		}
		this.valid = val;
	}

	public boolean isValid() {
		return this.valid;
	}

	/**
	 * Returns the duplicate status. Standard Bugzilla installations will have a duplicate status of RESOLVED, VERIFIED
	 * or CLOSED. <br>
	 * By default, the duplicate status will be RESOLVED.
	 * 
	 * @return The status to send if a bug is set to Duplicate
	 */
	public String getDuplicateStatus() {
		if (duplicateStatus == null || duplicateStatus.length() == 0) {
			duplicateStatus = defaultDuplicateStatus;
		}
		return duplicateStatus;
	}

}

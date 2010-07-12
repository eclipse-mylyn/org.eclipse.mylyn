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

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.bugzilla.core.service.BugzillaXmlRpcClient;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Charley Wang
 */
public class CustomTransitionManager implements Serializable {
	private static final long serialVersionUID = 3340305752692674487L;

	private static final String DEFAULT_DUPLICATE_STATUS = "RESOLVED"; //$NON-NLS-1$

	/** Default start status -- this uses the Mylyn default from before bug #317729 */
	public static final String DEFAULT_START_STATUS = "NEW"; //$NON-NLS-1$

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

	private boolean fromFile = false;

	private String filePath;

	private String duplicateStatus;

	private String startStatus;

	private boolean usedXml;

	public CustomTransitionManager() {
		operationMapByCurrentStatus = new HashMap<String, List<AbstractBugzillaOperation>>();
		operationMapByEndStatus = new HashMap<String, List<AbstractBugzillaOperation>>();
		closedStatuses = new ArrayList<String>();
		this.valid = false;
		this.filePath = ""; //$NON-NLS-1$
		this.fromFile = false;
		this.usedXml = false;
		duplicateStatus = DEFAULT_DUPLICATE_STATUS;
		startStatus = DEFAULT_START_STATUS;
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
		fromFile = true;
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
			throw new IOException(Messages.CustomTransitionManager_InvalidBugzillaOption + s);
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
		} else if (name.equals("StartStatus")) {//$NON-NLS-1$
			startStatus = value;
		}
	}

	private void parseTransitions(String s) throws IOException {
		String[] pieces = s.split(":"); //$NON-NLS-1$
		if (pieces.length < 4) {
			throw new IOException(Messages.CustomTransitionManager_InvalidBugzillaTransition + s);
		}
		String status = pieces[1];
		String[] endStatuses = pieces[3].split(","); //$NON-NLS-1$
		parse(status, endStatuses);
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
			duplicateStatus = DEFAULT_DUPLICATE_STATUS;
		}
		return duplicateStatus;
	}

	/**
	 * Returns the start status. Standard Bugzilla installations have 2 available start statuses, UNCONFIRMED and NEW.
	 * Each user has a permissions setting that determines whether their new bugs start as UNCONFIRMED or NEW. This
	 * permissions setting currently cannot be accessed, so this function does not try to guess and returns either the
	 * default status (NEW) or whichever status was set by a transition file.
	 * 
	 * @return The valid start status. Default value is NEW.
	 */
	public String getStartStatus() {
		return startStatus;
	}

	private void addTransition(String start, String endStatus) {
		if (!customNames
				&& (start.equals("REOPENED") && (endStatus.equals("UNCONFIRMED") || endStatus.equals("REOPENED")))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			//REOPENED should not retransition to REOPENED
			return;
		}
		List<AbstractBugzillaOperation> list = operationMapByCurrentStatus.get(start);
		if (list == null) {
			list = new ArrayList<AbstractBugzillaOperation>();
		}
		list.addAll(operationMapByEndStatus.get(endStatus));
		operationMapByCurrentStatus.put(start, list);
	}

	private void parse(String start, Object[] transitions) {
		addNewStatus(start);

		//Find valid transitions
		for (Object o : transitions) {
			if ((o instanceof HashMap<?, ?>)) {
				//Used for XMLRPC
				HashMap<?, ?> tran = (HashMap<?, ?>) o;
				String endStatus = (String) tran.get("name"); //$NON-NLS-1$
				addNewStatus(endStatus);
				if (!endStatus.equals(start)) {
					addTransition(start, endStatus);
				}
			} else if (o instanceof String) {
				//Used for files
				String endStatus = (String) o;
				addNewStatus(endStatus);
				if (!endStatus.equals(start)) {
					addTransition(start, endStatus);
				}
			}
		}
	}

	public void parse(BugzillaXmlRpcClient xmlClient) {
//		if (usedXml) {
//			//XMLRPC should not change, don't look it up again
//			return;
//		}
		this.filePath = ""; //$NON-NLS-1$
		operationMapByCurrentStatus.clear();
		operationMapByEndStatus.clear();
		closedStatuses.clear();
		defaultNames();
		setValid(false);
		//Assume custom names, we have no way to check
		customNames = true;

		try {
			String[] fields = new String[1];
			fields[0] = "bug_status"; //$NON-NLS-1$
			for (Object raw : xmlClient.getFieldsWithNames(fields)) {
				if (raw instanceof HashMap<?, ?>) {
					Object[] values = (Object[]) ((HashMap<?, ?>) raw).get("values"); //$NON-NLS-1$
					if (values == null) {
						continue;
					}
					for (Object status : values) {
						if (status instanceof HashMap<?, ?>) {
							//Get name
							HashMap<?, ?> map = (HashMap<?, ?>) status;
							String start = (String) map.get("name"); //$NON-NLS-1$
							//Get is_open
							Object is_open = map.get("is_open"); //$NON-NLS-1$
							if (is_open.toString().equals("false")) { //$NON-NLS-1$
								closedStatuses.add(start);
							}
							parse(start, (Object[]) map.get("can_change_to")); //$NON-NLS-1$

						}
					}
				} else {
					throw new XmlRpcException(Messages.CustomTransitionManager_UnexpectedResponse);
				}
			}
			//Should check if there are conditions we can use to terminate early
			if (operationMapByCurrentStatus.size() == 0) {
				throw new XmlRpcException(Messages.CustomTransitionManager_UnexpectedResponse);
			}

			setValid(true);
			usedXml = true;
			fromFile = false;
		} catch (XmlRpcException e) {
			setValid(false);
		}
	}

	/**
	 * Creates a new status with a single operation with the same name as the status itself. Does nothing if a status of
	 * that name already exists.
	 * 
	 * @param status
	 */
	private void addNewStatus(String status) {
		List<AbstractBugzillaOperation> list = operationMapByEndStatus.get(status);
		if (list == null) {
			list = new ArrayList<AbstractBugzillaOperation>();
		} else {
			return;
		}

		if (!closedStatuses.contains(status)) {
			list.add(new BugzillaOperation(AbstractBugzillaOperation.DEFAULT_LABEL_PREFIX + status));
		} else {
			list.add(new BugzillaOperation(AbstractBugzillaOperation.DEFAULT_LABEL_PREFIX + status, "resolution", //$NON-NLS-1$
					TaskAttribute.TYPE_SINGLE_SELECT, status));
		}

		operationMapByEndStatus.put(status, list);
	}

}

/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui;

import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.compare.structuremergeviewer.IStructureCreator;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;

/**
 * This implementation of the <code>IStructureCreator</code> interface makes the contents of a <code>BugReport</code>
 * object available as a hierarchical structure of <code>IStructureComparator</code>s.
 * <p>
 * It is used when comparing a modified bug report to the one on the corresponding server.
 */
public class BugzillaCompareStructureCreator implements IStructureCreator {

	/**
	 * Create a new BugzillaStructureCreator.
	 */
	public BugzillaCompareStructureCreator() {
		super();
	}

	public String getName() {
		return "Bugzilla Structure Creator";
	}

	public IStructureComparator getStructure(Object input) {
		if (input instanceof RepositoryTaskData) {
			RepositoryTaskData bugReport = (RepositoryTaskData) input;
			return BugzillaCompareNode.parseBugReport(bugReport);
		} else {
			return null;
		}
	}

	public IStructureComparator locate(Object path, Object input) {
		return null;
	}

	public String getContents(Object node, boolean ignoreWhitespace) {
		if (node instanceof BugzillaCompareNode) {
			String s = ((BugzillaCompareNode) node).getValue();
			if (ignoreWhitespace)
				s = s.trim();
			return s;
		}
		return null;
	}

	/**
	 * Called whenever a copy operation has been performed on a tree node. This implementation throws an
	 * <code>AssertionFailedException</code> since we cannot update a bug report object.
	 * 
	 * @param structure
	 *            the node for which to save the new content
	 * @param input
	 *            the object from which the structure tree was created in <code>getStructure</code>
	 */
	public void save(IStructureComparator node, Object input) {
		// ignore
	}

}

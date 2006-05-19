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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Mik Kersten
 */
public class NewRepositoryTaskWizard extends MultiRepositoryAwareWizard {

	private static final String TITLE = "New Repostiory Task";
	
	public NewRepositoryTaskWizard() {
		super(new NewRepositoryTaskPage(getConnectorKinds()), TITLE);
	}

	private static List<String> getConnectorKinds() {
		List<String> connectorKinds = new ArrayList<String>();
		for (AbstractRepositoryConnector client: MylarTaskListPlugin.getRepositoryManager().getRepositoryConnectors()) {
			if (client.canCreateNewTask()) {
				connectorKinds.add(client.getRepositoryType());
			} 
		} 
		return connectorKinds;
	}
}

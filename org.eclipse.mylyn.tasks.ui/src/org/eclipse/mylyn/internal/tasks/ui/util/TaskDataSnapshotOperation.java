/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.File;
import java.util.Set;

/**
 * @author Robert Elves
 */
public class TaskDataSnapshotOperation extends TaskDataExportOperation {

	public TaskDataSnapshotOperation(String destinationDirectory, String destinationFilename) {
		super(destinationDirectory, destinationFilename);
	}

	@Override
	protected void selectFiles(Set<File> filesToExport) {

		filesToExport.add(new File(getSourceFolder(), "tasks.xml.zip")); //$NON-NLS-1$
		filesToExport.add(new File(getSourceFolder(), "repositories.xml.zip")); //$NON-NLS-1$
		filesToExport.add(new File(getSourceFolder(), "contexts/activity.xml.zip")); //$NON-NLS-1$
	}

}

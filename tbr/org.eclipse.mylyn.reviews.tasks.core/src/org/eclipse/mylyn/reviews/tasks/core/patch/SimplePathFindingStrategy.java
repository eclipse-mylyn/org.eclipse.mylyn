/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.core.patch;

import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.compare.patch.ReaderCreator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * @author Kilian Matt
 */
public class SimplePathFindingStrategy implements ITargetPathStrategy {

	public ReaderCreator get(IPath path) {
		final IFile file = org.eclipse.core.resources.ResourcesPlugin
				.getWorkspace().getRoot().getFile(path);
		return new ReaderCreator() {
			@Override
			public Reader createReader() throws CoreException {
				return new InputStreamReader(file.getContents());
			}
		};
	}

	public boolean matches(IPath targetPath) {
		final IFile file = org.eclipse.core.resources.ResourcesPlugin
				.getWorkspace().getRoot().getFile(targetPath);

		return file.exists();
	}

}

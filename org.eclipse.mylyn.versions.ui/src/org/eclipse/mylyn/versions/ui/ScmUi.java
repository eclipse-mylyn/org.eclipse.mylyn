/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.ui;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.core.history.LocalFileRevision;
import org.eclipse.team.internal.ui.history.CompareFileRevisionEditorInput;
import org.eclipse.team.internal.ui.history.FileRevisionTypedElement;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Steffen Pingel
 */
public class ScmUi {

	private static class EmptyTypedElement implements ITypedElement {

		private final String name;

		public EmptyTypedElement(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public Image getImage() {
			return null;
		}

		public String getType() {
			return ITypedElement.UNKNOWN_TYPE;
		}

	}

	protected static ITypedElement getElementFor(IResource resource) {
		return SaveableCompareEditorInput.createFileElement((IFile) resource);
	}

	private static IResource getResource(IFileRevision revision) {
		if (revision instanceof LocalFileRevision) {
			LocalFileRevision local = (LocalFileRevision) revision;
			return local.getFile();
		}
		return null;
	}

	public static void openCompareEditor(IWorkbenchPage page, IFileRevision file1, IFileRevision file2) {
		Assert.isNotNull(file2);

		IResource resource = getResource(file2);
		if (resource != null) {
			IFileRevision temp = file1;
			file1 = file2;
			file2 = temp;
		}
		ITypedElement left;
		if (file1 != null) {
			resource = getResource(file1);
			if (resource != null) {
				left = getElementFor(resource);
			} else {
				left = new FileRevisionTypedElement(file1, null);
			}
		} else {
			left = new EmptyTypedElement(file2.getName());
		}
		ITypedElement right = new FileRevisionTypedElement(file2, null);

		CompareFileRevisionEditorInput input = new CompareFileRevisionEditorInput(left, right, page);
		CompareUI.openCompareEditor(input, OpenStrategy.activateOnOpen());
	}

}

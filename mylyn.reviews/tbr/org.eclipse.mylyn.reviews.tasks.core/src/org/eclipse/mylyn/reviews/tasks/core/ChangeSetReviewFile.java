/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tasks.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeType;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author mattk
 *
 */
public class ChangeSetReviewFile implements IReviewFile {

	private Change change;

	public ChangeSetReviewFile(Change change) {
		this.change = change;
	}

	public String getFileName() {
		return change.getTarget().getPath();
	}

	public boolean isNewFile() {
		return change.getChangeType().equals(ChangeType.ADDED);
	}

	public boolean canReview() {
		return true;
	}

	public ICompareInput getCompareInput() {
		ICompareInput ci = new DiffNode(Differencer.CHANGE, null,
				new CompareItem(change.getBase()), new CompareItem(change.getTarget()));
		return ci;
	}
	
	static class CompareItem implements IStreamContentAccessor, ITypedElement {
		private final ScmArtifact artifact;

		public CompareItem(ScmArtifact artifact) {
			this.artifact=artifact;
		}

		public InputStream getContents() throws CoreException {
			if (artifact==null) return new ByteArrayInputStream(new byte[0]);
			// FIXME
			return artifact.getFileRevision(new NullProgressMonitor()).getStorage(new NullProgressMonitor()).getContents();
		}
		public Image getImage() {
			return null;
		}

		public String getName() {
			return artifact.getPath();
		}

		public String getType() {
			return ITypedElement.TEXT_TYPE;
		}
	}

}

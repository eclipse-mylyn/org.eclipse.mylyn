/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.io.InputStream;

import org.eclipse.compare.ICompareInputLabelProvider;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.commons.workbench.CommonImageManger;
import org.eclipse.mylyn.internal.reviews.ui.providers.FileItemNodeLabelProvider;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.swt.graphics.Image;

/**
 * @author Steffen Pingel
 * @author Sebastien Dubois
 */
public class FileItemNode extends DiffNode {

	private final IFileItem fileItem;

	private IPath path;

	private String name;

	private ICompareInputLabelProvider labelProvider;

	public FileItemNode(ReviewBehavior behavior, IFileItem fileItem, IProgressMonitor monitor) {
		super(Differencer.NO_CHANGE);
		this.fileItem = fileItem;
		String targetPath = fileItem.getTarget().getPath();
		int kind = Differencer.CHANGE;
		if (targetPath == null) {
			targetPath = fileItem.getBase().getPath();
			kind = Differencer.ADDITION;
		}
		String basePath = fileItem.getBase().getPath();
		if (basePath == null) {
			basePath = targetPath;
			kind = Differencer.DELETION;
		}
		if (targetPath.equals("/COMMIT_MSG")) { //$NON-NLS-1$
			kind = Differencer.NO_CHANGE;
		}

		setLeft(getElement(behavior, fileItem.getBase(), basePath, monitor));
		setRight(getElement(behavior, fileItem.getTarget(), targetPath, monitor));
		labelProvider = new FileItemNodeLabelProvider();

		setKind(kind);
		IPath path = Path.fromPortableString(targetPath);
		setPath(path);
		name = path.lastSegment();
	}

	//Check if we can match the reviewed file with the workspace history contents
	private ITypedElement getElement(ReviewBehavior behavior, IFileVersion reviewFileVersion, String path,
			IProgressMonitor monitor) {
		org.eclipse.team.core.history.IFileRevision repoFileRevision = behavior.getFileRevision(reviewFileVersion);
		if (repoFileRevision != null) {
			InputStream repoFileContents = null;
			try {
				repoFileContents = repoFileRevision.getStorage(monitor).getContents();
				if (repoFileContents != null) {
					//First option:  The file under review is in sync with a version in history for a workspace file,
					//open the file Revision.  Best effort navigability.
					return new FileRevisionTypedElement(repoFileRevision, monitor);
				}
			} catch (CoreException e) {
				// This can be safely ignored.  We will use the fallback option below
			}
		}
		//Fallback option: No match i.e the repository is not in workspace or does not contain this version of the file.  No navigability.
		return new ByteArrayInput(CompareUtil.getContent(reviewFileVersion), path);
	}

	public FileItemNode(String name) {
		super(Differencer.NO_CHANGE);
		this.name = name;
		fileItem = null;
	}

	public IFileItem getFileItem() {
		return fileItem;
	}

	private static CommonImageManger imageManger;

	@Override
	public Image getImage() {
		if (imageManger == null) {
			imageManger = new CommonImageManger();
		}
		return fileItem != null ? imageManger.getFileImage(getName()) : imageManger.getFolderImage();
	}

	@Override
	public String getName() {
		return name;
	}

	public IPath getPath() {
		return path;
	}

	@Override
	public String getType() {
		return fileItem != null ? super.getType() : FOLDER_TYPE;
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ICompareInputLabelProvider getLabelProvider() {
		return labelProvider;
	}
}

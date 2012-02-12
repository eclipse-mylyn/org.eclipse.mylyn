/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.commons.workbench.CommonImageManger;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.swt.graphics.Image;

/**
 * @author Steffen Pingel
 */
public class FileItemNode extends DiffNode {

	private final IFileItem fileItem;

	private IPath path;

	private String name;

	public FileItemNode(IFileItem fileItem) {
		super(Differencer.NO_CHANGE);
		this.fileItem = fileItem;
		byte[] targetContent = CompareUtil.getContent(fileItem.getTarget());
		byte[] baseContent = CompareUtil.getContent(fileItem.getBase());
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
		setLeft(new ByteArrayInput(targetContent, targetPath));
		setRight(new ByteArrayInput(baseContent, basePath));
		setKind(kind);
		IPath path = Path.fromPortableString(targetPath);
		setPath(path);
		this.name = path.lastSegment();
	}

	public FileItemNode(String name) {
		super(Differencer.NO_CHANGE);
		this.name = name;
		this.fileItem = null;
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
		return (fileItem != null) ? imageManger.getFileImage(getName()) : imageManger.getFolderImage();
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
		return (fileItem != null) ? super.getType() : FOLDER_TYPE;
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public void setName(String name) {
		this.name = name;
	}

}

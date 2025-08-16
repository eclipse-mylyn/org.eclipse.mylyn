/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public abstract class AbstractDataLocator {

	protected abstract IPath getSystemDataPath();

	protected abstract IPath getLocatorDataSegment();

	public final IPath getModelPath() {
		return getSystemDataPath().append(getLocatorDataSegment());
	}

	public IPath getFileScalingFragment(String fileName) {
		return new Path(""); //$NON-NLS-1$
	}

	public IPath getParentDir(String containerSegment, String typeFragment, String fileName) {
		IPath path = getModelPath();
		path = path.append(containerSegment);
		path = path.append(typeFragment);
		path = path.append(getFileScalingFragment(fileName));
		return path;
	}

	public IPath getFilePath(String containerSegment, String typeFragment, String itemName, String artifactExtension) {
		IPath path = getParentDir(containerSegment, typeFragment, itemName);
		path = path.append(itemName);
		path = path.addFileExtension(artifactExtension);
		return path;
	}

	public IPath getObjectPath(String containerSegment, String typeFragment, String itemName) {
		IPath path = new Path(containerSegment).makeAbsolute();
		path = path.append(typeFragment);
		path = path.append(itemName);
		return path;
	}

	public IPath getFilePathFromObjectPath(IPath path) {
		return getFilePath(path.segment(0), path.segment(1), path.removeFileExtension().lastSegment(),
				path.getFileExtension());
	}

	public IPath getObjectPathFromFilePath(IPath path) {
		if (getModelPath().isPrefixOf(path)) {
			String fragment = parseScalingFragment(path);
			path = path.makeRelativeTo(getModelPath());
			path = path.removeFileExtension();
			String cleanPath = StringUtils.remove(path.toPortableString(), fragment);
			return new Path(cleanPath).makeAbsolute();
		}
		return path;
	}

	public IPath normalize(IPath path) {
		if (getModelPath().isPrefixOf(path)) {
			path = path.makeRelativeTo(getModelPath());

			return getFilePath(path.segment(0), path.segment(1), path.removeFileExtension().lastSegment(),
					path.getFileExtension());
		} else {
			return path;
		}
	}

	public String parseFileType(IPath path) {
		return path.segment(path.segmentCount() - 2);
	}

	public String parseContainerSegment(IPath path) {
		if (getModelPath().isPrefixOf(path)) {
			path = path.makeRelativeTo(getModelPath());
		}
		return path.segment(0);
	}

	public String parseScalingFragment(IPath path) {
		path = path.makeRelativeTo(getModelPath());
		path = path.removeFirstSegments(2).removeFileExtension().removeLastSegments(1);
		return path.toString();
	}

	public String parseFileName(IPath path) {
		return path.removeFileExtension().lastSegment();
	}

	public void migrate() {
	}
}

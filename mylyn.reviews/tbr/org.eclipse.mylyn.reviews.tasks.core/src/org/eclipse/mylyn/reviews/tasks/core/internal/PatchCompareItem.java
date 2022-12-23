/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tasks.core.internal;

import java.io.InputStream;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.patch.IFilePatchResult;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 * @author Kilian Matt
 */
public class PatchCompareItem implements IStreamContentAccessor, ITypedElement {
	public enum Kind {
		ORIGINAL, PATCHED
	};

	private IFilePatchResult result;
	private Kind kind;
	private String filename;

	public PatchCompareItem(IFilePatchResult result, Kind kind, String filename) {
		this.result = result;
		this.kind = kind;
		this.filename = filename;
		if(result == null || filename==null)
			throw new NullPointerException();
	}

	public InputStream getContents() throws CoreException {
		return kind == Kind.ORIGINAL ? result.getOriginalContents() : result
				.getPatchedContents();
	}

	public Image getImage() {
		return null;
	}

	public String getName() {
		return filename;
	}

	public String getType() {
		return ITypedElement.TEXT_TYPE;
	}
}

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
package org.eclipse.mylyn.reviews.tasks.core;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.patch.IFilePatch2;
import org.eclipse.compare.patch.PatchParser;
import org.eclipse.compare.patch.ReaderCreator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.reviews.tasks.core.internal.PatchReviewFile;
import org.eclipse.mylyn.reviews.tasks.core.internal.ReviewConstants;

/**
 * 
 * @author mattk
 * 
 */
public class PatchScopeItem implements IReviewScopeItem {

	private Attachment attachment;

	public PatchScopeItem(Attachment attachment) {
		this.attachment = attachment;
	}

	public Attachment getAttachment() {
		return attachment;
	}

	private ReaderCreator getPatch() {
		return new ReaderCreator() {

			@Override
			public Reader createReader() throws CoreException {
				try {
					return new InputStreamReader(
							new URL(attachment.getUrl()).openStream());
				} catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR,
							ReviewConstants.PLUGIN_ID, e.getMessage()));
				}

			}
		};
	}

	public List<IReviewFile> getReviewFiles(
			NullProgressMonitor nullProgressMonitor) throws CoreException {
		IFilePatch2[] parsedPatch = PatchParser.parsePatch(getPatch());
		List<IReviewFile> files = new ArrayList<IReviewFile>();
		for (IFilePatch2 filePatch : parsedPatch) {
			files.add(new PatchReviewFile(filePatch));
		}
		return files;
	}

	public String getDescription() {
		return "Patch "+attachment.getFileName();
	}

	public String getType(int count) {
		return count ==1? "patch":"patches";
	}

}
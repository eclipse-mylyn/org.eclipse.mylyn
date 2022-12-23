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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.reviews.tasks.core.internal.ReviewConstants;
import org.eclipse.swt.graphics.Image;

/**
 * @author mattk
 *
 */
public class ResourceScopeItem implements IReviewScopeItem {
	private Attachment attachment;

	public ResourceScopeItem(Attachment attachment) {
		this.attachment = attachment;
	}

	public Attachment getAttachment() {
		return attachment;
	}

	public List<IReviewFile> getReviewFiles(NullProgressMonitor monitor)
			throws CoreException {

		return Arrays.asList((IReviewFile) new ResourceReviewFile(attachment
				.getFileName(), attachment.getUrl()));
	}

	private static class ResourceReviewFile implements IReviewFile {

		private ICompareInput compareInput;
		private String fileName;
		private String url;

		public ResourceReviewFile(String fileName, String url) {
			this.fileName = fileName;
			this.url = url;
		}

		public ICompareInput getCompareInput() {
			if (compareInput == null) {
				ICompareInput ci = new DiffNode(Differencer.CHANGE, null,
						new CompareItem(fileName) {
							@Override
							public InputStream getContents()
									throws CoreException {
								return new ByteArrayInputStream(new byte[0]);
							}
						}, new CompareItem(fileName) {
							@Override
							public InputStream getContents()
									throws CoreException {
								try {
									return new URL(url)
											.openStream();
								} catch (Exception e) {
									throw new CoreException(new Status(
											IStatus.ERROR,
											ReviewConstants.PLUGIN_ID, e
													.getMessage()));
								}
							}
						});
				compareInput = ci;
			}
			return compareInput;

		}

		public String getFileName() {
			return fileName;
		}

		public boolean isNewFile() {
			return true;
		}

		public boolean canReview() {
			return true;

		}

		private static abstract class CompareItem implements IStreamContentAccessor,
				ITypedElement {

			private String filename;

			public CompareItem(String filename) {
				this.filename = filename;
			}

			public abstract InputStream getContents() throws CoreException;

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

	}

	public String getDescription() {
		return "Attachment "+attachment.getFileName();
	}

	public String getType(int count) {
		return count==1?"resource":"resources";
	}


}

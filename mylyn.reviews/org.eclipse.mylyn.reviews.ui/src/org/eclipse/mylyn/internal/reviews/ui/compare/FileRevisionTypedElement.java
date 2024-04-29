/*******************************************************************************
 * Copyright (c) 2013, Ericsson AB and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastien Dubois (Ericsson) - Adapted to use with Mylyn Reviews
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ISharedDocumentAdapter;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.SharedDocumentAdapter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * An {@link ITypedElement} wrapper for {@link IFileRevision} for use with Mylyn Reviews
 *
 * @author Sebastien Dubois
 */
public class FileRevisionTypedElement implements IAdaptable, IStreamContentAccessor, ITypedElement {

	private final IFileRevision fileRevision;

	private final IProgressMonitor runningMonitor;

	private ISharedDocumentAdapter sharedDocumentAdapter;

	public FileRevisionTypedElement(IFileRevision fileRevision, IProgressMonitor monitor) {
		this.fileRevision = fileRevision;
		runningMonitor = monitor;
	}

	private IFileRevision getFileRevision() {
		return fileRevision;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (ISharedDocumentAdapter.class.equals(adapter)) {
			synchronized (this) {
				if (sharedDocumentAdapter == null) {
					sharedDocumentAdapter = new SharedDocumentAdapter() {
						@Override
						public IEditorInput getDocumentKey(Object element) {
							return FileRevisionTypedElement.this.getDocumentKey(element);
						}

						@Override
						public void flushDocument(IDocumentProvider provider, IEditorInput documentKey,
								IDocument document, boolean overwrite) {
							// The document is read-only
						}
					};
				}
				return adapter.cast(sharedDocumentAdapter);
			}
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	@Override
	public InputStream getContents() throws CoreException {
		return fileRevision.getStorage(runningMonitor).getContents();
	}

	public IEditorInput getDocumentKey(Object element) {
		if (element instanceof FileRevisionTypedElement) {
			if (element.equals(this)) {
				return new FileRevisionEditorInput(fileRevision, runningMonitor);
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return fileRevision.getName();
	}

	@Override
	public Image getImage() {
		return CompareUI.getImage(getType());
	}

	@Override
	public String getType() {
		String extension = FilenameUtils.getExtension(getName());
		return extension != null && extension.length() > 0 ? extension : ITypedElement.TEXT_TYPE;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof final FileRevisionTypedElement otherElement) {
			return otherElement.getFileRevision().equals(fileRevision);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return fileRevision.hashCode();
	}

}

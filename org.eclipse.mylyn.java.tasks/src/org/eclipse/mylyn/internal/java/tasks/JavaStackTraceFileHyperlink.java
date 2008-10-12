/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.tasks;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.ui.IHighlightingHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Rob Elves
 */
public class JavaStackTraceFileHyperlink implements IHyperlink, IHighlightingHyperlink {

	private final IRegion region;

	private final String traceLine;

	private final IRegion highlightingRegion;

	public JavaStackTraceFileHyperlink(IRegion region, String traceLine, IRegion highlightingRegion) {
		this.region = region;
		this.traceLine = traceLine;
		this.highlightingRegion = highlightingRegion;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getHyperlinkText() {
		// ignore
		return null;
	}

	public String getTypeLabel() {
		// ignore
		return null;
	}

	public void open() {

		try {

			String typeName = getTypeName();
			int lineNumber = getLineNumber();

			// documents start at 0
			if (lineNumber > 0) {
				lineNumber--;
			}

			startSourceSearch(typeName, lineNumber);

		} catch (CoreException e1) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Type",
					"Failed to open type.");
			return;
		}

	}

	/**
	 * Starts a search for the type with the given name. Reports back to 'searchCompleted(...)'.
	 * 
	 * @param typeName
	 *            the type to search for
	 */
	protected void startSourceSearch(final String typeName, final int lineNumber) {
		Job search = new Job("Searching...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					// search for the type in the workspace
					Object result = OpenTypeAction.findTypeInWorkspace(typeName);
					searchCompleted(result, typeName, lineNumber, null);
				} catch (CoreException e) {
					searchCompleted(null, typeName, lineNumber, e.getStatus());
				}
				return Status.OK_STATUS;
			}

		};
		search.schedule();
	}

	protected void searchCompleted(final Object source, final String typeName, final int lineNumber,
			final IStatus status) {
		UIJob job = new UIJob("link search complete") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (source == null) {
					// did not find source
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Open Type", "Type could not be located.");
				} else {
					processSearchResult(source, typeName, lineNumber);
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * The search succeeded with the given result
	 * 
	 * @param source
	 *            resolved source object for the search
	 * @param typeName
	 *            type name searched for
	 * @param lineNumber
	 *            line number on link
	 */
	protected void processSearchResult(Object source, String typeName, int lineNumber) {
		IDebugModelPresentation presentation = JDIDebugUIPlugin.getDefault().getModelPresentation();
		IEditorInput editorInput = presentation.getEditorInput(source);
		if (editorInput != null) {
			String editorId = presentation.getEditorId(editorInput, source);
			if (editorId != null) {
				try {
					IEditorPart editorPart = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.openEditor(editorInput, editorId);
					if (editorPart instanceof ITextEditor && lineNumber >= 0) {
						ITextEditor textEditor = (ITextEditor) editorPart;
						IDocumentProvider provider = textEditor.getDocumentProvider();
						provider.connect(editorInput);
						IDocument document = provider.getDocument(editorInput);
						try {
							IRegion line = document.getLineInformation(lineNumber);
							textEditor.selectAndReveal(line.getOffset(), line.getLength());
						} catch (BadLocationException e) {
							MessageDialog.openInformation(PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getShell(), "Open Type", "Line not found in type.");
						}
						provider.disconnect(editorInput);
					}
				} catch (CoreException e) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Open Type", "Failed to open type.");
				}
			}
		}
	}

	// adapted from JavaStackTraceHyperlink
	private String getTypeName() {
		int start = traceLine.indexOf('(');
		int end = traceLine.indexOf(':');
		if (start >= 0 && end > start) {

			// get File name (w/o .java)
			String typeName = traceLine.substring(start + 1, end);
			typeName = typeName.substring(0, typeName.indexOf("."));

			String qualifier = traceLine.substring(0, start);
			// remove the method name
			start = qualifier.lastIndexOf('.');

			if (start >= 0) {
				// remove the class name
				start = (qualifier.subSequence(0, start).toString()).lastIndexOf('.');
				if (start == -1) {
					start = 0; // default package
				}
			}

			if (start >= 0) {
				qualifier = qualifier.substring(0, start);
			}

			if (qualifier.length() > 0) {
				typeName = qualifier + "." + typeName; //$NON-NLS-1$
			}
			return typeName.trim();
		}

		return "error"; // TODO: Complain
	}

	// adapted from JavaStackTraceHyperlink
	private int getLineNumber() throws CoreException {
		int index = traceLine.lastIndexOf(':');
		if (index >= 0) {
			String numText = traceLine.substring(index + 1);
			index = numText.indexOf(')');
			if (index >= 0) {
				numText = numText.substring(0, index);
			}
			try {
				return Integer.parseInt(numText);
			} catch (NumberFormatException e) {
				throw new CoreException(null);
			}
		}

		throw new CoreException(null);
	}

	public IRegion getHighlightingRegion() {
		return (highlightingRegion != null) ? highlightingRegion : region;
	}

}

/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Rob Elves
 */
public class JavaStackTraceFileHyperlink implements IHyperlink {

	private IRegion region;

	private String traceLine;

	public JavaStackTraceFileHyperlink(IRegion region, String traceLine) {
		this.region = region;
		this.traceLine = traceLine;
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

		int lineNumber;
		try {

			String typeName = getTypeName();
			lineNumber = getLineNumber();

			// documents start at 0
			if (lineNumber > 0) {
				lineNumber--;
			}
			Object sourceElement = getSourceElement(typeName);
			if (sourceElement != null) {
				IDebugModelPresentation presentation = JDIDebugUIPlugin.getDefault().getModelPresentation();
				IEditorInput editorInput = presentation.getEditorInput(sourceElement);
				if (editorInput != null) {
					String editorId = presentation.getEditorId(editorInput, sourceElement);
					if (editorId != null) {
						IEditorPart editorPart = JDIDebugUIPlugin.getActivePage().openEditor(editorInput, editorId);
						if (editorPart instanceof ITextEditor && lineNumber >= 0) {
							ITextEditor textEditor = (ITextEditor) editorPart;
							IDocumentProvider provider = textEditor.getDocumentProvider();
							provider.connect(editorInput);
							IDocument document = provider.getDocument(editorInput);
							try {
								IRegion line = document.getLineInformation(lineNumber);
								textEditor.selectAndReveal(line.getOffset(), line.getLength());
							} catch (BadLocationException e1) {
								MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
										.getShell(), "Open Type", "Line not found in type.");
							}
							provider.disconnect(editorInput);
						}
						return;
					}
				}
			}
			// did not find source
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Type",
					"Type could not be located.");
		} catch (CoreException e1) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Type",
					"Failed to open type.");
			return;
		}

	}

	// adapted from JavaStackTraceHyperlink
	private Object getSourceElement(String typeName) throws CoreException {
		Object result = null;
		result = OpenTypeAction.findTypeInWorkspace(typeName);
		// }
		return result;
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

}

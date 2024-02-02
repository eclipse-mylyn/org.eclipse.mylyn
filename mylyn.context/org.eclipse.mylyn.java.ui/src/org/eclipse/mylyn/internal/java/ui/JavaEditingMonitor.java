/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.java.ui.search.JavaImplementorsProvider;
import org.eclipse.mylyn.internal.java.ui.search.JavaReferencesProvider;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
public class JavaEditingMonitor extends AbstractUserInteractionMonitor {

	protected IJavaElement lastSelectedElement = null;

	protected IJavaElement lastResolvedElement = null;

	protected JavaEditor currentEditor;

	protected StructuredSelection currentSelection = null;

	public JavaEditingMonitor() {
	}

	/**
	 * Only public for testing. Note: Two sequential selections on the same element are deemed to be an edit of the selection as this is the
	 * best guess that can be made. See bug 252306.
	 */
	@Override
	public void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection, boolean contributeToContext) {
		try {
			IJavaElement selectedElement = null;
			if (selection instanceof StructuredSelection structuredSelection) {
				if (structuredSelection.equals(currentSelection)) {
					return;
				}
				currentSelection = structuredSelection;

				for (Object selectedObject : structuredSelection) {
					if (selectedObject instanceof IJavaElement) {
						IJavaElement checkedElement = checkIfAcceptedAndPromoteIfNecessary(
								(IJavaElement) selectedObject);
						if (checkedElement == null) {
							return;
						} else {
							selectedElement = checkedElement;
						}
					}
					if (selectedElement != null) {
						super.handleElementSelection(part, selectedElement, contributeToContext);
					}
				}
			} else if (selection instanceof TextSelection && part instanceof JavaEditor) {
				currentEditor = (JavaEditor) part;
				TextSelection textSelection = (TextSelection) selection;
				selectedElement = SelectionConverter.resolveEnclosingElement(currentEditor, textSelection);
				if (selectedElement instanceof IPackageDeclaration) {
					// HACK: ignoring these selections
					return;
				}
				IJavaElement[] resolved = SelectionConverter.codeResolve(currentEditor);
				if (resolved != null && resolved.length == 1 && !resolved[0].equals(selectedElement)) {
					lastResolvedElement = resolved[0];
				}

				boolean selectionResolved = false;
				if (selectedElement instanceof IMethod && lastSelectedElement instanceof IMethod) {
					// navigation between two elements
					if (lastResolvedElement != null && lastSelectedElement != null
							&& lastResolvedElement.equals(selectedElement)
							&& !lastSelectedElement.equals(lastResolvedElement)) {
						super.handleNavigation(part, selectedElement, JavaReferencesProvider.ID, contributeToContext);
						selectionResolved = true;
					} else if (lastSelectedElement != null && lastSelectedElement.equals(lastResolvedElement)
							&& !lastSelectedElement.equals(selectedElement)) {
						super.handleNavigation(part, selectedElement, JavaReferencesProvider.ID, contributeToContext);
						selectionResolved = true;
					}
				} else if (selectedElement != null && lastSelectedElement != null
						&& !lastSelectedElement.equals(selectedElement)) {
					if (lastSelectedElement.getElementName().equals(selectedElement.getElementName())) {
						// navigation between two elements
						if (selectedElement instanceof IMethod && lastSelectedElement instanceof IMethod) {
							super.handleNavigation(part, selectedElement, JavaImplementorsProvider.ID,
									contributeToContext);
							selectionResolved = true;
						} else if (selectedElement instanceof IType && lastSelectedElement instanceof IType) {
							super.handleNavigation(part, selectedElement, JavaImplementorsProvider.ID,
									contributeToContext);
							selectionResolved = true;
						}
					}
				}
				if (selectedElement != null) {
					// selection of an element
					if (!selectionResolved && selectedElement.equals(lastSelectedElement)) {
						super.handleElementEdit(part, selectedElement, contributeToContext);
					} else if (!selectedElement.equals(lastSelectedElement)) {
						super.handleElementSelection(part, selectedElement, contributeToContext);
					}
				}

				IJavaElement checkedElement = checkIfAcceptedAndPromoteIfNecessary(selectedElement);
				if (checkedElement == null) {
					return;
				} else {
					selectedElement = checkedElement;
				}
			}
			if (selectedElement != null) {
				lastSelectedElement = selectedElement;
			}
		} catch (JavaModelException e) {
			// ignore, fine to fail to resolve an element if the model is not up-to-date
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
					"Failed to update model based on selection", t)); //$NON-NLS-1$
		}
	}

	/**
	 * @return null for elements that aren't modeled
	 */
	protected IJavaElement checkIfAcceptedAndPromoteIfNecessary(IJavaElement element) {
		// if (element instanceof IPackageDeclaration) return null;
		if (element instanceof IImportContainer) {
			return element.getParent();
		} else if (element instanceof IImportDeclaration) {
			return element.getParent().getParent();
		} else {
			return element;
		}
	}

	/**
	 * For testing only.
	 */
	public void resetLastSelectedElement() {
		lastSelectedElement = null;
	}

}

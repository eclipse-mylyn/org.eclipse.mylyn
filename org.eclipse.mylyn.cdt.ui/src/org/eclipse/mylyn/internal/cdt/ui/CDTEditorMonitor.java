/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui;

import java.util.Iterator;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IFunction;
import org.eclipse.cdt.core.model.IMethod;
import org.eclipse.cdt.internal.ui.actions.SelectionConverter;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class CDTEditorMonitor extends AbstractUserInteractionMonitor {

	protected ICElement lastSelectedElement = null;

	protected ICElement lastResolvedElement = null;

	protected CEditor currentEditor;

	protected StructuredSelection currentSelection = null;

	public CDTEditorMonitor() {
		super();
	}

	/**
	 * Only public for testing
	 */
	@Override
	public void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection, boolean contributeToContext) {
		try {
			ICElement selectedElement = null;
			if (selection instanceof StructuredSelection) {
				StructuredSelection structuredSelection = (StructuredSelection) selection;

				if (structuredSelection.equals(currentSelection)) {
					return;
				}
				currentSelection = structuredSelection;

//				Object selectedObject = structuredSelection.getFirstElement();
				for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
					Object selectedObject = iterator.next();					
					if (selectedObject instanceof ICElement) {
						ICElement checkedElement = checkIfAcceptedAndPromoteIfNecessary((ICElement) selectedObject);
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
			} else {
				if (part instanceof CEditor) {
					currentEditor = (CEditor) part;
					selectedElement = SelectionConverter.getElementAtOffset(currentEditor);
					if (selectedElement == null)
						return; // nothing selected

					if (selectedElement != null) {
						if (selectedElement.equals(lastSelectedElement)) {
							super.handleElementEdit(part, selectedElement, contributeToContext);
						} else if (!selectedElement.equals(lastSelectedElement)) {
							super.handleElementSelection(part, selectedElement, contributeToContext);
						}
					}

					ICElement checkedElement = checkIfAcceptedAndPromoteIfNecessary(selectedElement);
					if (checkedElement == null) {
						return;
					} else {
						selectedElement = checkedElement;
					}
				}
			}
			if (selectedElement != null) {
				lastSelectedElement = selectedElement;
			}
		} catch (CModelException e) {
			// ignore, fine to fail to resolve an element if the model is not up-to-date
		} catch (Throwable t) {
			StatusHandler.log(t, CDTUIBridgePlugin.getResourceString("MylynCDT.log.updateModelFailed")); // $NON-NLS-1$
		}
	}

	/**
	 * @return null for elements that aren't modeled
	 */
	protected ICElement checkIfAcceptedAndPromoteIfNecessary(ICElement element) {
		if (element instanceof IMethod || element instanceof IFunction)
			return element;
		return null;
	}
}

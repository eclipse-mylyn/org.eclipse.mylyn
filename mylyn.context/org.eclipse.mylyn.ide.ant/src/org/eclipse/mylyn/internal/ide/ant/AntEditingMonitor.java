/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ant;

import org.eclipse.ant.internal.ui.editor.AntEditor;
import org.eclipse.ant.internal.ui.model.AntElementNode;
import org.eclipse.ant.internal.ui.model.AntModel;
import org.eclipse.ant.internal.ui.model.AntProjectNode;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.ide.ui.XmlNodeHelper;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public class AntEditingMonitor extends AbstractUserInteractionMonitor {

	public AntEditingMonitor() {
	}

	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection,
			boolean contributeToContext) {
		if (part instanceof AntEditor) {

			TextSelection textSelection = null;
			IEditorInput in = null;

			// assume that we are editing an xml file due to the editor used
			// this is the build.xml and other ant file editor
			AntEditor editor = (AntEditor) part;

			if (!(editor.getSelectionProvider().getSelection() instanceof TextSelection)) {
				return;
			}

			textSelection = (TextSelection) editor.getSelectionProvider().getSelection();
			in = editor.getEditorInput();

			// check if we have a text selection
			if (textSelection != null && editor.getAntModel() != null) {
				try {
					AntElementNode node = editor.getAntModel().getNode(textSelection.getOffset(), false);
					if (node == null) {
						return;
					}

					FileEditorInput fei = (FileEditorInput) in;
//					Method method = AntElementNode.class.getDeclaredMethod("getElementPath", new Class[] {});
//					method.setAccessible(true);
//					String path = (String) method.invoke(node, new Object[] {});
					String path = node.getElementPath();
					if (path == null) {
						return;
					}
					XmlNodeHelper xnode = new XmlNodeHelper(fei.getFile().getFullPath().toString(), path);
					super.handleElementSelection(part, xnode, contributeToContext);
				} catch (Exception e) {
					StatusHandler.log(
							new Status(IStatus.ERROR, AntUiBridgePlugin.ID_PLUGIN, "Resolving selection failed", e)); //$NON-NLS-1$
				}
			}
		}
		return;
	}

	public static AntElementNode getNode(AntModel antModel, String elementPath)
			throws SecurityException, NoSuchMethodException {
		AntProjectNode topNode;
		try {
			topNode = antModel.getProjectNode();
			return getNode(topNode, elementPath);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * HACK: using reflection to gain accessibility
	 */
	private static AntElementNode getNode(AntElementNode topNode, String elementPath)
			throws NoSuchMethodException, IllegalAccessException {
		if (topNode == null) {
			return null;
		}

//		Method method = AntElementNode.class.getDeclaredMethod("getElementPath", new Class[] {});
//		method.setAccessible(true);
//		String path = (String) method.invoke(topNode, new Object[] {});
		String path = topNode.getElementPath();
		if (path.compareTo(elementPath) == 0) {
			return topNode;
		}

		if (topNode.getChildNodes() == null) {
			return null;
		}

		for (Object obj : topNode.getChildNodes()) {
			if (obj instanceof AntElementNode node) {
				//				path = (String) method.invoke(node, new Object[] {});
				path = node.getElementPath();
				if (path.compareTo(elementPath) == 0) {
					return node;
				} else {
					AntElementNode node2 = getNode(node, elementPath);
					if (node2 != null) {
						return node2;
					}
				}
			}
		}
		return null;
	}
}

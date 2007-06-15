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
/*
 * Created on Apr 1, 2005
 */
package org.eclipse.mylyn.internal.ide.ant;

//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;

import org.eclipse.ant.internal.ui.editor.AntEditor;
import org.eclipse.ant.internal.ui.model.AntElementNode;
import org.eclipse.ant.internal.ui.model.AntModel;
import org.eclipse.ant.internal.ui.model.AntProjectNode;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.ide.xml.XmlNodeHelper;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Mik Kersten
 */
public class AntEditingMonitor extends AbstractUserInteractionMonitor {

	public AntEditingMonitor() {
		super();
	}

	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection, boolean contributeToContext) {
		if (part instanceof AntEditor) {

			TextSelection textSelection = null;
			IEditorInput in = null;

			// assume that we are editing an xml file due to the editor used
			// this is the build.xml and other ant file editor
			AntEditor editor = (AntEditor) part;

			if (!(editor.getSelectionProvider().getSelection() instanceof TextSelection))
				return;

			textSelection = (TextSelection) editor.getSelectionProvider().getSelection();
			in = editor.getEditorInput();

			// check if we have a text selection
			if (textSelection != null && editor != null && editor.getAntModel() != null) {
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
					StatusManager.log(e, "selection resolve failed");
				}
			}
		}
		return;
	}

	public static AntElementNode getNode(AntModel antModel, String elementPath) throws SecurityException,
			NoSuchMethodException {
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
	private static AntElementNode getNode(AntElementNode topNode, String elementPath) throws NoSuchMethodException,
			IllegalAccessException {
		if (topNode == null)
			return null;

//		Method method = AntElementNode.class.getDeclaredMethod("getElementPath", new Class[] {});
//		method.setAccessible(true);
//		String path = (String) method.invoke(topNode, new Object[] {});
		String path = topNode.getElementPath();
		if (path.compareTo(elementPath) == 0) {
			return topNode;
		}

		if (topNode.getChildNodes() == null)
			return null;

		for (Object obj : topNode.getChildNodes()) {
			if (obj instanceof AntElementNode) {
				AntElementNode node = (AntElementNode) obj;
//				path = (String) method.invoke(node, new Object[] {});
				path = node.getElementPath();
				if (path.compareTo(elementPath) == 0) {
					return node;
				} else {
					AntElementNode node2 = getNode(node, elementPath);
					if (node2 != null)
						return node2;
				}
			}
		}
		return null;
	}
}

/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.pde.ui;

import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.ide.ui.XmlNodeHelper;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.internal.core.plugin.ImportObject;
import org.eclipse.pde.internal.core.text.IDocumentAttributeNode;
import org.eclipse.pde.internal.core.text.IDocumentElementNode;
import org.eclipse.pde.internal.core.text.plugin.PluginModel;
import org.eclipse.pde.internal.core.text.plugin.PluginObjectNode;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestSourcePage;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public class PdeEditingMonitor extends AbstractUserInteractionMonitor {

	public PdeEditingMonitor() {
		super();
	}

	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection, boolean contributeToContext) {
		if (part instanceof ManifestEditor) {
			TextSelection textSelection = null;
			IEditorInput in = null;

			// assume that we are editing an xml file due to the editor used
			// this is the plugin.xml editor

			ManifestEditor editor = (ManifestEditor) part;

			// fix bug when user is looking in the cvs repository since the
			// input
			// is not a FileEditorInput
			if (!(editor.getEditorInput() instanceof FileEditorInput)) {
				return;
			}

			// make sure that the selection is a text selection
			if (!(editor.getSelection() instanceof TextSelection || editor.getSelection() instanceof StructuredSelection)) {
				return;
			} else if (editor.getSelection() instanceof StructuredSelection) {
				StructuredSelection s = (StructuredSelection) editor.getSelection();
				if (s.getFirstElement() instanceof PluginObjectNode) {
					PluginObjectNode n = (PluginObjectNode) s.getFirstElement();
					textSelection = new TextSelection(n.getOffset(), n.getLength());
				} else if (s.getFirstElement() instanceof ImportObject) {
					ImportObject io = (ImportObject) s.getFirstElement();
					if (io.getImport() instanceof PluginObjectNode) {
						PluginObjectNode n = (PluginObjectNode) io.getImport();
						textSelection = new TextSelection(n.getOffset(), n.getLength());
					}
				} else {
					return;
				}
			} else {
				// get the selection and the editor input
				textSelection = (TextSelection) editor.getSelection();
			}
			in = editor.getEditorInput();

			// check if we have a text selection
			if (textSelection != null) {
				try {

					// get the node for the selection
					int offset = textSelection.getOffset();
					PluginObjectNode node = getNode(editor, offset, false);

					if (node != null) {
						FileEditorInput fei = (FileEditorInput) in;

						// fix a bug when there is a selection and the editor
						// input is the manifest.mf file
						// not the plugin.xml
						if (fei.getFile().getFullPath().toString().toLowerCase(Locale.ENGLISH).endsWith("/manifest.mf")) { //$NON-NLS-1$
							return;
						}

						String nodeString = getStringOfNode(node);
						if (nodeString == null) {
							return;
						}

						// create the helper to get the handle for the node
						XmlNodeHelper xnode = new XmlNodeHelper(fei.getFile().getFullPath().toString(),
								nodeString.hashCode());

						// get the name for the node
//						String name = node.getXMLAttributeValue("name");
//						if (name == null)
//							name = node.getXMLTagName();
						super.handleElementSelection(part, xnode, contributeToContext);
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, PdeUiBridgePlugin.ID_PLUGIN,
							"Could not resolve selection", e)); //$NON-NLS-1$
				}
			}
		}
	}

	public static String getStringOfNode(IDocumentElementNode node) {
		if (node == null) {
			return null;
		}
		String s = node.getXMLTagName();
		for (IDocumentAttributeNode a : node.getNodeAttributes()) {
			s += a.getAttributeName() + "=" + a.getAttributeValue(); //$NON-NLS-1$
		}
		return s;
	}

	/**
	 * COPIED FROM ManifestSourcePage - from a getRangeElement body
	 */
	public static PluginObjectNode getNode(ManifestEditor editor, int offset, boolean hashCode) {
		ManifestSourcePage page = (ManifestSourcePage) editor.findPage("plugin-context"); //$NON-NLS-1$
		if (page != null) {
			IPluginModelBase model = (IPluginModelBase) page.getInputContext().getModel();
			PluginObjectNode node = (PluginObjectNode) PdeEditingMonitor.findNode(model.getPluginBase().getLibraries(),
					offset, hashCode);
			if (node == null) {
				node = (PluginObjectNode) PdeEditingMonitor.findNode(model.getPluginBase().getImports(), offset,
						hashCode);
			}
			if (node == null) {
				node = (PluginObjectNode) PdeEditingMonitor.findNode(model.getPluginBase().getExtensionPoints(),
						offset, hashCode);
			}
			if (node == null) {
				node = (PluginObjectNode) PdeEditingMonitor.findNode(model.getPluginBase().getExtensions(), offset,
						hashCode);
			}
			if (node == null) {
				node = (PluginObjectNode) PdeEditingMonitor.findNode(new IPluginObject[] { model.getPluginBase() },
						offset, hashCode);
			}
			return node;
		}
		return null;
	}

	public static PluginObjectNode getNode(IDocument d, IFile f, int num, boolean hashCode) throws CoreException {
		PluginModel model = new PluginModel(d, true);
		model.setUnderlyingResource(f);
		if (!model.isLoaded()) {
			model.load();
			model.setEnabled(true);
		}

		PluginObjectNode node = (PluginObjectNode) PdeEditingMonitor.findNode(model.getPluginBase().getLibraries(),
				num, hashCode);
		if (node == null) {
			node = (PluginObjectNode) PdeEditingMonitor.findNode(model.getPluginBase().getImports(), num, hashCode);
		}
		if (node == null) {
			node = (PluginObjectNode) PdeEditingMonitor.findNode(model.getPluginBase().getExtensionPoints(), num,
					hashCode);
		}
		if (node == null) {
			node = (PluginObjectNode) PdeEditingMonitor.findNode(model.getPluginBase().getExtensions(), num, hashCode);
		}
		if (node == null) {
			node = (PluginObjectNode) PdeEditingMonitor.findNode(new IPluginObject[] { model.getPluginBase() }, num,
					hashCode);
		}
		return node;
	}

	/**
	 * COPIED FROM ManifestSourcePage
	 */
	private static IDocumentElementNode findNode(IPluginObject[] nodes, int offset, boolean hashCode) {
		for (IPluginObject node3 : nodes) {
			IDocumentElementNode node = (IDocumentElementNode) node3;
			IDocumentElementNode[] children = node.getChildNodes();

			// changed region - added to check the children to make it work
			// properly
			IDocumentElementNode node2 = null;
			if (children.length > 0) {
				node2 = PdeEditingMonitor.findNode(children, offset, hashCode);
				// end changed region
			}

			if (node2 != null && node2 instanceof IPluginObject) {
				return node2;
			}

			if (!hashCode) {
				if (offset >= node.getOffset() && offset < node.getOffset() + node.getLength()) {
					return node;
				}
			} else {
				if (getStringOfNode(node).hashCode() == offset) {
					return node;
				}
			}
		}
		return null;
	}

	/**
	 * Copy of previous, taking different arguments
	 */
	private static IDocumentElementNode findNode(IDocumentElementNode[] nodes, int offset, boolean hashCode) {
		for (IDocumentElementNode node : nodes) {
			IDocumentElementNode[] children = node.getChildNodes();
			IDocumentElementNode node2 = null;
			if (children.length > 0) {
				node2 = PdeEditingMonitor.findNode(children, offset, hashCode);
			}
			if (node2 != null) {
				return node2;
			}

			if (!hashCode) {
				if (offset >= node.getOffset() && offset < node.getOffset() + node.getLength()) {
					return node;
				}
			} else {
				if (getStringOfNode(node).hashCode() == offset) {
					return node;
				}
			}
		}
		return null;
	}
}

// XXX used if we support the xmen editor
// if (part instanceof XMLTextEditor) {
// XMLTextEditor editor = (XMLTextEditor)part;
// TextSelection textSelection =
// (TextSelection)editor.getSelectionProvider().getSelection();
// if (textSelection != null) {
//
// try{
// // get the model for the xml elements
// XMLReconciler rec = editor.getModel();
//                    
// int start = textSelection.getStartLine();
// int end = textSelection.getEndLine();
//                    
// // get the node that was selected
// XMLNode node = rec.getNodeFromLine(start);
//                    
// ContextCorePlugin.getTaskscapeManager().handleElementSelected(
// node.getHandle(), node.getCanName(),
// IDegreeOfInterest.Value.Selections,
// ITaskscapeNode.Kind.XML);
//                              
// }catch(Exception e){
// //handle properly
// }
// }

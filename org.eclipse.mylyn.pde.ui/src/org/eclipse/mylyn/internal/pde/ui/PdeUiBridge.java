/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Apr 6, 2005
 */
package org.eclipse.mylyn.internal.pde.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.resources.ResourcesUiBridgePlugin;
import org.eclipse.pde.internal.core.text.plugin.PluginObjectNode;
import org.eclipse.pde.internal.ui.editor.FormOutlinePage;
import org.eclipse.pde.internal.ui.editor.ISortableContentOutlinePage;
import org.eclipse.pde.internal.ui.editor.PDEFormEditor;
import org.eclipse.pde.internal.ui.editor.PDESourcePage;
import org.eclipse.pde.internal.ui.editor.SourceOutlinePage;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.pde.internal.ui.editor.plugin.PluginInputContext;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class PdeUiBridge extends AbstractContextUiBridge {

	private final TreeViewerListener treeSelectionChangedListener;

	public PdeUiBridge() {
		treeSelectionChangedListener = new TreeViewerListener();
	}

	/**
	 * @see org.eclipse.mylyn.context.ui.AbstractContextUiBridge#open(org.eclipse.mylyn.context.core.IInteractionElement)
	 */
	@Override
	public void open(IInteractionElement node) {
		// get the handle of the node
		String handle = node.getHandleIdentifier();

		int first = handle.indexOf(";");
		String filename = "";
		if (first == -1) {
			filename = handle;
		} else {
			filename = handle.substring(0, first);
		}

		try {
			// get the file
			IPath path = new Path(filename);
			IFile f = (IFile) ((Workspace) ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);

			// open the xml file looking at the proper line
			IEditorPart editor = openInEditor(f, true);

			// if the editor is null, we had a problem and should return
			if (editor == null) {
				StatusHandler.log(new Status(IStatus.WARNING, PdeUiBridgePlugin.ID_PLUGIN,
						"Unable to open editor for file: " + filename));
				return;
			}

			// get the contents and create a new document so that we can get
			// the offsets to highlight
			// String content = XmlNodeHelper.getContents(f.getContents());
			//            
			// IDocument d = new Document(content);

			// if(first != -1){
			// int start = Integer.parseInt(handle.substring(first + 1));
			//                
			// // get the offsets for the element
			// int startOffset = d.getLineOffset(start);
			// int length = 0;
			//                
			// // set the selection if the selection provider is not null
			// ISelectionProvider selectionProvider =
			// editor.getEditorSite().getSelectionProvider();
			// if(selectionProvider != null)
			// selectionProvider.setSelection(new TextSelection(startOffset,
			// length));
			// }

		} catch (Exception e) {
			// ContextCorePlugin.log(e, "ERROR OPENING XML EDITOR\n" +
			// e.getMessage());
		}
	}

	/**
	 * Open a file in the appropriate editor
	 * 
	 * @param file
	 *            The IFile to open
	 * @param activate
	 *            Whether to activate the editor or not
	 * @return The IEditorPart that the file opened in
	 * @throws PartInitException
	 */
	private IEditorPart openInEditor(IFile file, boolean activate) throws PartInitException {
		if (file != null) {
			IWorkbenchPage p = ResourcesUiBridgePlugin.getDefault()
					.getWorkbench()
					.getActiveWorkbenchWindow()
					.getActivePage();
			if (p != null && file.exists()) {
				IEditorPart editorPart = IDE.openEditor(p, file, activate);
				// initializeHighlightRange(editorPart);
				return editorPart;
			}
		}
		return null;
	}

	@Override
	public void close(IInteractionElement node) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IEditorReference[] references = page.getEditorReferences();
			for (IEditorReference reference : references) {
				IEditorPart part = reference.getEditor(false);
				if (part != null) {
					// HACK: find better way to get the filename other than the tooltip
					if (("/" + part.getTitleToolTip()).equals(node.getHandleIdentifier())) {
						if (part instanceof FormEditor) {
							((FormEditor) part).close(true);
						} else if (part instanceof AbstractTextEditor) {
							((AbstractTextEditor) part).close(true);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart instanceof ManifestEditor;
	}

	/**
	 * HACK: use a lot of reflection to get the TreeViewer
	 */
	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editor) {
		if (editor instanceof PDEFormEditor) {
			PDESourcePage sp = null;
			List<TreeViewer> viewers = new ArrayList<TreeViewer>(2);
			if ((sp = (PDESourcePage) ((PDEFormEditor) editor).findPage(PluginInputContext.CONTEXT_ID)) != null) {
				ISortableContentOutlinePage page = sp.getContentOutline();
				if (page != null && page.getControl() != null) {
					try {
						if (page instanceof SourceOutlinePage) {
							// get the tree viewer for the outline
							Class<?> clazz2 = page.getClass();
							Field field2 = clazz2.getDeclaredField("viewer");
							field2.setAccessible(true);
							Object f2 = field2.get(page);
							if (f2 != null && f2 instanceof TreeViewer) {
								viewers.add((TreeViewer) f2);
							}
						}
					} catch (Exception e) {
						StatusHandler.log(new Status(IStatus.ERROR, PdeUiBridgePlugin.ID_PLUGIN,
								"Failed to get tree viewers", e));
						return null;
					}
				}
			}

			try {
				// get the current page of the outline
				Class<?> clazz = PDEFormEditor.class;
				Field field = null;
				try {
					field = clazz.getDeclaredField("formOutline");
				} catch (NoSuchFieldException e) {
					field = clazz.getDeclaredField("fFormOutline");
				}
				field.setAccessible(true);
				Object f = field.get(editor);
				if (f != null && f instanceof FormOutlinePage) {
					// get the tree viewer for the outline
					Class<?> clazz2 = FormOutlinePage.class;
					Field field2 = null;
					try {
						field2 = clazz2.getDeclaredField("treeViewer");
					} catch (NoSuchFieldException e) {
						field2 = clazz2.getDeclaredField("fTreeViewer");
					}
					field2.setAccessible(true);
					Object f2 = field2.get(f);
					if (f2 != null && f2 instanceof TreeViewer) {
						TreeViewer treeViewer = (TreeViewer) f2;
						viewers.add(treeViewer);
					}
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, PdeUiBridgePlugin.ID_PLUGIN, "Could not get PDE outline", e));
				return Collections.emptyList();
			}

			// add a listener so that when the selection changes, the view is
			// refreshed to attempt to keep the ui model the same as the
			// taskscape one
			for (TreeViewer viewer : viewers) {
				viewer.addSelectionChangedListener(treeSelectionChangedListener);
				viewer.addTreeListener(treeSelectionChangedListener);
			}

			return viewers;
		}
		return Collections.emptyList();
	}

	public void refreshOutline(Object element, boolean updateLabels, boolean setSelection) {
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		List<TreeViewer> treeViewers = getContentOutlineViewers(editorPart);
		for (TreeViewer treeViewer : treeViewers) {
			if (treeViewer != null) {
				if (element == null) {
					treeViewer.getControl().setRedraw(false);
					treeViewer.refresh(true);
					treeViewer.getControl().setRedraw(true);
					treeViewer.expandAll();
				} else if (element instanceof PluginObjectNode) {
					treeViewer.getControl().setRedraw(false);
					treeViewer.refresh(element, true);
					treeViewer.getControl().setRedraw(true);
					treeViewer.expandAll();
				}
			}
		}

	}

	/**
	 * Class to listen to the tree views to attempt to refresh them more frequently to keep the ui model consistant with
	 * the user selections
	 * 
	 * @author Shawn Minto
	 */
	private class TreeViewerListener implements ISelectionChangedListener, ITreeViewerListener {
		public void selectionChanged(SelectionChangedEvent event) {
			Object o = event.getSource();
			if (o instanceof TreeViewer) {
				((TreeViewer) o).refresh();
				((TreeViewer) o).expandAll();
			}
		}

		public void treeCollapsed(TreeExpansionEvent event) {
			Object o = event.getSource();
			if (o instanceof TreeViewer) {
				((TreeViewer) o).refresh();
			}

		}

		public void treeExpanded(TreeExpansionEvent event) {
			Object o = event.getSource();
			if (o instanceof TreeViewer) {
				((TreeViewer) o).refresh();
			}
		}
	}

	@Override
	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		return null;
	}

	@Override
	public IInteractionElement getElement(IEditorInput input) {
		return null;
	}

	@Override
	public String getContentType() {
		return PdeStructureBridge.CONTENT_TYPE;
	}
}

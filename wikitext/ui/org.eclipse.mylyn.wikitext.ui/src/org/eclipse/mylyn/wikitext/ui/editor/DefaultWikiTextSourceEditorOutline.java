/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ui.editor;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * a default source editor outline view that presents a structured outline in a tree view based on the
 * {@link OutlineItem heading-based outline}.
 * 
 * @author David Green
 * @since 1.3
 */
public class DefaultWikiTextSourceEditorOutline extends AbstractWikiTextSourceEditorOutline implements IShowInSource,
		IShowInTarget {

	private boolean disableReveal;

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setUseHashlookup(true);
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		viewer.setContentProvider(new BaseWorkbenchContentProvider());
		viewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		viewer.setInput(getEditor().getAdapter(OutlineItem.class));

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				revealInEditor(event.getSelection());
			}
		});
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				revealInEditor(event.getSelection());
			}
		});
		viewer.expandAll();

		updateSelectionToMatchEditor();

		new ToolTip(viewer.getControl(), ToolTip.RECREATE, false) {
			@Override
			protected Composite createToolTipContentArea(Event event, Composite parent) {

				Composite comp = new Composite(parent, SWT.NONE);
				comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

				GridLayout gl = new GridLayout(1, false);
				gl.marginBottom = 2;
				gl.marginTop = 2;
				gl.marginHeight = 0;
				gl.marginWidth = 0;
				gl.marginLeft = 2;
				gl.marginRight = 2;
				gl.verticalSpacing = 1;
				comp.setLayout(gl);

				Object tipItem = getToolTipItem(new Point(event.x, event.y));
				if (tipItem instanceof OutlineItem) {
					OutlineItem outlineItem = (OutlineItem) tipItem;
					Label label = new Label(comp, SWT.WRAP);
					label.setBackground(comp.getBackground());
					label.setText(outlineItem.getTooltip());
				}

				return comp;
			}

			@Override
			protected boolean shouldCreateToolTip(Event event) {
				final Object eventItem = getToolTipItem(new Point(event.x, event.y));
				boolean shouldCreate = eventItem != null && eventItem instanceof OutlineItem
						&& super.shouldCreateToolTip(event);
				if (!shouldCreate) {
					hide();
				}
				return shouldCreate;
			}

			protected Object getToolTipItem(Point point) {
				TreeItem item = ((Tree) getTreeViewer().getControl()).getItem(point);
				if (item != null) {
					return item.getData();
				}
				return null;
			}
		};

		MenuManager manager = new MenuManager("#PopUp"); //$NON-NLS-1$
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuManager) {
				contextMenuAboutToShow(menuManager);
			}
		});
		viewer.getTree().setMenu(manager.createContextMenu(viewer.getTree()));

	}

	private void contextMenuAboutToShow(IMenuManager menuManager) {
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_UNDO));
		menuManager.add(new GroupMarker(ITextEditorActionConstants.GROUP_SAVE));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_COPY));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_PRINT));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_EDIT));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_FIND));
		menuManager.add(new Separator(IWorkbenchActionConstants.GROUP_ADD));
		menuManager.add(new Separator(IWorkbenchActionConstants.GROUP_SHOW_IN));
		menuManager.add(new Separator(IWorkbenchActionConstants.GROUP_REORGANIZE));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_REST));
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void revealInEditor(ISelection selection) {
		if (disableReveal) {
			return;
		}
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof OutlineItem) {
				OutlineItem item = (OutlineItem) firstElement;
				if (getEditor() != null) {
					IShowInTarget target = (IShowInTarget) getEditor().getAdapter(IShowInTarget.class);
					if (target != null) {
						target.show(new ShowInContext(null, new StructuredSelection(item)));
					}
				}
			}
		}
	}

	private void refresh() {
		getTreeViewer().getTree().setRedraw(false);
		try {
			getTreeViewer().refresh();
			getTreeViewer().expandAll();
		} finally {
			getTreeViewer().getTree().setRedraw(true);
		}
	}

	public ShowInContext getShowInContext() {
		if (getEditor() != null) {
			IShowInSource source = (IShowInSource) getEditor().getAdapter(IShowInSource.class);
			if (source != null) {
				return source.getShowInContext();
			}
		}
		return null;
	}

	public boolean show(ShowInContext context) {
		if (getEditor() != null) {
			IShowInTarget target = (IShowInTarget) getEditor().getAdapter(IShowInTarget.class);
			if (target != null) {
				return target.show(context);
			}
		}
		return false;
	}

	@Override
	protected void editorPropertyChanged(Object source, int propId) {
		super.editorPropertyChanged(source, propId);
		if (getControl() != null && !getControl().isDisposed()) {
			if (propId == WikiTextSourceEditor.PROP_OUTLINE) {
				refresh();

				// update the outline selection from the editor
				getControl().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (getControl() != null && !getControl().isDisposed() && getEditor() != null) {
							updateSelectionToMatchEditor();
						}
					}
				});
			} else if (propId == WikiTextSourceEditor.PROP_OUTLINE_LOCATION) {
				updateSelectionToMatchEditor();
			}
		}
	}

	@Override
	public void setSelection(ISelection selection) {
		disableReveal = true;
		try {
			super.setSelection(selection);
		} finally {
			disableReveal = false;
		}
	}

	private void updateSelectionToMatchEditor() {
		if (getEditor() == null) {
			return;
		}
		IShowInSource source = (IShowInSource) getEditor().getAdapter(IShowInSource.class);
		if (source != null) {
			ShowInContext showInContext = source.getShowInContext();
			if (showInContext != null) {
				if (showInContext.getSelection() instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) showInContext.getSelection()).getFirstElement();
					if (firstElement instanceof OutlineItem) {
						setSelection(new StructuredSelection(firstElement));
					}
				}
			}
		}
	}
}

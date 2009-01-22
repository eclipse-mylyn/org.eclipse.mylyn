/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.internal.wikitext.ui.editor.dnd.DndConfigurationStrategy;
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
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * 
 * @author David Green
 */
public class MarkupEditorOutline extends ContentOutlinePage implements IShowInSource, IShowInTarget {

	private final MarkupEditor editor;

	private boolean disableReveal;

	private DndConfigurationStrategy dndConfigurationStrategy;

	public MarkupEditorOutline(MarkupEditor editor) {
		this.editor = editor;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setUseHashlookup(true);
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		viewer.setContentProvider(new BaseWorkbenchContentProvider());
		viewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		viewer.setInput(editor.getOutlineModel());

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				revealInEditor(event.getSelection(), true);
			}
		});
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				revealInEditor(event.getSelection(), false);
			}
		});
		viewer.expandAll();

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

		getSite().setSelectionProvider(viewer);

		MenuManager manager = new MenuManager("#PopUp"); //$NON-NLS-1$
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuManager) {
				contextMenuAboutToShow(menuManager);
			}
		});
		viewer.getTree().setMenu(manager.createContextMenu(viewer.getTree()));

		getSite().registerContextMenu(MarkupEditor.ID + ".outlineContextMenu", manager, viewer); //$NON-NLS-1$

		configureDnd();
		configureActions();
	}

	@Override
	public void dispose() {
		if (dndConfigurationStrategy != null) {
			dndConfigurationStrategy.dispose();
			dndConfigurationStrategy = null;
		}
		super.dispose();
	}

	private void configureDnd() {
		dndConfigurationStrategy = new DndConfigurationStrategy();
		dndConfigurationStrategy.configure(editor, getControl(), getTreeViewer());
	}

	private void configureActions() {
		registerAction(ITextEditorActionConstants.UNDO);
		registerAction(ITextEditorActionConstants.REDO);
		registerAction(ITextEditorActionConstants.REVERT);
		registerAction(ITextEditorActionConstants.SAVE);
		registerAction(ITextEditorActionConstants.FIND);
		registerAction(ITextEditorActionConstants.PRINT);
	}

	private void registerAction(String actionId) {
		IAction action = editor.getAction(actionId);
		if (action != null) {
			getSite().getActionBars().setGlobalActionHandler(actionId, action);
		}
	}

	protected void contextMenuAboutToShow(IMenuManager menuManager) {

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

		addAction(menuManager, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.UNDO);
		addAction(menuManager, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.REDO);
		addAction(menuManager, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.REVERT);
		addAction(menuManager, ITextEditorActionConstants.GROUP_SAVE, ITextEditorActionConstants.SAVE);
		addAction(menuManager, ITextEditorActionConstants.GROUP_FIND, ITextEditorActionConstants.FIND);
		addAction(menuManager, ITextEditorActionConstants.GROUP_PRINT, ITextEditorActionConstants.PRINT);

	}

	protected final void addAction(IMenuManager menu, String group, String actionId) {
		IAction action = editor.getAction(actionId);
		if (action != null) {
			if (action instanceof IUpdate) {
				((IUpdate) action).update();
			}

			IMenuManager subMenu = menu.findMenuUsingPath(group);
			if (subMenu != null) {
				subMenu.add(action);
			} else {
				menu.appendToGroup(group, action);
			}
		}
	}

	@Override
	protected TreeViewer getTreeViewer() {
		return super.getTreeViewer();
	}

	private void revealInEditor(ISelection selection, boolean open) {
		if (disableReveal) {
			return;
		}
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof OutlineItem) {
				OutlineItem item = (OutlineItem) firstElement;
				editor.selectAndReveal(item.getOffset(), item.getLength());
			}
		}
	}

	public void refresh() {
		getTreeViewer().getTree().setRedraw(false);
		try {
			getTreeViewer().refresh();
			getTreeViewer().expandAll();
		} finally {
			getTreeViewer().getTree().setRedraw(true);
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

	public ShowInContext getShowInContext() {
		return editor.getShowInContext();
	}

	public boolean show(ShowInContext context) {
		return editor.show(context);
	}
}

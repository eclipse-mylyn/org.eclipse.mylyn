/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 *
 *
 * @author David Green
 */
public class MarkupEditorOutline extends ContentOutlinePage {

	private final MarkupEditor editor;
	private boolean disableReveal;

	public MarkupEditorOutline(MarkupEditor editor) {
		this.editor = editor;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		getTreeViewer().setUseHashlookup(true);
		getTreeViewer().setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		getTreeViewer().setContentProvider(new OutlineContentProvider());
		getTreeViewer().setLabelProvider(new OutlineLabelProvider());
		getTreeViewer().setInput(editor.getOutlineModel());

		getTreeViewer().addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				revealInEditor(event.getSelection(), true);
			}
		});
		getTreeViewer().addPostSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				revealInEditor(event.getSelection(), false);
			}
		});
		getTreeViewer().expandAll();

		new ToolTip(getTreeViewer().getControl(),ToolTip.RECREATE,false) {
			@Override
			protected Composite createToolTipContentArea(Event event,
					Composite parent) {


				Composite comp = new Composite(parent,SWT.NONE);
				comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

				GridLayout gl = new GridLayout(1,false);
				gl.marginBottom=2;
				gl.marginTop=2;
				gl.marginHeight=0;
				gl.marginWidth=0;
				gl.marginLeft=2;
				gl.marginRight=2;
				gl.verticalSpacing=1;
				comp.setLayout(gl);

				Object tipItem = getToolTipItem(new Point(event.x,event.y));
				if (tipItem instanceof OutlineItem) {
					OutlineItem outlineItem = (OutlineItem) tipItem;
					Label label = new Label(comp,SWT.WRAP);
					label.setBackground(comp.getBackground());
					label.setText(outlineItem.getTooltip());
				}

				return comp;
			}


			@Override
			protected boolean shouldCreateToolTip(Event event) {
				final Object eventItem = getToolTipItem(new Point(event.x,event.y));
				boolean shouldCreate = eventItem != null && eventItem instanceof OutlineItem && super.shouldCreateToolTip(event);
				if (!shouldCreate) {
					hide();
				}
				return shouldCreate;
			}

			protected Object getToolTipItem(Point point) {
				TreeItem item = ((Tree)getTreeViewer().getControl()).getItem(point);
				if (item != null) {
					return item.getData();
				}
				return null;
			}
		};

		getSite().setSelectionProvider(getTreeViewer());
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
		getTreeViewer().refresh();
		getTreeViewer().expandAll();
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


}

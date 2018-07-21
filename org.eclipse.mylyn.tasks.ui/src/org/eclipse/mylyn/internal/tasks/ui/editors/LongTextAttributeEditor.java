/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.commons.workbench.editors.CommonTextSupport;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class LongTextAttributeEditor extends AbstractAttributeEditor {

	private SourceViewer viewer;

	boolean ignoreNotification;

	boolean suppressRefresh;

	public LongTextAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.MULTIPLE, ColumnSpan.MULTIPLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		int style = SWT.FLAT | SWT.MULTI | SWT.WRAP;
		if (!isReadOnly()) {
			style |= SWT.V_SCROLL;
		}
		viewer = new SourceViewer(parent, null, style);
		RepositoryTextViewerConfiguration configuration = RichTextEditor.installHyperlinkPresenter(viewer,
				getModel().getTaskRepository(), getModel().getTask(), Mode.DEFAULT);
		viewer.configure(configuration);
		viewer.setDocument(new Document(getValue()));
		final StyledText text = viewer.getTextWidget();
		text.setToolTipText(getDescription());
		toolkit.adapt(text, false, false);

		// enable cut/copy/paste
		CommonTextSupport.setTextViewer(text, viewer);

		if (isReadOnly()) {
			viewer.setEditable(false);
		} else {
			viewer.setEditable(true);
			text.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					try {
						suppressRefresh = true;
						setValue(text.getText());
						CommonFormUtil.ensureVisible(text);
					} finally {
						suppressRefresh = false;
					}
				}
			});
		}

		setControl(text);
	}

	public String getValue() {
		return getAttributeMapper().getValue(getTaskAttribute());
	}

	public void setValue(String text) {
		getAttributeMapper().setValue(getTaskAttribute(), text);
		attributeChanged();
	}

	@Override
	public void refresh() {
		if (viewer.getTextWidget() != null && !viewer.getTextWidget().isDisposed()) {
			try {
				ignoreNotification = true;
				viewer.getDocument().set(getValue());
			} finally {
				ignoreNotification = false;
			}
		}
	}

	@Override
	public boolean shouldAutoRefresh() {
		return !suppressRefresh;
	}

}

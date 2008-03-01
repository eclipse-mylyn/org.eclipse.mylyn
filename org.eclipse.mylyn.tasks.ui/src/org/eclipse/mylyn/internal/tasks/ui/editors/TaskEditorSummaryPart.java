/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.core.AbstractAttributeMapper;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class TaskEditorSummaryPart extends AbstractTaskEditorPart {

	private class TabVerifyKeyListener implements VerifyKeyListener {

		public void verifyKey(VerifyEvent event) {
			// if there is a tab key, do not "execute" it and instead select the Status control
			if (event.keyCode == SWT.TAB) {
				event.doit = false;
				if (headerComposite != null) {
					headerComposite.setFocus();
				}
			}
		}

	}

	private static final int COLUMN_MARGIN = 6;

	private Composite headerComposite;

	private boolean needsHeader;

	private RichTextAttributeEditor summaryEditor;

	public TaskEditorSummaryPart(AbstractTaskEditorPage taskEditorPage) {
		super(taskEditorPage);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, RepositoryTaskAttribute attribute) {
		if (attribute == null) {
			return;
		}

		AttributeEditorFactory attributeEditorFactory = getTaskEditorPage().getAttributeEditorFactory();
		AbstractAttributeMapper attributeMapper = getTaskData().getAttributeFactory().getAttributeMapper();

		String type = attributeMapper.getType(attribute);
		if (type != null) {
			AbstractAttributeEditor editor = attributeEditorFactory.createEditor(type, attribute);
			editor.createLabelControl(composite, toolkit);
			GridDataFactory.defaultsFor(editor.getLabelControl()).indent(COLUMN_MARGIN, 0).applyTo(
					editor.getLabelControl());
			editor.createControl(composite, toolkit);
		}
	}

	private void addSummaryText(Composite composite, FormToolkit toolkit) {
		RepositoryTaskAttribute attribute = getTaskData().getAttribute(RepositoryTaskAttribute.SUMMARY);
		summaryEditor = new RichTextAttributeEditor(getTaskEditorPage().getAttributeManager(), attribute, SWT.SINGLE);
		summaryEditor.createControl(composite, toolkit);
		// FIXME what does this do? 
		//summaryTextViewer.getTextWidget().setIndent(2);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(summaryEditor.getControl());

		// API EDITOR move to RichTextEditor?
		summaryEditor.getViewer().prependVerifyKeyListener(new TabVerifyKeyListener());
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout());

		addSummaryText(composite, toolkit);

		if (needsHeader()) {
			createHeaderLayout(composite, toolkit);
		}

		toolkit.paintBordersFor(composite);

		setControl(composite);
	}

	/**
	 * @author Raphael Ackermann (modifications) (bug 195514)
	 * @param toolkit
	 */
	protected void createHeaderLayout(Composite composite, FormToolkit toolkit) {
		headerComposite = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout(11, false);
		layout.verticalSpacing = 1;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		headerComposite.setLayout(layout);

		RepositoryTaskAttribute statusAtribute = getTaskData().getAttribute(RepositoryTaskAttribute.STATUS);
		addAttribute(headerComposite, toolkit, statusAtribute);

		RepositoryTaskAttribute priorityAttribute = getTaskData().getAttribute(RepositoryTaskAttribute.PRIORITY);
		addAttribute(headerComposite, toolkit, priorityAttribute);

		String key = getTaskData().getTaskKey();
		if (key != null) {
			Label label = toolkit.createLabel(headerComposite, "ID:");
			label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			GridDataFactory.defaultsFor(label).indent(COLUMN_MARGIN, 0).applyTo(label);

			Text text = new Text(headerComposite, SWT.FLAT | SWT.READ_ONLY);
			toolkit.adapt(text, true, true);
			text.setText(key);
		}

		RepositoryTaskAttribute dateCreation = getTaskData().getAttribute(RepositoryTaskAttribute.DATE_CREATION);
		addAttribute(headerComposite, toolkit, dateCreation);

		RepositoryTaskAttribute dateModified = getTaskData().getAttribute(RepositoryTaskAttribute.DATE_MODIFIED);
		addAttribute(headerComposite, toolkit, dateModified);
	}

	public boolean needsHeader() {
		return needsHeader;
	}

	@Override
	public void setFocus() {
		summaryEditor.getViewer().getControl().setFocus();
	}

	public void setNeedsHeader(boolean needsHeader) {
		this.needsHeader = needsHeader;
	}

}

/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies, Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractTaskEditorSection;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Miles Parker
 * @author Steffen Pingel
 */
public abstract class AbstractReviewSection extends AbstractTaskEditorSection implements IUiContext {

	protected Composite composite;

	protected FormToolkit toolkit;

	protected boolean modelContentsCurrent;

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		this.toolkit = toolkit;
		composite = toolkit.createComposite(parent);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 5).applyTo(composite);
		return composite;
	}

	public Label addTextClient(final FormToolkit toolkit, final Section section, String text) {
		return addTextClient(toolkit, section, text, true);
	}

	public Label addTextClient(final FormToolkit toolkit, final Section section, String text, boolean hideOnExpand) {
		final Label label = new Label(section, SWT.NONE);
		label.setText("  " + text); //$NON-NLS-1$
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		section.setTextClient(label);

		if (hideOnExpand) {
			label.setVisible(!section.isExpanded());
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent e) {
					label.setVisible(!section.isExpanded());
				}
			});
		}

		return label;
	}

	public static void appendMessage(Section section, String message) {
		Control textClient = section.getTextClient();
		if (textClient instanceof final Label textClientLabel && !textClient.isDisposed()) {
			textClientLabel.setText("  " + message); //$NON-NLS-1$
			textClientLabel.getParent().layout(true, true);
		}
	}

	public Composite getComposite() {
		return composite;
	}

	public FormToolkit getToolkit() {
		return toolkit;
	}

	@Override
	public TaskEditor getEditor() {
		return getTaskEditorPage().getEditor();
	}

	@Override
	public Shell getShell() {
		return getTaskEditorPage().getSite().getShell();
	}

	@Override
	public ITask getTask() {
		return getTaskEditorPage().getTask();
	}

	public IReview getReview() {
		return getReviewEditorPage().getReview();
	}

	@Override
	public TaskRepository getTaskRepository() {
		return getReviewEditorPage().getTaskRepository();
	}

	public IRepository getModelRepository() {
		return getReview().getRepository();
	}

	@Override
	public IReviewRemoteFactoryProvider getFactoryProvider() {
		return getReviewEditorPage().getFactoryProvider();
	}

	public AbstractReviewTaskEditorPage getReviewEditorPage() {
		return (AbstractReviewTaskEditorPage) getTaskEditorPage();
	}
}

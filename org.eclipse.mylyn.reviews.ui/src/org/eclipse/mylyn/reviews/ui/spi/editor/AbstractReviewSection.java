/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies, Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractTaskEditorSection;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.IRemoteEmfObserver;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
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
public abstract class AbstractReviewSection extends AbstractTaskEditorSection implements IUiContext,
		IRemoteEmfObserver<IRepository, IReview> {

	protected Composite composite;

	protected FormToolkit toolkit;

	protected boolean modelContentsCurrent;

	protected RemoteEmfConsumer<IRepository, IReview, ?, String, String> consumer;

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		consumer = getFactoryProvider().getReviewFactory().getConsumerForLocalKey(getFactoryProvider().getRoot(),
				getTask().getTaskId());
		consumer.addObserver(this);
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		this.toolkit = toolkit;
		composite = toolkit.createComposite(parent);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 5).applyTo(composite);
		checkCreateModelContent();
		return composite;
	}

	/**
	 * We don't know whether the model or the controls will be available first, so we handle both cases here.
	 */
	private void checkCreateModelContent() {
		if (composite != null) {
			if (!composite.isDisposed()) {
				if (!modelContentsCurrent && getReview() != null) {
					modelContentsCurrent = true;
					createModelContent();
				}
				updateMessage();
			}
		}
	}

	@SuppressWarnings("restriction")
	private void updateMessage() {
		if (composite != null) {
			if (consumer != null && consumer.getModelObject() != null) {
				getSection().setText(CommonUiUtil.toLabel(getPartName()));
			} else {
				getSection().setText(
						CommonUiUtil.toLabel(getPartName()) + " "
								+ org.eclipse.mylyn.internal.reviews.ui.Messages.Reviews_RetrievingDetails);
			}
		}
	}

	protected void createModelContent() {
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
		final Label textClientLabel = (Label) section.getTextClient();
		textClientLabel.setText("  " + message);
		textClientLabel.getParent().layout(true, true);
		//textClientLabel.setVisible(cachingInProgress || !section.isExpanded());
	}

	public Composite getComposite() {
		return composite;
	}

	public FormToolkit getToolkit() {
		return toolkit;
	}

	public TaskEditor getEditor() {
		return getTaskEditorPage().getEditor();
	}

	public Shell getShell() {
		return getTaskEditorPage().getSite().getShell();
	}

	public ITask getTask() {
		return getTaskEditorPage().getTask();
	}

	public IReview getReview() {
		if (consumer != null) {
			return consumer.getModelObject();
		}
		return null;
	}

	public TaskRepository getTaskRepository() {
		return getReviewEditorPage().getTaskRepository();
	}

	public IRepository getModelRepository() {
		return (IRepository) getReview().getGroup();
	}

	public IReviewRemoteFactoryProvider getFactoryProvider() {
		return getReviewEditorPage().getFactoryProvider();
	}

	public AbstractReviewTaskEditorPage getReviewEditorPage() {
		return (AbstractReviewTaskEditorPage) getTaskEditorPage();
	}

	public void created(IRepository parent, IReview object) {
		//ignore
	}

	public void updating(IRepository parent, IReview object) {
		//ignore
	}

	public void updated(IRepository parent, IReview object, boolean modified) {
		checkCreateModelContent();
		if (modified) {
			refresh();
		}
	}

	public void failed(IRepository parent, IReview object, IStatus status) {
		// ignore
	}
}

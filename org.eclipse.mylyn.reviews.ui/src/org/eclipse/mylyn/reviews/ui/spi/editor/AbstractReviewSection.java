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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractTaskEditorSection;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewGroup;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.ReviewsRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactoryProvider;
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
public abstract class AbstractReviewSection extends AbstractTaskEditorSection implements IUiContext {

	protected Composite composite;

	protected FormToolkit toolkit;

	protected boolean createdModelControls;

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		final IReviewGroup group = getReviewEditorPage().getRemoteFactory().getGroup();
		group.eAdapters().add(new AdapterImpl() {
			@Override
			public void notifyChanged(Notification notification) {
				if (notification.getNotifier() == group) {
					Object newValue = notification.getNewValue();
					if (newValue instanceof IReview && ((IReview) newValue).getId().equals(getTaskData().getTaskId())) {
						checkCreateModelControls();
					}
				}
			}
		});
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		this.toolkit = toolkit;
		composite = toolkit.createComposite(parent);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 5).applyTo(composite);
		checkCreateModelControls();
		return composite;
	}

	/**
	 * We don't know whether the model or the controls will be available first, so we handle both cases here.
	 */
	private void checkCreateModelControls() {
		if (composite != null && !createdModelControls) {
			if (!composite.isDisposed()) {
				if (getReviewEditorPage().getReview() != null) {
					createdModelControls = true;
					createModelControls();
					getSection().setText(CommonUiUtil.toLabel(getPartName()));
				} else {
					getSection().setText(CommonUiUtil.toLabel(getPartName()) + " [Loading contents...]");
				}
			}
		}
	}

	@Override
	public void refresh() {
		super.refresh();
	}

	protected void createModelControls() {
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
		return getReviewEditorPage().getReview();
	}

	public TaskRepository getTaskRepository() {
		return getReviewEditorPage().getTaskRepository();
	}

	public ReviewsRemoteFactoryProvider getRemoteFactoryProvider() {
		return getReviewEditorPage().getRemoteFactory();
	}

	public AbstractReviewTaskEditorPage getReviewEditorPage() {
		return (AbstractReviewTaskEditorPage) getTaskEditorPage();
	}

	protected abstract AbstractUiFactoryProvider<?> getUiFactoryProvider();
}

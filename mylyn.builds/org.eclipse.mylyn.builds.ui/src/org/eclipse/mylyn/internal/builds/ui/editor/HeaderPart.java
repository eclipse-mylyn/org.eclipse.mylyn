/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.internal.core.BuildPackage;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class HeaderPart extends AbstractBuildEditorPart {

	public HeaderPart() {
		span = 2;
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout());

		Label label = createLabel(composite, toolkit, Messages.HeaderPart_plan);
		GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
		Text text = createTextReadOnly(composite, toolkit, ""); //$NON-NLS-1$
		bind(text, IBuildPlan.class, BuildPackage.Literals.BUILD_ELEMENT__NAME);

		label = createLabel(composite, toolkit, Messages.HeaderPart_build);
		GridDataFactory.defaultsFor(label).indent(12, 0).applyTo(label);
		text = createTextReadOnly(composite, toolkit, ""); //$NON-NLS-1$
		bind(text, IBuild.class, BuildPackage.Literals.BUILD__BUILD_NUMBER);

		label = createLabel(composite, toolkit, Messages.HeaderPart_status);
		GridDataFactory.defaultsFor(label).indent(12, 0).applyTo(label);
		text = createTextReadOnly(composite, toolkit, ""); //$NON-NLS-1$
		IBuild build = getInput(IBuild.class);
		text.setText(getStatusLabel(build));

		createDuration(toolkit, composite, build);

		((GridLayout) composite.getLayout()).numColumns = composite.getChildren().length;
		toolkit.paintBordersFor(composite);

		return composite;
	}

	private void createDuration(FormToolkit toolkit, Composite composite, IBuild build) {
		String durationLabel;
		long duration;
		if (build.getState() == BuildState.RUNNING) {
			durationLabel = Messages.HeaderPart_executingFor;
			duration = System.currentTimeMillis() - build.getTimestamp();
		} else {
			durationLabel = Messages.HeaderPart_duration;
			duration = build.getDuration();
		}
		Label label = createLabel(composite, toolkit, durationLabel);
		GridDataFactory.defaultsFor(label).indent(12, 0).applyTo(label);
		Text text = createTextReadOnly(composite, toolkit, ""); //$NON-NLS-1$
		text.setText(DateUtil.getFormattedDurationShort(duration, true));
	}

	private String getStatusLabel(IBuild build) {
		if (build.getStatus() != null) {
			return build.getStatus().getLabel();
		}
		return build.getState() == BuildState.RUNNING ? Messages.HeaderPart_running : Messages.HeaderPart_unknown;
	}

	@Override
	public Control createControl(Composite parent, FormToolkit toolkit) {
		return createContent(parent, toolkit);
	}

}

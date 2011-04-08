/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.github.internal.GitHubTaskAttributes;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Editor part for viewing a issue's labels.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class GitHubIssueLabelPart extends AbstractTaskEditorPart {

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart#createControl(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite displayArea = toolkit.createComposite(parent);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 5)
				.spacing(0, 0).numColumns(2).applyTo(displayArea);

		TaskAttribute labels = getTaskData().getRoot().getAttribute(
				GitHubTaskAttributes.LABELS.getId());
		if (labels != null) {
			Label labelControl = toolkit.createLabel(displayArea, labels
					.getMetaData().getLabel());
			labelControl.setForeground(toolkit.getColors().getColor(
					IFormColors.TITLE));

			StringBuilder labelsValue = new StringBuilder();
			for (String value : labels.getValues()) {
				labelsValue.append(' ').append(value).append(',');
			}
			if (labelsValue.length() > 0) {
				labelsValue.deleteCharAt(labelsValue.length() - 1);
			}

			Text labelsText = toolkit.createText(displayArea,
					labelsValue.toString(), SWT.WRAP | SWT.READ_ONLY);
			GridDataFactory.swtDefaults().indent(5, 0).grab(true, false)
					.applyTo(labelsText);
		}

		setControl(displayArea);
	}
}
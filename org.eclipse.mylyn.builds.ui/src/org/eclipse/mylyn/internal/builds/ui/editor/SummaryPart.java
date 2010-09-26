/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildCause;
import org.eclipse.mylyn.builds.internal.core.BuildPackage;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class SummaryPart extends AbstractBuildEditorPart {

	public SummaryPart() {
		super(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setPartName("Summary");
		this.span = 2;
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(4, false));

		Label label;
		Text text;

		label = createLabel(composite, toolkit, "Completed on: ");
		GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
		text = createTextReadOnly(composite, toolkit, "");
		bind(text, IBuild.class, BuildPackage.Literals.BUILD__TIMESTAMP);

		label = createLabel(composite, toolkit, "Duration: ");
		GridDataFactory.defaultsFor(label).indent(10, 0).applyTo(label);
		text = createTextReadOnly(composite, toolkit, "");
		IBuild build = getInput(IBuild.class);
		text.setText(DateUtil.getFormattedDurationShort(build.getDuration(), true));

		if (build.getCause().size() > 0) {
			Set<String> causeDescriptions = new LinkedHashSet<String>();
			for (IBuildCause cause : build.getCause()) {
				if (cause.getDescription() != null) {
					causeDescriptions.add(cause.getDescription());
				}
			}
			StringBuilder sb = new StringBuilder();
			for (String string : causeDescriptions) {
				append(sb, string);
			}
			if (sb.length() > 0) {
				sb.append(".");
			}

			label = createLabel(composite, toolkit, "Cause: ");
			GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
			text = createTextReadOnly(composite, toolkit, "", SWT.WRAP);
			GridDataFactory.fillDefaults().indent(0, 0).span(3, 1).align(SWT.BEGINNING, SWT.TOP).applyTo(text);
			text.setText(sb.toString());
		}

		return composite;
	}

	private void append(StringBuilder sb, String text) {
		if (text != null) {
			if (sb.length() > 0) {
				sb.append(". ");
			}
			sb.append(text);
		}
	}

}

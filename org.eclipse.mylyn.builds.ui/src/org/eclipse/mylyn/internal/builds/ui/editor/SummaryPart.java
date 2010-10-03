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

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildCause;
import org.eclipse.mylyn.builds.internal.core.BuildPackage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Steffen Pingel
 */
public class SummaryPart extends AbstractBuildEditorPart {

	public SummaryPart() {
		super(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setPartName("Summary");
	}

	private void append(StringBuilder sb, String text) {
		if (text != null) {
			if (sb.length() > 0) {
				sb.append(". ");
			}
			sb.append(text);
		}
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		Label label;
		Text text;

		label = createLabel(composite, toolkit, "Completed on: ");
//		GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
		text = createTextReadOnly(composite, toolkit, "");
//		GridDataFactory.fillDefaults().span(5, 1).applyTo(text);
		bind(text, IBuild.class, BuildPackage.Literals.BUILD__TIMESTAMP);

		IBuild build = getInput(IBuild.class);

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
//			GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
			text = createTextReadOnly(composite, toolkit, "", SWT.WRAP);
//			GridDataFactory.fillDefaults().indent(0, 0).span(5, 1).align(SWT.BEGINNING, SWT.TOP).applyTo(text);
			text.setText(sb.toString());
		}

		return composite;
	}

	public Control createControl(Composite parent, FormToolkit toolkit) {
		return createContent(parent, toolkit);
	}

}

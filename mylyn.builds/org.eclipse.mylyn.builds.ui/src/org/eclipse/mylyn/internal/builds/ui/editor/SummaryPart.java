/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - Minor enhancements
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class SummaryPart extends AbstractBuildEditorPart {

	public SummaryPart() {
		super(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setPartName(Messages.SummaryPart_Summary);
	}

	private void append(StringBuilder sb, String text) {
		if (text != null) {
			if (sb.length() > 0) {
				sb.append(". "); //$NON-NLS-1$
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

		Text text;

		createLabel(composite, toolkit, Messages.SummaryPart_StartedOn);
		text = createTextReadOnly(composite, toolkit, ""); //$NON-NLS-1$
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
				sb.append("."); //$NON-NLS-1$
			}

			createLabel(composite, toolkit, Messages.SummaryPart_Cause);
			text = createTextReadOnly(composite, toolkit, "", SWT.WRAP); //$NON-NLS-1$
			text.setText(sb.toString());
		}

		return composite;
	}

	public Control createControl(Composite parent, FormToolkit toolkit) {
		return createContent(parent, toolkit);
	}

}

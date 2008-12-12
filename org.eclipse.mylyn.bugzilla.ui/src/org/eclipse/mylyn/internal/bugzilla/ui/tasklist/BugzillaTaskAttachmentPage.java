/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaFlag;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaFlagMapper;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.wizards.TaskAttachmentPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Frank Becker
 */
public class BugzillaTaskAttachmentPage extends TaskAttachmentPage {

	private final List<BugzillaFlag> flagAttributes = new ArrayList<BugzillaFlag>();

	private final FormToolkit toolkit;

	private ExpandableComposite flagExpandComposite = null;

	private Composite scrollComposite;

	public BugzillaTaskAttachmentPage(TaskAttachmentModel model) {
		super(model);
		toolkit = new FormToolkit(Display.getCurrent());
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		RepositoryConfiguration configuration = BugzillaCorePlugin.getRepositoryConfiguration(getModel().getTaskRepository()
				.getRepositoryUrl());
		if (configuration != null) {
			List<BugzillaFlag> flags = configuration.getFlags();
			TaskAttribute productAttribute = getModel().getAttribute().getTaskData().getRoot().getMappedAttribute(
					BugzillaAttribute.PRODUCT.getKey());
			TaskAttribute componentAttribute = getModel().getAttribute().getTaskData().getRoot().getMappedAttribute(
					BugzillaAttribute.COMPONENT.getKey());
			Control[] children = parent.getChildren();
			Composite pageComposite = (Composite) children[children.length - 1];
			Composite flagComposite = null;
			for (BugzillaFlag bugzillaFlag : flags) {
				if (bugzillaFlag.getType().equals("bug")) {
					continue;
				}
				if (!bugzillaFlag.isUsedIn(productAttribute.getValue(), componentAttribute.getValue())) {
					continue;
				}

				if (flagComposite == null) {
					flagComposite = createFlagSection(pageComposite);
				}
				BugzillaFlagMapper mapper = new BugzillaFlagMapper();
				mapper.setRequestee("");
				mapper.setSetter("");
				mapper.setState(" ");
				mapper.setFlagId(bugzillaFlag.getName());
				mapper.setNumber(0);
				final TaskAttribute attribute = getModel().getAttribute().createAttribute(
						"task.common.kind.flag_type" + bugzillaFlag.getFlagId());
				mapper.applyTo(attribute);

				Label flagLiteral = new Label(flagComposite, SWT.NONE);
				flagLiteral.setText("" + bugzillaFlag.getName());
				flagLiteral.setToolTipText(bugzillaFlag.getDescription());
				flagAttributes.add(bugzillaFlag);
				final Combo flagState = new Combo(flagComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
				flagState.add(" ");
				if (bugzillaFlag.isRequestable()) {
					flagState.add("?");
				}
				flagState.add("+");
				flagState.add("-");
				if (bugzillaFlag.isRequestable() && bugzillaFlag.isSpecifically_requestable()) {
					flagState.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 1, 1));
					final Text requesteeText = new Text(flagComposite, SWT.BORDER);
					requesteeText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
					requesteeText.setEditable(false);
					requesteeText.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							TaskAttribute requesteeAttribute = attribute.getAttribute("requestee");
							if (requesteeAttribute != null) {
								String value = requesteeText.getText().trim();
								requesteeAttribute.setValue(value);
							}
						}
					});
					flagState.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {
							// ignore
						}

						public void widgetSelected(SelectionEvent e) {
							TaskAttribute state = attribute.getAttribute("state");
							if (state != null) {
								String value = flagState.getItem(flagState.getSelectionIndex());
								state.setValue(value);
								requesteeText.setEditable(value.equals("?"));
							}
						}
					});
				} else {
					flagState.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 2, 1));
					flagState.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {
							// ignore
						}

						public void widgetSelected(SelectionEvent e) {
							TaskAttribute state = attribute.getAttribute("state");
							String value = flagState.getItem(flagState.getSelectionIndex());
							if (state != null && value != null) {
								state.setValue(value);
							}
						}
					});
				}
			}
		}
	}

	private Composite createFlagSection(Composite container) {
		flagExpandComposite = toolkit.createExpandableComposite(container, ExpandableComposite.COMPACT
				| ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		flagExpandComposite.setFont(container.getFont());
		flagExpandComposite.setBackground(container.getBackground());
		flagExpandComposite.setText("Flags");
		flagExpandComposite.setLayout(new GridLayout(3, false));
		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 3;
		flagExpandComposite.setLayoutData(g);
		flagExpandComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				getControl().getShell().pack();
			}
		});

		scrollComposite = new Composite(flagExpandComposite, SWT.NONE);
		scrollComposite.setLayout(new GridLayout(3, false));
		flagExpandComposite.setClient(scrollComposite);
		return scrollComposite;
	}

	@Override
	public void dispose() {
		if (toolkit != null) {
			if (toolkit.getColors() != null) {
				toolkit.dispose();
			}
		}
		super.dispose();
	}

}

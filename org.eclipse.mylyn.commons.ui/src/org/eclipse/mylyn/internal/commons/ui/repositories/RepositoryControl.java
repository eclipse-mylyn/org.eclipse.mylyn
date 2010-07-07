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

package org.eclipse.mylyn.internal.commons.ui.repositories;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.commons.ui.SectionComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @since 3.5
 */
public abstract class RepositoryControl extends Composite {

	private DataBindingContext bindingContext;

	public RepositoryControl(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	protected void bind(Button button, String property) {
		ISWTObservableValue uiElement = SWTObservables.observeSelection(button);
		IObservableValue modelElement = new RepositoryLocationValueProperty(property).observe(getWorkingCopy());
		bindingContext.bindValue(uiElement, modelElement, null, null);
	}

	protected void bind(Text text, String property) {
		ISWTObservableValue uiElement = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue modelElement = new RepositoryLocationValueProperty(property).observe(getWorkingCopy());
		bindingContext.bindValue(uiElement, modelElement, null, null);
	}

	protected void createContents() {
		bindingContext = new DataBindingContext();

		GridLayout layout = new GridLayout(3, false);
		setLayout(layout);

//		Composite this = new Composite(parent, SWT.NULL);
//		Layout layout = new FillLayout();
//		this.setLayout(layout);

		createServerSection();
		createUserSection();

		SectionComposite sectionComposite = new SectionComposite(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(sectionComposite);

		createProxySection(sectionComposite);
	}

	private void createProxySection(SectionComposite parent) {
		Composite composite = parent.createSection("Proxy Server Configuration");
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(composite);

		// ignore

	}

	private void createUserSection() {
		Label label;

		label = new Label(this, SWT.NONE);
		label.setText("&User:");

		Text userText = new Text(this, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(userText);
		bind(userText, "username");

		Button anonymousButton = new Button(this, SWT.CHECK);
		anonymousButton.setText("Anonymous");
		bind(anonymousButton, "anonymous");

		label = new Label(this, SWT.NONE);
		label.setText("&Password:");

		Text passwordText = new Text(this, SWT.BORDER | SWT.PASSWORD);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);

		Button savePasswordButton = new Button(this, SWT.CHECK);
		savePasswordButton.setText("Save Password");
	}

	private void createServerSection() {
		Label label;

		label = new Label(this, SWT.NONE);
		label.setText("&Server:");

		Text urlText = new Text(this, SWT.BORDER);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(urlText);
		bind(urlText, "url");

		label = new Label(this, SWT.NONE);
		label.setText("&Label:");

		Text labelText = new Text(this, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(labelText);
		bind(labelText, "label");

		Button disconnectedButton = new Button(this, SWT.CHECK);
		disconnectedButton.setText("Disconnected");
		bind(disconnectedButton, RepositoryLocation.OFFLINE);
	}

	protected abstract RepositoryLocation getWorkingCopy();

}

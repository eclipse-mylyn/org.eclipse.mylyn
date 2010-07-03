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

package org.eclipse.mylyn.commons.ui.repositories;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.repository.RepositoryLocation;
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

	private Button disconnectedButton;

	private IObservableMap properties;

	public RepositoryControl(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	protected void bind(Button button, String property) {
		ISWTObservableValue uiElement = SWTObservables.observeSelection(button);
		IObservableValue modelElement = Observables.observeMapEntry(getProperties(), property);
		bindingContext.bindValue(uiElement, modelElement, null, null);
	}

	protected void bind(Text text, String property) {
		ISWTObservableValue uiElement = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue modelElement = Observables.observeMapEntry(getProperties(), property);
		bindingContext.bindValue(uiElement, modelElement, null, null);
	}

	protected void createContents() {
		bindingContext = new DataBindingContext();

		GridLayout layout = new GridLayout(3, false);
		setLayout(layout);

//		Composite this = new Composite(parent, SWT.NULL);
//		Layout layout = new FillLayout();
//		this.setLayout(layout);

		Label label = new Label(this, SWT.NONE);
		label.setText("&Server:");

		Text urlText = new Text(this, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(urlText);
		bind(urlText, "url");

		disconnectedButton = new Button(this, SWT.CHECK);
		disconnectedButton.setText("Disconnected");
		bind(disconnectedButton, RepositoryLocation.OFFLINE);
	}

	private IObservableMap getProperties() {
		if (properties == null) {
			RepositoryLocation workingCopy = getWorkingCopy();
			properties = new ObservableMap(workingCopy.getProperties());
		}
		return properties;
	}

	protected abstract RepositoryLocation getWorkingCopy();

}

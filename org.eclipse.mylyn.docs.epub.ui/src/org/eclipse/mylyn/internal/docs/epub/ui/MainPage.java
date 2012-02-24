/*******************************************************************************
 * Copyright (c) 2011 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.docs.epub.ui;

import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MainPage extends WizardPage {

	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;

	private Text titleText;

	private Text copyrightText;

	private Text coverText;

	private Text styleSheetText;

	private Text identifierText;

	private Text subjectText;

	/**
	 * @wbp.nonvisual location=681,21
	 */
	private EPUB2Bean bean = new EPUB2Bean();

	private Combo schemeText;

	private Text authorText;

	private Combo combo;

	private DateTime dateTime;

	/**
	 * Create the wizard.
	 * 
	 * @wbp.parser.constructor
	 */
	public MainPage() {
		super("wizardPage");
		setMessage("Define properties for the resulting EPUB file.");
		setImageDescriptor(EPUBUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.docs.epub.ui",
				"icons/wizard-banner.png"));
		setTitle("EPUB Properties");
	}

	public MainPage(EPUB2Bean bean) {
		this();
		this.bean = bean;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, true));

		Group grpRequiredDetails = new Group(container, SWT.NONE);
		grpRequiredDetails.setLayout(new GridLayout(4, false));
		GridData gd_grpRequiredDetails = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_grpRequiredDetails.heightHint = 187;
		grpRequiredDetails.setLayoutData(gd_grpRequiredDetails);
		grpRequiredDetails.setText("Header:");

		Label lblTitle = new Label(grpRequiredDetails, SWT.NONE);
		lblTitle.setText("Title:");

		titleText = new Text(grpRequiredDetails, SWT.BORDER);
		titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label lblAuthor = new Label(grpRequiredDetails, SWT.NONE);
		lblAuthor.setText("Author:");

		authorText = new Text(grpRequiredDetails, SWT.BORDER);
		authorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_1 = new Label(grpRequiredDetails, SWT.NONE);
		lblNewLabel_1.setText("Date:");

		dateTime = new DateTime(grpRequiredDetails, SWT.BORDER | SWT.LONG);

		Label lblIdentifier = new Label(grpRequiredDetails, SWT.NONE);
		lblIdentifier.setText("Identifier:");

		identifierText = new Text(grpRequiredDetails, SWT.BORDER);
		identifierText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblScheme = new Label(grpRequiredDetails, SWT.NONE);
		lblScheme.setText("Scheme:");

		schemeText = new Combo(grpRequiredDetails, SWT.BORDER);
		schemeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		schemeText.add("UUID");

		Label lblCopyright = new Label(grpRequiredDetails, SWT.NONE);
		lblCopyright.setText("Copyright:");

		copyrightText = new Text(grpRequiredDetails, SWT.BORDER);
		copyrightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblLanguage = new Label(grpRequiredDetails, SWT.NONE);
		lblLanguage.setText("Language:");

		combo = new Combo(grpRequiredDetails, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Set<String> locales = bean.getLocales().keySet();
		for (String name : locales) {
			combo.add(name);
		}

		Label lblDescription = new Label(grpRequiredDetails, SWT.NONE);
		lblDescription.setText("Subject:");

		subjectText = new Text(grpRequiredDetails, SWT.BORDER);
		subjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Group grpCoverAndStyling = new Group(container, SWT.NONE);
		grpCoverAndStyling.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		grpCoverAndStyling.setText("Cover and styling:");
		grpCoverAndStyling.setLayout(new GridLayout(3, false));
		Label lblNewLabel = new Label(grpCoverAndStyling, SWT.NONE);
		lblNewLabel.setText("Cover image:");

		coverText = new Text(grpCoverAndStyling, SWT.BORDER);
		coverText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button button = new Button(grpCoverAndStyling, SWT.NONE);
		button.setText("...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// XXX: Replace with ResourceSelectionDialog?
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				dialog.setFilterNames(new String[] { "Supported Image Formats" });
				dialog.setFilterExtensions(new String[] { "*.png;*.gif;*.jpg;*.svg" });
				dialog.setFilterPath(bean.getMarkupFile().getAbsolutePath());
				String s = dialog.open();
				if (s != null) {
					coverText.setText(s);
				}
			}
		});
		Label lblStyleSheet = new Label(grpCoverAndStyling, SWT.NONE);
		lblStyleSheet.setBounds(0, 0, 59, 14);
		lblStyleSheet.setText("Style Sheet:");

		styleSheetText = new Text(grpCoverAndStyling, SWT.BORDER);
		styleSheetText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button button_1 = new Button(grpCoverAndStyling, SWT.NONE);
		button_1.setText("...");
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// XXX: Replace with ResourceSelectionDialog?
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				dialog.setFilterNames(new String[] { "Cascading Style Sheets" });
				dialog.setFilterExtensions(new String[] { "*.css" });
				dialog.setFilterPath(bean.getMarkupFile().getAbsolutePath());
				String s = dialog.open();
				if (s != null) {
					styleSheetText.setText(s);
				}

			}
		});
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue beanCoverObserveValue = PojoObservables.observeValue(bean, "cover");
		IObservableValue coverTextTextObserveValue = PojoObservables.observeValue(coverText, "text");
		bindingContext.bindValue(beanCoverObserveValue, coverTextTextObserveValue, null, null);
		//
		IObservableValue textObserveTextObserveWidget = SWTObservables.observeText(titleText, SWT.Modify);
		IObservableValue beanTitleObserveValue = PojoObservables.observeValue(bean, "title");
		bindingContext.bindValue(textObserveTextObserveWidget, beanTitleObserveValue, null, null);
		//
		IObservableValue subjectTextObserveTextObserveWidget = SWTObservables.observeText(subjectText, SWT.Modify);
		IObservableValue beanSubjectObserveValue = PojoObservables.observeValue(bean, "subject");
		bindingContext.bindValue(subjectTextObserveTextObserveWidget, beanSubjectObserveValue, null, null);
		//
		IObservableValue text_4ObserveTextObserveWidget = SWTObservables.observeText(identifierText, SWT.Modify);
		IObservableValue beanIdentifierObserveValue = PojoObservables.observeValue(bean, "identifier");
		bindingContext.bindValue(text_4ObserveTextObserveWidget, beanIdentifierObserveValue, null, null);
		//
		IObservableValue text_1ObserveTextObserveWidget = SWTObservables.observeText(copyrightText, SWT.Modify);
		IObservableValue beanRightsObserveValue = PojoObservables.observeValue(bean, "rights");
		bindingContext.bindValue(text_1ObserveTextObserveWidget, beanRightsObserveValue, null, null);
		//
		IObservableValue text_3ObserveTextObserveWidget = SWTObservables.observeText(authorText, SWT.Modify);
		IObservableValue beanCreatorObserveValue = PojoObservables.observeValue(bean, "creator");
		bindingContext.bindValue(text_3ObserveTextObserveWidget, beanCreatorObserveValue, null, null);
		//
		IObservableValue schemeTextObserveTextObserveWidget = SWTObservables.observeText(schemeText);
		IObservableValue beanIdSchemeObserveValue = PojoObservables.observeValue(bean, "scheme");
		bindingContext.bindValue(schemeTextObserveTextObserveWidget, beanIdSchemeObserveValue, null, null);
		//
		IObservableValue comboObserveTextObserveWidget = SWTObservables.observeText(combo);
		IObservableValue beanLanguageObserveValue = PojoObservables.observeValue(bean, "language");
		bindingContext.bindValue(comboObserveTextObserveWidget, beanLanguageObserveValue, null, null);
		//
		IObservableValue coverTextObserveTextObserveWidget = SWTObservables.observeText(coverText, SWT.Modify);
		bindingContext.bindValue(coverTextObserveTextObserveWidget, beanCoverObserveValue, null, null);
		//
		IObservableValue styleSheetTextObserveTextObserveWidget = SWTObservables
				.observeText(styleSheetText, SWT.Modify);
		IObservableValue beanStyleSheetObserveValue = PojoObservables.observeValue(bean, "styleSheet");
		bindingContext.bindValue(styleSheetTextObserveTextObserveWidget, beanStyleSheetObserveValue, null, null);
		//
		IObservableValue dateTimeObserveSelectionObserveWidget = SWTObservables.observeSelection(dateTime);
		IObservableValue beanPublicationDateObserveValue = PojoObservables.observeValue(bean, "publicationDate");
		bindingContext.bindValue(dateTimeObserveSelectionObserveWidget, beanPublicationDateObserveValue, null, null);
		//
		return bindingContext;
	}
}

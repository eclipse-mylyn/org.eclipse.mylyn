/*******************************************************************************
 * Copyright (c) 2011-2014 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.docs.epub.ui;

import java.io.File;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.docs.epub.core.PublicationProxy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class MainPage extends WizardPage {

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
	private PublicationProxy bean = new PublicationProxy();

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
		super("wizardPage"); //$NON-NLS-1$
		setMessage(Messages.MainPage_0);
		setImageDescriptor(
				AbstractUIPlugin.imageDescriptorFromPlugin(EPUBUIPlugin.PLUGIN_ID, "icons/wizard-banner.png")); //$NON-NLS-1$
		setTitle(Messages.MainPage_1);
	}

	public MainPage(PublicationProxy bean) {
		this();
		this.bean = bean;
	}

	/**
	 * Create contents of the wizard.
	 *
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
		grpRequiredDetails.setText(Messages.MainPage_2);

		Label lblTitle = new Label(grpRequiredDetails, SWT.NONE);
		lblTitle.setText(Messages.MainPage_3);

		titleText = new Text(grpRequiredDetails, SWT.BORDER);
		titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label lblAuthor = new Label(grpRequiredDetails, SWT.NONE);
		lblAuthor.setText(Messages.MainPage_4);

		authorText = new Text(grpRequiredDetails, SWT.BORDER);
		authorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_1 = new Label(grpRequiredDetails, SWT.NONE);
		lblNewLabel_1.setText(Messages.MainPage_5);

		dateTime = new DateTime(grpRequiredDetails, SWT.BORDER | SWT.LONG);

		Label lblIdentifier = new Label(grpRequiredDetails, SWT.NONE);
		lblIdentifier.setText(Messages.MainPage_6);

		identifierText = new Text(grpRequiredDetails, SWT.BORDER);
		identifierText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblScheme = new Label(grpRequiredDetails, SWT.NONE);
		lblScheme.setText(Messages.MainPage_7);

		schemeText = new Combo(grpRequiredDetails, SWT.BORDER);
		schemeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		schemeText.add(Messages.MainPage_8);

		Label lblCopyright = new Label(grpRequiredDetails, SWT.NONE);
		lblCopyright.setText(Messages.MainPage_9);

		copyrightText = new Text(grpRequiredDetails, SWT.BORDER);
		copyrightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblLanguage = new Label(grpRequiredDetails, SWT.NONE);
		lblLanguage.setText(Messages.MainPage_10);

		combo = new Combo(grpRequiredDetails, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Set<String> locales = bean.getLocales().keySet();
		for (String name : locales) {
			combo.add(name);
		}

		Label lblDescription = new Label(grpRequiredDetails, SWT.NONE);
		lblDescription.setText(Messages.MainPage_11);

		subjectText = new Text(grpRequiredDetails, SWT.BORDER);
		subjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Group grpCoverAndStyling = new Group(container, SWT.NONE);
		grpCoverAndStyling.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		grpCoverAndStyling.setText(Messages.MainPage_12);
		grpCoverAndStyling.setLayout(new GridLayout(3, false));
		Label lblNewLabel = new Label(grpCoverAndStyling, SWT.NONE);
		lblNewLabel.setText(Messages.MainPage_13);

		coverText = new Text(grpCoverAndStyling, SWT.BORDER);
		coverText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button button = new Button(grpCoverAndStyling, SWT.NONE);
		button.setText("..."); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// XXX: Replace with ResourceSelectionDialog?
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				dialog.setFilterNames(new String[] { Messages.MainPage_15 });
				dialog.setFilterExtensions(new String[] { "*.png;*.gif;*.jpg;*.svg" }); //$NON-NLS-1$
				dialog.setFilterPath(bean.getMarkupFile().getAbsolutePath());
				String s = dialog.open();
				if (s != null) {
					coverText.setText(s);
				}
			}
		});
		Label lblStyleSheet = new Label(grpCoverAndStyling, SWT.NONE);
		lblStyleSheet.setBounds(0, 0, 59, 14);
		lblStyleSheet.setText(Messages.MainPage_16);

		styleSheetText = new Text(grpCoverAndStyling, SWT.BORDER);
		styleSheetText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button button_1 = new Button(grpCoverAndStyling, SWT.NONE);
		button_1.setText("..."); //$NON-NLS-1$
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// XXX: Replace with ResourceSelectionDialog?
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				dialog.setFilterNames(new String[] { Messages.MainPage_18 });
				dialog.setFilterExtensions(new String[] { "*.css" }); //$NON-NLS-1$
				dialog.setFilterPath(bean.getMarkupFile().getAbsolutePath());
				String s = dialog.open();
				if (s != null) {
					styleSheetText.setText(s);
				}

			}
		});
		m_bindingContext = initDataBindings();
		WizardPageSupport.create(this, m_bindingContext);
		setMessage(Messages.MainPage_0);
	}

	private final class StringValidator implements IValidator {
		private final String errorText;

		private final ControlDecoration controlDecoration;

		public StringValidator(String errorText, Control control) {
			this.errorText = errorText;
			controlDecoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
			controlDecoration.setImage(fieldDecoration.getImage());
		}

		public IStatus validate(Object value) {
			if (value instanceof String) {
				String text = (String) value;
				if (text.trim().length() == 0) {
					controlDecoration.show();
					return ValidationStatus.cancel(errorText);
				}
			}
			controlDecoration.hide();
			return ValidationStatus.ok();
		}
	}

	private final class FileValidator implements IValidator {

		private final String errorText;

		private final String[] fileSuffixes;

		private final ControlDecoration controlDecoration;

		public FileValidator(String errorText, Control control, String[] fileSuffixes) {
			this.errorText = errorText;
			this.fileSuffixes = fileSuffixes;
			controlDecoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
			controlDecoration.setImage(fieldDecoration.getImage());
			controlDecoration.hide();
		}

		public IStatus validate(Object value) {
			if (value instanceof String && ((String) value).length() > 0) {
				File file = new File((String) value);
				if (!file.exists()) {
					controlDecoration.show();
					return ValidationStatus.error("The specified file must exist."); //$NON-NLS-1$
				}
				boolean suffixOK = false;
				String name = file.getName();
				for (String suffix : fileSuffixes) {
					if (name.endsWith(suffix)) {
						suffixOK = true;
					}
				}
				if (!suffixOK) {
					controlDecoration.show();
					return ValidationStatus.error(errorText);
				}
			}
			controlDecoration.hide();
			return ValidationStatus.ok();
		}
	}

	protected DataBindingContext initDataBindings() {

		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue textObserveTextObserveWidget = SWTObservables.observeText(titleText, SWT.Modify);
		final IObservableValue beanTitleObserveValue = PojoObservables.observeValue(bean, "title"); //$NON-NLS-1$
		UpdateValueStrategy titleStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		titleStrategy.setBeforeSetValidator(new StringValidator("A title must be specified", titleText)); //$NON-NLS-1$
		bindingContext.bindValue(textObserveTextObserveWidget, beanTitleObserveValue, titleStrategy, null);
		//
		IObservableValue text_3ObserveTextObserveWidget = SWTObservables.observeText(authorText, SWT.Modify);
		final IObservableValue beanCreatorObserveValue = PojoObservables.observeValue(bean, "creator"); //$NON-NLS-1$
		UpdateValueStrategy authorStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		authorStrategy.setBeforeSetValidator(new StringValidator("An author must be specified", authorText)); //$NON-NLS-1$
		bindingContext.bindValue(text_3ObserveTextObserveWidget, beanCreatorObserveValue, authorStrategy, null);
		//
		IObservableValue dateTimeObserveSelectionObserveWidget = SWTObservables.observeSelection(dateTime);
		final IObservableValue beanPublicationDateObserveValue = PojoObservables.observeValue(bean, "publicationDate"); //$NON-NLS-1$
		bindingContext.bindValue(dateTimeObserveSelectionObserveWidget, beanPublicationDateObserveValue, null, null);
		//
		IObservableValue text_4ObserveTextObserveWidget = SWTObservables.observeText(identifierText, SWT.Modify);
		final IObservableValue beanIdentifierObserveValue = PojoObservables.observeValue(bean, "identifier"); //$NON-NLS-1$
		UpdateValueStrategy identifierStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		identifierStrategy
				.setBeforeSetValidator(new StringValidator("An identifier must be specified", identifierText)); //$NON-NLS-1$
		bindingContext.bindValue(text_4ObserveTextObserveWidget, beanIdentifierObserveValue, identifierStrategy, null);
		//
		IObservableValue schemeTextObserveTextObserveWidget = SWTObservables.observeText(schemeText);
		final IObservableValue beanIdSchemeObserveValue = PojoObservables.observeValue(bean, "scheme"); //$NON-NLS-1$
		UpdateValueStrategy schemeStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		schemeStrategy.setBeforeSetValidator(new StringValidator("An identifier scheme must be specified", schemeText)); //$NON-NLS-1$
		bindingContext.bindValue(schemeTextObserveTextObserveWidget, beanIdSchemeObserveValue, schemeStrategy, null);
		//
		IObservableValue text_1ObserveTextObserveWidget = SWTObservables.observeText(copyrightText, SWT.Modify);
		final IObservableValue beanRightsObserveValue = PojoObservables.observeValue(bean, "rights"); //$NON-NLS-1$
		UpdateValueStrategy rightsStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		rightsStrategy.setBeforeSetValidator(new StringValidator("Rights must be specified", copyrightText)); //$NON-NLS-1$
		bindingContext.bindValue(text_1ObserveTextObserveWidget, beanRightsObserveValue, rightsStrategy, null);
		//
		IObservableValue comboObserveTextObserveWidget = SWTObservables.observeText(combo);
		final IObservableValue beanLanguageObserveValue = PojoObservables.observeValue(bean, "language"); //$NON-NLS-1$
		UpdateValueStrategy languageStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		languageStrategy.setBeforeSetValidator(new StringValidator("A language must be specified", combo)); //$NON-NLS-1$
		bindingContext.bindValue(comboObserveTextObserveWidget, beanLanguageObserveValue, languageStrategy, null);
		//
		IObservableValue subjectTextObserveTextObserveWidget = SWTObservables.observeText(subjectText, SWT.Modify);
		final IObservableValue beanSubjectObserveValue = PojoObservables.observeValue(bean, "subject"); //$NON-NLS-1$
		UpdateValueStrategy subjectStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		subjectStrategy.setBeforeSetValidator(new StringValidator("A subject must be specified", subjectText)); //$NON-NLS-1$
		bindingContext.bindValue(subjectTextObserveTextObserveWidget, beanSubjectObserveValue, subjectStrategy, null);
		//
		IObservableValue coverObserveTextObserveWidget = SWTObservables.observeText(coverText, SWT.Modify);
		IObservableValue beanCoverObserveValue = PojoObservables.observeValue(bean, "cover"); //$NON-NLS-1$
		UpdateValueStrategy coverStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		coverStrategy.setBeforeSetValidator(new FileValidator(
				"The cover image must be a valid image file of type PNG, SVG or JPEG.", coverText, new String[] { //$NON-NLS-1$
						".png", ".svg", ".jpeg", ".jpg" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		bindingContext.bindValue(coverObserveTextObserveWidget, beanCoverObserveValue, coverStrategy, null);
		//
		IObservableValue styleSheetTextObserveTextObserveWidget = SWTObservables.observeText(styleSheetText,
				SWT.Modify);
		IObservableValue beanStyleSheetObserveValue = PojoObservables.observeValue(bean, "styleSheet"); //$NON-NLS-1$
		UpdateValueStrategy styleSheetStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		styleSheetStrategy.setBeforeSetValidator(new FileValidator("The style sheet must be a valid CSS file.", //$NON-NLS-1$
				styleSheetText, new String[] { ".css" })); //$NON-NLS-1$
		bindingContext.bindValue(styleSheetTextObserveTextObserveWidget, beanStyleSheetObserveValue, styleSheetStrategy,
				null);
		//
		return bindingContext;
	}

	@Override
	public boolean isPageComplete() {
		boolean ok = super.isPageComplete();
		if (ok) {
			setMessage("Press finish to generate an EPUB from the Wiki markup."); //$NON-NLS-1$
		}
		return ok;
	}
}

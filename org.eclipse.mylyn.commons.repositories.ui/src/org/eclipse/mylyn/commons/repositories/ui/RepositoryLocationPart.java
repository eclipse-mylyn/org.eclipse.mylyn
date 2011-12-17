/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     GitHub - fix for bug 352919
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.ui;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.dialog.DialogPageSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.RepositoryValidator;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UsernamePasswordCredentials;
import org.eclipse.mylyn.commons.workbench.forms.SectionComposite;
import org.eclipse.mylyn.internal.commons.repositories.ui.RepositoryLocationValueProperty;
import org.eclipse.mylyn.internal.commons.repositories.ui.RepositoriesUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 * @since 3.5
 */
public class RepositoryLocationPart {

	public class UrlValidator implements IValidator {

		public IStatus validate(Object value) {
			if (!isValidUrl(value.toString())) {
				return new Status(IStatus.ERROR, RepositoriesUiPlugin.ID_PLUGIN,
						Messages.RepositoryLocationPart_Enter_a_valid_server_url);
			}
			return Status.OK_STATUS;
		}

	}

	private class UsernamePasswordListener implements ModifyListener, SelectionListener {

		private final AuthenticationType authenticationType;

		private final Button enabledButton;

		private boolean enablementReversed;

		private final Text passwordText;

		private final Button savePasswordButton;

		private boolean updating;

		private final Text userText;

		public UsernamePasswordListener(AuthenticationType authenticationType, Button enabledButton, Text userText,
				Text passwordText, Button savePasswordButton) {
			this.authenticationType = authenticationType;
			this.enabledButton = enabledButton;
			this.userText = userText;
			this.passwordText = passwordText;
			this.savePasswordButton = savePasswordButton;
			init();
		}

		private void apply() {
			if (updating) {
				return;
			}
			if (isEnabled()) {
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userText.getText(),
						passwordText.getText());
				getWorkingCopy().setCredentials(authenticationType, credentials);
			} else {
				getWorkingCopy().setCredentials(authenticationType, null);
			}
		}

		protected void init() {
			enabledButton.addSelectionListener(this);
			userText.addModifyListener(this);
			passwordText.addModifyListener(this);
			savePasswordButton.addSelectionListener(this);
		}

		protected boolean isEnabled() {
			return enabledButton.getSelection() != isEnablementReversed();
		}

		public boolean isEnablementReversed() {
			return enablementReversed;
		}

		public void modifyText(ModifyEvent event) {
			apply();
		}

		private void restore() {
			try {
				updating = true;
				UsernamePasswordCredentials credentials = getWorkingCopy().getCredentials(authenticationType,
						UsernamePasswordCredentials.class);
				if (credentials != null) {
					enabledButton.setSelection(!isEnablementReversed());
					userText.setText(credentials.getUserName());
					passwordText.setText(credentials.getPassword());
					savePasswordButton.setSelection(true);
				} else {
					enabledButton.setSelection(isEnablementReversed());
					userText.setText(""); //$NON-NLS-1$
					passwordText.setText(""); //$NON-NLS-1$
					savePasswordButton.setSelection(true);
				}
			} finally {
				updating = false;
			}
			updateWidgetEnablement();
		}

		public void setEnablementReversed(boolean enablementReversed) {
			this.enablementReversed = enablementReversed;
		}

		private void updateWidgetEnablement() {
			boolean enabled = isEnabled();
			userText.setEnabled(enabled);
			passwordText.setEnabled(enabled);
			savePasswordButton.setEnabled(enabled);
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			apply();

		}

		public void widgetSelected(SelectionEvent event) {
			apply();
			if (event.widget == enabledButton) {
				updateWidgetEnablement();
			}
		}

	}

	protected static final String PREFS_PAGE_ID_NET_PROXY = "org.eclipse.ui.net.NetPreferences"; //$NON-NLS-1$

	private DataBindingContext bindingContext;

	private boolean needsAdditionalSections;

	private boolean needsAnonymousLogin;

	private boolean needsHttpAuth;

	private boolean needsProxy;

	private boolean needsValidation;

	private IAdaptable serviceLocator;

	private final RepositoryLocation workingCopy;

	public RepositoryLocationPart(RepositoryLocation workingCopy) {
		this.workingCopy = workingCopy;
		setNeedsProxy(false);
		setNeedsHttpAuth(false);
		setNeedsValidation(true);
	}

	protected void applyValidatorResult(RepositoryValidator validator) {
		IStatus status = validator.getResult();
		String message = status.getMessage();
		if (message == null || message.length() == 0) {
			message = null;
		}
		switch (status.getSeverity()) {
		case IStatus.OK:
			if (status == Status.OK_STATUS) {
//				if (getUserName().length() > 0) {
//					message = "Credentials are valid.";
//				} else {
				message = Messages.RepositoryLocationPart_Repository_is_valid;
//				}
			}
			getPartContainer().setMessage(message, IMessageProvider.INFORMATION);
			break;
		case IStatus.INFO:
			getPartContainer().setMessage(message, IMessageProvider.INFORMATION);
			break;
		case IStatus.WARNING:
			getPartContainer().setMessage(message, IMessageProvider.WARNING);
			break;
		default:
			getPartContainer().setMessage(message, IMessageProvider.ERROR);
			break;
		}
	}

	private void bind(AuthenticationType authType, Button anonymousButton, Text userText, Text passwordText,
			Button savePasswordButton, boolean reverseEnablement) {
		UsernamePasswordListener listener = new UsernamePasswordListener(authType, anonymousButton, userText,
				passwordText, savePasswordButton);
		listener.setEnablementReversed(reverseEnablement);
		listener.restore();
	}

	protected void bind(Button button, String property) {
		ISWTObservableValue uiElement = SWTObservables.observeSelection(button);
		IObservableValue modelElement = new RepositoryLocationValueProperty(property, Boolean.FALSE.toString()).observe(workingCopy);
		bindingContext.bindValue(uiElement, modelElement, null, null);
	}

	protected void bind(Text text, String property) {
		bind(text, property, null, null);
	}

	protected void bind(Text text, String property, UpdateValueStrategy targetObservableValue,
			UpdateValueStrategy modelObservableValue) {
		ISWTObservableValue uiElement = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue modelElement = new RepositoryLocationValueProperty(property, null).observe(workingCopy);
		bindingContext.bindValue(uiElement, modelElement, targetObservableValue, modelObservableValue);
	}

	/**
	 * Returns whether this page can be validated or not.
	 * <p>
	 * This information is typically used by the wizard to set the enablement of the validation UI affordance.
	 * </p>
	 * 
	 * @return <code>true</code> if this page can be validated, and <code>false</code> otherwise
	 * @see #needsValidation()
	 * @see IWizardContainer#updateButtons()
	 */
	public boolean canValidate() {
		return getValidator() != null;
	}

	protected Control createAdditionalContents(Composite composite) {
		return null;
	}

	public Control createContents(Composite parent) {
		bindingContext = new DataBindingContext();
		WizardPage wizardPage = getContainer(WizardPage.class);
		if (wizardPage != null) {
			WizardPageSupport.create(wizardPage, bindingContext);
		} else {
			DialogPage page = getContainer(DialogPage.class);
			if (page != null) {
				DialogPageSupport.create(page, bindingContext);
			}
		}
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(composite);

//		Composite this = new Composite(parent, SWT.NULL);
//		Layout layout = new FillLayout();
//		this.setLayout(layout);

		createServerSection(composite);
		createUserSection(composite);

		Control control = createAdditionalContents(composite);
		if (control != null) {
			GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(control);
		}

		if (needsHttpAuth() || needsProxy() || needsAdditionalSections()) {
			SectionComposite sectionComposite = new SectionComposite(composite, SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(sectionComposite);

			if (needsHttpAuth()) {
				createHttpAuthSection(sectionComposite);
			}
			if (needsProxy()) {
				createProxySection(sectionComposite);
			}
			createSections(sectionComposite);
		}

//		Button validateButton = new Button(composite, SWT.PUSH);
//		validateButton.setText("Validate");
//		validateButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				validate();
//			}
//		});

		return composite;
	}

	private void createHttpAuthSection(SectionComposite parent) {
		int style = SWT.NONE;
		if (getWorkingCopy().getCredentials(AuthenticationType.HTTP, UsernamePasswordCredentials.class) != null) {
			style |= ExpandableComposite.EXPANDED;
		}
		ExpandableComposite section = parent.createSection(Messages.RepositoryLocationPart_HTTP_Authentication, style);
		section.clientVerticalSpacing = 5;

		Composite composite = new Composite(section, SWT.NONE);
		section.setClient(composite);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(composite);

		Label label;

		Button enableButton = new Button(composite, SWT.CHECK);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(enableButton);
		enableButton.setText(Messages.RepositoryLocationPart_Enable_HTTP_Authentication);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_User);

		Text userText = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(userText);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_Password);

		Text passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);

		Button savePasswordButton = new Button(composite, SWT.CHECK);
		savePasswordButton.setText(Messages.RepositoryLocationPart_Save_Password);

		bind(AuthenticationType.HTTP, enableButton, userText, passwordText, savePasswordButton, false);
	}

	private void createProxySection(final SectionComposite parent) {
		ExpandableComposite section = parent.createSection(Messages.RepositoryLocationPart_Proxy_Server_Configuration);

		Composite composite = new Composite(section, SWT.NONE);
		section.setClient(composite);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(composite);

		Label label;

		Button systemProxyButton = new Button(composite, SWT.CHECK);
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(systemProxyButton);
		systemProxyButton.setText(Messages.RepositoryLocationPart_Use_global_Network_Connections_preferences);
//		systemProxyButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				updateProxyEnablement(systemProxyButton.getSelection());
//			}
//		});
		bind(systemProxyButton, RepositoryLocation.PROPERTY_PROXY_USEDEFAULT);

		Link changeProxySettingsLink = new Link(composite, SWT.NONE);
		changeProxySettingsLink.setText(Messages.RepositoryLocationPart_Change_Settings);
		changeProxySettingsLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PreferenceDialog dlg = PreferencesUtil.createPreferenceDialogOn(parent.getShell(),
						PREFS_PAGE_ID_NET_PROXY, new String[] { PREFS_PAGE_ID_NET_PROXY }, null);
				dlg.open();
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_Proxy_Host);

		Text proxyHostText = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(proxyHostText);
		bind(proxyHostText, RepositoryLocation.PROPERTY_PROXY_HOST);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_Proxy_Port);

		Text proxyPortText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(proxyPortText);
		bind(proxyPortText, RepositoryLocation.PROPERTY_PROXY_PORT);

		// authentication

		Button enableButton = new Button(composite, SWT.CHECK);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(enableButton);
		enableButton.setText(Messages.RepositoryLocationPart_Enable_Proxy_Authentication);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_User);

		Text userText = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(userText);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_Password);

		Text passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);

		Button savePasswordButton = new Button(composite, SWT.CHECK);
		savePasswordButton.setText(Messages.RepositoryLocationPart_Save_Password);

		bind(AuthenticationType.PROXY, enableButton, userText, passwordText, savePasswordButton, false);
	}

	protected void createSections(SectionComposite sectionComposite) {
	}

	private void createServerSection(Composite parent) {
		Label label;

		label = new Label(parent, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_Server);

		Text urlText = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(urlText);
		bind(urlText, RepositoryLocation.PROPERTY_URL, getUrlUpdateValueStrategy(), null);

		label = new Label(parent, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_Label);

		Text labelText = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(labelText);
		bind(labelText, RepositoryLocation.PROPERTY_LABEL);

		Button disconnectedButton = new Button(parent, SWT.CHECK);
		disconnectedButton.setText(Messages.RepositoryLocationPart_Disconnected);
		bind(disconnectedButton, RepositoryLocation.PROPERTY_OFFLINE);
	}

	protected UpdateValueStrategy getUrlUpdateValueStrategy() {
		return new UpdateValueStrategy().setAfterConvertValidator(new UrlValidator());
	}

	private void createUserSection(Composite parent) {
		Label label;

		label = new Label(parent, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_User);

		Text userText = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(userText);
		bind(userText, RepositoryLocation.PROPERTY_USERNAME);

		Button anonymousButton = new Button(parent, SWT.CHECK);
		anonymousButton.setText(Messages.RepositoryLocationPart_Anonymous);

		label = new Label(parent, SWT.NONE);
		label.setText(Messages.RepositoryLocationPart_Password);

		Text passwordText = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);

		Button savePasswordButton = new Button(parent, SWT.CHECK);
		savePasswordButton.setText(Messages.RepositoryLocationPart_Save_Password);

		bind(AuthenticationType.REPOSITORY, anonymousButton, userText, passwordText, savePasswordButton, true);
	}

	public <T> T getContainer(Class<T> clazz) {
		return (T) getServiceLocator().getAdapter(clazz);
	}

	public IPartContainer getPartContainer() {
		return getContainer(IPartContainer.class);
	}

	private IAdaptable getServiceLocator() {
		return serviceLocator;
	}

	protected RepositoryValidator getValidator() {
		return null;
	}

	protected RepositoryLocation getWorkingCopy() {
		return workingCopy;
	}

	public boolean isValidUrl(String url) {
		if (url.startsWith("https://") || url.startsWith("http://")) { //$NON-NLS-1$//$NON-NLS-2$
			try {
				new URI(url);
				return true;
			} catch (Exception e) {
				// fall through
			}
		}
		return false;
	}

	public boolean needsAdditionalSections() {
		return needsAdditionalSections;
	}

	public boolean needsAnonymousLogin() {
		return needsAnonymousLogin;
	}

	public boolean needsHttpAuth() {
		return this.needsHttpAuth;
	}

	public boolean needsProxy() {
		return this.needsProxy;
	}

	public boolean needsValidation() {
		return needsValidation;
	}

	public void setNeedsAdditionalSections(boolean needsAdditionalSections) {
		this.needsAdditionalSections = needsAdditionalSections;
	}

	public void setNeedsAnonymousLogin(boolean needsAnonymousLogin) {
		this.needsAnonymousLogin = needsAnonymousLogin;
	}

	public void setNeedsHttpAuth(boolean needsHttpAuth) {
		this.needsHttpAuth = needsHttpAuth;
	}

	public void setNeedsProxy(boolean needsProxy) {
		this.needsProxy = needsProxy;
	}

	public void setNeedsValidation(boolean needsValidation) {
		this.needsValidation = needsValidation;
	}

	public void setServiceLocator(IAdaptable container) {
		this.serviceLocator = container;
	}

	/**
	 * Validate settings provided by the {@link #getValidator() validator}, typically the server settings.
	 */
	public void validate() {
		final RepositoryValidator validator = getValidator();
		if (validator == null) {
			return;
		}

		final AtomicReference<IStatus> result = new AtomicReference<IStatus>();
		try {
			getContainer(IPartContainer.class).run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(Messages.RepositoryLocationPart_Validating_repository, IProgressMonitor.UNKNOWN);
					try {
						result.set(validator.run(monitor));
					} catch (OperationCanceledException e) {
						result.set(Status.CANCEL_STATUS);
						throw new InterruptedException();
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, RepositoriesUiPlugin.ID_PLUGIN,
							Messages.RepositoryLocationPart_Unexpected_error_during_repository_validation, e),
					StatusManager.SHOW | StatusManager.BLOCK | StatusManager.LOG);
			return;
		} catch (InterruptedException e) {
			// canceled
			return;
		}
		if (result.get() == null) {
			validator.setResult(Status.OK_STATUS);
		} else {
			validator.setResult(result.get());
		}
		getPartContainer().updateButtons();
		applyValidatorResult(validator);
	}

}

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

package org.eclipse.mylyn.internal.commons.ui.team;

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
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.RepositoryValidator;
import org.eclipse.mylyn.internal.commons.ui.SectionComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 * @since 3.5
 */
public class RepositoryLocationPart {

	public class UrlValidator implements IValidator {

		public IStatus validate(Object value) {
			if (!isValidUrl(value.toString())) {
				return new Status(IStatus.ERROR, TeamUiPlugin.ID_PLUGIN, "Enter a valid server url.");
			}
			return Status.OK_STATUS;
		}

	}

	private DataBindingContext bindingContext;

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
	}

	protected void applyValidatorResult(IStatus status) {
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
				message = "Repository is valid.";
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

	protected void bind(Button button, String property) {
		ISWTObservableValue uiElement = SWTObservables.observeSelection(button);
		IObservableValue modelElement = new RepositoryLocationValueProperty(property).observe(workingCopy);
		bindingContext.bindValue(uiElement, modelElement, null, null);
	}

	protected void bind(Text text, String property) {
		bind(text, property, null, null);
	}

	protected void bind(Text text, String property, UpdateValueStrategy targetObservableValue,
			UpdateValueStrategy modelObservableValue) {
		ISWTObservableValue uiElement = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue modelElement = new RepositoryLocationValueProperty(property).observe(workingCopy);
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
		return false;
	}

	public Control createContents(Composite parent) {
		bindingContext = new DataBindingContext();
		DialogPage page = getContainer(DialogPage.class);
		if (page != null) {
			DialogPageSupport.create(page, bindingContext);
		} else {
			WizardPage wizardPage = getContainer(WizardPage.class);
			if (wizardPage != null) {
				WizardPageSupport.create(wizardPage, bindingContext);
			}
		}
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(composite);

//		Composite this = new Composite(parent, SWT.NULL);
//		Layout layout = new FillLayout();
//		this.setLayout(layout);

		createServerSection(composite);
		createUserSection(composite);

		SectionComposite sectionComposite = new SectionComposite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(sectionComposite);

		if (needsHttpAuth()) {
			createHttpAuthSection(sectionComposite);
		}
		if (needsProxy()) {
			createProxySection(sectionComposite);
		}
		createSections(sectionComposite);

		Button validateButton = new Button(composite, SWT.PUSH);
		validateButton.setText("Validate");
		validateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});

		return composite;
	}

	private void createProxySection(SectionComposite parent) {
		Composite composite = parent.createSection("Proxy Server Configuration");
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(composite);

		// ignore

	}

	private void createHttpAuthSection(SectionComposite parent) {
		Composite composite = parent.createSection("HTTP Authentication");
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(composite);

		// ignore

	}

	protected void createSections(SectionComposite sectionComposite) {
	}

	private void createServerSection(Composite parent) {
		Label label;

		label = new Label(parent, SWT.NONE);
		label.setText("&Server:");

		Text urlText = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(urlText);
		bind(urlText, RepositoryLocation.PROPERTY_URL,
				new UpdateValueStrategy().setAfterConvertValidator(new UrlValidator()), null);

		label = new Label(parent, SWT.NONE);
		label.setText("&Label:");

		Text labelText = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(labelText);
		bind(labelText, RepositoryLocation.PROPERTY_LABEL);

		Button disconnectedButton = new Button(parent, SWT.CHECK);
		disconnectedButton.setText("Disconnected");
		bind(disconnectedButton, RepositoryLocation.PROPERTY_OFFLINE);
	}

	private void createUserSection(Composite parent) {
		Label label;

		label = new Label(parent, SWT.NONE);
		label.setText("&User:");

		Text userText = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(userText);
		bind(userText, RepositoryLocation.PROPERTY_USERNAME);

		Button anonymousButton = new Button(parent, SWT.CHECK);
		anonymousButton.setText("Anonymous");
		bind(anonymousButton, "anonymous");

		label = new Label(parent, SWT.NONE);
		label.setText("&Password:");

		Text passwordText = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);

		Button savePasswordButton = new Button(parent, SWT.CHECK);
		savePasswordButton.setText("Save Password");
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
	protected void validate() {
		final RepositoryValidator validator = getValidator();
		if (validator == null) {
			return;
		}

		final AtomicReference<IStatus> result = new AtomicReference<IStatus>();
		try {
			getContainer(IPartContainer.class).run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Validating repository", IProgressMonitor.UNKNOWN);
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
					new Status(IStatus.ERROR, TeamUiPlugin.ID_PLUGIN, "Unexpected error during repository validation.",
							e), StatusManager.SHOW | StatusManager.BLOCK | StatusManager.LOG);
			return;
		} catch (InterruptedException e) {
			// canceled
			return;
		}

		getPartContainer().updateButtons();
		applyValidatorResult(result.get());
	}

}

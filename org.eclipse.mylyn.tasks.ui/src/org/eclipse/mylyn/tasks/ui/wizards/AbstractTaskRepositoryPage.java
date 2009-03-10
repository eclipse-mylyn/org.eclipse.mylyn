/*******************************************************************************
 * Copyright (c) 2004, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Helen Bershadskaya - improvements for bug 242445
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * An abstract base class for repository settings page that supports the <code>taskRepositoryPageContribution</code>
 * extension point.
 * 
 * {@link ITaskRepositoryPage} implementations are encouraged to extend {@link AbstractRepositorySettingsPage} if
 * possible as it provides a standard UI for managing server settings.
 * 
 * @see AbstractRepositorySettingsPage
 * @author David Green
 * @author Steffen Pingel
 * @since 3.1
 */
public abstract class AbstractTaskRepositoryPage extends WizardPage implements ITaskRepositoryPage {

	private static final String CLASS = "class"; //$NON-NLS-1$

	private static final String ID = "id"; //$NON-NLS-1$

	private static final String KIND = "connectorKind"; //$NON-NLS-1$

	private static final String TASK_REPOSITORY_PAGE_CONTRIBUTION = "taskRepositoryPageContribution"; //$NON-NLS-1$

	private static final String TASK_REPOSITORY_PAGE_CONTRIBUTION_EXTENSION = "org.eclipse.mylyn.tasks.ui.taskRepositoryPageContribution"; //$NON-NLS-1$

	private static final Comparator<AbstractTaskRepositoryPageContribution> CONTRIBUTION_COMPARATOR = new ContributionComparator();

	private final TaskRepository repository;

	private final List<AbstractTaskRepositoryPageContribution> contributions;

	FormToolkit toolkit;

	private final AbstractTaskRepositoryPageContribution.Listener contributionListener = new AbstractTaskRepositoryPageContribution.Listener() {
		public void validationRequired(AbstractTaskRepositoryPageContribution contribution) {
			validatePageSettings();
		}
	};

	/**
	 * @since 3.1
	 */
	public AbstractTaskRepositoryPage(String title, String description, TaskRepository repository) {
		super(title);
		if (repository != null && !repository.getConnectorKind().equals(getConnectorKind())) {
			throw new IllegalArgumentException(
					"connectorKind of repository does not match connectorKind of page, expected '" + getConnectorKind() + "', got '" + repository.getConnectorKind() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		this.repository = repository;
		this.contributions = new ArrayList<AbstractTaskRepositoryPageContribution>();
		setTitle(title);
		setDescription(description);
	}

	/**
	 * Get the kind of connector supported by this page.
	 * 
	 * @return the kind of connector, never null
	 * @since 3.1
	 */
	public abstract String getConnectorKind();

	@Override
	public void dispose() {
		if (toolkit != null) {
			toolkit.dispose();
			toolkit = null;
		}
		super.dispose();
	}

	/**
	 * Creates the contents of the page. Subclasses may override this method to change where the contributions are
	 * added.
	 * 
	 * @since 2.0
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		toolkit = new FormToolkit(TasksUiPlugin.getDefault().getFormColors(parent.getDisplay()));

		Composite compositeContainer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		compositeContainer.setLayout(layout);

//		Composite compositeContainer = new Composite(parent, SWT.NULL);
//		Layout layout = new FillLayout();
//		compositeContainer.setLayout(layout);

		createSettingControls(compositeContainer);
		createContributionControls(compositeContainer);

		setControl(compositeContainer);
		//getControl().getShell().pack();
	}

	/**
	 * Creates the controls of this page.
	 * 
	 * @since 3.1
	 */
	protected abstract void createSettingControls(Composite parent);

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete() && conributionsIsPageComplete();
	}

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && contributionsCanFlipToNextPage();
	}

	private boolean contributionsCanFlipToNextPage() {
		for (AbstractTaskRepositoryPageContribution contribution : contributions) {
			if (!contribution.canFlipToNextPage()) {
				return false;
			}
		}
		return true;
	}

	private boolean conributionsIsPageComplete() {
		for (AbstractTaskRepositoryPageContribution contribution : contributions) {
			if (!contribution.isPageComplete()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Subclasses should only call this method if they override {@link #createContents(Composite)}.
	 * 
	 * @param parentControl
	 *            the container into which the contributions will create their UI
	 * @since 3.1
	 */
	protected void createContributionControls(final Composite parentControl) {
		contributions.clear();
		contributions.addAll(findApplicableContributors());

		if (!contributions.isEmpty()) {
			final List<AbstractTaskRepositoryPageContribution> badContributions = new ArrayList<AbstractTaskRepositoryPageContribution>();
			for (final AbstractTaskRepositoryPageContribution contribution : contributions) {
				SafeRunnable.run(new SafeRunnable() {
					public void run() throws Exception {
						contribution.init(getConnectorKind(), repository);
						contribution.addListener(contributionListener);
					}

					@Override
					public void handleException(Throwable e) {
						badContributions.add(contribution);
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
								"Problems occured when initializing contribution \"{0}\"", contribution.getId()), e)); //$NON-NLS-1$
					}
				});
			}
			contributions.removeAll(badContributions);

			Collections.sort(contributions, CONTRIBUTION_COMPARATOR);

			for (final AbstractTaskRepositoryPageContribution contribution : contributions) {
				final ExpandableComposite section = createSection(parentControl, contribution.getTitle());
				section.setToolTipText(contribution.getDescription());

				SafeRunnable.run(new SafeRunnable() {
					public void run() throws Exception {
						Control control = contribution.createControl(section);
						section.setClient(control);
					}

					@Override
					public void handleException(Throwable e) {
						section.dispose();
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
								"Problems occured when creating control for contribution \"{0}\"", //$NON-NLS-1$
								contribution.getId()), e));
					}
				});
			}
		}
	}

	/**
	 * @since 3.1
	 */
	protected ExpandableComposite createSection(final Composite parentControl, String title) {
		final ExpandableComposite section = toolkit.createExpandableComposite(parentControl,
				ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.COMPACT);
		section.clientVerticalSpacing = 0;
		section.setBackground(parentControl.getBackground());
		section.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				parentControl.layout(true);
				getControl().getShell().pack();
			}
		});
		section.setText(title);
		GridDataFactory.fillDefaults().indent(0, 5).grab(true, false).span(3, SWT.DEFAULT).applyTo(section);
		return section;
	}

	/**
	 * Validate the settings of this page, not including contributions. This method should not be called directly by
	 * page implementations. Always run on a UI thread.
	 * 
	 * @return the status, or null if there are no messages.
	 * @see #validatePageSettings()
	 * @since 3.1
	 */
	protected abstract IStatus validate();

	/**
	 * Overriding methods should call <code>super.applyTo(repository)</code>
	 * 
	 * @since 3.1
	 */
	public void applyTo(TaskRepository repository) {
		applyContributionSettingsTo(repository);
	}

	private void applyContributionSettingsTo(TaskRepository repository) {
		for (AbstractTaskRepositoryPageContribution contribution : contributions) {
			contribution.applyTo(repository);
		}
	}

	/**
	 * Returns a status if there is a message to display, otherwise null.
	 */
	private IStatus computeValidation() {
		final MultiStatus cumulativeResult = new MultiStatus(TasksUiPlugin.ID_PLUGIN, IStatus.OK,
				Messages.AbstractTaskRepositoryPage_Validation_failed, null);

		// validate the page
		IStatus result = validate();
		if (result != null) {
			cumulativeResult.add(result);
		}

		// validate contributions
		for (final AbstractTaskRepositoryPageContribution contribution : contributions) {
			SafeRunnable.run(new SafeRunnable() {
				public void run() throws Exception {
					IStatus result = contribution.validate();
					if (result != null) {
						cumulativeResult.add(result);
					}
				}

				@Override
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
							"Problems occured when validating contribution \"{0}\"", contribution.getId()), e)); //$NON-NLS-1$
				}
			});
		}

		return cumulativeResult;
	}

	/**
	 * Validate all settings in the page including contributions. This method should be called whenever a setting is
	 * changed on the page.
	 * 
	 * The results of validation are applied and the buttons of the page are updated.
	 * 
	 * @see #validate(IProgressMonitor)
	 * @see #applyValidationResult(IStatus[])
	 */
	private void validatePageSettings() {
		IStatus validationStatus = computeValidation();
		applyValidationResult(validationStatus);
		getWizard().getContainer().updateButtons();
	}

	/**
	 * Apply the results of validation to the page. The implementation finds the most {@link IStatus#getSeverity()
	 * severe} status and {@link #setMessage(String, int) applies the message} to the page.
	 * 
	 * @param status
	 *            the status of the validation, or null
	 */
	private void applyValidationResult(IStatus status) {
		if (status == null || status.isOK()) {
			setMessage(null, IMessageProvider.INFORMATION);
			setErrorMessage(null);
		} else {
			// find the most severe status
			int messageType;
			switch (status.getSeverity()) {
			case IStatus.OK:
			case IStatus.INFO:
				messageType = IMessageProvider.INFORMATION;
				break;
			case IStatus.WARNING:
				messageType = IMessageProvider.WARNING;
				break;
			case IStatus.ERROR:
			default:
				messageType = IMessageProvider.ERROR;
				break;
			}
			setErrorMessage(null);
			setMessage(status.getMessage(), messageType);
		}
	}

	private List<AbstractTaskRepositoryPageContribution> findApplicableContributors() {
		List<AbstractTaskRepositoryPageContribution> contributors = new ArrayList<AbstractTaskRepositoryPageContribution>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		IExtensionPoint editorExtensionPoint = registry.getExtensionPoint(TASK_REPOSITORY_PAGE_CONTRIBUTION_EXTENSION);
		IExtension[] editorExtensions = editorExtensionPoint.getExtensions();
		for (IExtension extension : editorExtensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(TASK_REPOSITORY_PAGE_CONTRIBUTION)) {
					String kind = element.getAttribute(KIND);
					if (kind == null || kind.length() == 0 || kind.equals(getConnectorKind())) {
						String id = element.getAttribute(ID);
						try {
							if (id == null || id.length() == 0) {
								throw new IllegalStateException(TASK_REPOSITORY_PAGE_CONTRIBUTION + "/@" + ID //$NON-NLS-1$
										+ " is required"); //$NON-NLS-1$
							}
							Object contributor = element.createExecutableExtension(CLASS);
							AbstractTaskRepositoryPageContribution pageContributor = (AbstractTaskRepositoryPageContribution) contributor;
							pageContributor.setId(id);
							if (pageContributor.isEnabled()) {
								contributors.add(pageContributor);
							}
						} catch (Exception e) {
							StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load " //$NON-NLS-1$
									+ TASK_REPOSITORY_PAGE_CONTRIBUTION + " '" + id + "' from plug-in " //$NON-NLS-1$//$NON-NLS-2$
									+ element.getContributor().getName(), e));
						}
					}
				}
			}
		}

		return contributors;
	}

	private static class ContributionComparator implements Comparator<AbstractTaskRepositoryPageContribution> {

		public int compare(AbstractTaskRepositoryPageContribution o1, AbstractTaskRepositoryPageContribution o2) {
			if (o1 == o2) {
				return 0;
			}
			String s1 = o1.getTitle();
			String s2 = o2.getTitle();
			int i = s1.compareTo(s2);
			if (i == 0) {
				i = o1.getId().compareTo(o2.getId());
			}
			return i;
		}

	}
}

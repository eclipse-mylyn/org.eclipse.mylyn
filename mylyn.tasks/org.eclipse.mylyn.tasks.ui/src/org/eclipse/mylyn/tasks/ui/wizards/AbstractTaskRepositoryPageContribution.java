/*******************************************************************************
 * Copyright (c) 2004, 2011 David Green and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A contribution to a {@link ITaskRepositoryPage}, which enables plug-ins to contribute UI to the task repository settings. subclasses must
 * have a default public constructor.
 *
 * @author David Green
 * @since 3.1
 */
public abstract class AbstractTaskRepositoryPageContribution {

	/**
	 * A listener interface that should be implemented by classes wishing to be notified of changes that occur within the contribution.
	 *
	 * @since 3.1
	 */
	public interface Listener {

		/**
		 * Called when the state of the contribution changes such that validation should be performed
		 *
		 * @param contribution
		 *            the contribution that changed
		 * @see ITaskRepositoryPageContribution#validate(IProgressMonitor)
		 */
		void validationRequired(AbstractTaskRepositoryPageContribution contribution);

	}

	private final List<Listener> listeners = new CopyOnWriteArrayList<>();

	private final String title;

	private final String description;

	private TaskRepository repository;

	private String connectorKind;

	private String id;

	/**
	 * @param title
	 *            the title of the contribution, as displayed to the user, usually used as a section heading
	 * @param description
	 *            the description of the contribution, as displayed to the user, typically as a tool-tip
	 * @since 3.1
	 */
	protected AbstractTaskRepositoryPageContribution(String title, String description) {
		this.title = title;
		this.description = description;
		id = ""; //$NON-NLS-1$
	}

	/**
	 * Initialize the contribution
	 *
	 * @param connectorKind
	 *            the kind of connector for which this is a contribution
	 * @param repository
	 *            the repository for which this contribution was created, or null if the repository is not yet available
	 * @since 3.1
	 */
	public void init(String connectorKind, TaskRepository repository) {
		this.connectorKind = connectorKind;
		this.repository = repository;
	}

	/**
	 * Add a listener to this contribution. The contribution must notify the listener at the appropriate times, for example when a setting
	 * has changed in the UI.
	 *
	 * @see #removeListener(Listener)
	 * @since 3.1
	 */
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener from this contribution.
	 *
	 * @see #addListener(Listener)
	 * @since 3.1
	 */
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 * @since 3.1
	 */
	public abstract Control createControl(Composite parent);

	/**
	 * @see IDialogPage#getTitle()
	 * @since 3.1
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @see IDialogPage#getDescription()
	 * @since 3.1
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see IWizardPage#isPageComplete()
	 * @since 3.1
	 */
	public abstract boolean isPageComplete();

	/**
	 * @see IWizardPage#canFlipToNextPage()
	 * @since 3.1
	 */
	public abstract boolean canFlipToNextPage();

	/**
	 * Validate the settings of the contribution. Contributions should expect this method to be called often and should thus return quickly.
	 * Always called on the UI thread.
	 *
	 * @return the status (errors) on the contribution, or null if there are none. A MultiStatus should be used to return multiple error
	 *         messages or warnings.
	 * @since 3.1
	 */
	public abstract IStatus validate();

	/**
	 * Apply the settings in the contribution to the given repository.
	 *
	 * @param repository
	 *            the repository to which settings should be applied
	 * @see ITaskRepositoryPage#applyTo(TaskRepository)
	 * @since 3.1
	 */
	public abstract void applyTo(TaskRepository repository);

	/**
	 * Requests a validation.
	 *
	 * @see #validate()
	 * @since 3.1
	 */
	protected void fireValidationRequired() {
		for (Listener l : listeners) {
			l.validationRequired(this);
		}
	}

	/**
	 * Returns the repository for which this contribution was created, or null if it was created for a new repository.
	 *
	 * @since 3.1
	 */
	protected final TaskRepository getRepository() {
		return repository;
	}

	/**
	 * Returns the kind of connector for which this contribution was created.
	 *
	 * @since 3.1
	 */
	protected final String getConnectorKind() {
		return connectorKind;
	}

	void setId(String id) {
		Assert.isNotNull(id);
		this.id = id;
	}

	/**
	 * Returns the id as it is declared in the contribution extension point.
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Indicate if this contribution is enabled. Subclasses may override to programatically disable the contribution when needed. This
	 * method is called early in the contributor's lifecycle, before any controls are created. the default implementation returns true.
	 */
	public boolean isEnabled() {
		return true;
	}
}

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

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProduct;
import org.eclipse.mylyn.internal.tasks.bugs.AbstractSupportElement;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProduct;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProvider;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProviderManager;
import org.eclipse.mylyn.internal.tasks.bugs.TasksBugsPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

/**
 * @author Steffen Pingel
 */
public class ReportBugOrEnhancementWizard extends Wizard {

	private class SupportContentProvider implements IStructuredContentProvider {

		private SupportProviderManager providerManager;

		private Object input;

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof SupportProvider) {
				List<SupportProduct> providerProducts = getProdcuts(inputElement);
				return providerProducts.toArray();
			} else if (input == inputElement) {
				List<AbstractSupportElement> elements = new ArrayList<AbstractSupportElement>();
				elements.addAll(providerManager.getProviders());
				elements.addAll(providerManager.getCategories());
				return elements.toArray();
			} else {
				return new Object[0];
			}
		}

		private List<SupportProduct> getProdcuts(Object inputElement) {
			Collection<SupportProduct> products = providerManager.getProducts();
			SupportProvider provider = (SupportProvider) inputElement;
			List<SupportProduct> providerProducts = new ArrayList<SupportProduct>();
			for (SupportProduct product : products) {
				if (provider.equals(product.getProvider()) && product.isInstalled()) {
					providerProducts.add(product);
				}
			}
			return providerProducts;
		}

		public void dispose() {
			// ignore
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.input = newInput;
			this.providerManager = TasksBugsPlugin.getTaskErrorReporter().getProviderManager();
		}

	}

	public ReportBugOrEnhancementWizard() {
		setForcePreviousAndNextButtons(true);
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.ReportBugOrEnhancementWizard_Report_Bug_or_Enhancement);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	@Override
	public void addPages() {
		SelectSupportElementPage page = new SelectSupportElementPage("selectProvider", new SupportContentProvider()); //$NON-NLS-1$
		page.setInput(new Object());
		addPage(page);
	}

	@Override
	public boolean canFinish() {
		return getSelectedElement() instanceof SupportProduct;
	}

	public AbstractSupportElement getSelectedElement() {
		IWizardPage page = getContainer().getCurrentPage();
		if (page != null) {
			return ((SelectSupportElementPage) page).getSelectedElement();
		}
		return null;
	}

	@Override
	public boolean performFinish() {
		final AbstractSupportElement product = getSelectedElement();
		if (!(product instanceof SupportProduct)) {
			return false;
		}

		// delay run this until after the dialog has been closed
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				TasksBugsPlugin.getTaskErrorReporter().handle(new ProductStatus((IProduct) product));
			}
		});

		return true;
	}

}

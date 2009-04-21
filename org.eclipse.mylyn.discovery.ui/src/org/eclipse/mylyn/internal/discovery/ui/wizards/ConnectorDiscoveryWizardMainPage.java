/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.discovery.core.model.AbstractDiscoverySource;
import org.eclipse.mylyn.internal.discovery.core.model.BundleDiscoveryStrategy;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptor;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDiscovery;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCategory;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.Icon;
import org.eclipse.mylyn.internal.discovery.core.model.RemoteBundleDiscoveryStrategy;
import org.eclipse.mylyn.internal.discovery.ui.DiscoveryUi;
import org.eclipse.mylyn.internal.discovery.ui.util.DiscoveryCategoryComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * The main wizard page that allows users to select connectors that they wish to
 * install.
 * 
 * @author David Green
 */
public class ConnectorDiscoveryWizardMainPage extends WizardPage {

	private List<ConnectorDescriptor> installableConnectors = new ArrayList<ConnectorDescriptor>();

	private ConnectorDiscovery discovery;

	private Composite body;

	private List<Resource> disposables = new ArrayList<Resource>();

	public ConnectorDiscoveryWizardMainPage() {
		super(ConnectorDiscoveryWizardMainPage.class.getSimpleName());
		setTitle("Connector Discovery");
		// setImageDescriptor(image);
		setDescription("Select connectors to install");
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));
		//		
		// { // header
		// Composite header = new Composite(container,SWT.NULL);
		// header.setLayout(new GridLayout(1,false));
		// GridDataFactory.fillDefaults().grab(true, false).applyTo(header);
		//			
		// // TODO: header needed? instructions, refresh button, filter
		// checkboxes?
		// }
		{ // container
			body = new Composite(container, SWT.NULL);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(body);
		}

		setControl(container);
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Resource resource : disposables) {
			resource.dispose();
		}
		disposables.clear();
	}

	public void createBodyContents() {
		// remove any existing contents
		for (Control child : new ArrayList<Control>(Arrays.asList(body
				.getChildren()))) {
			child.dispose();
		}
		body.setLayout(new GridLayout(1, true));

		// we put the contents in a scrolled composite since we don't know how
		// big it will be
		ScrolledComposite scrolledComposite = new ScrolledComposite(body,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(
				scrolledComposite);

		Composite scrolledContents = new Composite(scrolledComposite, SWT.NONE);
		createDiscoveryContents(scrolledContents);

		Point bodyIdealSize = scrolledContents.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true);
		scrolledContents.setSize(bodyIdealSize);

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(bodyIdealSize);

		scrolledComposite.setContent(scrolledContents);

		// we've changed it so it needs to know
		body.layout(true);
	}

	private void createDiscoveryContents(Composite container) {
		container.setLayout(new GridLayout(2, false));

		if (discovery != null) {
			List<DiscoveryCategory> categories = new ArrayList<DiscoveryCategory>(
					discovery.getCategories());
			Collections.sort(categories, new DiscoveryCategoryComparator());

			for (DiscoveryCategory category : categories) {
				if (category.getConnectors().isEmpty()) {
					// don't add empty categories
					continue;
				}
				{ // category header
					Label iconLabel = new Label(container, SWT.NULL);
					if (category.getIcon() != null) {
						Image image = computeIconImage(category.getSource(),
								category.getIcon());
						if (image != null) {
							iconLabel.setImage(image);
						}
					}
					Label nameLabel = new Label(container, SWT.NULL);
					GridDataFactory.fillDefaults().grab(true, false).applyTo(
							nameLabel);
					nameLabel.setText(category.getName());
				}

				Composite categoryContainer = new Composite(container, SWT.NULL);
				GridDataFactory.fillDefaults().span(2, 1).grab(true, false)
						.indent(30, 5).applyTo(categoryContainer);
				categoryContainer.setLayout(new GridLayout(2, false));

				for (final DiscoveryConnector connector : category.getConnectors()) {
					final Button checkbox = new Button(categoryContainer,SWT.CHECK|SWT.FLAT);
					if (connector.getIcon() != null) {
						Image image = computeIconImage(connector.getSource(),
								connector.getIcon());
						if (image != null) {
							checkbox.setImage(image);
						}
					}
					checkbox.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {
							widgetSelected(e);
						}
						public void widgetSelected(SelectionEvent e) {
							boolean selected = checkbox.getSelection();
							modifySelection(connector, selected);
						}
					});
					Label nameLabel = new Label(categoryContainer, SWT.NULL);
					GridDataFactory.fillDefaults().grab(true, false).applyTo(
							nameLabel);
					nameLabel.setText(connector.getName());
					nameLabel.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseUp(MouseEvent e) {
							boolean selected = !checkbox.getSelection();
							checkbox.setSelection(selected);
							modifySelection(connector, selected);
						}
					});
				}
			}
		}
		container.layout(true);
		container.redraw();
	}

	private Image computeIconImage(AbstractDiscoverySource discoverySource,
			Icon icon) {
		// FIXME: which image?
		String imagePath = icon.getImage32();
		if (imagePath != null && imagePath.length() > 0) {
			URL resource = discoverySource.getResource(imagePath);
			if (resource != null) {
				ImageDescriptor descriptor = ImageDescriptor
						.createFromURL(resource);
				Image image = descriptor.createImage();
				if (image != null) {
					disposables.add(image);
					return image;
				}
			}
		}
		return null;
	}

	private void maybeUpdateDiscovery() {
		if (!getControl().isDisposed() && isCurrentPage() && discovery == null) {
			boolean wasCancelled = false;
			try {
				getContainer().run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						ConnectorDiscovery connectorDiscovery = new ConnectorDiscovery();
						connectorDiscovery.getDiscoveryStrategies().add(
								new BundleDiscoveryStrategy());
						RemoteBundleDiscoveryStrategy remoteDiscoveryStrategy = new RemoteBundleDiscoveryStrategy();
						// FIXME: the discovery directory URL
						remoteDiscoveryStrategy
								.setDirectoryUrl("http://localhost/~dgreen/plugins/directory.txt");
						connectorDiscovery.getDiscoveryStrategies().add(
								remoteDiscoveryStrategy);
						try {
							connectorDiscovery.performDiscovery(monitor);
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						}
						ConnectorDiscoveryWizardMainPage.this.discovery = connectorDiscovery;
						if (monitor.isCanceled()) {
							throw new InterruptedException();
						}
					}
				});
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				IStatus status;
				if (!(cause instanceof CoreException)) {
					status = new Status(IStatus.ERROR, DiscoveryUi.BUNDLE_ID,
							"Unexpected exception", cause);
				} else {
					status = ((CoreException) cause).getStatus();
				}
				DiscoveryUi.logAndDisplayStatus("Connector Discovery Error",
						status);
			} catch (InterruptedException e) {
				// cancelled by user so nothing to do here.
				wasCancelled = true;
			}
			if (discovery != null) {
				discoveryUpdated(wasCancelled);
			}
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && discovery == null) {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					maybeUpdateDiscovery();
				}
			});
		}
	}

	private void discoveryUpdated(boolean wasCancelled) {
		createBodyContents();
		if (discovery != null && !wasCancelled) {
			int categoryWithConnectorCount = 0;
			for (DiscoveryCategory category : discovery.getCategories()) {
				categoryWithConnectorCount += category.getConnectors().size();
			}
			if (categoryWithConnectorCount == 0) {
				// nothing was discovered: notify the user
				MessageDialog
						.openWarning(
								getShell(),
								"No Connectors Found",
								"Connector discovery completed without finding any connectors.  Please check your Internet connection and try again.");
			}
		}
	}

	public List<ConnectorDescriptor> getInstallableConnectors() {
		return installableConnectors;
	}

	private void modifySelection(
			final DiscoveryConnector connector,
			boolean selected) {
		connector.setSelected(selected);
		
		if (selected) {
			installableConnectors.add(connector);
		} else {
			installableConnectors.remove(connector);
		}
		setPageComplete(!installableConnectors.isEmpty());
	}
}

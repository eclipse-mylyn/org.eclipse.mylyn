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
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * The main wizard page that allows users to select connectors that they wish to install.
 * 
 * @author David Green
 */
public class ConnectorDiscoveryWizardMainPage extends WizardPage {

	private static final String COLOR_WHITE = "white"; //$NON-NLS-1$

	private final List<ConnectorDescriptor> installableConnectors = new ArrayList<ConnectorDescriptor>();

	private ConnectorDiscovery discovery;

	private Composite body;

	private final List<Resource> disposables = new ArrayList<Resource>();

	private Font h2Font;

	private Font h1Font;

	private Color colorWhite;

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
		h1Font = null;
		h2Font = null;
	}

	public void createBodyContents() {
		// remove any existing contents
		for (Control child : new ArrayList<Control>(Arrays.asList(body.getChildren()))) {
			child.dispose();
		}
		initializeFonts();
		initializeColors();

		body.setLayout(new GridLayout(1, true));

		// we put the contents in a scrolled composite since we don't know how
		// big it will be
		ScrolledComposite scrolledComposite = new ScrolledComposite(body, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(scrolledComposite);

		Composite scrolledContents = new Composite(scrolledComposite, SWT.NONE);
		scrolledContents.setBackground(colorWhite);
		createDiscoveryContents(scrolledContents);

		Point bodyIdealSize = scrolledContents.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		scrolledContents.setSize(bodyIdealSize);

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(bodyIdealSize);

		scrolledComposite.setContent(scrolledContents);

		// we've changed it so it needs to know
		body.layout(true);
	}

	private void initializeColors() {
		ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		if (!colorRegistry.hasValueFor(COLOR_WHITE)) {
			colorRegistry.put(COLOR_WHITE, new RGB(255, 255, 255));
		}
		colorWhite = colorRegistry.get(COLOR_WHITE);
	}

	private void initializeFonts() {
		// create a level-2 heading font
		if (h2Font == null) {
			Font baseFont = JFaceResources.getDialogFont();
			FontData[] fontData = baseFont.getFontData();
			for (FontData data : fontData) {
				data.style = data.style | SWT.BOLD;
				data.height = data.height * 1.25f;
			}
			h2Font = new Font(Display.getCurrent(), fontData);
			disposables.add(h2Font);
		}
		// create a level-1 heading font
		if (h1Font == null) {
			Font baseFont = JFaceResources.getDialogFont();
			FontData[] fontData = baseFont.getFontData();
			for (FontData data : fontData) {
				data.style = data.style | SWT.BOLD;
				data.height = data.height * 1.35f;
			}
			h1Font = new Font(Display.getCurrent(), fontData);
			disposables.add(h1Font);
		}
	}

	private void createDiscoveryContents(Composite container) {
		container.setLayout(new GridLayout(2, false));

		final Color background = container.getBackground();
		if (discovery != null) {
			List<DiscoveryCategory> categories = new ArrayList<DiscoveryCategory>(discovery.getCategories());
			Collections.sort(categories, new DiscoveryCategoryComparator());

			for (DiscoveryCategory category : categories) {
				if (category.getConnectors().isEmpty()) {
					// don't add empty categories
					continue;
				}
				{ // category header
					Label iconLabel = new Label(container, SWT.NULL);
					iconLabel.setBackground(background);
					if (category.getIcon() != null) {
						Image image = computeIconImage(category.getSource(), category.getIcon());
						if (image != null) {
							iconLabel.setImage(image);
						}
					}
					GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(iconLabel);

					Label nameLabel = new Label(container, SWT.NULL);
					nameLabel.setBackground(background);
					GridDataFactory.fillDefaults().grab(true, false).applyTo(nameLabel);
					nameLabel.setFont(h1Font);
					nameLabel.setText(category.getName());

					Label description = new Label(container, SWT.NULL | SWT.WRAP);
					description.setBackground(background);
					GridDataFactory.fillDefaults().grab(true, false).hint(100, SWT.DEFAULT).applyTo(description);
					description.setText(category.getDescription());
				}

				Composite categoryContainer = new Composite(container, SWT.NULL);
				categoryContainer.setBackground(background);
				GridDataFactory.fillDefaults().span(2, 1).grab(true, false).indent(0, 5).applyTo(categoryContainer);
				categoryContainer.setLayout(new GridLayout(1, false));

				Composite border = new Composite(categoryContainer, SWT.NULL);
				GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 3).applyTo(border);
				border.addPaintListener(new ConnectorBorderPaintListener());
				for (final DiscoveryConnector connector : category.getConnectors()) {

					Composite connectorContainer = new Composite(categoryContainer, SWT.NULL);
					connectorContainer.setBackground(background);
					GridDataFactory.fillDefaults().grab(true, false).applyTo(connectorContainer);
					GridLayout categoryLayout = new GridLayout(2, false);
					categoryLayout.marginLeft = 30;
					categoryLayout.marginTop = 2;
					categoryLayout.marginBottom = 2;
					connectorContainer.setLayout(categoryLayout);

					final Button checkbox = new Button(connectorContainer, SWT.CHECK | SWT.FLAT);
					checkbox.setBackground(background);

					MouseAdapter selectMouseListener = new MouseAdapter() {
						@Override
						public void mouseUp(MouseEvent e) {
							boolean selected = !checkbox.getSelection();
							checkbox.setSelection(selected);
							modifySelection(connector, selected);
						}
					};
					connectorContainer.addMouseListener(selectMouseListener);

					if (connector.getIcon() != null) {
						Image image = computeIconImage(connector.getSource(), connector.getIcon());
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
					GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(checkbox);

					Label nameLabel = new Label(connectorContainer, SWT.NULL);
					nameLabel.setBackground(background);
					GridDataFactory.fillDefaults().grab(true, false).applyTo(nameLabel);
					nameLabel.setFont(h2Font);
					nameLabel.setText(connector.getName());
					nameLabel.addMouseListener(selectMouseListener);

					Label description = new Label(connectorContainer, SWT.NULL | SWT.WRAP);
					description.setBackground(background);
					GridDataFactory.fillDefaults().grab(true, false).hint(100, SWT.DEFAULT).applyTo(description);
					description.setText(connector.getDescription());
					description.addMouseListener(selectMouseListener);

					border = new Composite(categoryContainer, SWT.NULL);
					GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 1).applyTo(border);
					border.addPaintListener(new ConnectorBorderPaintListener());
				}
			}
		}
		container.layout(true);
		container.redraw();
	}

	private Image computeIconImage(AbstractDiscoverySource discoverySource, Icon icon) {
		// FIXME: which image?
		String imagePath = icon.getImage32();
		if (imagePath != null && imagePath.length() > 0) {
			URL resource = discoverySource.getResource(imagePath);
			if (resource != null) {
				ImageDescriptor descriptor = ImageDescriptor.createFromURL(resource);
				Image image = descriptor.createImage();
				if (image != null) {
					disposables.add(image);

					Rectangle bounds = image.getBounds();
					if (bounds.x != 32 || bounds.y != 32) {
						// FIXME: scale image
					}

					return image;
				}
			}
		}
		// FIXME: provide a spacer
		return null;
	}

	private void maybeUpdateDiscovery() {
		if (!getControl().isDisposed() && isCurrentPage() && discovery == null) {
			boolean wasCancelled = false;
			try {
				getContainer().run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						ConnectorDiscovery connectorDiscovery = new ConnectorDiscovery();
						connectorDiscovery.getDiscoveryStrategies().add(new BundleDiscoveryStrategy());
						RemoteBundleDiscoveryStrategy remoteDiscoveryStrategy = new RemoteBundleDiscoveryStrategy();
						// FIXME: the discovery directory URL
						remoteDiscoveryStrategy.setDirectoryUrl("http://localhost/~dgreen/plugins/directory.txt");
						connectorDiscovery.getDiscoveryStrategies().add(remoteDiscoveryStrategy);
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
					status = new Status(IStatus.ERROR, DiscoveryUi.BUNDLE_ID, "Unexpected exception", cause);
				} else {
					status = ((CoreException) cause).getStatus();
				}
				DiscoveryUi.logAndDisplayStatus("Connector Discovery Error", status);
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
				MessageDialog.openWarning(getShell(), "No Connectors Found",
						"Connector discovery completed without finding any connectors.  Please check your Internet connection and try again.");
			}
		}
	}

	public List<ConnectorDescriptor> getInstallableConnectors() {
		return installableConnectors;
	}

	private void modifySelection(final DiscoveryConnector connector, boolean selected) {
		connector.setSelected(selected);

		if (selected) {
			installableConnectors.add(connector);
		} else {
			installableConnectors.remove(connector);
		}
		setPageComplete(!installableConnectors.isEmpty());
	}

	public class ConnectorBorderPaintListener implements PaintListener {
		public void paintControl(PaintEvent e) {
			Composite composite = (Composite) e.widget;
			Rectangle bounds = composite.getBounds();
			GC gc = e.gc;
			gc.setLineStyle(SWT.LINE_DOT);
			gc.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
		}
	}

}

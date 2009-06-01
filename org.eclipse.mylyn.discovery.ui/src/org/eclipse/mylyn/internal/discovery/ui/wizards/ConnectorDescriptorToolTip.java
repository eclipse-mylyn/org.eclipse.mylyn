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

import java.net.URL;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.internal.discovery.core.model.AbstractDiscoverySource;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.Overview;
import org.eclipse.mylyn.internal.provisional.commons.ui.GradientToolTip;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author David Green
 */
class ConnectorDescriptorToolTip extends GradientToolTip {

	private static final String COLOR_BLACK = "black";

	private final DiscoveryConnector descriptor;

	private Color colorBlack;

	public ConnectorDescriptorToolTip(Control control, DiscoveryConnector descriptor) {
		super(control, ToolTip.RECREATE, true);
		this.descriptor = descriptor;
		setHideOnMouseDown(false); // required for links to work
	}

	@Override
	protected Composite createToolTipArea(Event event, final Composite parent) {
		if (colorBlack == null) {
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			if (!colorRegistry.hasValueFor(COLOR_BLACK)) {
				colorRegistry.put(COLOR_BLACK, new RGB(0, 0, 0));
			}
			colorBlack = colorRegistry.get(COLOR_BLACK);
		}

		GridLayoutFactory.fillDefaults().applyTo(parent);

		Composite container = new Composite(parent, SWT.NULL);
		container.setBackground(null);
		GridDataFactory.fillDefaults().grab(true, true).hint(/*347*/650, SWT.DEFAULT).applyTo(container);

		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).spacing(3, 0).applyTo(container);

		final Overview overview = descriptor.getOverview();
		if (overview != null) {
			int borderWidth = 1;

			String summary = overview.getSummary();
			Image image = null;
			if (overview.getScreenshot() != null) {
				image = computeImage(descriptor.getSource(), overview.getScreenshot());
				if (image != null) {
					final Image fimage = image;
					container.addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							fimage.dispose();
						}
					});
				}
			}
			if (summary != null) {
				Label summaryLabel = new Label(container, SWT.WRAP);
				GridDataFactory.fillDefaults().grab(true, false).hint(320, 240 + (borderWidth * 2)).span(
						image == null ? 2 : 1, 1).applyTo(summaryLabel);
				summaryLabel.setText(summary);
				summaryLabel.setBackground(null);
			}
			if (image != null) {
				final Composite imageContainer = new Composite(container, SWT.BORDER);
				GridLayoutFactory.fillDefaults().applyTo(imageContainer);

				GridDataFactory.fillDefaults().grab(false, false).align(SWT.CENTER, SWT.BEGINNING).hint(
						320 + (borderWidth * 2), 240 + (borderWidth * 2)).applyTo(imageContainer);

				Label imageLabel = new Label(imageContainer, SWT.NULL);
				GridDataFactory.fillDefaults().hint(320, 240).indent(borderWidth, borderWidth).applyTo(imageLabel);
				imageLabel.setImage(image);
				imageLabel.setBackground(null);
				imageLabel.setSize(320, 240);

				// creates a border
				imageContainer.setBackground(colorBlack);
			}
			if (overview.getUrl() != null && overview.getUrl().length() > 0) {
				Link link = new Link(container, SWT.NULL);
				GridDataFactory.fillDefaults().grab(false, false).span(2, 1).align(SWT.BEGINNING, SWT.CENTER).applyTo(
						link);
				link.setText(Messages.ConnectorDescriptorToolTip_detailsLink);
				link.setBackground(null);
				link.setToolTipText(NLS.bind(Messages.ConnectorDescriptorToolTip_detailsLink_tooltip, overview.getUrl()));
				link.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						WorkbenchUtil.openUrl(overview.getUrl(), IWorkbenchBrowserSupport.AS_EXTERNAL);
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});
			}
		}
		// hack: cause the tooltip to gain focus so that we can capture the escape key
		//       this must be done async since the tooltip is not yet visible.
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				if (!parent.isDisposed()) {
					parent.setFocus();
				}
			}
		});
		return container;
	}

	private Image computeImage(AbstractDiscoverySource discoverySource, String imagePath) {
		URL resource = discoverySource.getResource(imagePath);
		if (resource != null) {
			ImageDescriptor descriptor = ImageDescriptor.createFromURL(resource);
			Image image = descriptor.createImage();
			return image;
		}
		return null;
	}
}

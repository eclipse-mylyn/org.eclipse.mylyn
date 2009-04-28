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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.internal.discovery.core.model.AbstractDiscoverySource;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.Overview;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author David Green
 */
class ConnectorDescriptorToolTip extends ToolTip {

	private final DiscoveryConnector descriptor;

	private final Color background;

	public ConnectorDescriptorToolTip(Control control, DiscoveryConnector descriptor) {
		super(control, ToolTip.RECREATE, true);
		this.descriptor = descriptor;
		background = control.getBackground();
	}

	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {
		parent.setBackground(background);

		Composite container = new Composite(parent, SWT.NULL);
		container.setBackground(background);

		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).spacing(0, 3).applyTo(container);

		Overview overview = descriptor.getOverview();
		if (overview != null) {
			String summary = overview.getSummary();
			if (summary != null) {
				Label summaryLabel = new Label(container, SWT.WRAP);
				GridDataFactory.fillDefaults().grab(false, false).hint(100, SWT.DEFAULT).span(2, 1).applyTo(
						summaryLabel);
				summaryLabel.setText(summary);
				summaryLabel.setBackground(background);
			}
			if (overview.getScreenshot() != null) {
				final Image image = computeImage(descriptor.getSource(), overview.getScreenshot());
				if (image != null) {
					container.addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							image.dispose();
						}
					});

					// put the image in a scrolled composite.  This achieves two things:
					// - we get a border
					// - if the supplied image is too large, it doesn't blow out the layout
					// FIXME: do we want to scale images here?
					ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL
							| SWT.BORDER);
					scrolledComposite.setBackground(background);
					GridDataFactory.fillDefaults().grab(false, false).align(SWT.CENTER, SWT.CENTER).span(2, 1).hint(
							320, 240).applyTo(scrolledComposite);

					Label imageLabel = new Label(scrolledComposite, SWT.NULL);
					imageLabel.setBackground(background);
					imageLabel.setImage(image);

					scrolledComposite.setExpandHorizontal(true);
					scrolledComposite.setExpandVertical(true);
					scrolledComposite.setMinSize(320, 240);

					scrolledComposite.setContent(imageLabel);
				}
			}
		}

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

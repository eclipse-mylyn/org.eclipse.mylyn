/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.github.ui.AvatarStore.IAvatarCallback;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Avatar label displaying an image and label for a avatar url.
 */
public class AvatarLabel implements IAvatarCallback {

	/**
	 * AVATAR_SIZE
	 */
	public static final int AVATAR_SIZE = 42;

	private Composite displayArea;
	private Composite avatarImage;
	private AvatarStore store;
	private IRepositoryPerson person;
	private TaskAttribute attribute;

	/**
	 * Create avatar label
	 * 
	 * @param store
	 * @param person
	 * @param attribute
	 */
	public AvatarLabel(AvatarStore store, IRepositoryPerson person,
			TaskAttribute attribute) {
		this.store = store;
		this.person = person;
		this.attribute = attribute;
	}

	/**
	 * Set visible
	 * 
	 * @param visible
	 * @return this label
	 */
	public AvatarLabel setVisible(boolean visible) {
		if (!displayArea.isDisposed())
			displayArea.setVisible(visible);
		return this;
	}

	/**
	 * Layout label
	 * 
	 * @return this label
	 */
	public AvatarLabel layout() {
		if (!displayArea.isDisposed())
			displayArea.getParent().getParent().layout(true, true);
		return this;
	}

	/**
	 * Get main composite
	 * 
	 * @return composite
	 */
	public Composite getControl() {
		return this.displayArea;
	}

	/**
	 * Create label
	 * 
	 * @param parent
	 * @param toolkit
	 * @return this label
	 */
	public AvatarLabel create(Composite parent, FormToolkit toolkit) {
		displayArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(2, 2).spacing(1, 1)
				.applyTo(displayArea);
		GridDataFactory.fillDefaults().span(1, 2).grab(false, false)
				.applyTo(displayArea);
		toolkit.adapt(displayArea, false, false);

		avatarImage = new Composite(displayArea, SWT.NONE);
		if (person != null)
			avatarImage.setToolTipText(person.getPersonId());

		GridDataFactory.swtDefaults().grab(false, false)
				.align(SWT.CENTER, SWT.CENTER).hint(AVATAR_SIZE, AVATAR_SIZE)
				.applyTo(avatarImage);

		Label label = toolkit.createLabel(displayArea, attribute.getMetaData()
				.getLabel());
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setToolTipText(avatarImage.getToolTipText());

		ImageData data = store.getAvatar(attribute.getValue());
		if (data != null)
			setImage(store.getScaledImage(AVATAR_SIZE, data));
		else {
			store.loadAvatar(attribute.getValue(), this);
			setVisible(false);
		}

		return this;
	}

	private AvatarLabel setImage(final Image image) {
		if (!avatarImage.isDisposed()) {
			avatarImage.setBackgroundImage(image);
			avatarImage.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					image.dispose();
				}
			});
		}
		return this;
	}

	/**
	 * @see org.eclipse.mylyn.internal.github.ui.AvatarStore.IAvatarCallback#loaded(org.eclipse.swt.graphics.ImageData,
	 *      org.eclipse.mylyn.internal.github.ui.AvatarStore)
	 */
	public void loaded(final ImageData data, final AvatarStore store) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			public void run() {
				Image image = store.getScaledImage(AVATAR_SIZE, data);
				setImage(image).setVisible(true).layout();
			}
		});
	}

}

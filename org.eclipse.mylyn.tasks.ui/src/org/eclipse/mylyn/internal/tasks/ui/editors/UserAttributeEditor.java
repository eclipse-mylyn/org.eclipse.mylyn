/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.mylyn.commons.identity.Account;
import org.eclipse.mylyn.commons.identity.IIdentity;
import org.eclipse.mylyn.commons.identity.IProfileImage;
import org.eclipse.mylyn.commons.identity.spi.ProfileImage;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class UserAttributeEditor extends AbstractAttributeEditor {

	private static final int IMAGE_SIZE = 48;

	private Label label;

	private IIdentity identity;

	private final PropertyChangeListener imageListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals("image")) { //$NON-NLS-1$
				final ProfileImage profileImage = (ProfileImage) event.getNewValue();
				Display.getDefault().asyncExec(new Runnable() {

					public void run() {
						if (!label.isDisposed()) {
							updateImage(profileImage);
						}
					}
				});
			}
		}
	};

	private Image image;

	public UserAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE));
	}

	protected Image updateImage(IProfileImage profileImage) {
		if (image != null) {
			image.dispose();
		}
		ImageData data;
		if (profileImage != null) {
			data = new ImageData(new ByteArrayInputStream(profileImage.getData()));
			if (data.width != IMAGE_SIZE || data.height != IMAGE_SIZE) {
				data = data.scaledTo(IMAGE_SIZE, IMAGE_SIZE);
			}
		} else {
			data = CommonImages.PERSON_LARGE.getImageData();
		}
		Image image = new Image(label.getDisplay(), data);
		label.setImage(image);
		return image;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		label = new Label(parent, SWT.NONE);
		label.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		label.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (image != null) {
					image.dispose();
				}
				if (identity != null) {
					identity.removePropertyChangeListener(imageListener);
				}
			}
		});
		refresh();
		toolkit.adapt(label, false, false);
		setControl(label);
	}

	public Image getValue() {
		return image;
	}

	@Override
	protected boolean shouldAutoRefresh() {
		// do not auto refresh to avoid picking up partially entered accounts
		return false;
	}

	@Override
	public void refresh() {
		if (label.isDisposed()) {
			return;
		}
		if (identity != null) {
			identity.removePropertyChangeListener(imageListener);
		}
		if (TaskAttribute.TYPE_PERSON.equals(getTaskAttribute().getMetaData().getType())) {
			IRepositoryPerson person = getTaskAttribute().getTaskData()
					.getAttributeMapper()
					.getRepositoryPerson(getTaskAttribute());
			label.setToolTipText(getLabel() + " " + person.toString()); //$NON-NLS-1$
		} else {
			label.setToolTipText(getDescription());
		}
		Account account = TasksUiInternal.getAccount(getTaskAttribute());
		identity = TasksUiPlugin.getDefault().getIdentityModel().getIdentity(account);
		identity.addPropertyChangeListener(imageListener);
		Future<IProfileImage> result = identity.requestImage(IMAGE_SIZE, IMAGE_SIZE);
		if (result.isDone()) {
			try {
				updateImage(result.get(0, TimeUnit.SECONDS));
			} catch (Exception e) {
				// the event listener will eventually update the image
				updateImage(null);
			}
		} else {
			updateImage(null);
		}
	}

}

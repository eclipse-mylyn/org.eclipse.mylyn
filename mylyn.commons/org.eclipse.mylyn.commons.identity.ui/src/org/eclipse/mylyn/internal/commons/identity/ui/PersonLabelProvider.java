/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.ui;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.commons.identity.core.Account;
import org.eclipse.mylyn.commons.identity.core.IIdentity;
import org.eclipse.mylyn.commons.identity.core.IProfile;
import org.eclipse.mylyn.commons.identity.core.IProfileImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class PersonLabelProvider extends LabelProvider {

	private static final int IMAGE_SIZE = 16;

	private final ImageRegistry registry = new ImageRegistry();

	@Override
	public void dispose() {
		registry.dispose();
		super.dispose();
	}

	@Override
	public Image getImage(Object object) {
		if (object instanceof PeopleCategory) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		} else if (object instanceof IIdentity identity) {
			Image image = registry.get(identity.getId().toString());
			if (image == null) {
				Future<IProfileImage> result = identity.requestImage(IMAGE_SIZE, IMAGE_SIZE);
				if (result.isDone()) {
					try {
						IProfileImage profileImage = result.get(0, TimeUnit.SECONDS);
						if (profileImage != null) {
							ImageData data = new ImageData(new ByteArrayInputStream(profileImage.getData()));
							if (data.width != IMAGE_SIZE || data.height != IMAGE_SIZE) {
								data = data.scaledTo(IMAGE_SIZE, IMAGE_SIZE);
							}
							registry.put(identity.getId().toString(), ImageDescriptor.createFromImageData(data));
							image = registry.get(identity.getId().toString());
						}
					} catch (Exception e) {
						// ignore
					}
				}
			}
			return image;
		}
		return null;
	}

	@Override
	public String getText(Object object) {
		if (object instanceof IIdentity identity) {
			Future<IProfile> result = identity.requestProfile();
			if (result.isDone()) {
				try {
					IProfile profile = result.get(0, TimeUnit.SECONDS);
					if (profile.getName() != null) {
						return profile.getName();
					} else if (profile.getEmail() != null) {
						return profile.getEmail();
					}
				} catch (Exception e) {
					// ignore
				}
			}
			//return identity.getAccounts()[0].getId();
			return identity.getId().toString();
		} else if (object instanceof Account account) {
			if (account.getName() != null) {
				return account.getName() + " <" + account.getId() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				return account.getId();
			}
		}
		return null;
	}

}

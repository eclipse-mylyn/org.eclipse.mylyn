/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.activity.ui;

import org.eclipse.mylyn.commons.activity.ui.UserActivityListener;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author Steffen Pingel
 */
public interface IUserActivityManager {

	public void addAttentionListener(UserActivityListener listener);

	public void removeAttentionListener(UserActivityListener listener);

}

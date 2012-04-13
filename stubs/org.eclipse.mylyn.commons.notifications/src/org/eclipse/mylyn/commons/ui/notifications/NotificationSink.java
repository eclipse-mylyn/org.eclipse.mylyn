/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.notifications;

/**
 * @author Steffen Pingel
 * @deprecated use classes in the <code>org.eclipse.mylyn.commons.notifications.core</code> bundle instead
 */
@Deprecated
public abstract class NotificationSink {

	public abstract void notify(NotificationSinkEvent event);

}

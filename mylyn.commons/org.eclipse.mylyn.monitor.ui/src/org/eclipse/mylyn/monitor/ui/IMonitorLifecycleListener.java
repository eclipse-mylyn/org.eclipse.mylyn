/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.monitor.ui;

/**
 * Notified of monitor life-cycle changes.
 * 
 * @author Brian de Alwis
 * @since 3.0
 */
public interface IMonitorLifecycleListener {

	void startMonitoring();

	void stopMonitoring();

}

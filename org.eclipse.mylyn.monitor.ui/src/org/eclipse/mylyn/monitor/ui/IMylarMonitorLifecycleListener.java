/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.ui;

/**
 * Notified of monitor lifecycle changes.
 * 
 * @author Brian de Alwis
 * @since 2.0
 */
public interface IMylarMonitorLifecycleListener {

	void startMonitoring();

	void stopMonitoring();

}

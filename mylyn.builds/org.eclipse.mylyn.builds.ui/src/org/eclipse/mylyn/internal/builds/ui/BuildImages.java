/*******************************************************************************
 * Copyright (c) 2010, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class BuildImages {

	private static final URL baseURL = BuildsUiPlugin.getDefault().getBundle().getEntry("/icons/"); //$NON-NLS-1$

	public static final String T_OBJ = "obj16"; //$NON-NLS-1$

	public static final String T_OVR = "ovr16"; //$NON-NLS-1$

	public static final String T_ETOOL = "etool16"; //$NON-NLS-1$

	public static final String T_DTOOL = "dtool16"; //$NON-NLS-1$

	public static final String T_VIEW = "eview16"; //$NON-NLS-1$

	public static final ImageDescriptor VIEW_BUILDS = create(T_VIEW, "build-view.png"); //$NON-NLS-1$

	public static final ImageDescriptor VIEW_HISTORY = create(T_VIEW, "history_view.gif"); //$NON-NLS-1$

	public static final ImageDescriptor STATUS_PASSED = create(T_OBJ, "passed-status.png"); //$NON-NLS-1$

	public static final ImageDescriptor STATUS_FAILED = create(T_OBJ, "failed-status.gif"); //$NON-NLS-1$

	public static final ImageDescriptor STATUS_UNSTABLE = create(T_OBJ, "unstable-status.png"); //$NON-NLS-1$

	public static final ImageDescriptor STATUS_DISABLED = create(T_OBJ, "disabled-status.png"); //$NON-NLS-1$

	public static final ImageDescriptor TEST = create(T_OBJ, "test.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TEST_PASSED = create(T_OBJ, "testok.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TEST_FAILED = create(T_OBJ, "testfail.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TEST_ERROR = create(T_OBJ, "testerr.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TEST_IGNORED = create(T_OBJ, "testignored.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TEST_SUITE = create(T_OBJ, "tsuite.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TEST_SUITE_PASSED = create(T_OBJ, "tsuiteok.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TEST_SUITE_FAILED = create(T_OBJ, "tsuitefail.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TEST_SUITE_ERROR = create(T_OBJ, "tsuiteerror.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TEST_SUITE_IGNORED = create(T_OBJ, "tsuiteignored.gif"); //$NON-NLS-1$

	public static final ImageDescriptor SERVER = create(T_OBJ, "server.gif"); //$NON-NLS-1$

	public static final ImageDescriptor SERVER_DISABLED = ImageDescriptor.createWithFlags(SERVER, SWT.IMAGE_DISABLE);

	public static final ImageDescriptor HEALTH_00 = create(T_OBJ, "weather_lightning.png"); //$NON-NLS-1$

	public static final ImageDescriptor HEALTH_20 = create(T_OBJ, "weather_rain.png"); //$NON-NLS-1$

	public static final ImageDescriptor HEALTH_40 = create(T_OBJ, "weather_clouds.png"); //$NON-NLS-1$

	public static final ImageDescriptor HEALTH_60 = create(T_OBJ, "weather_cloudy.png"); //$NON-NLS-1$

	public static final ImageDescriptor HEALTH_80 = create(T_OBJ, "weather_sun.png"); //$NON-NLS-1$

	public static final ImageDescriptor CHANGE_SET = create(T_OBJ, "changeset.gif"); //$NON-NLS-1$

	public static final ImageDescriptor RUN = create(T_ETOOL, "run_exc.gif"); //$NON-NLS-1$

	public static final ImageDescriptor RUN_DISABLED = create(T_DTOOL, "run_exc.gif"); //$NON-NLS-1$

	public static final ImageDescriptor ABORT = PlatformUI.getWorkbench()
			.getSharedImages()
			.getImageDescriptor(org.eclipse.ui.ISharedImages.IMG_ELCL_STOP);

	public static final ImageDescriptor ABORT_DISABLED = PlatformUI.getWorkbench()
			.getSharedImages()
			.getImageDescriptor(org.eclipse.ui.ISharedImages.IMG_ELCL_STOP_DISABLED);

	public static final ImageDescriptor CONSOLE = create(T_ETOOL, "console.gif"); //$NON-NLS-1$

	public static final ImageDescriptor FILTER_SUCCEEDING = create(T_ETOOL, "filter_succeeding.png"); //$NON-NLS-1$

	public static final ImageDescriptor JUNIT = create(T_VIEW, "junit.gif"); //$NON-NLS-1$

	public static final ImageDescriptor DECORATION_RUNNING = create(T_OVR, "run_co.gif"); //$NON-NLS-1$;

	public static final ImageDescriptor FILTER_FAILURES = create(T_OBJ, "failures.gif"); //$NON-NLS-1$

	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (baseURL == null) {
			throw new MalformedURLException();
		}

		StringBuilder buffer = new StringBuilder(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}

}

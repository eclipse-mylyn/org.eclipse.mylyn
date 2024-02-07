/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.reviews.core.spi.remote.ReviewsDataLocator;

@SuppressWarnings("nls")
public final class TestDataLocator extends ReviewsDataLocator {
	@Override
	public IPath getSystemDataPath() {
		return new Path(FileUtils.getTempDirectory().getAbsolutePath()).append("gerrit_tests");
	}
}
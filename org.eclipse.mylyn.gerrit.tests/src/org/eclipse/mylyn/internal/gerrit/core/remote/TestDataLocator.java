/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.reviews.core.spi.remote.ReviewsDataLocator;

public final class TestDataLocator extends ReviewsDataLocator {
	@Override
	public IPath getSystemDataPath() {
		return new Path(FileUtils.getTempDirectory().getAbsolutePath()).append("gerrit_tests");
	}
}
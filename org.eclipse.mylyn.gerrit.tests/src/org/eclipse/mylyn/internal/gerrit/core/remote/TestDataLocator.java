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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractDataLocator;

final class TestDataLocator extends AbstractDataLocator {
	@Override
	public IPath getSystemPath() {
		return new Path(FileUtils.getTempDirectory().getAbsolutePath() + File.separator
				+ "org.eclipse.mylyn.gerrit.tests");
	}
}
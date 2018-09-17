/*******************************************************************************
 * Copyright (c) 2018 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Predicate;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Encapsulates file-related operations of TaskDataManager
 */
public class TaskDataFileManager {

	private static final String ENCODING_UTF_8 = "UTF-8"; //$NON-NLS-1$

	private static final String EXTENSION = ".zip"; //$NON-NLS-1$

	private static final String FOLDER_TASKS = "tasks"; //$NON-NLS-1$

	private static final String FOLDER_DATA = "offline"; //$NON-NLS-1$

	private static final String FOLDER_TASKS_1_0 = "offline"; //$NON-NLS-1$

	private static final int FILENAME_MAX_LEN = 255 - EXTENSION.length(); // 255 is an OS limit for file name

	private String dataPath;

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public File getFile(ITask task, String kind) {
		return getFile(task.getRepositoryUrl(), task, kind);
	}

	public File getFile(String repositoryUrl, ITask task, String kind) {
		File path = getDirectory(repositoryUrl, task);
		String fileName = getFileName(task, path);
		return new File(path, fileName + EXTENSION);
	}

	private File getDirectory(String repositoryUrl, ITask task) {
		Assert.isNotNull(dataPath);
		String repositoryPath = task.getConnectorKind() + "-" + CoreUtil.asFileName(repositoryUrl); //$NON-NLS-1$
		return new File(dataPath + File.separator + FOLDER_TASKS + File.separator + repositoryPath + File.separator
				+ FOLDER_DATA);
	}

	private String getFileName(ITask task, File path) {
		return getFileName(task, filename -> new File(path, filename + EXTENSION).exists());
	}

	// the method is made protected for unit testing
	protected String getFileName(ITask task, Predicate<String> fileExists) {
		String encodedFileName = CoreUtil.asFileName(task.getTaskId());

		// for backwards-compatibility with versions that always encoded file names,
		// we will use an encoded name if the file with an encoded name already exists
		if (fileExists.test(encodedFileName)) {
			return encodedFileName;
		}

		// if file with encoded name does not exist, we will only encode file name if it is required
		String fileName;
		if (requiresEncoding(task.getTaskId())) {
			fileName = encodedFileName;
		} else {
			fileName = task.getTaskId();
		}

		// trim the file name if it is too long
		return trimFilenameIfRequired(fileName);
	}

	/**
	 * Checks if input contains characters other than ones returned by {@link CoreUtil.asFileName}
	 *
	 * @param fileName
	 * @return true or false
	 */
	private boolean requiresEncoding(String fileName) {
		return !fileName.matches("^[a-zA-Z0-9%\\.]+$"); //$NON-NLS-1$
	}

	private String trimFilenameIfRequired(String filename) {
		if (filename.length() > FILENAME_MAX_LEN) {
			// replace a long file name with a shorter name + the hash
			String hashCode = getHashCode(filename);
			return filename.substring(0, FILENAME_MAX_LEN - hashCode.length() - 1) + "." + hashCode; //$NON-NLS-1$
		}

		return filename;
	}

	private String getHashCode(String text) {
		return Integer.toUnsignedString(text.hashCode());
	}

	public File getFile10(ITask task, String kind) {
		try {
			String pathName = URLEncoder.encode(task.getRepositoryUrl(), ENCODING_UTF_8);
			String fileName = task.getTaskId() + EXTENSION;
			File path = new File(dataPath + File.separator + FOLDER_TASKS_1_0, pathName);
			return new File(path, fileName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}
}

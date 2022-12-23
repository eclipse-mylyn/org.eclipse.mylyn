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

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

public class FileInfo {

	private char status;

	private int linesInserted;

	private int linesDeleted;

	private int sizeDelta;

	private int size;

	private boolean binary;

	public char getStatus() {
		return status == '\0' ? 'M' : status;
	}

	public int getLinesInserted() {
		return linesInserted;
	}

	public int getLinesDeleted() {
		return linesDeleted;
	}

	public int getSizeDelta() {
		return sizeDelta;
	}

	public int getSize() {
		return size;
	}

	public boolean isBinary() {
		return binary;
	}

}

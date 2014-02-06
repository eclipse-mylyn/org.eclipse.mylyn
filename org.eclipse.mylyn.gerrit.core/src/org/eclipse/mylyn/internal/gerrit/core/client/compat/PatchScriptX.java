/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import com.google.gerrit.common.data.PatchScript;

/**
 * Provides support for binary content.
 */
public class PatchScriptX extends PatchScript {

	private byte[] binaryA;

	private byte[] binaryB;

	public boolean isBinary() {
		for (String header : getPatchHeader()) {
			if (header.contains("Binary files differ")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	public byte[] getBinaryA() {
		return binaryA;
	}

	public void setBinaryA(byte[] binaryA) {
		this.binaryA = binaryA;
	}

	public byte[] getBinaryB() {
		return binaryB;
	}

	public void setBinaryB(byte[] binaryB) {
		this.binaryB = binaryB;
	}

}

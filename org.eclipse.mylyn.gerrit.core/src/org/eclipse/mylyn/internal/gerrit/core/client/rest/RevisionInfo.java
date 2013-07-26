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
package org.eclipse.mylyn.internal.gerrit.core.client.rest;

/**
 * Data model object for <a
 * href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#revision-info">RevisionInfo</a>.
 */
public class RevisionInfo {

	private boolean draft;

	private int _number;

	public boolean isDraft() {
		return draft;
	}

	public int getNumber() {
		return _number;
	}
}

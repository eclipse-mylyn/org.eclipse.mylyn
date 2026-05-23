/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.builds.ui.editor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Steffen Pingel
 */
public class TimestampToStringConverter implements IConverter<Long, String> {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z"); //$NON-NLS-1$

	@Override
	public String convert(Long source) {
		if (source != null) {
			return Instant.ofEpochMilli(source).atZone(ZoneId.systemDefault()).format(formatter);
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public Object getFromType() {
		return Long.class;
	}

	@Override
	public Object getToType() {
		return String.class;
	}

}

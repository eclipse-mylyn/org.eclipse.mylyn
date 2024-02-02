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
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.internal.databinding.conversion.DateConversionSupport;

/**
 * @author Steffen Pingel
 */
public class TimestampToStringConverter extends DateConversionSupport implements IConverter {

	@Override
	public Object convert(Object source) {
		if (source != null) {
			return format(new Date((Long) source));
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

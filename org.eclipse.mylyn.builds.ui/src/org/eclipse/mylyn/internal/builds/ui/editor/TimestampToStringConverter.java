/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

	public Object convert(Object source) {
		if (source != null) {
			return format(new Date((Long) source));
		}
		return ""; //$NON-NLS-1$
	}

	public Object getFromType() {
		return Long.class;
	}

	public Object getToType() {
		return String.class;
	}

}

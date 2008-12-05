/*******************************************************************************
 * Copyright (c) 2004, 2008 Willian Mitsuda and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Format attachment size values originally in bytes to nice messages.
 * <p>
 * This formatter tries to use the most applicable measure unit based on size magnitude, i.e.:
 * <p>
 * <ul>
 * <li>< 1 KB - byte based: 1 byte, 100 bytes, etc. <li>>= 1 KB and < 1 MB - KB based: 2.00 KB, 100.76 KB <li>>= 1 MB
 * and < 1 GB - MB based: 1.00 MB, 33.33 MB <li>>= 1 GB - GB based: 2.00 GB
 * </ul>
 * <p>
 * This formatter assumes 1 KB == 1024 bytes, <strong>NOT</strong> 1000 bytes.
 * <p>
 * This formatter always uses 2 decimal places.
 * <p>
 * The size is provided as a String, because it will probably come from a attachment attribute. If the value cannot be
 * decoded, for any reason, it returns {@link #UNKNOWN_SIZE}
 * 
 * @author Willian Mitsuda
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class AttachmentSizeFormatter {

	/**
	 * Default value returned by this formatter when the size is unparseable, contain errors, etc.
	 */
	public static final String UNKNOWN_SIZE = "-"; //$NON-NLS-1$

	public final static AttachmentSizeFormatter getInstance() {
		return new AttachmentSizeFormatter();
	}

	private final DecimalFormat decimalFormat;

	public AttachmentSizeFormatter() {
		this(Locale.getDefault());
	}

	public AttachmentSizeFormatter(Locale locale) {
		this.decimalFormat = (DecimalFormat) NumberFormat.getInstance(locale);
	}

	public String format(String sizeInBytes) {
		if (sizeInBytes == null) {
			return UNKNOWN_SIZE;
		}
		try {
			return format(Long.parseLong(sizeInBytes));
		} catch (NumberFormatException e) {
			return UNKNOWN_SIZE;
		}
	}

	public String format(long size) {
		if (size < 0) {
			return UNKNOWN_SIZE;
		}
		if (size < 1024) {
			// format as byte
			if (size == 1) {
				return Messages.AttachmentSizeFormatter_1_byte;
			}
			DecimalFormat fmt = new DecimalFormat(Messages.AttachmentSizeFormatter_0_bytes);
			return fmt.format(size);
		} else if (size >= 1024 && size <= 1048575) {
			// format as KB
			double formattedValue = size / 1024.0;
			decimalFormat.applyPattern(Messages.AttachmentSizeFormatter_0_KB);
			return decimalFormat.format(formattedValue);
		} else if (size >= 1048576 && size <= 1073741823) {
			// format as MB
			double formattedValue = size / 1048576.0;
			decimalFormat.applyPattern(Messages.AttachmentSizeFormatter_0_MB);
			return decimalFormat.format(formattedValue);
		}

		// format as GB
		double formattedValue = size / 1073741824.0;
		decimalFormat.applyPattern(Messages.AttachmentSizeFormatter_0_GB);
		return decimalFormat.format(formattedValue);
	}

}

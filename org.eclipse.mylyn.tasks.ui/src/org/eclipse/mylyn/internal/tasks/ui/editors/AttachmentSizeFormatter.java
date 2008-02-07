/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.text.DecimalFormat;

/**
 * Format attachment size values originally in bytes to nice messages
 * <p>
 * This formatter tries to use the most applicable measure unit based on size magnitude, i.e.:
 * <p>
 * <ul>
 * <li>< 1 KB - byte based: 1 byte, 100 bytes, etc.
 * <li>Between 1 and 999 KB - KB based: 0.50 KB, 2.00 KB, 100.76 KB
 * <li>Between 1 MB and 999 MB - MB based: 1.00 MB, 33.33 MB
 * <li>>= 1 GB - GB based: 2.00 GB
 * </ul>
 * <p>
 * This formatter assumes 1 KB == 1000 bytes, <strong>NOT</strong> 1024 bytes.
 * <p>
 * This formatter always uses 2 decimal places.
 * <p>
 * The size is provided as a String, because it will probably come from a attachment attribute. If the value cannot be
 * decoded, for any reason, it returns {@link #UNKNOWN_SIZE}
 * 
 * @author Willian Mitsuda
 */
public class AttachmentSizeFormatter {

	/**
	 * Default value returned by this formatter when the size is unparseable, contain errors, etc.
	 */
	public static final String UNKNOWN_SIZE = "-";

	public static String format(String sizeInBytes) {
		// Ensures it can be converted to an int
		if (sizeInBytes == null) {
			return UNKNOWN_SIZE;
		}
		int size = 0;
		try {
			size = Integer.parseInt(sizeInBytes);
		} catch (NumberFormatException e) {
			return UNKNOWN_SIZE;
		}

		// Discover the magnitude
		if (size < 0) {
			return UNKNOWN_SIZE;
		}
		if (size < 1000) {
			// Format as byte
			if (size == 1) {
				return "1 byte";
			}
			DecimalFormat fmt = new DecimalFormat("0 bytes");
			return fmt.format(size);
		} else if (size >= 1000 && size <= 999994) {
			// Format as KB
			double formattedValue = size / 1000.0;
			DecimalFormat fmt = new DecimalFormat("0.00 kB");
			return fmt.format(formattedValue);
		} else if (size >= 999995 && size <= 999994444) {
			// Format as MB
			double formattedValue = size / 1000000.0;
			DecimalFormat fmt = new DecimalFormat("0.00 MB");
			return fmt.format(formattedValue);
		}

		// Format as GB
		double formattedValue = size / 1000000000.0;
		DecimalFormat fmt = new DecimalFormat("0.00 GB");
		return fmt.format(formattedValue);
	}

}
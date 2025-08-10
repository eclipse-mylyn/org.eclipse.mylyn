/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @since 3.22
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.commons.ui.messages"; //$NON-NLS-1$

	public static String ConfigurableColumnTableViewerSupport_Configure_Columns;

	public static String TableColumnDescriptorDialog_Alignment;

	public static String TableColumnDescriptorDialog_autosize;

	public static String TableColumnDescriptorDialog_Center;

	public static String TableColumnDescriptorDialog_Change_Column_Settings;

	public static String TableColumnDescriptorDialog_DefaultSortColumn;

	public static String TableColumnDescriptorDialog_Down;

	public static String TableColumnDescriptorDialog_False;

	public static String TableColumnDescriptorDialog_Left;

	public static String TableColumnDescriptorDialog_Name;

	public static String TableColumnDescriptorDialog_please_enter_value_for_Width;

	public static String TableColumnDescriptorDialog_Right;

	public static String TableColumnDescriptorDialog_Sort_Direction;

	public static String TableColumnDescriptorDialog_True;

	public static String TableColumnDescriptorDialog_Up;

	public static String TableColumnDescriptorDialog_Width;

	public static String TableColumnDescriptorDialog_Width_is_not_a_valid_number;

	public static String TableColumnDescriptorDialog_Width_must_be_greater_or_equal_0;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 	This class implements the implementation of the review table view information.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the table view information
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.model;

import java.util.ArrayList;

import org.eclipse.swt.SWT;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 * 
 */
// ------------------------------------------------------------------------
// Constants
// ------------------------------------------------------------------------
// Definition of the review table list {name, width of the column, Resizeable,
// Moveable}
public enum ReviewTableDefinition {
	// 			Name 			Width 	Resize Moveable
	   STARRED(	"", 			20, 	false,	true, SWT.LEFT), 
	   ID(		"ID", 			80, 	false,	true, SWT.LEFT),
	   SUBJECT(	"Subject",	 	200, 	true,	true, SWT.LEFT),
	   OWNER(	"Owner", 		140, 	true, 	true, SWT.LEFT),
	   PROJECT(	"Project", 		200, 	true, 	true, SWT.LEFT),
	   BRANCH(	"Branch", 		100, 	true, 	true, SWT.LEFT),
	   UPDATED(	"Updated", 		100, 	true, 	true, SWT.RIGHT),
	   CR(		"CR", 			28, 	false, 	true, SWT.LEFT),
//	   IC(		"IC", 			28, 	false, 	true, SWT.LEFT),
	   VERIFY(	"V", 			28,		false, 	true, SWT.LEFT);
	   
	private final String  fHeader;
	private final int     fwidth;
	private final boolean fResize;
	private final boolean fMoveable;
	private final int     fAlignment;

	private ReviewTableDefinition(String aName, int aWidth, boolean aResize, boolean aMove, int align) {
		fHeader = aName;
		fwidth = aWidth;
		fResize = aResize;
		fMoveable = aMove;
		fAlignment = align;
	}

	public String getName() {
		return fHeader;
	}

	public int getWidth() {
		return fwidth;
	}

	public boolean getResize() {
		return fResize;
	}

	public boolean getMoveable() {
		return fMoveable;
	}

	public int getAlignment() {
		return fAlignment;
	}

	public static String[] getColumnName() {
		ArrayList<String> listName = new ArrayList<String>();
		for (ReviewTableDefinition st : ReviewTableDefinition.values()) {
			listName.add(st.getName());
		}
		return listName.toArray(new String[] {});
	}

	public static int getMinimumWidth() {
		int width = 0;
		for (int index = 0; index < ReviewTableDefinition.values().length; index++) {
			width += ReviewTableDefinition.values()[index].getWidth();
		}
		return width;
	}

	// public static int getColumnNumber (String st) {
	// GerritPlugin.Ftracer.traceInfo("getColumnNumber(): " +
	// (ReviewTableDefinition.valueOf(st).ordinal() + 1));
	// //The ordinal starts at zero, so add 1
	// return ReviewTableDefinition.valueOf(st).ordinal();
	// }

}

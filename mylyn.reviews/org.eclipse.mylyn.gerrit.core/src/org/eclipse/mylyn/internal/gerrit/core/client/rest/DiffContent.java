/*******************************************************************************
 * Copyright (c) 2019 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.ArrayList;

public class DiffContent {

	private ArrayList<String> a; // text that is deleted in B

	private ArrayList<String> b; // text that is inserted in B

	private ArrayList<String> ab; // text in the file on both sides (unchanged).

	private DiffIntraLineInfo edit_A;

	private DiffIntraLineInfo edit_B;

	private boolean due_to_rebase;

	public DiffIntraLineInfo getEdit_A() {
		return edit_A;
	}

	public void setEdit_A(DiffIntraLineInfo edit_A) {
		this.edit_A = edit_A;
	}

	public DiffIntraLineInfo getEdit_B() {
		return edit_B;
	}

	public void setEdit_B(DiffIntraLineInfo edit_B) {
		this.edit_B = edit_B;
	}

	public boolean isDue_to_rebase() {
		return due_to_rebase;
	}

	public void setDue_to_rebase(boolean due_to_rebase) {
		this.due_to_rebase = due_to_rebase;
	}

	public ArrayList<String> getA() {
		return a;
	}

	public void setA(ArrayList<String> a) {
		this.a = a;
	}

	public ArrayList<String> getB() {
		return b;
	}

	public void setB(ArrayList<String> b) {
		this.b = b;
	}

	public ArrayList<String> getAb() {
		return ab;
	}

	public void setAb(ArrayList<String> ab) {
		this.ab = ab;
	}
}

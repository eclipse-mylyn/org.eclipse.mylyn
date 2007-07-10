/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Aug 6, 2004
 */
package org.eclipse.mylyn.internal.java.ui.editor;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 */
public class FocusedCompilationUnitEditor extends CompilationUnitEditor {

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
	}

	@Override
	public void createPartControl(Composite parent) {
		JavaTextTools textTools = JavaPlugin.getDefault().getJavaTextTools();
		setSourceViewerConfiguration(new FocusedJavaSourceViewerConfiguration(textTools.getColorManager(),
				getPreferenceStore(), this, IJavaPartitions.JAVA_PARTITIONING));
		super.createPartControl(parent);
	}
}

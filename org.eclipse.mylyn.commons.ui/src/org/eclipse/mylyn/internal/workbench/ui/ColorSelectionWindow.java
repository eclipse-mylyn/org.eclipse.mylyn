/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.workbench.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Popup window for color selection
 * 
 * @author Willian Mitsuda
 */
public class ColorSelectionWindow extends Window {

	private ColorCanvas[] colors;

	public ColorSelectionWindow(Shell shell) {
		super(shell);
		setShellStyle(SWT.BORDER);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.addShellListener(new ShellAdapter() {

			@Override
			public void shellDeactivated(ShellEvent e) {
				close();
			}

		});
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite colorComposite = new Composite(parent, SWT.NONE);
		colorComposite.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		colorComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		colorComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(8).spacing(1, 1).margins(1, 1).equalWidth(
				true).create());

		// EGA classic palette
		colors = new ColorCanvas[16];
		colors[0] = createColorCanvas(colorComposite, new RGB(0, 0, 0));
		colors[1] = createColorCanvas(colorComposite, new RGB(0, 0, 170));
		colors[2] = createColorCanvas(colorComposite, new RGB(0, 170, 0));
		colors[3] = createColorCanvas(colorComposite, new RGB(0, 170, 170));
		colors[4] = createColorCanvas(colorComposite, new RGB(170, 0, 0));
		colors[5] = createColorCanvas(colorComposite, new RGB(170, 0, 170));
		colors[6] = createColorCanvas(colorComposite, new RGB(170, 85, 0));
		colors[7] = createColorCanvas(colorComposite, new RGB(170, 170, 170));
		colors[8] = createColorCanvas(colorComposite, new RGB(85, 85, 85));
		colors[9] = createColorCanvas(colorComposite, new RGB(85, 85, 255));
		colors[10] = createColorCanvas(colorComposite, new RGB(85, 255, 85));
		colors[11] = createColorCanvas(colorComposite, new RGB(85, 255, 255));
		colors[12] = createColorCanvas(colorComposite, new RGB(255, 85, 85));
		colors[13] = createColorCanvas(colorComposite, new RGB(255, 85, 255));
		colors[14] = createColorCanvas(colorComposite, new RGB(255, 255, 85));
		colors[15] = createColorCanvas(colorComposite, new RGB(255, 255, 255));

		Button closeButton = new Button(parent, SWT.PUSH);
		closeButton.setText("&Close");
		closeButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		closeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}

		});
		return parent;
	}

	private ColorCanvas createColorCanvas(Composite parent, RGB rgb) {
		final ColorCanvas canvas = new ColorCanvas(parent, SWT.NONE, rgb);
		canvas.setLayoutData(GridDataFactory.fillDefaults().hint(16, 16).create());
		canvas.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				selectedRGB = canvas.getRGB();
				close();
			}

		});
		return canvas;
	}

	private RGB selectedRGB;

	public RGB getSelectedRGB() {
		return selectedRGB;
	}

}

/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.mylar.monitor.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Dialog that displays the contents of a file to the user
 * 
 * @author Shawn Minto
 *
 */
public class FileDisplayDialog extends MessageDialog{

    private static String contents = "";
    
    public FileDisplayDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, String contents) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
                dialogImageType, dialogButtonLabels, defaultIndex);
    }
    
    public static int openShowFile(Shell parent, String title, String message, File file) throws FileNotFoundException {
        contents = getContents(file);
        FileDisplayDialog dialog = new FileDisplayDialog(parent, title, null, // accept
                // the
                // default
                // window
                // icon
                message, NONE,
                new String[] { IDialogConstants.OK_LABEL}, 0, contents);
        // ok is the default
        return dialog.open();            
    }

    @Override
    public Control createCustomArea(Composite parent){
    GridLayout layout = new GridLayout();
        parent.setLayout(layout);
        layout.numColumns = 1;

        Text t = new Text(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 1;
        gd.verticalSpan = 50;
        t.setLayoutData(gd);
        t.setEditable(false);
        t.setText(contents);
        return parent;
    }
    
    /** Get the contents of an InputStream
    * 
    * @param is
    *            The InputStream to get the contents for
    * @return The <code>String</code> representing the contents
    */
   public static String getContents(File f) throws FileNotFoundException {
       String fileContents = "";

       // create a new reader for the stream
       BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
       try {

           // get the contents
           String s = "";
           while ((s = br.readLine()) != null) {
               fileContents += s + "\n";
           }
       } catch (IOException e) {
           MylarStatusHandler.log(e, "couldn't get contents");
       }
       return fileContents;
   }

}
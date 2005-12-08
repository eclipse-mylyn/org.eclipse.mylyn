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
/*
 * Created on May 22, 2005
  */
package org.eclipse.mylar.core.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.util.ContextReader;
import org.eclipse.mylar.core.util.ContextWriter;
import org.eclipse.mylar.core.util.ErrorLogger;

/**
 * @author Mik Kersten
 */
public class MylarContextExternalizer {

    public static final String INTERACTION_EVENT_ID = "interactionEvent";
    public static final String TASKSCAPE_ID = "taskMemory";
    
    private ContextReader reader = new ContextReader();
    private ContextWriter writer = new ContextWriter();
    
    public void writeContextToXML(MylarContext context, File file) { 
        try {
        	if (context.getInteractionHistory().isEmpty()) return;
        	if (!file.exists()) {        		
        		file.createNewFile();
        	}
            OutputStream stream = new FileOutputStream(file);
            writer.setOutputStream(stream);
            writer.writeContextToStream(context);
            stream.close();
        } catch (IOException e) {
        	ErrorLogger.fail(e, "Could not write: " + file.getAbsolutePath(), true);
        }
    }
    
    public MylarContext readContextFromXML(File file) {
        try {
            if (!file.exists()) return null;
            return reader.readContext(file);
        } catch (Exception e) {
        	ErrorLogger.fail(e, "Could not read: " + file.getAbsolutePath(), true);
        }
        return null;
    }
    
    @Deprecated
    public MylarContext readTaskscapeFromFile(String sourcePath) {
        //Taskscape loadedTaskscape;
        File file = new File(sourcePath);
        try {
            if (!file.exists()) {
                return null;
            } else {
                FileInputStream fileStream = new FileInputStream(file);
                ObjectInputStream stream = new ObjectInputStream(fileStream);
                MylarContext taskscape =  (MylarContext)stream.readObject();
                fileStream.close();
                return taskscape;
            }
        } catch (InvalidClassException ice) {
            String saveFilePath = file.getAbsolutePath() + "-save";
            file.renameTo(new File(saveFilePath)); // TODO: this fails?
            ErrorLogger.log("Found invalid taskscape, moved to: " + saveFilePath, this);
        } catch (Exception e) {
            ErrorLogger.log(e, "Could not load taskscape");
        } 
        return null;
    }
 
    @Deprecated
    public void writeTaskscapeToFile(IMylarContext taskscape, String destinationPath) {
        try {
            File file = new File(destinationPath);
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
            stream.writeObject(taskscape);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            ErrorLogger.log(e, "Could not save taskscape");
        }
    }

}

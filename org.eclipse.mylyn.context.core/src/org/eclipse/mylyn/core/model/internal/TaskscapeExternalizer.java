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
package org.eclipse.mylar.core.model.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.util.TaskscapeXmlReader;
import org.eclipse.mylar.core.util.TaskscapeXmlWriter;


/**
 * @author Mik Kersten
 * 
 * TODO: stop using java.io ?
 */
public class TaskscapeExternalizer {

    public static final String INTERACTION_EVENT_ID = "interactionEvent";
    public static final String TASKSCAPE_ID = "taskMemory";
    
    private TaskscapeXmlReader reader = new TaskscapeXmlReader();
    private TaskscapeXmlWriter writer = new TaskscapeXmlWriter();
    
    public void writeXMLTaskscapeToFile(Taskscape taskscape, File file) { 
        try {
        	if (!file.exists()) {        		
        		file.createNewFile();
        	}
            OutputStream stream = new FileOutputStream(file);
            writer.setOutputStream(stream);
            writer.writeTaskscapeToStream(taskscape);
            stream.close();
        } catch (IOException e) {
            MylarPlugin.log("could not write: " + file.getName());
        	MylarPlugin.log(this.getClass().toString(), e);
        }
    }
    
    public Taskscape readXMLTaskscapeFromFile(File file) {
        try {
            if (!file.exists()) return null;
            return reader.readTaskscape(file);
        } catch (Exception e) {
        	MylarPlugin.log(this.getClass().toString(), e);
        }
        return null;
    }
    
    @Deprecated
    public Taskscape readTaskscapeFromFile(String sourcePath) {
        //Taskscape loadedTaskscape;
        File file = new File(sourcePath);
        try {
            if (!file.exists()) {
                return null;
            } else {
                FileInputStream fileStream = new FileInputStream(file);
                ObjectInputStream stream = new ObjectInputStream(fileStream);
                Taskscape taskscape =  (Taskscape)stream.readObject();
                fileStream.close();
                return taskscape;
            }
        } catch (InvalidClassException ice) {
            String saveFilePath = file.getAbsolutePath() + "-save";
            file.renameTo(new File(saveFilePath)); // TODO: this fails?
            MylarPlugin.log(this, "Found invalid taskscape, moved to: " + saveFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            MylarPlugin.fail(e, "Could not load taskscape: " + e.getMessage()
                    + ", " + e.getStackTrace()[0], true);
        } 
        return null;
    }
 
    @Deprecated
    public void writeTaskscapeToFile(ITaskscape taskscape, String destinationPath) {
        try {
            File file = new File(destinationPath);
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
            stream.writeObject(taskscape);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            MylarPlugin.fail(e, "Could not save taskscape", true);
        }
    }

}

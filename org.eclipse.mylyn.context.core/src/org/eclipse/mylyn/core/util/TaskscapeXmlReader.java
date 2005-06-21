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

package org.eclipse.mylar.core.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.InteractionEvent;
import org.eclipse.mylar.core.model.TaskscapeManager;
import org.eclipse.mylar.core.model.InteractionEvent.Kind;
import org.eclipse.mylar.core.model.internal.Taskscape;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Mik Kersten
 */
public class TaskscapeXmlReader {
    
	static int readVersion; 
    
    public Taskscape readTaskscape(File file) {
        if (!file.exists()) return null;
        try {
            Document doc = openAsDOM(file);
            Element root = doc.getDocumentElement();
            readVersion = Integer.parseInt(root.getAttribute("Version"));
            String id = root.getAttribute("Id");
            Taskscape t = new Taskscape(id, TaskscapeManager.getScalingFactors());
			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node child = list.item(i);
				InteractionEvent ie = readInteractionEvent(child);
				if (ie != null) {
					t.parseEvent(ie);
				}				
			}
			return t;
        } catch (Exception e) {
            MylarPlugin.log("could not read taskscape, recreating", this);
            file.renameTo(new File(file.getAbsolutePath() + "-save"));
            return null;
        }
    }

    public Document openAsDOM(File inputFile) throws IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(inputFile); 
        } catch (SAXException se) {
            MylarPlugin.log(se, "could not build");
        } catch (ParserConfigurationException e) {
        	MylarPlugin.log(e, "could not parse");
		}
        return document;
    }
    
    public InteractionEvent readInteractionEvent(Node n) {
		try {
			Element e = (Element) n;
			String kind = e.getAttribute("Kind");
			String startDate = e.getAttribute("StartDate");
			String endDate = e.getAttribute("EndDate");
			String originId = checkStringFormat(e.getAttribute("OriginId"));
			String structureKind = checkStringFormat(e.getAttribute("StructureKind"));
			String structureHandle = checkStringFormat(e.getAttribute("StructureHandle"));
			String navigation = checkStringFormat(e.getAttribute("Navigation"));
			String delta = checkStringFormat(e.getAttribute("Delta"));
			String interest = e.getAttribute("Interest");
			
			String formatString = "yyyy-MM-dd HH:mm:ss.S z";
	    	SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
			InteractionEvent ie = new InteractionEvent(Kind.fromString(kind),
					structureKind, structureHandle, originId, navigation,
					delta, Float.parseFloat(interest), format.parse(startDate),
					format.parse(endDate));
			return ie;
		} catch (ParseException e) {
			MylarPlugin.log(e, "could not read interaction event");
		}
    	return null;
    }
    
    private String checkStringFormat(String string) {
    	StringBuffer result = new StringBuffer(string.length() + 10);
		for (int i = 0; i < string.length(); ++i) {
			char xChar = string.charAt(i);
			if (xChar == '&') {
				i++;
				StringBuffer escapeChar = new StringBuffer(10);
				boolean flag = true;
				while(flag) {		
					xChar = string.charAt(i++);
					if (xChar == ';') {
						flag = false;
						i--;
					} else {
						escapeChar.append(xChar);
					}					
				}
				result.append(getReplacement(escapeChar.toString()));
			} else {
				result.append(xChar);
			}
		}
		return result.toString();
    }    
    
    static char getReplacement(String s) {
    	if (s.equals("lt")) {
    		return '<';
    	} else if (s.equals("gt")){
    		return '>';
    	} else if (s.equals("quot")){
    		return '"';
    	} else if (s.equals("apos")){
     		return '\'';
     	} else if (s.equals("amp")){
    		return '&';
    	} else if (s.equals("x0D")){
    		return '\r';
    	} else if (s.equals("x0A")){
    		return '\n';
    	} else if (s.equals("x09")){
    		return '\u0009';
    	} 
    	return 0;
	}
}

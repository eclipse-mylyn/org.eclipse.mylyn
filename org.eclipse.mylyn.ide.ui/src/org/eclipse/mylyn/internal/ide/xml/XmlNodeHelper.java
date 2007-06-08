/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Apr 4, 2005
 */
package org.eclipse.mylyn.internal.ide.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.mylyn.core.MylarStatusHandler;

/**
 * Class to help get the handle for an xml file
 * 
 * @author Shawn Minto
 */
public class XmlNodeHelper {

	/** The filename of the xml file */
	private String filename;

	/** The start line of the node */
	private String end;

	public XmlNodeHelper(String handle) {
		int first = handle.indexOf(";");
		if (first == -1) {
			filename = handle;
			end = "";
		} else {
			filename = handle.substring(0, first);
			end = handle.substring(first + 1);
		}
	}

	/**
	 * Constructor - used for pde
	 * 
	 * @param filename
	 *            The filename
	 * @param startOffset
	 *            The start line for the node
	 */
	public XmlNodeHelper(String filename, int s) {
		this.filename = filename;
		this.end = "" + s;
	}

	/**
	 * Constructor - used for ant
	 * 
	 * @param filename
	 *            The filename
	 * @param startOffset
	 *            The start line for the node
	 */
	public XmlNodeHelper(String filename, String s) {
		this.filename = filename;
		this.end = s;
	}

	// /**
	// * Constructor
	// * @param fei The FileEditorInput for the editor the node is in
	// * @param offset The offset of the node
	// * @throws CoreException
	// * @throws BadLocationException
	// */
	// public XmlNodeHelper(FileEditorInput fei, int offset) throws
	// CoreException {
	//
	// this.filename = fei.getFile().getFullPath().toString();
	// InputStream i = fei.getFile().getContents();
	// String contents = getContents(i);
	// Document d = new Document(contents);
	// try{
	// startOffset = d.getLineOfOffset(offset - 1);
	// } catch (BadLocationException e){
	// // SHAWNTODO this happens when the document has not been saved and
	// selections
	// // are made to new nodes
	// startOffset = 0;
	// ContextCorePlugin.log(e, "Unable to get start line from the editor: " +
	// filename + ":" + offset);
	// }
	// }

	/**
	 * Get the handle for the node Format: filename;startOffset
	 * 
	 * @return The to the node handle in String form
	 */
	public String getHandle() {
		return filename + ";" + getValue();
	}

	public String getFilename() {
		if (filename != null)
			filename = filename.trim();
		return filename;
	}

	public String getValue() {
		return end;
	}

	@Override
	public boolean equals(Object e) {
		if (e instanceof XmlNodeHelper) {
			XmlNodeHelper xnode = ((XmlNodeHelper) e);
			return xnode.getHandle().equals(getHandle());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getHandle().hashCode();
	}
	

	/**
	 * Get the contents of an InputStream
	 * 
	 * @param is
	 *            The InputStream to get the contents for
	 * @return The <code>String</code> representing the contents
	 */
	public static String getContents(InputStream is) {
		String contents = "";

		// create a new reader for the stream
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {

			// get the contents
			String s = "";
			char[] cbuf = new char[512];
			while (br.read(cbuf) != -1) {
				s = new String(cbuf);
				contents += s;
			}
		} catch (IOException e) {
			MylarStatusHandler.log(e, "couldn't get contents");
		}
		return contents;
	}

	// XXX needed if we are parsing the data again to get info instead of using
	// the xml models
	//
	// private void parseContents(IDocument d, String contents){
	// String [] lines = contents.split("\n");
	// boolean inQuote = false;
	// boolean attr = false;
	// String attrName = "";
	// String attrVal = "";
	// String oldAttrVal = "";
	// char prevChar = ' ';
	// for(int i = endLine - 1; i >= 0; i--){
	// // parse backwards
	//            
	// String line = lines[i];
	// for(int cpos = line.length()-1; cpos >=0; cpos--){
	//                
	// if(attr){
	// if(attrName.length() != 0 && (isWhitespace(line.charAt(cpos))))
	// {
	// attr = false;
	// if(attrName.toLowerCase(Locale.ENGLISH).equals("name"))
	// {
	// name = oldAttrVal;
	// }
	// }
	// attrName = line.charAt(cpos) + attrName;
	// }
	//                               
	// if(line.charAt(cpos) == '"' && inQuote){
	// inQuote = false;
	// }else if(line.charAt(cpos) == '"' && !inQuote){
	// inQuote = true;
	// }else if(!inQuote && line.charAt(cpos) == '<' && prevChar != '/'){
	// int space = line.indexOf(" ", cpos);
	// if(space == -1)
	// tagName = line.substring(cpos + 1);
	// else
	// tagName = line.substring(cpos + 1, space);
	// return;
	// }else if(!inQuote && line.charAt(cpos) =='='){
	// attr = true;
	// attrName = "";
	// oldAttrVal = attrVal;
	// attrVal = "";
	// }else if(inQuote){
	// attrVal = line.charAt(cpos) + attrVal;
	// }
	// prevChar = line.charAt(cpos);
	// }
	// }
	// }
	//    
	// private boolean isWhitespace(char c){
	// return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	// }
	//    
	// public String getCanName()
	// {
	// int slash = filename.lastIndexOf("/");
	// String file = slash==-1?filename:filename.substring(slash+1);
	// String s = file + ": " + getTagName();
	// if(getName() != null)
	// s += " \"" + getName() + "\"";
	// s += " : " + startOffset;
	// return s;
	// }
	//
	// public String getTagName() {
	// if(tagName != null)
	// tagName = tagName.trim();
	// return tagName;
	// }

	// public void setTagName(String tagName) {
	// this.tagName = tagName;
	// }

	// public String getName() {
	// if(name != null)
	// name = name.trim();
	// return name;
	// }

	// public void setName(String name) {
	// this.name = name;
	// }

	//
	// public int getEndLine() {
	// return endLine;
	// }
	//    
	//
	// public void setEndLine(int endLine) {
	// this.endLine = endLine;
	// }

	//    
	//
	// public void setStartLine(int startOffset) {
	// this.startLine = startOffset;
	// }

}

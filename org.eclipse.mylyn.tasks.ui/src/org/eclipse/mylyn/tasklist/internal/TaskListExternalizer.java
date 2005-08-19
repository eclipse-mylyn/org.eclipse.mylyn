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
package org.eclipse.mylar.tasklist.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.AbstractCategory;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.ITaskListExternalizer;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class TaskListExternalizer {
	
	private List<ITaskListExternalizer> externalizers = new ArrayList<ITaskListExternalizer>();
	private DefaultTaskListExternalizer defaultExternalizer = new DefaultTaskListExternalizer();
	
	private String readVersion = "";
	private boolean hasCaughtException = false;
	
	public void initExtensions() {
		TaskListExtensionReader.initExtensions(externalizers, defaultExternalizer);
	}
	
//	public void addExternalizer(ITaskListExternalizer externalizer) {
//		externalizers.add(externalizer);
//		defaultExternalizer.setExternalizers(externalizers);
//		MylarTasklistPlugin.getTaskListManager().getTaskList().clear();
//		readTaskList(MylarTasklistPlugin.getTaskListManager().getTaskList(), MylarTasklistPlugin.getTaskListManager().getTaskListFile());
//		if(MylarTasklistPlugin.getDefault().getContributor() != null){
//			MylarTasklistPlugin.getDefault().getContributor().restoreState(TaskListView.getDefault());
//			if (TaskListView.getDefault() != null) {
//				TaskListView.getDefault().getViewer().refresh();
//			}
//		}
//	}
	
	public void removeExternalizer(ITaskListExternalizer externalizer) {
		externalizers.remove(externalizer);
	}
	
	public void writeTaskList(TaskList tlist, File outFile) {
		initExtensions();
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;

		try {
			db = dbf.newDocumentBuilder();
			doc = db.newDocument();
		} catch (ParserConfigurationException e) {
			MylarPlugin.log(e, "could not create document");
			e.printStackTrace();
		}

		Element root = doc.createElement("TaskList");
		root.setAttribute("Version", "1.0.1");

		for (ITaskListExternalizer externalizer : externalizers) {
			externalizer.createRegistry(doc, root);
		}		

		for (AbstractCategory category : tlist.getCategories()) {
			Element element = null;
			for (ITaskListExternalizer externalizer : externalizers) {
				if (externalizer.canCreateElementFor(category)) element = externalizer.createCategoryElement(category, doc, root);
			}
			if (element == null && defaultExternalizer.canCreateElementFor(category)) {
				defaultExternalizer.createCategoryElement(category, doc, root);		
			} else if(element == null){
				MylarPlugin.log("Did not externalize: " + category, this);
			}
		}
		for (ITask task : tlist.getRootTasks()) {
			try {
				Element element = null;
				for (ITaskListExternalizer externalizer : externalizers) {
					if (externalizer.canCreateElementFor(task)) element = externalizer.createTaskElement(task, doc, root);
				}
				if (element == null && defaultExternalizer.canCreateElementFor(task)) {
					defaultExternalizer.createTaskElement(task, doc, root);
				} else if(element == null){
					MylarPlugin.log("Did not externalize: " + task, this);
				}
			}catch (Exception e) {
				MylarPlugin.log(e, e.getMessage());
			}			
		}
		doc.appendChild(root);
		writeDOMtoFile(doc, outFile);
		return;
	}

	/**
	 * Writes an XML file from a DOM.
	 * 
	 * doc  - the document to write
	 * file - the file to be written to
	 */
	private void writeDOMtoFile(Document doc, File file) {
		try {
			// A file output stream is an output stream for writing data to a File
			//
			OutputStream outputStream = new FileOutputStream(file);
			writeDOMtoStream(doc, outputStream);
			outputStream.flush();
			outputStream.close();
		} catch (Exception fnfe) {
			MylarPlugin.log(fnfe, "Tasklist could not be found");
		}
	}

	/**
	 * Writes the provided XML document out to the specified output stream.
	 * 
	 * doc - the document to be written
	 * outputStream - the stream to which the document is to be written
	 */
	private void writeDOMtoStream(Document doc, OutputStream outputStream) {
		// Prepare the DOM document for writing
		// DOMSource - Acts as a holder for a transformation Source tree in the 
		// form of a Document Object Model (DOM) tree
		//
		Source source = new DOMSource(doc);

		// StreamResult - Acts as an holder for a XML transformation result
		// Prepare the output stream
		//
		Result result = new StreamResult(outputStream);

		// An instance of this class can be obtained with the 
		// TransformerFactory.newTransformer  method. This instance may 
		// then be used to process XML from a variety of sources and write 
		// the transformation output to a variety of sinks
		//

		Transformer xformer = null;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			//Transform the XML Source to a Result
			//
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}
	}
	
//	private void writeTask(ITask task, Document doc, Element parent) {
//
//	}
	
	public void readTaskList(TaskList tlist, File inFile) {
		initExtensions();
		MylarTasklistPlugin.getDefault().restoreTaskHandlerState();
		hasCaughtException = false;
		try {
			// parse file
			//
			if (!inFile.exists())
				return;
			Document doc = openAsDOM(inFile);
			if (doc == null) {
				handleException(inFile, null, new MylarExternalizerException("Tasklist was not well formed XML"));
				return;
			}
			// read root node to get version number
			//
			Element root = doc.getDocumentElement();
			readVersion = root.getAttribute("Version");

			if (readVersion.equals("1.0.0")) {
				MylarPlugin.log("version: " + readVersion + " not supported", this);
//				NodeList list = root.getChildNodes();
//				for (int i = 0; i < list.getLength(); i++) {
//					Node child = list.item(i);
//					readTasksToNewFormat(child, tlist);
//					//tlist.addRootTask(readTaskAndSubTasks(child, null, tlist));
//				}
			} else {
				NodeList list = root.getChildNodes();
				for (int i = 0; i < list.getLength(); i++) {
					Node child = list.item(i);
					boolean wasRead = false;
					try {
						if (child.getNodeName().endsWith(DefaultTaskListExternalizer.TAG_CATEGORY)) {													
							for (ITaskListExternalizer externalizer : externalizers) {
								if (externalizer.canReadCategory(child)) {
									externalizer.readCategory(child, tlist);
									wasRead = true;
									break;
								}
							}
							if (!wasRead && defaultExternalizer.canReadCategory(child)) {
								defaultExternalizer.readCategory(child, tlist);
							} else {
								// MylarPlugin.log("Did not read: " +
								// child.getNodeName(), this);
							}						
						} else {
							for (ITaskListExternalizer externalizer : externalizers) {
								if (externalizer.canReadTask(child)) {
									// TODO add the tasks properly
									ITask newTask = externalizer.readTask(child, tlist, null, null);
									ITaskHandler taskHandler = MylarTasklistPlugin.getDefault().getTaskHandlerForElement(newTask);
								    if(taskHandler != null){
							    		newTask = taskHandler.taskAdded(newTask);
							    	}
								    tlist.internalAddRootTask(newTask);
									
									wasRead = true;
									break;
								}
							}
							if (!wasRead && defaultExternalizer.canReadTask(child)) {
								tlist.internalAddRootTask(defaultExternalizer.readTask(child, tlist, null, null));
							} else {
	//							MylarPlugin.log("Did not read: " + child.getNodeName(), this);
							}
						}
					} catch (Exception e) {
						handleException(inFile, child, e);
					}
				}
			}
		} catch (Exception e) {
			handleException(inFile, null, e);
		}
		if (hasCaughtException) {
			// if exception was caught, write out the new task file, so that it doesn't happen again.
			// this is OK, since the original (corrupt) tasklist is saved.
			// TODO: The problem with this is that if the orignal tasklist has tasks and bug reports, but a 
			// task is corrupted, the new tasklist that is written will not include the bug reports (since the
			// bugzilla externalizer is not loaded. So there is a potentila that we can lose bug reports.
			writeTaskList(tlist, inFile);
		}
		MylarTasklistPlugin.getDefault().restoreTaskHandlerState();
	}

	/**
	 * Opens the specified XML file and parses it into a DOM Document.
	 * 
	 * Filename - the name of the file to open
	 * Return   - the Document built from the XML file
	 * Throws   - XMLException if the file cannot be parsed as XML
	 *          - IOException if the file cannot be opened
	 */
	private Document openAsDOM(File inputFile) throws IOException {

		// A factory API that enables applications to obtain a parser 
		// that produces DOM object trees from XML documents
		//
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Using DocumentBuilder, obtain a Document from XML file.
		//
		DocumentBuilder builder = null;
		Document document = null;
		try {
			// create new instance of DocumentBuilder
			//
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			inputFile.renameTo(new File(inputFile.getName() + "save.xml"));
			MylarPlugin.log(pce, "Failed to load XML file");
		}
		try {
			// Parse the content of the given file as an XML document 
			// and return a new DOM Document object. Also throws IOException
			document = builder.parse(inputFile);
		} catch (SAXException se) {
			inputFile.renameTo(new File(inputFile.getName() + "save.xml"));
			MylarPlugin.log(se, "Failed to parse XML file");
		}
		return document;
	}
	
	private void handleException(File inFile, Node child, Exception e) {
		hasCaughtException = true;
		String name = inFile.getAbsolutePath();
		name = name.substring(0, name.lastIndexOf('.')) + "-save1.xml";
		File save = new File(name);
		int i = 2;
		while(save.exists()) {			
			name = name.substring(0, name.lastIndexOf('.')-1) + i + ".xml";
			save = new File(name);
			i++;
		}
		if (!copy(inFile, save)) {
			inFile.renameTo(new File(name));
		}			
		if (child == null) {
			MylarPlugin.log(e, "Could not read task list");
		} else {
			MylarPlugin.log(e, "Tasks may have been lost from " + child.getNodeName());
		}		
	}
    private boolean copy(File src, File dst) {
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
    }

	
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private Document openAsDOM(String input) throws IOException {

		// A factory API that enables applications to obtain a parser 
		// that produces DOM object trees from XML documents
		//
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Using DocumentBuilder, obtain a Document from XML file.
		//
		DocumentBuilder builder = null;
		Document document = null;
		try {
			// create new instance of DocumentBuilder
			//
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			MylarPlugin.log(pce, "Failed to load XML file");
		}
		try {
			// Parse the content of the given file as an XML document 
			// and return a new DOM Document object. Also throws IOException
			StringReader s = new StringReader(input);
			InputSource in = new InputSource(s);
			document = builder.parse(in);
		} catch (SAXException se) {
			MylarPlugin.log(se, "Failed to parse XML file");
		}
		return document;
	}
    
    public void readTaskList(TaskList tlist, String input) {
		initExtensions();
		try {

			Document doc = openAsDOM(input);
			if (doc == null) {
				return;
			}
			// read root node to get version number
			//
			Element root = doc.getDocumentElement();
			readVersion = root.getAttribute("Version");

			if (readVersion.equals("1.0.0")) {
				MylarPlugin.log("version: " + readVersion + " not supported", this);
//				NodeList list = root.getChildNodes();
//				for (int i = 0; i < list.getLength(); i++) {
//					Node child = list.item(i);
//					readTasksToNewFormat(child, tlist);
//					//tlist.addRootTask(readTaskAndSubTasks(child, null, tlist));
//				}
			} else {
				NodeList list = root.getChildNodes();
				for (int i = 0; i < list.getLength(); i++) {
					Node child = list.item(i);
					boolean wasRead = false;
					try {
						if (child.getNodeName().endsWith(DefaultTaskListExternalizer.TAG_CATEGORY)) {													
							for (ITaskListExternalizer externalizer : externalizers) {
								if (externalizer.canReadCategory(child)) {
									externalizer.readCategory(child, tlist);
									wasRead = true;
									break;
								}
							}
							if (!wasRead && defaultExternalizer.canReadCategory(child)) {
								defaultExternalizer.readCategory(child, tlist);
							} else {
								// MylarPlugin.log("Did not read: " +
								// child.getNodeName(), this);
							}						
						} else {
							for (ITaskListExternalizer externalizer : externalizers) {
								if (externalizer.canReadTask(child)) {
									// TODO add the tasks properly
									ITask newTask = externalizer.readTask(child, tlist, null, null);
									ITaskHandler taskHandler = MylarTasklistPlugin.getDefault().getTaskHandlerForElement(newTask);
								    if(taskHandler != null){
							    		newTask = taskHandler.taskAdded(newTask);
							    	}
								    tlist.internalAddRootTask(newTask);
									
									wasRead = true;
									break;
								}
							}
							if (!wasRead && defaultExternalizer.canReadTask(child)) {
								tlist.internalAddRootTask(defaultExternalizer.readTask(child, tlist, null, null));
							} else {
	//							MylarPlugin.log("Did not read: " + child.getNodeName(), this);
							}
						}
					} catch (Exception e) {
						MylarPlugin.log(e, "can't read xml string");
					}
				}
			}
		} catch (Exception e) {
			MylarPlugin.log(e, "can't read xml string");
		}
	}
    
    public String getTaskListXml(TaskList tlist) {
		// TODO make this and writeTaskList use the same base code
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;

		try {
			db = dbf.newDocumentBuilder();
			doc = db.newDocument();
		} catch (ParserConfigurationException e) {
			MylarPlugin.log(e, "could not create document");
			e.printStackTrace();
		}

		Element root = doc.createElement("TaskList");
		root.setAttribute("Version", "1.0.1");

		for (ITaskListExternalizer externalizer : externalizers) {
			externalizer.createRegistry(doc, root);
		}		

		for (AbstractCategory category : tlist.getCategories()) {
			Element element = null;
			for (ITaskListExternalizer externalizer : externalizers) {
				if (externalizer.canCreateElementFor(category)) element = externalizer.createCategoryElement(category, doc, root);
			}
			if (element == null && defaultExternalizer.canCreateElementFor(category)) {
				defaultExternalizer.createCategoryElement(category, doc, root);		
			} else if(element == null){
				MylarPlugin.log("Did not externalize: " + category, this);
			}
		}
		for (ITask task : tlist.getRootTasks()) {
			try {
				Element element = null;
				for (ITaskListExternalizer externalizer : externalizers) {
					if (externalizer.canCreateElementFor(task)) element = externalizer.createTaskElement(task, doc, root);
				}
				if (element == null && defaultExternalizer.canCreateElementFor(task)) {
					defaultExternalizer.createTaskElement(task, doc, root);
				} else if(element == null){
					MylarPlugin.log("Did not externalize: " + task, this);
				}
			}catch (Exception e) {
				MylarPlugin.log(e, e.getMessage());
			}			
		}
		doc.appendChild(root);
		StringWriter sw = new StringWriter();
		
		Source source = new DOMSource(doc);
 
		Result result = new StreamResult(sw);

		Transformer xformer = null;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			//Transform the XML Source to a Result
			//
			xformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return sw.toString();
	}
    

// private static ITask readTaskAndSubTasks(Node node, ITask root, TaskList
// tlist) {
//		//extract node and create new sub task
//		//
//		Element e = (Element) node;
//		ITask t;
//		String handle = "";
//		if (e.hasAttribute("ID")) {
//			handle = e.getAttribute("ID");
//		} else {
//			handle = e.getAttribute("Handle");
//		}
//		
//		String label = e.getAttribute("Label");
//		String priority = e.getAttribute("Priority");
//
//		if (e.getAttribute("Bugzilla").compareTo("true") == 0) {
//			t = new BugzillaTask(handle, label, true);
//			BugzillaTask bt = (BugzillaTask) t;
//			bt.setState(BugTaskState.FREE);
//			bt.setLastRefresh(new Date(new Long(e.getAttribute("LastDate"))
//					.longValue()));
//			if (e.getAttribute("Dirty").compareTo("true") == 0) {
//				bt.setDirty(true);
//			} else {
//				bt.setDirty(false);
//			}
//			if (bt.readBugReport() == false) {
//				MylarPlugin.log("Failed to read bug report", null);
//			}
//		} else {
//			t = new Task(handle, label);			
//		}
//		t.setPriority(priority);
//		t.setPath(e.getAttribute("Path"));
//		
//		if (e.getAttribute("Active").compareTo("true") == 0) {
//			t.setActive(true);
//			tlist.setActive(t, true);
//		} else {
//			t.setActive(false);
//		}
//
//		if (e.getAttribute("Complete").compareTo("true") == 0) {
//			t.setCompleted(true);
//		} else {
//			t.setCompleted(false);
//		}
//		if (e.getAttribute("IsCategory").compareTo("true") == 0) {
//			t.setIsCategory(true);
//		} else {
//			t.setIsCategory(false);
//		}
//
//		if (e.hasAttribute("Notes")) {
//			t.setNotes(e.getAttribute("Notes"));			
//		} else {
//			t.setNotes("");
//		}
//		if (e.hasAttribute("Elapsed")) {
//			t.setElapsedTime(e.getAttribute("Elapsed"));			
//		} else {
//			t.setElapsedTime("");
//		}
//		if (e.hasAttribute("Estimated")) {
//			t.setEstimatedTime(e.getAttribute("Estimated"));			
//		} else {
//			t.setEstimatedTime("");
//		}
//		
//		int i = 0;
//		while (e.hasAttribute("link"+i)) {
//			t.getRelatedLinks().add(e.getAttribute("link"+i));
//			i++;
//		}
//				
//		if (!readVersion.equals("1.0.0")) {
//			// for newer revisions
//		}
//
//		i = 0;
//		NodeList list = e.getChildNodes();
//		for (i = 0; i < list.getLength(); i++) {
//			Node child = list.item(i);
//			t.addSubTask(readTaskAndSubTasks(child, t, tlist));
//		}
//		if (root != null) {
//			t.setParent(root);
//		}
//		return t;
//	}	
	
//	private void readTaskCategory(Node node, TaskList tlist) {
//		Element e = (Element) node;
//		TaskCategory cat = new TaskCategory(e.getAttribute("Name"));
//		tlist.addCategory(cat);
//		NodeList list = node.getChildNodes();
//		for (int i = 0; i < list.getLength(); i++) {
//			Node child = list.item(i);
//			cat.addTask(readTask(child, tlist, cat, null));
//		}
//	}

	
//	private ITask readTask(Node node, TaskList tlist, TaskCategory cat, ITask parent) {
//		Element e = (Element) node;
//		ITask t;
//		String handle = e.getAttribute("Handle");		
//		String label = e.getAttribute("Label");
//		String priority = e.getAttribute("Priority");
//
//
//		} else {
//			t = new Task(handle, label);			
//		}
//		t.setPriority(priority);
//		t.setPath(e.getAttribute("Path"));
//		
//		if (e.getAttribute("Active").compareTo("true") == 0) {
//			t.setActive(true);
//			tlist.setActive(t, true);
//		} else {
//			t.setActive(false);
//		}
//		if (e.getAttribute("Complete").compareTo("true") == 0) {
//			t.setCompleted(true);
//		} else {
//			t.setCompleted(false);
//		}		
//		if (e.hasAttribute("Notes")) {
//			t.setNotes(e.getAttribute("Notes"));			
//		} else {
//			t.setNotes("");
//		}
//		if (e.hasAttribute("Elapsed")) {
//			t.setElapsedTime(e.getAttribute("Elapsed"));			
//		} else {
//			t.setElapsedTime("");
//		}
//		if (e.hasAttribute("Estimated")) {
//			t.setEstimatedTime(e.getAttribute("Estimated"));			
//		} else {
//			t.setEstimatedTime("");
//		}
//		
//		int i = 0;
//		while (e.hasAttribute("link"+i)) {
//			t.getRelatedLinks().add(e.getAttribute("link"+i));
//			i++;
//		}
//		t.setCategory(cat);
//		t.setParent(parent);
//		NodeList list = e.getChildNodes();
//		for (i = 0; i < list.getLength(); i++) {
//			Node child = list.item(i);
//			t.addSubTask(readTask(child, tlist, null, t));
//		}
//		return t;
//	}
	
//	private void readTasksToNewFormat(Node node, TaskList tlist) {
//		Element e = (Element) node;
//		ITask t;
//		String handle = e.getAttribute("Handle");
//		String label = e.getAttribute("Label");
//		
//		if (e.getAttribute("IsCategory").compareTo("true") == 0) {
//			TaskCategory c = new TaskCategory(label);
//			NodeList list = e.getChildNodes();
//			for (int i = 0; i < list.getLength(); i++) {
//				Node child = list.item(i);
//				readSubTasksToNewFormat(child, tlist, c);
//			}
//			tlist.addCategory(c);
//		} else {			
//			String priority = e.getAttribute("Priority");
//			if (e.getAttribute("Bugzilla").compareTo("true") == 0) {
//				t = new BugzillaTask(handle, label, true);
//				BugzillaTask bt = (BugzillaTask) t;
//				bt.setState(BugTaskState.FREE);
//				bt.setLastRefresh(new Date(new Long(e.getAttribute("LastDate"))
//						.longValue()));
//				if (e.getAttribute("Dirty").compareTo("true") == 0) {
//					bt.setDirty(true);
//				} else {
//					bt.setDirty(false);
//				}
//				if (bt.readBugReport() == false) {
//					MylarPlugin.log("Failed to read bug report", null);
//				}
//			} else {
//				t = new Task(handle, label);
//			}
//			t.setPriority(priority);
//			t.setPath(e.getAttribute("Path"));
//			t.setNotes(e.getAttribute("Notes"));
//			t.setElapsedTime(e.getAttribute("Elapsed"));
//			t.setEstimatedTime(e.getAttribute("Estimated"));
//
//			if (e.getAttribute("Active").compareTo("true") == 0) {
//				t.setActive(true);
//				tlist.setActive(t, true);
//			} else {
//				t.setActive(false);
//			}
//			if (e.getAttribute("Complete").compareTo("true") == 0) {
//				t.setCompleted(true);
//			} else {
//				t.setCompleted(false);
//			}
//
//			int i = 0;
//			while (e.hasAttribute("link" + i)) {
//				t.getRelatedLinks().add(e.getAttribute("link" + i));
//				i++;
//			}
//			tlist.addRootTask(t);
//			i = 0;
//			NodeList list = e.getChildNodes();
//			for (i = 0; i < list.getLength(); i++) {
//				Node child = list.item(i);
//				readSubTasksToNewFormat(child, tlist, null);
//			}
//		}
//	}
//	private void readSubTasksToNewFormat(Node node, TaskList tlist, TaskCategory cat) {
//		Element e = (Element) node;
//		ITask t;
//		String handle = e.getAttribute("Handle");
//		String label = e.getAttribute("Label");
//		String priority = e.getAttribute("Priority");
//		if (e.getAttribute("Bugzilla").compareTo("true") == 0) {
//			t = new BugzillaTask(handle, label, true);
//			BugzillaTask bt = (BugzillaTask) t;
//			bt.setState(BugTaskState.FREE);
//			bt.setLastRefresh(new Date(new Long(e.getAttribute("LastDate"))
//					.longValue()));
//			if (e.getAttribute("Dirty").compareTo("true") == 0) {
//				bt.setDirty(true);
//			} else {
//				bt.setDirty(false);
//			}
//			if (bt.readBugReport() == false) {
//				MylarPlugin.log("Failed to read bug report", null);
//			}
//		} else {
//			t = new Task(handle, label);
//		}
//		t.setPriority(priority);
//		t.setPath(e.getAttribute("Path"));
//		t.setNotes(e.getAttribute("Notes"));
//		t.setElapsedTime(e.getAttribute("Elapsed"));
//		t.setEstimatedTime(e.getAttribute("Estimated"));
//
//		if (e.getAttribute("Active").compareTo("true") == 0) {
//			t.setActive(true);
//			tlist.setActive(t, true);
//		} else {
//			t.setActive(false);
//		}
//		if (e.getAttribute("Complete").compareTo("true") == 0) {
//			t.setCompleted(true);
//		} else {
//			t.setCompleted(false);
//		}
//
//		int i = 0;
//		while (e.hasAttribute("link" + i)) {
//			t.getRelatedLinks().add(e.getAttribute("link" + i));
//			i++;
//		}
//		if (cat == null) {
//			tlist.addRootTask(t);
//		} else {
//			cat.addTask(t);
//			t.setCategory(cat);
//		}
//		
//		i = 0;
//		NodeList list = e.getChildNodes();
//		for (i = 0; i < list.getLength(); i++) {
//			Node child = list.item(i);
//			readSubTasksToNewFormat(child, tlist, cat);
//		}
//	}
}


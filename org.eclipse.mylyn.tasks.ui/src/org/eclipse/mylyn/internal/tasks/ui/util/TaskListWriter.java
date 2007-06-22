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
package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.TaskExternalizationException;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 * @author Rob Elves
 * 
 * TODO: move to core?
 */
public class TaskListWriter {

	private static final String TRANSFORM_PROPERTY_VERSION = "version";

	// May 2007: There was a bug when reading in 1.1
	// Result was an infinite loop within the parser
	private static final String XML_VERSION = "1.0";

	public static final String ATTRIBUTE_VERSION = "Version";

	public static final String ELEMENT_TASK_LIST = "TaskList";

	private static final String VALUE_VERSION = "1.0.1";

	private static final String VALUE_VERSION_1_0_0 = "1.0.0";

	private static final String FILE_SUFFIX_SAVE = "save.xml";

	private List<AbstractTaskListFactory> externalizers;

	private DelegatingTaskExternalizer delagatingExternalizer;

	private List<Node> orphanedTaskNodes = new ArrayList<Node>();

	private List<Node> orphanedQueryNodes = new ArrayList<Node>();

	private String readVersion = "";

	private boolean hasCaughtException = false;

	public TaskListWriter() {
		this.delagatingExternalizer = new DelegatingTaskExternalizer();
	}

	public void setDelegateExternalizers(List<AbstractTaskListFactory> externalizers) {
		this.externalizers = externalizers;
		this.delagatingExternalizer.setFactories(externalizers);
	}

	public void writeTaskList(TaskList taskList, File outFile) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;

		try {
			db = dbf.newDocumentBuilder();
			doc = db.newDocument();
		} catch (ParserConfigurationException e) {
			StatusManager.log(e, "could not create document");
			return;
		}

		Element root = doc.createElement(ELEMENT_TASK_LIST);
		root.setAttribute(ATTRIBUTE_VERSION, VALUE_VERSION);

		// create the categories
		for (AbstractTaskContainer category : taskList.getCategories()) {
			// if (!category.getHandleIdentifier().equals(TaskArchive.HANDLE)) {
			delagatingExternalizer.createCategoryElement(category, doc, root);
			// }
		}

		for (AbstractRepositoryQuery query : taskList.getQueries()) {
//			Element element = null;
			try {
//				for (ITaskListElementFactory externalizer : externalizers) {
//					if (externalizer.canCreateElementFor(query))
//						element = externalizer.createQueryElement(query, doc, root);
//				}
//				if (element == null && delagatingExternalizer.canCreateElementFor(query)) {
				delagatingExternalizer.createQueryElement(query, doc, root);
//				}
			} catch (Throwable t) {
				StatusManager.fail(t, "Did not externalize: " + query.getSummary(), true);
			}
//			if (element == null) {
//				StatusManager.log("Did not externalize: " + query, this);
//			}
		}

		for (AbstractTask task : taskList.getAllTasks()) {
			delagatingExternalizer.createTaskElement(task, doc, root);
//			createTaskElement(doc, root, task);
		}

		// Persist orphaned tasks...
		for (Node orphanedTaskNode : orphanedTaskNodes) {
			Node tempNode = doc.importNode(orphanedTaskNode, true);
			if (tempNode != null) {
				root.appendChild(tempNode);
			}
		}

		// Persist orphaned queries....
		for (Node orphanedQueryNode : orphanedQueryNodes) {
			Node tempNode = doc.importNode(orphanedQueryNode, true);
			if (tempNode != null) {
				root.appendChild(tempNode);
			}
		}

		doc.appendChild(root);
		writeDOMtoFile(doc, outFile);
		return;
	}

//	private void createTaskElement(Document doc, Element root, AbstractTask task) {
//		try {
//			Element element = null;
//			for (ITaskListElementFactory externalizer : externalizers) {
//				if (externalizer.canCreateElementFor(task)) {
//					element = externalizer.createTaskElement(task, doc, root);
//					break;
//				}
//			}
//			if (element == null) {// &&
	// delagatingExternalizer.canCreateElementFor(task))
	// {
//				delagatingExternalizer.createTaskElement(task, doc, root);
//			} else if (element == null) {
//				StatusManager.log("Did not externalize: " + task, this);
//			}
//		} catch (Exception e) {
//			StatusManager.log(e, e.getMessage());
//		}
//	}

	/**
	 * Writes an XML file from a DOM.
	 * 
	 * doc - the document to write file - the file to be written to
	 */
	private void writeDOMtoFile(Document doc, File file) {
		try {
			ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file));
			ZipEntry zipEntry = new ZipEntry(ITasksUiConstants.OLD_TASK_LIST_FILE);
			outputStream.putNextEntry(zipEntry);
			outputStream.setMethod(ZipOutputStream.DEFLATED);
			// OutputStream outputStream = new FileOutputStream(file);
			writeDOMtoStream(doc, outputStream);
			outputStream.flush();
			outputStream.closeEntry();
			outputStream.close();
		} catch (Exception fnfe) {
			StatusManager.log(fnfe, "TaskList could not be found");
		}
	}

	/**
	 * Writes the provided XML document out to the specified output stream.
	 * 
	 * doc - the document to be written outputStream - the stream to which the document is to be written
	 */
	private void writeDOMtoStream(Document doc, OutputStream outputStream) {
		// Prepare the DOM document for writing
		// DOMSource - Acts as a holder for a transformation Source tree in the
		// form of a Document Object Model (DOM) tree
		Source source = new DOMSource(doc);

		// StreamResult - Acts as an holder for a XML transformation result
		// Prepare the output stream
		Result result = new StreamResult(outputStream);

		// An instance of this class can be obtained with the
		// TransformerFactory.newTransformer method. This instance may
		// then be used to process XML from a variety of sources and write
		// the transformation output to a variety of sinks

		Transformer xformer = null;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(TRANSFORM_PROPERTY_VERSION, XML_VERSION);
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * TODO: fix this old mess
	 */
	public void readTaskList(TaskList taskList, File inFile, TaskDataManager taskDataManager) {
		hasCaughtException = false;
		Map<AbstractTask, NodeList> tasksWithSubtasks = new HashMap<AbstractTask, NodeList>();
		orphanedTaskNodes.clear();
		orphanedQueryNodes.clear();
		try {
			if (!inFile.exists())
				return;
			Document doc = openAsDOM(inFile);
			if (doc == null) {
				handleException(inFile, null, new TaskExternalizationException("TaskList was not well formed XML"));
				return;
			}
			Element root = doc.getDocumentElement();
			readVersion = root.getAttribute(ATTRIBUTE_VERSION);

			if (readVersion.equals(VALUE_VERSION_1_0_0)) {
				StatusManager.log("version: " + readVersion + " not supported", this);
			} else {
				NodeList list = root.getChildNodes();

				// NOTE: order is important, first read the categories
				for (int i = 0; i < list.getLength(); i++) {
					Node child = list.item(i);
					try {
						if (child.getNodeName().endsWith(DelegatingTaskExternalizer.KEY_CATEGORY)) {
							delagatingExternalizer.readCategory(child, taskList);
						}
					} catch (Exception e) {
						handleException(inFile, child, e);
					}
				}

				// then read the tasks
				for (int i = 0; i < list.getLength(); i++) {
					Node child = list.item(i);
					try {
						if (!child.getNodeName().endsWith(DelegatingTaskExternalizer.KEY_CATEGORY)
								&& !child.getNodeName().endsWith(AbstractTaskListFactory.KEY_QUERY)) {

							AbstractTask task = delagatingExternalizer.readTask(child, taskList, null, null);
							if (task == null) {
								orphanedTaskNodes.add(child);
							} else {
								if (child.getChildNodes() != null && child.getChildNodes().getLength() > 0) {
									tasksWithSubtasks.put(task, child.getChildNodes());
								}
							}
						}
					} catch (Exception e) {
						// TODO: Save orphans here too?
						// If data is source of exception then error will just repeat
						// now that orphans are re-saved upon task list save. So for now we
						// log the error warning the user and make a copy of the bad tasklist.
						handleException(inFile, child, e);
					}
				}

				for (AbstractTask task : tasksWithSubtasks.keySet()) {
					NodeList nodes = tasksWithSubtasks.get(task);
					delagatingExternalizer.readSubTasks(task, nodes, taskList);
				}

				// then queries and hits which get linked to tasks
				for (int i = 0; i < list.getLength(); i++) {
					Node child = list.item(i);
					try {
						boolean wasRead = false;
						if (child.getNodeName().endsWith(AbstractTaskListFactory.KEY_QUERY)) {
							for (AbstractTaskListFactory externalizer : externalizers) {
								Set<String> queryTagNames = externalizer.getQueryElementNames();
								if (queryTagNames != null && queryTagNames.contains(child.getNodeName())) {
									Element childElement = (Element) child;
									// TODO: move this stuff into externalizer
									String repositoryUrl =childElement.getAttribute(DelegatingTaskExternalizer.KEY_REPOSITORY_URL);
									String queryString = childElement.getAttribute(AbstractTaskListFactory.KEY_QUERY_STRING);
									if (queryString.length() == 0) { // fallback for legacy
										queryString = childElement.getAttribute(AbstractTaskListFactory.KEY_QUERY);
									}
									String label = childElement.getAttribute(DelegatingTaskExternalizer.KEY_NAME);
									if (label.length() == 0) { // fallback for legacy
										label = childElement.getAttribute(DelegatingTaskExternalizer.KEY_LABEL);
									}
																		
									AbstractRepositoryQuery query = externalizer.createQuery(repositoryUrl, queryString, label, childElement);
									if (query != null) {
										wasRead = true;
										if (childElement.getAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH) != null && !childElement.getAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH).equals("")) {
											query.setLastSynchronizedStamp(childElement.getAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH));
										}
										taskList.internalAddQuery(query);
									}
									NodeList queryChildren = child.getChildNodes();
									for (int ii = 0; ii < queryChildren.getLength(); ii++) {
										Node queryNode = queryChildren.item(ii);
										try {
											delagatingExternalizer.readQueryHit((Element) queryNode, taskList, query);
										} catch (TaskExternalizationException e) {
											hasCaughtException = true;
										}
									}
									break;
								}
							}
							if (!wasRead) {
								orphanedQueryNodes.add(child);
							}
						}
					} catch (Exception e) {
						handleException(inFile, child, e);
					}
				}

				// bug#173710 - task number incorrect resulting in invalid task
				// list
				// Doing count each time
				int largest = taskList.findLargestTaskHandle();
				taskList.setLastTaskNum(largest);

			}
		} catch (Exception e) {
			handleException(inFile, null, e);
		}
		if (hasCaughtException) {
			// if exception was caught, write out the new task file, so that it
			// doesn't happen again.
			// this is OK, since the original (corrupt) tasklist is saved.
			writeTaskList(taskList, inFile);
		}
	}

	/**
	 * Opens the specified XML file and parses it into a DOM Document.
	 * 
	 * Filename - the name of the file to open Return - the Document built from the XML file Throws - XMLException if
	 * the file cannot be parsed as XML - IOException if the file cannot be opened
	 */
	private Document openAsDOM(File inputFile) throws IOException {

		// A factory API that enables applications to obtain a parser
		// that produces DOM object trees from XML documents
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Using DocumentBuilder, obtain a Document from XML file.
		DocumentBuilder builder = null;
		Document document = null;
		try {
			// create new instance of DocumentBuilder
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			inputFile.renameTo(new File(inputFile.getName() + FILE_SUFFIX_SAVE));
			IOException ioe = new IOException("Failed to load XML file");
			ioe.initCause(pce);
			throw ioe;
		}
		try {
			// Parse the content of the given file as an XML document
			// and return a new DOM Document object. Also throws IOException
			InputStream inputStream = null;
			if (inputFile.getName().endsWith(ITasksUiConstants.DEFAULT_TASK_LIST_FILE)) {
				// is zipped context
				inputStream = new ZipInputStream(new FileInputStream(inputFile));
				((ZipInputStream) inputStream).getNextEntry();
			} else {
				inputStream = new FileInputStream(inputFile);
			}
			document = builder.parse(inputStream);
			// document = builder.parse(inputFile);
		} catch (SAXException se) {
			// TODO: Use TaskListBackupManager to attempt restore from backup
			MessageDialog.openWarning(null, "Mylar task list corrupt",
					"Unable to read the Mylar task list. Please restore from previous backup via File > Import > Mylar Task Data");
		}
		return document;
	}

	private void handleException(File inFile, Node child, Exception e) {
		hasCaughtException = true;
		String name = inFile.getAbsolutePath();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yy-MM-dd-ss");
		name = name.substring(0, name.lastIndexOf('.')) + "-failed-" + sdf.format(date) + ".zip";
		File save = new File(name);
		if (save.exists()) {
			if (!save.delete()) {
				StatusManager.log("Unable to delete old backup tasklist file", this);
				return;
			}
		}
		if (!copy(inFile, save)) {
			inFile.renameTo(new File(name));
		}
		if (child == null) {
			StatusManager.log(e, ITasksUiConstants.MESSAGE_RESTORE);
		} else {
			e.printStackTrace(); // in case logging plug-in has not yet started
			StatusManager.log(e, "Tasks may have been lost from " + child.getNodeName());
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

	// private Document openAsDOM(String input) throws IOException {
	//
	// // A factory API that enables applications to obtain a parser
	// // that produces DOM object trees from XML documents
	// //
	// DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	//
	// // Using DocumentBuilder, obtain a Document from XML file.
	// //
	// DocumentBuilder builder = null;
	// Document document = null;
	// try {
	// // create new instance of DocumentBuilder
	// //
	// builder = factory.newDocumentBuilder();
	// } catch (ParserConfigurationException pce) {
	// MylarStatusHandler.log(pce, "Failed to load XML file");
	// }
	// try {
	// // Parse the content of the given file as an XML document
	// // and return a new DOM Document object. Also throws IOException
	// StringReader s = new StringReader(input);
	// InputSource in = new InputSource(s);
	// document = builder.parse(in);
	// } catch (SAXException se) {
	// MylarStatusHandler.log(se, "Failed to parse XML file");
	// }
	// return document;
	// }

	// public void readTaskList(TaskList taskList, String input) {
	// try {
	// Document doc = openAsDOM(input);
	// if (doc == null) {
	// return;
	// }
	// Element root = doc.getDocumentElement();
	// readVersion = root.getAttribute(ATTRIBUTE_VERSION);
	//
	// if (readVersion.equals(VALUE_VERSION_1_0_0)) {
	// MylarStatusHandler.log("version: " + readVersion + " not supported",
	// this);
	// } else {
	// NodeList list = root.getChildNodes();
	// for (int i = 0; i < list.getLength(); i++) {
	// Node child = list.item(i);
	// boolean wasRead = false;
	// try {
	// if
	// (child.getNodeName().endsWith(DelegatingTaskExternalizer.KEY_CATEGORY)) {
	// // for (ITaskListExternalizer externalizer : externalizers) {
	// // if (externalizer.canReadCategory(child)) {
	// // externalizer.readCategory(child, taskList);
	// // wasRead = true;
	// // break;
	// // }
	// // }
	// if (delagatingExternalizer.canReadCategory(child)) {
	// delagatingExternalizer.readCategory(child, taskList);
	// }
	// } else {
	// for (ITaskListExternalizer externalizer : externalizers) {
	// if (externalizer.canReadTask(child)) {
	// ITask newTask = externalizer.readTask(child, taskList, null, null);
	// // externalizer.getRepositoryClient().addTaskToArchive(newTask);
	// taskList.addTaskToArchive(newTask);
	//									
	// taskList.internalAddRootTask(newTask);
	//
	// wasRead = true;
	// break;
	// }
	// }
	// if (!wasRead && delagatingExternalizer.canReadTask(child)) {
	// taskList.internalAddRootTask(delagatingExternalizer.readTask(child,
	// taskList, null, null));
	// }
	// }
	// } catch (Exception e) {
	// MylarStatusHandler.log(e, "can't read xml string");
	// }
	// }
	// }
	// } catch (Exception e) {
	// MylarStatusHandler.log(e, "can't read xml string");
	// }
	// }

	// public String getTaskListXml(TaskList tlist) {
	// // TODO make this and writeTaskList use the same base code
	// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	// DocumentBuilder db;
	// Document doc = null;
	//
	// try {
	// db = dbf.newDocumentBuilder();
	// doc = db.newDocument();
	// } catch (ParserConfigurationException e) {
	// MylarStatusHandler.log(e, "could not create document");
	// e.printStackTrace();
	// }
	//
	// Element root = doc.createElement(ELEMENT_TASK_LIST);
	// root.setAttribute(ATTRIBUTE_VERSION, VALUE_VERSION);
	//
	// for (ITaskListExternalizer externalizer : externalizers) {
	// externalizer.createRegistry(doc, root);
	// }
	//
	// for (ITaskContainer category : tlist.getCategories()) {
	// Element element = null;
	// for (ITaskListExternalizer externalizer : externalizers) {
	// if (externalizer.canCreateElementFor(category))
	// element = externalizer.createCategoryElement(category, doc, root);
	// }
	// if (element == null &&
	// delagatingExternalizer.canCreateElementFor(category)) {
	// delagatingExternalizer.createCategoryElement(category, doc, root);
	// } else if (element == null) {
	// MylarStatusHandler.log("Did not externalize: " + category, this);
	// }
	// }
	// for (ITask task : tlist.getRootTasks()) {
	// try {
	// Element element = null;
	// for (ITaskListExternalizer externalizer : externalizers) {
	// if (externalizer.canCreateElementFor(task))
	// element = externalizer.createTaskElement(task, doc, root);
	// }
	// if (element == null && delagatingExternalizer.canCreateElementFor(task))
	// {
	// delagatingExternalizer.createTaskElement(task, doc, root);
	// } else if (element == null) {
	// MylarStatusHandler.log("Did not externalize: " + task, this);
	// }
	// } catch (Exception e) {
	// MylarStatusHandler.log(e, e.getMessage());
	// }
	// }
	// doc.appendChild(root);
	// StringWriter sw = new StringWriter();
	//
	// Source source = new DOMSource(doc);
	//
	// Result result = new StreamResult(sw);
	//
	// Transformer xformer = null;
	// try {
	// xformer = TransformerFactory.newInstance().newTransformer();
	// // Transform the XML Source to a Result
	// //
	// xformer.transform(source, result);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return sw.toString();
	// }

	public void setDelegatingExternalizer(DelegatingTaskExternalizer delagatingExternalizer) {
		this.delagatingExternalizer = delagatingExternalizer;
	}

	public List<AbstractTaskListFactory> getExternalizers() {
		return externalizers;
	}
}

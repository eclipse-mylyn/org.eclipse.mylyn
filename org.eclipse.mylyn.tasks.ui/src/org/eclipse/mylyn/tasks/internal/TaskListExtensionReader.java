package org.eclipse.mylar.tasks.internal;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.ITaskHandler;
import org.eclipse.mylar.tasks.ITaskListDynamicSubMenuContributor;
import org.eclipse.mylar.tasks.ITaskListExternalizer;
import org.eclipse.mylar.tasks.ITaskListener;
import org.eclipse.mylar.tasks.MylarTasksPlugin;

public class TaskListExtensionReader {
	private static boolean extensionsRead = false;
	private static TaskListExtensionReader thisReader = new TaskListExtensionReader();
	
	// read the extensions and load the required plugins
	public static void initExtensions(List<ITaskListExternalizer> externalizers, DefaultTaskListExternalizer defaultExternalizer) {
		// code from "contributing to eclipse" with modifications for deprecated code
		if(!extensionsRead){
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry.getExtensionPoint(MylarTasksPlugin.TASK_CONTRIBUTER_EXTENSION_POINT_ID);
			IExtension[] extensions = extensionPoint.getExtensions();
			for(int i = 0; i < extensions.length; i++){
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for(int j = 0; j < elements.length; j++){
					if(elements[j].getName().compareTo(MylarTasksPlugin.TASK_HANDLER_ELEMENT) == 0){
						readTaskHandler(elements[j], externalizers);
					} else if(elements[j].getName().compareTo(MylarTasksPlugin.TASK_LISTENER_ELEMENT) == 0){
						readTaskListener(elements[j]);
					} else if(elements[j].getName().compareTo(MylarTasksPlugin.DYNAMIC_POPUP_ELEMENT) == 0){
						readDynamicPopupContributor(elements[j]);
					}
				}
			}
			defaultExternalizer.setExternalizers(externalizers);
			extensionsRead = true;
		}
	}

	private static void readTaskListener(IConfigurationElement element) {
		try{
			Object taskListener = element.createExecutableExtension(MylarTasksPlugin.TASK_LISTENER_CLASS_ID);
			if (taskListener instanceof ITaskListener) {
				MylarTasksPlugin.getDefault().addTaskListListener((ITaskListener) taskListener);
			} else {
				MylarPlugin.log("Could not load tasklist listener: " + taskListener.getClass().getCanonicalName() + " must implement " + ITaskListener.class.getCanonicalName(), thisReader);	
			}
		} catch (CoreException e){
			MylarPlugin.log(e, "Could not load tasklist listener extension");
		}
	}

	private static void readDynamicPopupContributor(IConfigurationElement element) {
		try{
			Object dynamicPopupContributor = element.createExecutableExtension(MylarTasksPlugin.DYNAMIC_POPUP_CLASS_ID);
			if (dynamicPopupContributor instanceof ITaskListDynamicSubMenuContributor) {
				MylarTasksPlugin.getDefault().addDynamicPopupContributor((ITaskListDynamicSubMenuContributor) dynamicPopupContributor);
			} else {
				MylarPlugin.log("Could not load dyanmic popup menu: " + dynamicPopupContributor.getClass().getCanonicalName() + " must implement " + ITaskListDynamicSubMenuContributor.class.getCanonicalName(), thisReader);	
			}
		} catch (CoreException e){
			MylarPlugin.log(e, "Could not load dynamic popup extension");
		}
	}

	private static void readTaskHandler(IConfigurationElement element, List<ITaskListExternalizer> externalizers) {
		try{
			Object externalizer = element.createExecutableExtension(MylarTasksPlugin.EXTERNALIZER_CLASS_ID);
			if (externalizer instanceof ITaskListExternalizer) {
				externalizers.add((ITaskListExternalizer) externalizer);
			} else {
				MylarPlugin.log("Could not load externalizer: " + externalizer.getClass().getCanonicalName() + " must implement " + ITaskListExternalizer.class.getCanonicalName(), thisReader);	
			}
			
			Object taskHandler = element.createExecutableExtension(MylarTasksPlugin.ACTION_CONTRIBUTER_CLASS_ID);
			if (taskHandler instanceof ITaskHandler) {
				MylarTasksPlugin.getDefault().addTaskHandler((ITaskHandler) taskHandler);
				
			}else {
				MylarPlugin.log("Could not load contributor: " + taskHandler.getClass().getCanonicalName() + " must implement " + ITaskHandler.class.getCanonicalName(), thisReader);	
			}
		} catch (CoreException e){
			MylarPlugin.log(e, "Could not load task handler extension");
		}
	}
}

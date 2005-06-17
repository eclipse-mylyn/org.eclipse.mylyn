package org.eclipse.mylar.tasks.util;

public class RelativePathUtil {
	public static String findRelativePath(String baseDirectory, String filePath) {
		if (filePath.startsWith(baseDirectory)) {
			return filePath.substring(baseDirectory.length(), filePath.lastIndexOf('.'));
		} else {
			StringBuffer result = new StringBuffer(filePath.length());
			String[] rootFolders = baseDirectory.split("/");
			String[] pathFolders = filePath.split("/");
			int diff = 0;
			for (int i = 0; i < pathFolders.length; i++) {
				if (!rootFolders[i].equals(pathFolders[i])) {
					diff = i;
					while (i < rootFolders.length) {
						result.append('.');
						result.append('.');
						result.append('/');
						i++;
					}
					while(diff < pathFolders.length - 1){
						result.append(pathFolders[diff]);
						diff++;				
					}					
					result.append(pathFolders[diff].substring(0, pathFolders[diff].lastIndexOf('.')));
				}
			}
			return result.toString();
		}		
	}
}

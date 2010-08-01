package org.eclipse.mylyn.reviews.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewPackage;

public class ReviewDataStore {

	private String storeRootDir;

	public ReviewDataStore(String storeRootDir) {
		this.storeRootDir = storeRootDir;
	}

	public void storeReviewData(String repositoryUrl, String taskId,
			Review review, String id) {
		try {
			
			File file = getFile(repositoryUrl, taskId);
			createDirectoriesIfNecessary(file);
			if (!file.exists()) {
				file.createNewFile();
				ZipOutputStream outputStream = new ZipOutputStream(
						new FileOutputStream(file));
				ResourceSet resourceSet = new ResourceSetImpl();

				Resource resource = resourceSet.createResource(URI
						.createFileURI("")); //$NON-NLS-1$

				resource.getContents().add(review);
				resource.getContents().add(review.getScope().get(0));
				if (review.getResult() != null)
					resource.getContents().add(review.getResult());

				outputStream.putNextEntry(new ZipEntry(id));
				resource.save(outputStream, null);
				outputStream.closeEntry();
				outputStream.close();
			} else {
				// TODO append

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void createDirectoriesIfNecessary(File file) {
		File parent = file.getParentFile();
		if(!parent.exists()) {
			parent.mkdirs();
		}
	}

	public List<Review> loadReviewData(String repositoryUrl, String taskId) {
		List<Review> reviews = new ArrayList<Review>();
		try {

			File file = getFile(repositoryUrl, taskId);
			ZipInputStream inputStream = new ZipInputStream(
					new FileInputStream(file));
			inputStream.getNextEntry();
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getPackageRegistry().put(ReviewPackage.eNS_URI,
					ReviewPackage.eINSTANCE);
			Resource resource = resourceSet.createResource(URI.createURI(""));
			resource.load(inputStream, null);
			for (EObject item : resource.getContents()) {
				if (item instanceof Review) {
					Review review = (Review) item;
					reviews.add(review);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return reviews;
	}

	private File getFile(String repositoryUrl, String taskId) {
		File path = new File(storeRootDir + File.separator + "reviews"
				+ File.separator + URLEncoder.encode(repositoryUrl)
				+ File.separator);
		return new File(path, taskId);
	}
}

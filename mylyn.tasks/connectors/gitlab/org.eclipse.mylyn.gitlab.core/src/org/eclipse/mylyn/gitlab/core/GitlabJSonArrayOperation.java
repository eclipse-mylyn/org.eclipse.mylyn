package org.eclipse.mylyn.gitlab.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;

import com.google.gson.JsonArray;

public abstract class GitlabJSonArrayOperation extends GitlabOperation<JsonArray> {

	public GitlabJSonArrayOperation(CommonHttpClient client, String urlSuffix) {
		super(client, urlSuffix);
	}
	@Override
	protected JsonArray execute(IOperationMonitor monitor) throws IOException, GitlabException {
		JsonArray result = null;
		HttpRequestBase request = createHttpRequestBase();
		addHttpRequestEntities(request);
		CommonHttpResponse response = execute(request, monitor);
		result = processAndRelease(response, monitor);
		Header[] lh = response.getResponse().getHeaders("Link");
		if (lh.length > 0) {
//			System.out.print("Page Act: ");
//			System.out.println(response.getResponse().getHeaders("X-Page")[0].getValue());
//			System.out.print("Page Count: ");
//			System.out.println(response.getResponse().getHeaders("X-Total-Pages")[0].getValue());
			Header lh1 = lh[0];
			for (String lh2 : lh1.getValue().split(", ")) {
				String[] lh3 = lh2.split("; ");
//				System.out.print(lh3[1]);
//				System.out.print("  ");
//				System.out.println(lh3[0]);
				if ("rel=\"next\"".equals(lh3[1])) {
//					System.out.println("process "+lh3[0].substring(1, lh3[0].length()-1));
					HttpRequestBase looprequest = new HttpGet(lh3[0].substring(1, lh3[0].length()-1));
					addHttpRequestEntities(looprequest);
					CommonHttpResponse loopresponse = execute(looprequest, monitor);
					JsonArray loopresult = processAndRelease(loopresponse, monitor);
					result.addAll(loopresult);
				break;
				}
			}
		}		
		
		return result;
	}
}

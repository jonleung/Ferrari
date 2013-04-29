package edu.upenn.cis455.bigfs;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.util.StringUtils;

public class Page {

	String NEWLINE_DELIMITER = "<newLineDelimeter/>";

	String documentId;
	String url;
	String headers;
	String html;
	List<String> urls;

	public static Page parseDocLine(String line) {
		String docLineRegex = "<deemegjonraj type=\"documentId\">(.*?)</deemegjonraj><deemegjonraj type=\"url\">(.*?)</deemegjonraj><deemegjonraj type=\"headers\">(.*?)</deemegjonraj><deemegjonraj type=\"html\">(.*?)</deemegjonraj>";
		Pattern p = Pattern.compile(docLineRegex);
		Matcher m = p.matcher(line);
		if (m != null) {
			m.find();
			if (m.matches()) {				
				Page page = new Page();
				page.documentId = m.group(1);
				page.url = m.group(2);
				page.headers = parseNewlinedHeaders(m.group(3));
				page.html = m.group(4);

				return page;
			}
			else {
				throw new RuntimeException(String.format("Invalid Bigfile Line: %s", line));
			}
		}
		else {
			throw new RuntimeException(String.format("Invalid Bigfile Line: %s", line));
		}
	}

	public static String parseNewlinedHeaders(String headers) {
		return headers.replaceAll("<newLineDelimeter/>", "\n");
	}

	public Page() {}

	private String encodeComponent(String type, String value) {
		return String.format("<deemegjonraj type=\"%s\">%s</deemegjonraj>", type, value);
	}

	public boolean valid() {
		return this.url != null &&
				this.documentId != null &&
				this.headers != null && 
				this.html != null &&
				this.urls != null;
	}

	public String getDocEncoding() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(encodeComponent("documentId", this.documentId));
		stringBuilder.append(encodeComponent("url", this.url));
		stringBuilder.append(encodeComponent("headers", this.headers));
		stringBuilder.append(encodeComponent("html", this.html));
		stringBuilder.append("\n");

		return stringBuilder.toString();
	}

	public String getUrlEncoding() {
		StringBuilder stringBuilder = new StringBuilder();

		for (String url : urls) {
			if(url != null) {
				stringBuilder.append(this.url + " ");
			}
			stringBuilder.append(url + "\n");

		}		
		return stringBuilder.toString();
	}

	// Get / Set
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHeaders() {
		return headers;
	}
	public void setHeaders(String headers) {
		this.headers = headers.replace("\n", NEWLINE_DELIMITER).replace("\r", "");
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html.replace("\n", "").replace("\r", "");
	}
	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public String toString() {
		return String.format("<documentId: %s, url: %s, headers: %s, html: %s>", this.documentId, this.url, this.headers, this.html);
	}

	public static void main(String[] args) {
//    String aLine = "<deemegjonraj type=\"documentId\">55b0096f913f500a36b74457f1a7a93182bbd9b384b9e6546daa05af493cd269</deemegjonraj><deemegjonraj type=\"url\">http://helloworld.com</deemegjonraj><deemegjonraj type=\"headers\">header1:value1<newLineDelimeter/>header2:value2<newLineDelimeter/>header1:value1<newLineDelimeter/>header2:value2</deemegjonraj><deemegjonraj type=\"html\"><h1>Hello World </h1></deemegjonraj>";
//    Page page = Page.parseDocLine(aLine);
//    System.out.println(page);
		
//		Page page = new Page();
//		page.setDocumentId("55b0096f913f500a36b74457f1a7a93182bbd9b384b9e6546daa05af493cd269");
//		page.setHeaders("header1:value");
//		page.setUrl("http://jonl.org");
//		page.setHtml("")
		
		
		
	}


}
package edu.upenn.cis455.indexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Testing 
{

	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("/home/csdeepthi/Downloads/smallfile.txt"));
		String value = br.readLine();
		Page page = Page.parseDocLine(value);
		System.out.println("url: " + page.getUrl());
		String html = page.getHtml();
		String url = page.getUrl();
		Document doc = Jsoup.parse(html, url);
		String text = doc.text();
		System.out.println("text: " + text);
		String[] words = text.split(" ");
		System.out.println("words.length: " + words.length);
	}
}

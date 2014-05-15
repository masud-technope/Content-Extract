package htmlview;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CustomizePageContent {
	
	static String styleTag="<style type=\"text/css\">code, pre, blockquote{background-color:#efefef; padding:2px;} body{text-align:left;} </style>";
	
	public static Document customizerContent(Document doc, Element modifiedBody)
	{
		//changing the body
		doc.body().html(modifiedBody.html());
		//changing head
		doc.head().children().remove();
		doc.head().html(styleTag);
		return doc;
	}

}

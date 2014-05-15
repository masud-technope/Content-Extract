package misc;

import japa.parser.ast.Comment;

import java.util.List;

import ofey.DOMNode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.sun.org.apache.xerces.internal.dom.ChildNode;

import utility.ContentLoader;
import core.StaticData;

public class MyJsoupTest {
	
	static int tagcount=0;

	static void recordElement(Element elem)
	{
		DOMNode node=new DOMNode();
		node.tagName=elem.tagName();
		Elements elems=elem.getAllElements();
		Elements linkElems=elems.select("a");
		System.out.println(elem.tagName()+" Total tags:"+elems.size()+" Link elems:"+linkElems.size());
		for(Element el:elems){System.out.println(el.tagName());}
	}
	
	static void discoverNode(Element elem)
	{
		Elements children=elem.children();
		for(Element elem2:children){
			discoverNode(elem2);
		}
		recordElement(elem);
	}
	
	public static void main(String[] args){
	    //String codeFile=StaticData.CE_Data_Home+"/ccontext/2.txt";
	    String codeFile="./data/mypage.html";
		String codeStr=ContentLoader.loadFileContent(codeFile);
		Document document=Jsoup.parse(codeStr);
		for(Element e:document.getAllElements()){
			for(Node n:e.childNodes()){
//				if(n instanceof Comment){
//					System.out.println(n);
//				}
			}
		}
	    
		/*String pageHTML="<html><head></head><body><div><p>this is a paragrph</p><code>this is a code</code></div></body></html>";
		Document doc=Jsoup.parse(pageHTML);
		Element elem=doc.select("p").get(0);
		String str=elem.attr("iscontent");
		System.out.println(str);*/
		
		
	}
}

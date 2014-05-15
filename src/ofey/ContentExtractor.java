package ofey;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ContentExtractor {
	
	Element body;
	HashMap<Element, DOMNode> nodeMap;
	ArrayList<String> deleteTags;
	ArrayList<Integer> filterChildIndices;
	double thresholdTD;
	double thresholdCTD;
	Document localDoc=null;
	
	public ContentExtractor(Element body, HashMap<Element, DOMNode> nodeMap, double thresholdTD)
	{
		//initialization
		this.body=body;
		this.nodeMap=nodeMap;
		this.thresholdTD=thresholdTD;
		this.deleteTags = new ArrayList<>();
		this.filterChildIndices=new ArrayList<>();
		this.populateDeleteTagList();
	}
	public ContentExtractor(Element body,double thresholdCTD,HashMap<Element, DOMNode> nodeMap)
	{
		//initialization
		this.body=body;
		this.nodeMap=nodeMap;
		this.thresholdCTD=thresholdCTD;
		this.deleteTags = new ArrayList<>();
		this.filterChildIndices=new ArrayList<>();
		this.populateDeleteTagList();
	}
	
	protected void populateDeleteTagList() {
		//this.deleteTags.add("a");
		this.deleteTags.add("script");
		this.deleteTags.add("style");
		this.deleteTags.add("noscript");
		this.deleteTags.add("comment");
	}
	
	protected Element discoverSubTree(Element subTree) {
		// discover the subtree
		try {
			String compDensityStr = subTree.attr("compdensity").trim();
			if (compDensityStr.isEmpty()) {
			} else {
				double compDensity = Double.parseDouble(compDensityStr);
				if (compDensity >= this.thresholdCTD) {
					Element T = getMaximumCTDSumElement(subTree);
					// System.out.println(T.tagName());
					T = markAsContent(T);
					for (Element elem : subTree.children()) {
						elem = discoverSubTree(elem);
					}
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return subTree;
	}
	
	protected Element markAsContent(Element elem) {
		// marking as content node
		elem.attr("iscontent", "1");
		return elem;
	}
	
	protected Element cascadeMarkContent(Element elem) {
		// cascading the mark content
		Elements children = elem.children();
		for (Element child : children) {
			cascadeMarkContent(child);
		}
		// marking parent and children as content
		String iscontentStr=elem.attr("iscontent").trim();
		if(!iscontentStr.isEmpty()){
			int iscontent=Integer.parseInt(iscontentStr);
			if(iscontent==1){
				markAsContent(elem.parent());
				for (Element elem2 : elem.children()) {
					elem2=markAsContent(elem2);
				}
			}
		}
		return elem;
	}
	
	
	
	protected Element getMaximumTDSumElement(Element root) {
		// code for maximum sum element
		Elements elems = root.getAllElements();
		double maxTDSum = 0;
		Element maxTDSumElem = elems.first();
		for (Element elem : elems) {
			DOMNode dnode = this.nodeMap.get(elem);
			if (dnode.textDensitySum > maxTDSum) {
				// collecting the maximum valued tag
				maxTDSum = dnode.textDensitySum;
				maxTDSumElem = elem;
			}
		}
		// returning the max scored element
		return maxTDSumElem;
	}
	
	protected Element getMaximumCTDSumElement(Element root) {
		// code for maximum sum element
		Elements elems = root.getAllElements();
		double maxCTDSum = 0;
		Element maxCTDSumElem = elems.first();
		for (Element elem : elems) {
			try {
				double densum = Double.parseDouble(elem.attr("compdensum"));
				if (densum > maxCTDSum) {
					// collecting the maximum valued tag
					maxCTDSum = densum;
					maxCTDSumElem = elem;
				}
			} catch (Exception e2) {
			}
		}
		// returning the max scored element
		return maxCTDSumElem;
	}
	
	public Element refineDOMTree() {
		// code for refining the DOM tree
		Elements children = this.body.children();
		for (int i=0;i<children.size();i++) {
			Element element=children.get(i);
			if (this.deleteTags.contains(element.tagName())) {
				filterChildIndices.add(i);
				continue;
			}
			String compDensityStr=element.attr("compdensity").trim();
			if(compDensityStr.isEmpty()){
				filterChildIndices.add(i);
				//this.localDoc.html(this.localDoc.html().replaceFirst(element.html(), ""));
				continue;
			}else{
				double compdensity=Double.parseDouble(compDensityStr);
				if(compdensity>=this.thresholdCTD){
					element=discoverSubTree(element);
					//cascading content mark
					element=cascadeMarkContent(element);
					//refine subtree
					element=cleanSubTree(element);
				}else{
					filterChildIndices.add(i);
				}
			}
		}
		
		//now cleaning the DOM tree 
		Element modifiedBody=deleteChildren(this.body);
		//refine the DOM tree
		//Element refinedBody=trimPageBody(modifiedBody);
		return modifiedBody;
	}
	
	protected Element cleanSubTree(Element subTree) {
		// code for cleaning the sub tree
		Elements elems = subTree.children();
		ArrayList<Element> temp = new ArrayList<>(elems);
		for (Element elem : temp) {
			cleanSubTree(elem);
			// now deal with leaf node
			String iscontentStr=elem.attr("iscontent").trim();
			if(iscontentStr.isEmpty()){
				elems.remove(elem);
			}else{
				//do nothing
			}
		}
		if (temp.size() > 0) {
			subTree.html(elems.toString());
		}
		return subTree;
	}
	
	protected Element deleteChildren(Element body)
	{
		//code for deleting the noise blocks
		//System.out.println(this.filterChildIndices);
		Elements children=body.children();
		System.out.println("Delete them:"+ this.filterChildIndices);
		ArrayList<Element> temp=new ArrayList<>(children);
		try{
		for(Integer index:this.filterChildIndices){
			Element elem=temp.get(index);
			children.remove(elem);
		}}catch(Exception e){
			e.printStackTrace();
		}
		body.html(children.toString());
		return body;
	}
	
	
	protected void printSubTree(Element subTree) {
		Elements children = subTree.children();
		for (Element elem : children) {
			printSubTree(elem);
		}
		try {
			DOMNode dnode = this.nodeMap.get(subTree);
			if (dnode.isContentNode) {
				System.out.println(subTree.text());
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void showContentTags()
	{
		//code for showing the content tags
		for(Element elem:this.nodeMap.keySet()){
			DOMNode dnode=this.nodeMap.get(elem);
			if(dnode.isContentNode){
				System.out.println(elem.text());
			}
		}
	}
	
	public void showRefinedDocument()
	{
		//to be added
	}
}

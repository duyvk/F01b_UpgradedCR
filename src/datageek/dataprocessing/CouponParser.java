package datageek.dataprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class CouponParser {
	public void getAllItems(String fileName) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			File file  = new File (fileName);
			if(file.exists()){
				Document doc = db.parse(file);
				Element docEle = doc.getDocumentElement();

				//Print root element of the document
				System.out.println("Root element of the document: "
						+ docEle.getNodeName());

				NodeList itemList = docEle.getElementsByTagName("item");
				System.out.println("Total items: " + itemList.getLength());

				List <Item> items = new ArrayList<Item>();
				for(int i =0; i < itemList.getLength();i++){
					Item tempItemObject = new Item();
					Element tmpItemEle= (Element) itemList.item(i);

					NodeList deal = tmpItemEle.getElementsByTagName("deal");
					NodeList merchant = tmpItemEle.getElementsByTagName("merchant");

					addAttribute(deal.item(0).getChildNodes(), tempItemObject.deal);
					addAttribute(merchant.item(0).getChildNodes(), tempItemObject.merchant);

					items.add(tempItemObject);
					System.out.println("Item N0:" + items.size() );
					tempItemObject.printItem();

				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addAttribute(NodeList nl, Map<String,String> list){
		for(int i =0; i < nl.getLength();i++){
			Node temp = nl.item(i);
			String key ="", value = "";
			//System.out.println(temp.getNodeName());
			key = temp.getNodeName();
			if(temp.getChildNodes().getLength()==0)
				//System.out.println("N/A");
				value = "N/A";
			else
				//System.out.println(temp.getChildNodes().item(0).getNodeValue());
				value = temp.getChildNodes().item(0).getNodeValue();
			//System.out.println("------------");
			list.put(key,value);
		}
	}

	public static String parseValue (Node node){
		StringBuffer text = new StringBuffer();
		NodeList nodeChildren = node.getChildNodes();
		String value;
		Node tmpNode;

		for (int i = 0; i < nodeChildren.getLength();i++){
			tmpNode = nodeChildren.item(i);
			value = tmpNode.getNodeValue();
			if(value!= null){
				text.append(value);
			}
		}
		return text.toString();
	}



	public static void main (String []args){
		String fileName = "data/input/coupon.xml";
		CouponParser parser = new CouponParser();
		parser.getAllItems(fileName);

	}
}

class Item {
	public Map<String, String> deal = new HashMap<String, String>();
	public Map<String, String> merchant = new HashMap<String, String>();
	public void printItem(){
		printMap("deal",deal);
		printMap("merchant",merchant);
		System.out.println("-----------------------------------------");
	}

	public void printMap(String name,Map<String, String> mp){
		System.out.println("----"+name);
		//Get Map in Set interface to get key and value
        Set s=mp.entrySet();

        //Move next key and value of Map by iterator
        Iterator it=s.iterator();

        while(it.hasNext())
        {
            // key=value separator this by Map.Entry to get key and value
            Map.Entry m =(Map.Entry)it.next();

            // getKey is used to get key of Map
            String key=(String)m.getKey();

            // getValue is used to get value of key in Map
            String value=(String)m.getValue();

            System.out.println("--------"+key+" : "+value);
        }
	}

}

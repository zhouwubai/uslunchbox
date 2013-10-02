package edu.fiu.cs.tomcatcollector.output;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class XMLEventToJSONEvent {
	
	public static JSONObject toJSONObject(Element elem) {
		try {
			JSONObject obj = new JSONObject();
			for (Object attrObj : elem.attributes()) {
				Attribute attr = (Attribute)attrObj;
				obj.put(attr.getName(), attr.getValue());
			}
			for (Object child: elem.elements()) {
				Element childElem = (Element)child;
				JSONObject childObj = new JSONObject();
				if (childElem.attributeCount() > 0) { // has attribute
					childObj.put(childElem.getName(), toJSONObject(childElem));
					obj.put(childElem.getName(), childObj);
				}
				else { // has no attribute
					if (childElem.elements().size() == 0) { // no sub element
						obj.put(childElem.getName(), childElem.getTextTrim());
					}
					else { // has sub element						
						// all sub elements have the same name
						String firstSubElementName = ((Element)childElem.elements().get(0)).getName();
						if (childElem.elements(firstSubElementName).size() == childElem.elements().size()) {
							obj.put(childElem.getName(), toJSONArray(childElem));
						}
						else {
							childObj.put(childElem.getName(), toJSONObject(childElem));
							obj.put(childElem.getName(), childObj);
						}
					}
				}
				
			}
			return obj;		
		}catch(JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static JSONArray toJSONArray(Element elem) throws JSONException {
		JSONArray childArray = new JSONArray();
		for (Object child: elem.elements()) {
			Element childElem = (Element)child;
			JSONObject childObj = new JSONObject();
			if (childElem.attributeCount() > 0) { // has attribute
				childObj.put(childElem.getName(), toJSONObject(childElem));
				childArray.put(childObj);
			}
			else { // has no attribute
				if (childElem.elements().size() == 0) { // has no sub element
					childArray.put(childElem.getTextTrim());
				} else { // has sub element			
					// all sub elements have the same name
					String firstSubElementName = ((Element) childElem.elements().get(0)).getName();
					if (childElem.elements(firstSubElementName).size() == childElem.elements().size()) {
						childArray.put(toJSONArray(childElem));
					} else {
						childObj.put(childElem.getName(),toJSONObject(childElem));
						childArray.put(childObj);
					}
				}
			}
		}
		return childArray;
	}

}

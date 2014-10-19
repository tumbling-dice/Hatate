package inujini_.hatate.scraping;

import inujini_.hatate.scraping.Scraper.AttributeFilter;
import inujini_.hatate.scraping.Scraper.XAttribute;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.val;

import org.xmlpull.v1.XmlPullParser;

public final class XmlPullParserExtensions {

	public static List<XAttribute> getAttributes(XmlPullParser parser) {
		val attrs = new ArrayList<XAttribute>();
		for(int i = 0, attrCount = parser.getAttributeCount(); i < attrCount; i++) {
			attrs.add(createAttribute(parser, i));
		}

		return attrs;
	}

	public static boolean hasAttribute(XmlPullParser parser, String namespace, @NonNull String name, String value) {
		for(int i = 0, attrCount = parser.getAttributeCount(); i < attrCount; i++) {
			if(name.equals(parser.getAttributeName(i))
				&& (value == null || value.equals(parser.getAttributeValue(i)))
				&& (namespace == null || namespace.equals(parser.getAttributePrefix(i)))) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasAttribute(XmlPullParser parser, String name, String value) {
		return hasAttribute(parser, null, name, value);
	}

	public static boolean hasAttribute(XmlPullParser parser, String name) {
		return hasAttribute(parser, null, name, null);
	}

	public static boolean hasAttribute(XmlPullParser parser, AttributeFilter attributeFilter) {

		for(int i = 0, attrCount = parser.getAttributeCount(); i < attrCount; i++) {
			if(attributeFilter.filter(createAttribute(parser, i))) return true;
		}

		return false;
	}

	private static XAttribute createAttribute(XmlPullParser parser, int index) {
		val attr = new XAttribute();
		attr.setNamespace(parser.getAttributePrefix(index));
		attr.setName(parser.getAttributeName(index));
		attr.setValue(parser.getAttributeValue(index));

		return attr;
	}
//
//	private static XAttribute createAttribute(String namespace, String name, String value) {
//		val attr = new XAttribute();
//		attr.setNamespace(namespace);
//		attr.setName(name);
//		attr.setValue(value);
//
//		return attr;
//	}

}
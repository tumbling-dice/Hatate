/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.scraping;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.val;

import org.xmlpull.v1.XmlPullParserException;

public interface Scraper extends Closeable {

	/* inner objects */
	@Data
	public static class XElement {
		private String namespace;
		private String tagName;
		private List<XAttribute> attributes;
		private String text;
		private List<XElement> innerElements;

		public String getAttributeValue(@NonNull String attributeName) {
			if(attributes == null) return null;

			for(val attr : attributes) {
				if(attributeName.equals(attr.getName())) {
					return attr.getValue();
				}
			}

			return null;
		}

		public XElement findInnerElement(String tagName) {
			return findInnerElement(null, tagName, null);
		}

		public XElement findInnerElement(String namespace, String tagName) {
			return findInnerElement(namespace, tagName, null);
		}

		public XElement findInnerElement(String namespace, @NonNull String tagName, AttributeFilter attributeFilter) {

			for(val e : this.innerElements) {
				if(!tagName.equals(e.getTagName())
					|| (namespace != null && !namespace.equals(e.getNamespace()))) {

					val tmp = seek(e, namespace, tagName, attributeFilter);
					if(tmp != null) return tmp;
					continue;
				}

				if(attributeFilter != null) {
					boolean isHit = false;
					for(val attr : e.getAttributes()) {
						if(attributeFilter.filter(attr)) {
							isHit = true;
							break;
						}
					}

					if(!isHit) {
						val tmp = seek(e, namespace, tagName, attributeFilter);
						if(tmp != null) return tmp;
						continue;
					}
				}

				return e;
			}

			return null;
		}

		private static XElement seek(XElement element, String namespace, String tagName, AttributeFilter attributeFilter) {
			val elements = element.getInnerElements();
			if(elements == null) return null;

			for(val e : elements) {
				val tmp = e.findInnerElement(namespace, tagName, attributeFilter);
				if(tmp != null) return tmp;
			}

			return null;
		}

	}

	@Data
	public static class XAttribute {
		private String namespace;
		private String name;
		private String value;
	}

	public interface AttributeFilter {
		boolean filter(XAttribute attribute);
	}

	/* methods */
	List<XElement> extract(String namespace, String tagName, AttributeFilter attributeFilter) throws XmlPullParserException, IOException;
	XElement specify(String namespace, String tagName, AttributeFilter attributeFilter) throws XmlPullParserException, IOException;

}
/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.scraping;

import inujini_.hatate.scraping.Scraper.AttributeFilter;
import inujini_.hatate.scraping.Scraper.XAttribute;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.val;

import org.xmlpull.v1.XmlPullParser;

/**
 * {@link XmlPullParser}の拡張メソッド群.
 */
public final class XmlPullParserExtensions {

	/**
	 * 現イベントに含まれている全Attributeの取得.
	 * @param parser
	 * @return 全Attributeのリスト
	 */
	public static List<XAttribute> getAttributes(XmlPullParser parser) {
		val attrs = new ArrayList<XAttribute>();
		for(int i = 0, attrCount = parser.getAttributeCount(); i < attrCount; i++) {
			attrs.add(createAttribute(parser, i));
		}

		return attrs;
	}

	/**
	 * 現イベントに指定されたAttributeが存在するかどうかをチェックする.
	 * @param parser
	 * @param namespace 名前空間（nullの場合はチェックしない）
	 * @param name 属性名
	 * @param value 属性の値（nullの場合はチェックしない）
	 * @return 現イベント内にnamespace、name、valueのすべてが一致するAttributeが存在すればtrue.
	 */
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

	/**
	 * 現イベントに指定されたAttributeが存在するかどうかをチェックする.
	 * @param parser
	 * @param name 属性名
	 * @param value 属性の値（nullの場合はチェックしない）
	 * @return 現イベント内にname、valueのすべてが一致するAttributeが存在すればtrue.
	 * @see #hasAttribute(XmlPullParser, String, String, String)
	 */
	public static boolean hasAttribute(XmlPullParser parser, String name, String value) {
		return hasAttribute(parser, null, name, value);
	}

	/**
	 * 現イベントに指定されたAttributeが存在するかどうかをチェックする.
	 * @param parser
	 * @param name 属性名
	 * @return 現イベント内にnameが一致するAttributeが存在すればtrue.
	 * @see #hasAttribute(XmlPullParser, String, String, String)
	 */
	public static boolean hasAttribute(XmlPullParser parser, String name) {
		return hasAttribute(parser, null, name, null);
	}

	/**
	 * 現イベントに指定されたAttributeが存在するかどうかをチェックする.
	 * @param parser
	 * @param attributeFilter
	 * @return 現イベントに含まれているAttributeからattributeFilterでtrueが返されればtrue.
	 */
	public static boolean hasAttribute(XmlPullParser parser, AttributeFilter attributeFilter) {

		for(int i = 0, attrCount = parser.getAttributeCount(); i < attrCount; i++) {
			if(attributeFilter.filter(createAttribute(parser, i)))
				return true;
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

}
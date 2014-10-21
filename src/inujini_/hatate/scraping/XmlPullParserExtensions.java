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
 * {@link XmlPullParser}�̊g�����\�b�h�Q.
 */
public final class XmlPullParserExtensions {

	/**
	 * ���C�x���g�Ɋ܂܂�Ă���SAttribute�̎擾.
	 * 
	 * @param parser
	 * @return �SAttribute�̃��X�g
	 */
	public static List<XAttribute> getAttributes(XmlPullParser parser) {
		val attrs = new ArrayList<XAttribute>();
		for(int i = 0, attrCount = parser.getAttributeCount(); i < attrCount; i++) {
			attrs.add(createAttribute(parser, i));
		}

		return attrs;
	}

	/**
	 * ���C�x���g�Ɏw�肳�ꂽAttribute�����݂��邩�ǂ������`�F�b�N����.
	 * 
	 * @param parser
	 * @param namespace ���O��ԁinull�̏ꍇ�̓`�F�b�N���Ȃ��j
	 * @param name ������
	 * @param value �����̒l�inull�̏ꍇ�̓`�F�b�N���Ȃ��j
	 * @return ���C�x���g����namespace�Aname�Avalue�̂��ׂĂ���v����Attribute�����݂����true.
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
	 * ���C�x���g�Ɏw�肳�ꂽAttribute�����݂��邩�ǂ������`�F�b�N����.
	 * 
	 * @param parser
	 * @param name ������
	 * @param value �����̒l�inull�̏ꍇ�̓`�F�b�N���Ȃ��j
	 * @return ���C�x���g����name�Avalue�̂��ׂĂ���v����Attribute�����݂����true.
	 * @see #hasAttribute(XmlPullParser, String, String, String)
	 */
	public static boolean hasAttribute(XmlPullParser parser, String name, String value) {
		return hasAttribute(parser, null, name, value);
	}

	/**
	 * ���C�x���g�Ɏw�肳�ꂽAttribute�����݂��邩�ǂ������`�F�b�N����.
	 * 
	 * @param parser
	 * @param name ������
	 * @return ���C�x���g����name����v����Attribute�����݂����true.
	 * @see #hasAttribute(XmlPullParser, String, String, String)
	 */
	public static boolean hasAttribute(XmlPullParser parser, String name) {
		return hasAttribute(parser, null, name, null);
	}

	/**
	 * ���C�x���g�Ɏw�肳�ꂽAttribute�����݂��邩�ǂ������`�F�b�N����.
	 * 
	 * @param parser
	 * @param attributeFilter
	 * @return ���C�x���g�Ɋ܂܂�Ă���Attribute����attributeFilter��true���Ԃ�����true.
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
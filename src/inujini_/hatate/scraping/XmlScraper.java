/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.scraping;

import java.io.InputStreamReader;
import java.io.Reader;

import lombok.val;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlScraper extends ScraperImpl {

	public XmlScraper(InputStreamReader source) throws XmlPullParserException {
		super(source);
	}

	@Override
	protected XmlPullParser createParser(String source, XmlPullParserFactory factory) throws XmlPullParserException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected XmlPullParser createParser(Reader source, XmlPullParserFactory factory) throws XmlPullParserException {
		factory.setNamespaceAware(true);

		val parser = factory.newPullParser();
		parser.setInput(source);
		return parser;
	}


}
package inujini_.hatate.scraping;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.val;
import lombok.experimental.ExtensionMethod;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

@ExtensionMethod({XmlPullParserExtensions.class})
public abstract class ScraperImpl implements Scraper {

	private XmlPullParser _parser;
	private String _source;
	private Reader _sourceReader;

	public ScraperImpl(String source) throws XmlPullParserException {
		_source = source;
		_parser = createParser(source, XmlPullParserFactory.newInstance());
	}

	public ScraperImpl(Reader source) throws XmlPullParserException {
		_sourceReader = source;
		_parser = createParser(source, XmlPullParserFactory.newInstance());
	}

	protected abstract XmlPullParser createParser(String source, XmlPullParserFactory factory)
			throws XmlPullParserException;
	protected abstract XmlPullParser createParser(Reader source, XmlPullParserFactory factory)
			throws XmlPullParserException;

	public List<XElement> extract(String tagName) throws XmlPullParserException, IOException {
		return extract(null, tagName, null);
	}

	public List<XElement> extract(String tagName, AttributeFilter attributeFilter)
			throws XmlPullParserException, IOException {
		return extract(null, tagName, attributeFilter);
	}

	@Override
	public List<XElement> extract(String namespace, @NonNull String tagName, AttributeFilter attributeFilter)
			throws XmlPullParserException, IOException {
		if(_parser == null) {
			if(_source != null) {
				createParser(_source, XmlPullParserFactory.newInstance());
			} else {
				createParser(_sourceReader, XmlPullParserFactory.newInstance());
			}
		}

		int ev = _parser.getEventType();
		val elements = new ArrayList<XElement>();

		while (ev != XmlPullParser.END_DOCUMENT) {

			switch(ev) {
			case XmlPullParser.START_TAG:
				if(tagName.equals(_parser.getName())
					&& (namespace == null || namespace.equals(_parser.getPrefix()))
					&& (attributeFilter == null || _parser.hasAttribute(attributeFilter))) {

					elements.add(createElement(_parser));
				}

				break;
			}

			ev = _parser.next();
		}

		return elements;
	}

	public XElement specify(String tagName, AttributeFilter attributeFilter)
			throws XmlPullParserException, IOException {
		return specify(null, tagName, attributeFilter);
	}

	@Override
	public XElement specify(String namespace, @NonNull String tagName, @NonNull AttributeFilter attributeFilter)
			throws XmlPullParserException, IOException {

		if(_parser == null) {
			if(_source != null) {
				createParser(_source, XmlPullParserFactory.newInstance());
			} else {
				createParser(_sourceReader, XmlPullParserFactory.newInstance());
			}
		}

		int ev = _parser.getEventType();

		while(ev != XmlPullParser.END_DOCUMENT) {
			switch(ev) {
			case XmlPullParser.START_TAG:
				if(tagName.equals(_parser.getName())
					&& (namespace == null || namespace.equals(_parser.getPrefix()))
					&& _parser.hasAttribute(attributeFilter)) {

					val element = createElement(_parser);
					close();
					return element;
				}

				break;
			}

			ev = _parser.next();
		}

		return null;
	}

	protected static XElement createElement(XmlPullParser parser) throws XmlPullParserException, IOException {

		int ev = parser.getEventType();

		val element = new XElement();
		val innerElements = new ArrayList<XElement>();
		boolean isInner = false;

		while(ev != XmlPullParser.END_TAG && ev != XmlPullParser.END_DOCUMENT) {

			switch(ev) {
			case XmlPullParser.START_TAG:
				if(isInner) {
					innerElements.add(createElement(parser));
					break;
				}

				element.setNamespace(parser.getPrefix());
				element.setTagName(parser.getName());
				element.setAttributes(parser.getAttributes());
				isInner = true;

				break;
			case XmlPullParser.TEXT:
				element.setText(parser.getText().trim());
				break;
			}

			ev = parser.next();
		}

		element.setInnerElements(innerElements);

		return element;
	}

	@Override
	public void close() throws IOException {
		_parser = null;
		if(_sourceReader != null) _sourceReader.close();
	}

}

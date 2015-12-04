package com.adam4.misc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TestXML
{

	public static void main(String[] args) throws SAXException, IOException,
			ParserConfigurationException
	{

		String xml = "<xml><entry><key>test1</key><value>32</value><timestamp>2015-01-01 11:12:13.999</timestamp></entry></xml>";

		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
		SAXParser parser = parserFactor.newSAXParser();
		SaxHandler handler = new SaxHandler();

		parser.parse(new ByteArrayInputStream(xml.getBytes()), handler);

	}

}

class SaxHandler extends DefaultHandler
{
	String swtch = "error";
	StringBuilder store = new StringBuilder();
	String key = "unset";
	int value = -1;
	Timestamp t = new Timestamp(new Date().getTime());
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		swtch = localName;
		System.out.println("startElement " + localName);	
	}
	
	@Override
	public void characters(char[] chars, int i, int i1) throws SAXException
	{
		switch (swtch)
		{
		case "key":
		case "value":
		case "timestamp":
			store.append(chars);
			break;
		case "entry":
			break;
		default:
			break;
		}

	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		//System.out.println("endElement " + localName);	
		switch (localName)
		{
		case "key":
			key = store.toString();
			System.out.println("key: " + key);
			break;
		case "value":
		//	value = Integer.parseInt(store.toString());
			value = 42;
			System.out.println("value: " + value);
			break;
		case "timestamp":
			break;
		default:
			break;
		}
	}
	
}

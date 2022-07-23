package com.mobile.finalproject.ma01_20190981;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;


public class TrackingInfoXmlParser {

    public enum TagType { NONE, TITLE, SPATIAL, DESCRIPTION, ALTERNATIVE_TITLE };

    final static String TAG_ITEM = "item";
    final static String TAG_TITLE = "title";
    final static String TAG_SPATIAL = "spatial";
    final static String TAG_DESCRIPTION = "description";
    final static String TAG_ALTERNATIVE_TITLE = "alternativeTitle";

    public TrackingInfoXmlParser() {
    }

    public ArrayList<TrackingInfo> parse(String xml) {

        ArrayList<TrackingInfo> resultList = new ArrayList();
        TrackingInfo dto = null;

        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            dto = new TrackingInfo();
                        } else if (parser.getName().equals(TAG_TITLE)) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals(TAG_SPATIAL)) {
                            if (dto != null) tagType = TagType.SPATIAL;
                        } else if (parser.getName().equals(TAG_DESCRIPTION)) {
                            if (dto != null) tagType = TagType.DESCRIPTION;
                        } else if (parser.getName().equals(TAG_ALTERNATIVE_TITLE)) {
                            if (dto != null) tagType = TagType.ALTERNATIVE_TITLE;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case SPATIAL:
                                dto.setSpatial(parser.getText());
                                break;
                            case DESCRIPTION:
                                    if(parser.getText() != null)
                                        dto.setDescription(parser.getText());
                                    else
                                        dto.setDescription("설명 없음");
                                break;
                            case ALTERNATIVE_TITLE:
                                if(parser.getText() != null)
                                    dto.setAlternativeTitle(parser.getText());
                                else
                                    dto.setDescription("설명 없음");
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}

package com.tourismwebsite.factory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * @Author j
 * @Date 2023/8/24 14:55
 * @Version 1.0
 */
public class BaseFactory {
    public static Object getInterface(String id){
        InputStream resourceAsStream = BaseFactory.class.getClassLoader().getResourceAsStream("application.xml");
        SAXReader reader = new SAXReader();

        try {
            Document read = reader.read(resourceAsStream);
            Element element = (Element) read.selectSingleNode("//context[@id='" + id + "']");
            String referenceName = element.getTextTrim();
            Class<?> aClass = Class.forName(referenceName);
            return  aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());

        }
    }
}

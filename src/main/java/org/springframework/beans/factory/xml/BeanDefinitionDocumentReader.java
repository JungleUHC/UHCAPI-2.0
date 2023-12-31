package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.w3c.dom.Document;

public interface BeanDefinitionDocumentReader {
  void registerBeanDefinitions(Document paramDocument, XmlReaderContext paramXmlReaderContext) throws BeanDefinitionStoreException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/xml/BeanDefinitionDocumentReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
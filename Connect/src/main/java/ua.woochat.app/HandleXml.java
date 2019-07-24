package ua.woochat.app;

import org.apache.log4j.Logger;

import javax.xml.bind.*;
import java.io.*;

public class HandleXml {

    private final static Logger logger = Logger.getLogger(HandleXml.class);

    /**
     * Method marshalling instance of the class to XML file
     * @param marshalClass class for marshalling
     * @param user user for saving
     * @param stream stream for writing the object
     */
    public static void marshalling(Class marshalClass, Object user, FileOutputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(marshalClass);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(user, stream);
        } catch (PropertyException e) {
            logger.error("Error was encountered while setting a property on marshaller", e);
        } catch (JAXBException e) {
            logger.error("JAXB exceptions", e);
        }
    }

    /**
     * Method marshalling instance of the class to XML string
     * @param marshClass class for marshalling
     * @param user user for saving
     * @return writer.toString()
     */
    public static String marshallingWriter(Class marshClass, Object user) {
        StringWriter writer = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(marshClass);
            Marshaller marshaller = context.createMarshaller();
            //writer = new StringWriter();
            marshaller.marshal(user, writer);
        } catch (PropertyException e) {
            logger.error("Error was encountered while setting a property on marshaller", e);
        } catch (JAXBException e) {
            logger.error("JAXB exceptions", e);
        }
        return writer.toString();
    }

    /**
     * Method unMarshalling XML string to instance of the class Message
     * @param str text for unmarshalling
     * @return instance of the class Message
     */
    public static Message unMarshallingMessage(String str) {
        Message message = null;
        try {
            StringReader reader = new StringReader(str);
            JAXBContext context = JAXBContext.newInstance(Message.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            message = (Message) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            logger.error("JAXB exceptions", e);
        }
        return message;
    }

    /**
     * Method unMarshalling XML file to instance of the class UserAndGroups
     * @param file file with object for unmarshalling
     * @param unMarshalClass class for unmarshalling
     * @return instance of the class UserAndGroups
     */
    public static UsersAndGroups unMarshalling(File file, Class unMarshalClass) {
        UsersAndGroups user = null;
        try {
            JAXBContext context = JAXBContext.newInstance(unMarshalClass);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            user = (UsersAndGroups) unmarshaller.unmarshal(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("File not found exceptions", e);
        } catch (JAXBException e) {
            logger.error("JAXB exceptions", e);
        }
        System.out.println(user.toString());
        return user;
    }
}

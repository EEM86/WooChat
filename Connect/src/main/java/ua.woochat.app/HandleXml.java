package ua.woochat.app;

import javax.xml.bind.*;
import java.io.*;

public class HandleXml {

    public static void marshalling(Class marshalClass, Object user, FileOutputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(marshalClass);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(user, stream);
        } catch (PropertyException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static String marshallingWriter(Class marshClass, Object user) {
        StringWriter writer = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(marshClass);
            Marshaller marshaller = context.createMarshaller();
            //writer = new StringWriter();
            marshaller.marshal(user, writer);
        } catch (PropertyException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public static Message unMarshallingMessage(String str) throws JAXBException {
        StringReader reader = new StringReader(str);
        JAXBContext context = JAXBContext.newInstance(Message.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        Message message = (Message) unmarshaller.unmarshal(reader);
        return message;
    }

    public static UsersAndGroups unMarshalling(File file, Class unMarshalClas) {
        UsersAndGroups user = null;
        try {
            JAXBContext context = JAXBContext.newInstance(unMarshalClas);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            user = (UsersAndGroups) unmarshaller.unmarshal(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        System.out.println(user.toString());
        return user;
    }
}

package ua.woochat.server.model;

import ua.woochat.app.Message;

import javax.xml.bind.*;
import java.io.*;

public class JaxbXml {

    public void marshalling(Class marshClass, Object user, FileOutputStream stream) {
        try {
            //создание объекта Marshaller, который выполняет сериализацию
            JAXBContext context = JAXBContext.newInstance(marshClass);
            Marshaller marshaller = context.createMarshaller();
            //marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // сама сериализация
            marshaller.marshal(user, stream);
        } catch (PropertyException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public Message unMarshallingMessage (String str) throws JAXBException {
        StringReader reader = new StringReader(str);
        JAXBContext context = JAXBContext.newInstance(Message.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        Message message = (Message) unmarshaller.unmarshal(reader);
        return message;
    }

    public User unMarshalling(File file) {
        User user = null;
        try {
            JAXBContext context = JAXBContext.newInstance(User.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            user = (User) unmarshaller.unmarshal(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return user;

    }
}

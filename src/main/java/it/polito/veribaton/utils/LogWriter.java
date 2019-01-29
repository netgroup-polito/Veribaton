package it.polito.veribaton.utils;

import com.google.gson.Gson;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogWriter {
    public static void logJson(Object o, String path){
        try {
            Gson gson = new Gson();
            String nfvJson = gson.toJson(o);
            File outJson = new File(path);
            FileOutputStream os = new FileOutputStream(outJson);
            os.write(nfvJson.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logXml(Object o, String path){
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(o, new File(path));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}

package HoloBackend;


import java.io.*;
import java.util.HashMap;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

public class XMLReader {

    public Map<String, String> ReadPatientDetail(File file)
    {
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            String patientId = doc.getElementsByTagName("patientId").item(0).getTextContent();
            String revisionNumber = doc.getElementsByTagName("revisionNumber").item(0).getTextContent();
            String lastEditedBy = doc.getElementsByTagName("lastEditedBy").item(0).getTextContent();
            String timeOfLastEdit = doc.getElementsByTagName("timeOfLastEdit").item(0).getTextContent();
            String gender = doc.getElementsByTagName("gender").item(0).getTextContent();
            String firstName = doc.getElementsByTagName("firstName").item(0).getTextContent();
            String lastName = doc.getElementsByTagName("lastName").item(0).getTextContent();
            String DateOfBirth = doc.getElementsByTagName("DateOfBirth").item(0).getTextContent();
            String age = doc.getElementsByTagName("age").item(0).getTextContent();
            Map<String,String> patient_map = new HashMap<String,String>();
            patient_map.put("patientId",patientId);
            patient_map.put("lastEditedBy",lastEditedBy);
            patient_map.put("revisionNumber",revisionNumber);
            patient_map.put("timeOfLastEdit",timeOfLastEdit);
            patient_map.put("gender",gender);
            patient_map.put("firstName",firstName);
            patient_map.put("lasrName",lastName);
            patient_map.put("DateOfBirth",DateOfBirth);
            patient_map.put("age",age);
            return patient_map;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public ArrayList<Map<String, String>> ReadMeshes(File file)
    {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            Node meshes_node = doc.getElementsByTagName("meshes").item(0);
            NodeList nodeList = ((Element) meshes_node).getElementsByTagName("mesh"); // store each mesh


            ArrayList<Map<String,String>> mesh_collection = new ArrayList<Map<String,String>>();
            for (int j = 0 ; j<= nodeList.getLength()-1 ;j++)
            {
                Map<String,String> mesh_map = new HashMap<String, String>();
                mesh_map.put("meshName",((Element)nodeList.item(j)).getElementsByTagName("meshName").item(0).getTextContent());
                mesh_map.put("fileType",((Element)nodeList.item(j)).getElementsByTagName("fileType").item(0).getTextContent());
                mesh_map.put("author",((Element)nodeList.item(j)).getElementsByTagName("author").item(0).getTextContent());
                mesh_map.put("timeOfAddition",((Element)nodeList.item(j)).getElementsByTagName("timeOfAddition").item(0).getTextContent());
                mesh_map.put("revisionNumber",((Element)nodeList.item(j)).getElementsByTagName("revisionNumber").item(0).getTextContent());
                mesh_map.put("rawData",((Element)nodeList.item(j)).getElementsByTagName("rawData").item(0).getTextContent());
                mesh_collection.add(mesh_map);
            }
            return mesh_collection;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Map<String, String>> ReadAnnotations(File file)
    {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            Node annotations_node = doc.getElementsByTagName("annotations").item(0);
            NodeList nodeList = ((Element) annotations_node).getElementsByTagName("annotation"); // store each mesh


            ArrayList<Map<String,String>> mesh_collection = new ArrayList<Map<String,String>>();
            for (int j = 0 ; j<= nodeList.getLength()-1 ;j++)
            {
                Map<String,String> annotation_map = new HashMap<String, String>();
                annotation_map.put("author",((Element)nodeList.item(j)).getElementsByTagName("author").item(0).getTextContent());
                annotation_map.put("timeOfEdit",((Element)nodeList.item(j)).getElementsByTagName("timeOfEdit").item(0).getTextContent());
                annotation_map.put("revisionNumber",((Element)nodeList.item(j)).getElementsByTagName("revisionNumber").item(0).getTextContent());
                annotation_map.put("annotationData",((Element)nodeList.item(j)).getElementsByTagName("annotationData").item(0).getTextContent());
                mesh_collection.add(annotation_map);
            }
            return mesh_collection;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }





}

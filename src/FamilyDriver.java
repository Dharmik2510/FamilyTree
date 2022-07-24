import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FamilyDriver {
    public static Connection connection;
    private static final String findIfRelationsExists = "SELECT * FROM relation_type";
    private static FamilyTreeManagement familyTreeManagement;
    private  static  MediaArchiveManagement mediaArchiveManagement;
    private static  ReporterManagement reporterManagement;
;
    public static void main(String[] args) {

        try {

            checkForRelationData(connection);
            executeFamily();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void executeFamily() throws Exception {

        // Test cases //



        //TODO: adding data to perosn as well as relationship table

        PersonIdentity p0 = familyTreeManagement.addPerson("A");
        PersonIdentity p1 = familyTreeManagement.addPerson("B");
        PersonIdentity p2 = familyTreeManagement.addPerson("C");
        PersonIdentity p3 = familyTreeManagement.addPerson("D");
        PersonIdentity p4 = familyTreeManagement.addPerson("E");
        PersonIdentity p5 = familyTreeManagement.addPerson("F");
        PersonIdentity p6 = familyTreeManagement.addPerson("G");
        PersonIdentity p7 = familyTreeManagement.addPerson("H");

        familyTreeManagement.recordPartnering(p3, p4);
        familyTreeManagement.recordChild(p0, p1);
        familyTreeManagement.recordChild(p0, p2);
        familyTreeManagement.recordChild(p1, p3);
        familyTreeManagement.recordChild(p2, p4);
        familyTreeManagement. recordChild(p3, p5);
        familyTreeManagement.recordChild(p3, p6);
        familyTreeManagement.recordChild(p4, p5);
        familyTreeManagement.recordChild(p4, p6);
        familyTreeManagement.recordDissolution(p6, p7);

        /*-----------------------------------------------------------------------------*/

        //TODO: adding attributes to the person

        HashMap<String,String> attributes = new HashMap<>();
        attributes.put("birth_location","sanand");
        attributes.put("death_location","");
        attributes.put("birth_day","25");
        attributes.put("birth_month","10");
        attributes.put("birth_year","1998");
        attributes.put("death_day","");
        attributes.put("death_month","");
        attributes.put("death_year","");
        attributes.put("gender","male");
        attributes.put("occupation","student");
        familyTreeManagement.recordAttributes(new PersonIdentity(1),attributes);

        HashMap<String,String> attr = new HashMap<>();
        attr.put("birth_location","ahmedabad");
        attr.put("death_location","");
        attr.put("birth_day","24");
        attr.put("birth_month","08");
        attr.put("birth_year","1999");
        attr.put("death_day","");
        attr.put("death_month","");
        attr.put("death_year","");
        attr.put("gender","female");
        attr.put("occupation","student");
        familyTreeManagement.recordAttributes(new PersonIdentity(2),attr);


/*------------------------------------------------------------------------------------------------*/

        //TODO: RECORDING NOTES AND REFRENCES
        familyTreeManagement.recordNote(new PersonIdentity(1),"note1");
        familyTreeManagement.recordReference(new PersonIdentity(1),"reference1");
        familyTreeManagement.recordNote(new PersonIdentity(2),"note2");
        familyTreeManagement.recordNote(new PersonIdentity(3),"note1");
        familyTreeManagement.recordReference(new PersonIdentity(3),"reference1");


        /*---------------------------------------ancestores --------------------------------*/

        //TODO: Finding anscestors

        Set<PersonIdentity> personIdentitySet =reporterManagement.ancestores(new PersonIdentity(6),3);
        if(personIdentitySet==null)
        {
            System.out.println("No anscestors found:");
        }
        else
        {
            for(PersonIdentity personIdentity :personIdentitySet)
            {
                System.out.println("anscestor is:" + personIdentity.getPerson_id() );
            }

        }

        /*---------------------------------descendents--------------------------------------*/


        //TODO: Finding descendants

        Set<PersonIdentity> descendents =reporterManagement.descendents(new PersonIdentity(1),3);
        if(descendents==null)
        {
            System.out.println("No descendents found:");
        }
        else
        {
            for(PersonIdentity personIdentity :descendents)
            {
                System.out.println("descendent is:" + personIdentity.getPerson_id() );
            }

        }

        /*--------------------------------------------- Relationship ---------------------------*/

        //TODO:find Relation

             reporterManagement.findRelation(new PersonIdentity(1),new PersonIdentity(3));



        /*--------------------------------- Adding Media Data ------------------------------------- */


        //TODO: Adding media data

         FileIdentifier fileIdentifier = mediaArchiveManagement.addMediaFile("test1");
         HashMap<String,String> mediaAattr = new HashMap<>();
            mediaAattr.put("media_file_name","File_1");
            mediaAattr.put("date_last_updated","2005");
            mediaAattr.put("media_location","halifax");
            mediaArchiveManagement.recordMediaAttributes(fileIdentifier,mediaAattr);

        FileIdentifier fileIdentifier1 = mediaArchiveManagement.addMediaFile("test2");
        HashMap<String,String> mediaAattr1 = new HashMap<>();
        mediaAattr1.put("media_file_name","File_2");
        mediaAattr1.put("date_last_updated","2005-05");
        mediaAattr1.put("media_location","halifax");
        mediaArchiveManagement.recordMediaAttributes(fileIdentifier1,mediaAattr1);

        /*-------------------------------------------- tagMedia() -----------------------------------------------------------------*/

            //TODO: Adding tags to media

        mediaArchiveManagement.tagMedia(new FileIdentifier(1),"first tag");
        mediaArchiveManagement.tagMedia(new FileIdentifier(1),"second tag");
        mediaArchiveManagement.tagMedia(new FileIdentifier(1),"third tag");
        mediaArchiveManagement.tagMedia(new FileIdentifier(2),"fourth tag");
        mediaArchiveManagement.tagMedia(new FileIdentifier(2),"fifth tag");
        mediaArchiveManagement.tagMedia(new FileIdentifier(2),"sixth tag");


        /*----------------------------------------------peopleInMedia()------------------------------------------------------------*/

        //TODO:add person in the media

        List<PersonIdentity> persons = new ArrayList<>();
        persons.add(new PersonIdentity(1));
        persons.add(new PersonIdentity(1));
        persons.add(new PersonIdentity(2));
        System.out.println(mediaArchiveManagement.peopleInMedia(new FileIdentifier(1),persons));

/*--------------------------------------------------------------------------------------------------------------------------*/

        //TODO: findname()
        System.out.println(reporterManagement.findName(new PersonIdentity(32)));
        PersonIdentity personIdentity = reporterManagement.findPerson("dhairya");


        //TODO:findMediaFile()
        FileIdentifier fileIdentifier_1 =reporterManagement.findMediaFile("File_1");
        System.out.println(fileIdentifier_1.getMedia_file_id());

        //TODO:findMediaFile()
        System.out.println(reporterManagement.findMediaFile(new FileIdentifier(1)));

        //TODO: notesAndReferences()
        List<String> noteAndRef = reporterManagement.notesAndReferences(new PersonIdentity(1));
       if(noteAndRef==null)
       {
           System.out.println("No data found");
       }
       else
       {
            for(String value :noteAndRef)
            {
                System.out.println(value);
            }
       }




    //TODO: findMediaByTag()

       Set<FileIdentifier> fileIdentifierSet = reporterManagement.findMediaByTag("first tag","","2005-05-01");
        for (FileIdentifier fileIdentifier_2: fileIdentifierSet) {
            System.out.println(fileIdentifier_2.getMedia_file_id());
        }

        Set<FileIdentifier> fileIdentifierSet1 = reporterManagement.findMediaByTag("first tag",null,null);
        for (FileIdentifier fileIdentifier2: fileIdentifierSet1) {
            System.out.println(fileIdentifier2.getMedia_file_id());
        }

        //TODO: findMediaByLocation()
        Set<FileIdentifier> fileIdentifierSet3 = reporterManagement.findMediaByLocation("halifax",null,"2005-08-25");
        if(fileIdentifierSet3!=null)
        {
            for (FileIdentifier fileIdentifier4: fileIdentifierSet3) {
                System.out.println(fileIdentifier4.getMedia_file_id());
            }

        }
        else
        {
            System.out.println("No data found: ");
        }


        //TODO peopleInMedia()

      List<PersonIdentity> personIdentities = new ArrayList<>();
        personIdentities.add(new PersonIdentity(1));
        personIdentities.add(new PersonIdentity(2));
        personIdentities.add(new PersonIdentity(3));
        personIdentities.add(new PersonIdentity(4));
        mediaArchiveManagement.peopleInMedia(new FileIdentifier(1),personIdentities);

        //TODO: findIndividualInMedia()
        Set<PersonIdentity> personIdentity_set= new HashSet<>();
        personIdentity_set.add(new PersonIdentity(1));
        personIdentity_set.add(new PersonIdentity(2));
        List<FileIdentifier> fileIdentifierList =reporterManagement.findIndividualsMedia(personIdentity_set,"2005-01-01",null);
        for(FileIdentifier file:fileIdentifierList)
        {
            System.out.println(file.getMedia_file_id());
        }

        //TODO: findBiologicalMedia()

       List<FileIdentifier> biologicalList =reporterManagement.findBiologicalFamilyMedia(new PersonIdentity(1));
        if(biologicalList==null)
        {
            System.out.println("No such file found");
        }
        else
        {
            for(FileIdentifier fileIdentifier_3:fileIdentifierList)
            {
                System.out.println(fileIdentifier_3.getMedia_file_id());
            }
        }



    }

    private static void checkForRelationData(Connection connection) throws SQLException {
        connection = DatabaseConnector.getConnection();
        familyTreeManagement = new FamilyTreeManagement(connection);
        mediaArchiveManagement = new MediaArchiveManagement(connection);
        reporterManagement = new ReporterManagement(connection);
        ResultSet relation_set;
        PreparedStatement relation_statement = connection.prepareStatement(findIfRelationsExists);
        relation_set = relation_statement.executeQuery(findIfRelationsExists);
        while (!relation_set.next()) {
            PopulateRelationshipTable.insertIntoRelationType();
            return;
        }
    }
}

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



public class ReporterManagement extends AbstractReporterManagement
{
    //connection to database
    Connection connection;

    //set that will store the each person
    Set<PersonIdentity> personIdentities;

    //set that will store each media files
    Set<FileIdentifier> fileIdentifiers;

    //List that will store notes and references of the given person
    List<String> notesAndReferenceList;

    //List that will store each media files
    List<FileIdentifier> fileIdentifierList;


    ReporterManagement(Connection connection)
    {
        this.connection = connection;
    }

    /**
     * This method will find the id of the person with the given name if present in the database
     * @param
     * name - name of the person to be found
     * @return
     * PersonIdentity - id of the person from database
     *
     */
    @Override
    PersonIdentity findPerson(String name) throws Exception {
        int count =0;
        int id = 0;
        if(name==null || name.isEmpty())
        {
            System.out.println("Please provide valid name: ");
            return null;
        }
        PreparedStatement find_person_statement = connection.prepareStatement(QueryCollector.check_if_person_name_exist);
        find_person_statement.setString(1,name);
        ResultSet resultSet = find_person_statement.executeQuery();
        while (resultSet.next())
        {
            count++;
            if(count>1)
            {
                throw new Exception("Found more than one person with the same name in the database");
            }
            else
            {
                 id = resultSet.getInt(1);
            }

        }
        return new PersonIdentity(id);
    }

    /**
     * This method will find the id of the file with the given name if present in the database
     * @param
     * name - name of the file to be found
     * @return
     * FileIdentifier - id of the given file from database
     *
     */
    @Override
    FileIdentifier findMediaFile(String name) throws SQLException {
        if(name==null || name.isEmpty())
        {
            System.out.println("Please provide valid name: ");
            return null;
        }
        PreparedStatement find_media_file_statement = connection.prepareStatement(QueryCollector.find_media_file_using_name);
        find_media_file_statement.setString(1,name);
        ResultSet resultSet = find_media_file_statement.executeQuery();
        while (resultSet.next())
        {
            int id = resultSet.getInt(1);
            System.out.println("file id: " +id);
            return new FileIdentifier(id);
        }
        return null;
    }


    /**
     * This method will find the name of the person with the given person_id present in the database
     * @param
     * id - id(PersonIdentity) of the person to be found
     * @return
     * String - name of the person from database
     *
     */
    @Override
    String findName(PersonIdentity id) throws SQLException {
        if(id==null || id.getPerson_id()<=0)
        {
            return null;
        }
        PreparedStatement check_if_exist_statement = this.connection.prepareStatement(QueryCollector.check_if_person_exist);
        check_if_exist_statement.setInt(1, id.getPerson_id());
        ResultSet check_if_exist_result = check_if_exist_statement.executeQuery();

        while (check_if_exist_result.next()) {
            String name = check_if_exist_result.getString(2);
            return name;
        }
        return null;

    }


    /**
     * This method will find the name of the file with the given id if present in the database
     * @param
     * fileId - id(FileIdentifier) of the file to be found
     * @return
     * String - name of the given file from database
     *
     */
    @Override
    String findMediaFile(FileIdentifier fileId) throws SQLException {
        if(fileId==null || fileId.getMedia_file_id()<=0)
        {
            return null;
        }

        PreparedStatement found_media_statement = connection.prepareStatement(QueryCollector.find_media_file_using_id);
        found_media_statement.setInt(1, fileId.getMedia_file_id());
        ResultSet resultSet = found_media_statement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString(2);
            return name;
        }
        return "";

    }


    /**
     * This method will find the ancestores of the given person passed as an argument
     * @param
     * person - person whose ancestores to be found
     * @param
     * generations - no of generations upto which ancestores to be found
     * @return
     * Set of the personIdenity objects containing ancestores of the given person
     */

    @Override
    Set<PersonIdentity> ancestores(PersonIdentity person, Integer generations) throws SQLException {

        if(person==null || generations==null || generations<0 || person.getPerson_id()<=0 || foundInParentChild(person, "anscestor"))
        {
            return null;
        }
        personIdentities = new TreeSet<>(new Comparator<PersonIdentity>() {
            @Override
            public int compare(PersonIdentity o1, PersonIdentity o2) {
                return o1.getPerson_id()-o2.getPerson_id();
            }
        });
        PreparedStatement anscestor_statement = connection.prepareStatement(QueryCollector.find_anscestors);
        anscestor_statement.setInt(1,person.getPerson_id());
        anscestor_statement.setInt(2,generations);
        ResultSet resultSet = anscestor_statement.executeQuery();
        while (resultSet.next())
        {
            personIdentities.add(new PersonIdentity(resultSet.getInt(1)));
        }
        return personIdentities;
    }

    /**
     * This method will find the descendents of the given person passed as an argument
     * @param
     * person - person whose descendents to be found
     * @param
     * generations - no of generations upto which descendents to be found
     * @return
     * Set of the personIdenity objects containing descendents of the given person
     */
    @Override
    Set<PersonIdentity> descendents(PersonIdentity person, Integer generations) throws SQLException {

        if(person==null || generations==null || generations<0 || person.getPerson_id()<=0 || foundInParentChild(person, "descendent"))
        {
            return null;
        }
        personIdentities = new TreeSet<>(new Comparator<PersonIdentity>() {
            @Override
            public int compare(PersonIdentity o1, PersonIdentity o2) {
                return o2.getPerson_id()-o1.getPerson_id();
            }
        });
        PreparedStatement descendent_statement = connection.prepareStatement(QueryCollector.find_descendent);
        descendent_statement.setInt(1,person.getPerson_id());
        descendent_statement.setInt(2,generations);
        ResultSet resultSet = descendent_statement.executeQuery();
        while (resultSet.next())
        {
            personIdentities.add(new PersonIdentity(resultSet.getInt(1)));
        }
        resultSet.close();
        return personIdentities;


    }

    /**
     *  This method will find the relation between two persons passed as an argument
     * @param
     * person1 - first person
     * @param
     * person2 - second person
     * @return
     * BiologicalRelation - Will return the object of the BiologicalRelation class that will contain the cousinshp
     * and degree of removal
     */
    @Override
    BiologicalRelation findRelation(PersonIdentity person1, PersonIdentity person2) throws SQLException {
        if (person1 == null || person2 == null || person1.getPerson_id() <= 0 && person2.getPerson_id() < 0) {
            System.out.println("illegal values of person passed");
            return null;
        }
        PreparedStatement relation_statement = connection.prepareStatement(QueryCollector.find_relation);
        relation_statement.setInt(1, person1.getPerson_id());
        relation_statement.setInt(2, person2.getPerson_id());
        ResultSet resultSet = relation_statement.executeQuery();
        while (resultSet.next())
        {
                System.out.println("Degree of Cousinship is: " + resultSet.getInt(1));
                 System.out.println("Degree of removal is: " + resultSet.getInt(2));
                return new BiologicalRelation(resultSet.getInt(1),resultSet.getInt(2));
        }
        return null;
    }

    /**
     *This method will give all the notes and references associated with the given person passed as an argument
     * @param
     * person - person whose notes and references to be found
     * @return
     * List<String> containing all notes and references in insertion order
     */
    @Override
    List<String> notesAndReferences(PersonIdentity person) throws SQLException {
        //creating the notes and reference list
        notesAndReferenceList = new ArrayList<>();
        if(person==null || person.getPerson_id()<=0 || findName(person)==null)
        {
            return null;
        }
        //preparing statement for execution of the query
        PreparedStatement notes_ref_statement = connection.prepareStatement(QueryCollector.find_notes_reference);
        //setting the field
        notes_ref_statement.setInt(1,person.getPerson_id());
        ResultSet resultSet = notes_ref_statement.executeQuery();

        //loop to check if you have resut
        while (resultSet.next())
        {
            notesAndReferenceList.add(resultSet.getString(1));
        }
        return notesAndReferenceList;
    }

    /**
     *This method will give all the file_ids of for which given tag associated and in the given date range
     * @param
     * tag - tag for which files to be found
     * @param
     * startDate - starting date from where to start searching
     * @param
     * endDate -ending date till where to search
     * @return
     * Set<FileIdentifier> containing files fall in the given range which has the tag passed as an argument.
     */
    @Override
    Set<FileIdentifier> findMediaByTag(String tag, String startDate, String endDate) throws SQLException {

        //check for base condition
        if(tag==null || tag.isEmpty())
        {
            return null;
        }
        return findMedia("tag",tag,startDate,endDate);


    }


    /**
     *This method will give all the file_ids of for which given location associated and in the given date range
     * @param
     * location - location for which files to be found
     * @param
     * startDate - starting date from where to start searching
     * @param
     * endDate -ending date till where to search
     * @return
     * Set<FileIdentifier> containing files fall in the given range which has the location passed as an argument.
     */
    @Override
    Set<FileIdentifier> findMediaByLocation(String location, String startDate, String endDate) throws SQLException
    {
        //check for base condition
        if(location==null || location.isEmpty())
        {
            return null;
        }
       return  findMedia("location",location,startDate,endDate);
    }


    /**
     *This method will find all the files for the given set of people in the passed date range
     * @param
     * people - set of people whose files need to be found
     * @param
     * startDate - starting date
     * @param
     * endDate - ending date
     * @return
     * List<FileIdentifier> - list containing all files associated with these set of people
     */
    @Override
    List<FileIdentifier> findIndividualsMedia(Set<PersonIdentity> people, String startDate, String endDate) throws SQLException {
       //checking base condition
        if(people==null || people.isEmpty())
        {
            return null;
        }
        fileIdentifierList = new ArrayList<>();
        StringBuilder query =new StringBuilder("SELECT media_data.media_file_id from media_data inner join person_in_media\n" +
                "on media_data.media_file_id = person_in_media.media_file_id\n" +
                "WHERE person_in_media.person_id in (");

        //dynamically building the query
        for(PersonIdentity personIdentity :people)
        {
            query.append(personIdentity.getPerson_id()).append(",");
        }

        //Removing comma from the end
        String q =query.substring(0,query.length()-1) + ")";

            String sDate =DateFormatter.getFormattedDate(startDate);
            String eDate = DateFormatter.getFormattedDate(endDate);
            //checking what start and end dates are passed
            if(sDate==null && eDate==null)
            {
                q +="and\n(media_data.date_last_updated >= " + null + " or " + null +" is null)\n" +
                        "and (media_data.date_last_updated <= " + null+ " or "+ null + " is null )\n" +
                        "order by  date_last_updated desc, media_data.media_file_name";
            }
            else if(sDate==null)
            {
                q +="and\n(media_data.date_last_updated >=" + null + " or " + null +" is null)\n" +
                        "and (media_data.date_last_updated <= \"" + eDate+ "\" or \""+ eDate + "\" is null )\n" +
                        "order by  date_last_updated desc, media_data.media_file_name";
            }
            else if(eDate==null)
            {

                q +="and\n(media_data.date_last_updated >= \"" + sDate + "\" or \"" + sDate +"\" is null)\n" +
                        "and (media_data.date_last_updated <= " + null+ " or "+ null + " is null )\n" +
                        "order by  date_last_updated desc, media_data.media_file_name";
            }
            else
            {
                q +="and\n(media_data.date_last_updated >= \"" + sDate + "\" or \"" + sDate +"\" is null)\n" +
                        "and (media_data.date_last_updated <=\"" + eDate + "\" or \""+ eDate + "\" is null )\n" +
                        "order by  date_last_updated desc, media_data.media_file_name";
            }

            //preparing statement for execution of query
        PreparedStatement find_individual_statement = connection.prepareStatement(q);
        ResultSet  resultSet = find_individual_statement.executeQuery();
        while (resultSet.next())
        {
            fileIdentifierList.add(new FileIdentifier(resultSet.getInt(1)));
        }
        return fileIdentifierList;
    }


    /**
     * This method will find the set of media files that include the given person's immediate children
     * @param
     * person - for whose immediate children's files need to be found
     * @return
     * List<FileIdentifier> - list containing all files associated with the immediate children of the given passed person
     */
    @Override
    List<FileIdentifier> findBiologicalFamilyMedia(PersonIdentity person) throws SQLException {
       //checking base condition
        if(person==null || person.getPerson_id()<=0)
        {
            return null;
        }
        //Initializing the list
        fileIdentifierList = new ArrayList<>();
        //preparing the statement
        PreparedStatement find_biological_family_media_statement = connection.prepareStatement(QueryCollector.find_biological_family_media);
        find_biological_family_media_statement.setInt(1,person.getPerson_id());
        ResultSet resultSet = find_biological_family_media_statement.executeQuery();
        while (resultSet.next())
        {
            fileIdentifierList.add(new FileIdentifier(resultSet.getInt(1)));
        }
        return fileIdentifierList;
    }


    //Common method for ancestores and descendents to check if the given person passed
    // as an argument present in the parent_child table or not
    //To improve the performance
    private boolean foundInParentChild(PersonIdentity person,String type) throws SQLException {
        PreparedStatement found_in_pc_statement = null;
        switch (type)
        {
            case "anscestor":
                found_in_pc_statement = connection.prepareStatement("select * from relationship where person_id_1 =? and relationship_code = 1");
                found_in_pc_statement.setInt(1,person.getPerson_id());
                break;
            case "descendent":
                found_in_pc_statement = connection.prepareStatement("select * from relationship where person_id_2 =? and relationship_code = 1");
                found_in_pc_statement.setInt(1,person.getPerson_id());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        ResultSet resultSet = found_in_pc_statement.executeQuery();
        while (resultSet.next())
        {
            return false;
        }
        return true;
    }


    private Set<FileIdentifier> findMedia(String type,String content, String startDate, String endDate) throws SQLException
    {
        fileIdentifiers = new HashSet<>();
        PreparedStatement find_media_by_statement;
        if(type.equals("tag"))
        {
            find_media_by_statement = connection.prepareStatement(QueryCollector.find_media_by_tag);
        }
        else
        {
           find_media_by_statement = connection.prepareStatement(QueryCollector.find_media_by_location);
        }

        String sDate = DateFormatter.getFormattedDate(startDate);
        String eDate = DateFormatter.getFormattedDate(endDate);
        find_media_by_statement.setString(1,content);
        find_media_by_statement.setString(2,sDate);
        find_media_by_statement.setString(3,sDate);
        find_media_by_statement.setString(4,eDate);
        find_media_by_statement.setString(5,eDate);
        ResultSet resultSet = find_media_by_statement.executeQuery();
        while (resultSet.next())
        {
            fileIdentifiers.add(new FileIdentifier(resultSet.getInt(1)));
        }
        return fileIdentifiers;
    }
}

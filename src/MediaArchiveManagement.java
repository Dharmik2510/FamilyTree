import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MediaArchiveManagement extends AbstractMediaArchiveManagement {


    private Connection connection;
    //boolean to maintain the checks
    public static boolean check = false;

    MediaArchiveManagement(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method will add the fileLocation in the database
     *
     * @param fileLocation - given unique fileLocation
     * @return FileIdentifier - unique media_file_id associated with the file location.
     */
    @Override
    FileIdentifier addMediaFile(String fileLocation) throws SQLException {
        //checking for base condition
        if (fileLocation == null || fileLocation.isEmpty()) {
            System.out.println("Please provide valid fileLocation");
            return null;
        }
        //preparing the staement for execution
        PreparedStatement add_media_file_statement = connection.prepareStatement(QueryCollector.add_media_file, Statement.RETURN_GENERATED_KEYS);
        //setting the field of the query
        add_media_file_statement.setString(1, fileLocation.toLowerCase());
        add_media_file_statement.executeUpdate();
        ResultSet resultSet = add_media_file_statement.getGeneratedKeys();
        while (resultSet.next()) {
            int file_id = resultSet.getInt(1);
            System.out.println("media added with file id: " + file_id);
            return new FileIdentifier(file_id);
        }

        return null;

    }

    /**
     * This method will update the media attributes
     *
     * @param fileIdentifier - media_file_id of which attribute has to be updated
     * @param attributes-    map that has the all attributes stored for the given media_file_id
     * @return Boolean - true if attributes recorded otherwise false.
     */

    @Override
    Boolean recordMediaAttributes(FileIdentifier fileIdentifier, Map<String, String> attributes) throws SQLException {
        //setting the base condition
        if (fileIdentifier == null || attributes == null || fileIdentifier.getMedia_file_id() <= 0 || !foundMediaFile(fileIdentifier.getMedia_file_id()) || attributes.isEmpty()) {
            return false;
        }
        //formatting the date to the format "YYYY-MM-DD OR YYYY-MM OR YY"
        String formatted_date = DateFormatter.getFormattedDate(attributes.get("date_last_updated"));
        attributes.put("date_last_updated", formatted_date);
        PreparedStatement record_attribute_statement = connection.prepareStatement("UPDATE media_data set media_file_name =?,media_location =?,date_last_updated =?  WHERE media_file_id = ?");
        record_attribute_statement.setString(1, attributes.get("media_file_name").toLowerCase());
        record_attribute_statement.setString(2, attributes.get("media_location").toLowerCase());
        record_attribute_statement.setString(3, attributes.get("date_last_updated"));
        record_attribute_statement.setInt(4, fileIdentifier.getMedia_file_id());
        int result = record_attribute_statement.executeUpdate();
        return result > 0;
    }


    /**
     * This method will record that a set of people appear in the given media file
     *
     * @param fileIdentifier - media_file_id for which person has to be added
     * @param people-        set of people to be added in the given media file
     * @return Boolean - true if attributes added otherwise false.
     */
    @Override
    Boolean peopleInMedia(FileIdentifier fileIdentifier, List<PersonIdentity> people) throws SQLException {

        //initializing the list
        ArrayList<Integer> list = new ArrayList<>();
        if (people == null || people.isEmpty() || fileIdentifier == null || fileIdentifier.getMedia_file_id() <= 0) {
            return false;
        }
        if (foundMediaFile(fileIdentifier.getMedia_file_id())) {

            PreparedStatement people_in_media_statement = connection.prepareStatement(QueryCollector.check_if_person_exist);
            ResultSet people_in_media_set = null;
            for (PersonIdentity personIdentity : people) {
                people_in_media_statement.setInt(1, personIdentity.getPerson_id());
                people_in_media_set = people_in_media_statement.executeQuery();
                while (people_in_media_set.next()) {
                    list.add(people_in_media_set.getInt(1));
                }
            }


            PreparedStatement check_if_already_exist_statement = connection.prepareStatement("select person_id from person_in_media where media_file_id = ? and person_id= ?;");
            ResultSet resultSet1 = null;
            for (int i = 0; i < list.size(); i++) {
                check_if_already_exist_statement.setInt(1, fileIdentifier.getMedia_file_id());
                check_if_already_exist_statement.setInt(2, list.get(i));
                ResultSet firstResult = check_if_already_exist_statement.executeQuery();
                while (firstResult.next()) {

                    check = true;

                }
                if (check) {
                    check = false;
                } else {
                    people_in_media_statement = connection.prepareStatement("INSERT INTO person_in_media (media_file_id,person_id) VALUES (?,?)");
                    people_in_media_statement.setInt(1, fileIdentifier.getMedia_file_id());
                    people_in_media_statement.setInt(2, list.get(i));
                    people_in_media_statement.executeUpdate();
                }
            }

        }
        return false;

    }


    /**
     * This method will record the tag for a media file
     * A media file can have many tags
     *
     * @param fileIdentifier - media_file_id for which tag has to be added
     * @param tag-           tag to be added
     * @return Boolean - true if attributes added otherwise false.
     */
    @Override
    Boolean tagMedia(FileIdentifier fileIdentifier, String tag) throws SQLException {

        if (fileIdentifier == null || tag == null || tag.isEmpty() || fileIdentifier.getMedia_file_id() <= 0 || !foundMediaFile(fileIdentifier.getMedia_file_id())) {
            return false;
        }
        PreparedStatement media_tag_statement = connection.prepareStatement("INSERT INTO media_tags (media_file_id, tag) VALUES (?,?);");
        media_tag_statement.setInt(1, fileIdentifier.getMedia_file_id());
        media_tag_statement.setString(2, tag.toLowerCase());
        int result = media_tag_statement.executeUpdate();
        return result > 0;

    }


    //This method will check if passed file is present in media_data table or not
    private boolean foundMediaFile(int media_file_id) throws SQLException {
        PreparedStatement found_media_statement = connection.prepareStatement("SELECT * From media_data WHERE media_file_id = ?");
        found_media_statement.setInt(1, media_file_id);
        ResultSet resultSet = found_media_statement.executeQuery();
        while (resultSet.next()) {

            return true;
        }
        return false;
    }


}

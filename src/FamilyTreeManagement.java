import java.sql.*;
import java.util.Map;

public class FamilyTreeManagement extends AbstractFamilyTreeManagement {

    //relation enum that will store each relation_type and its value
    public enum relation {
        parent_child(1),
        Marriage(2),
        Dissolution(3);
        private final int id;

        relation(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    //Connection
    private final Connection connection;

    //enum for notes and references type
    private enum type {NOTES, REFERENCES}

    //setting connection as passed in an argument
    FamilyTreeManagement(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method will add person to the database
     * Will throw SQL Exception if operation is unsuccessful.
     *
     * @param name - name of the given person
     * @return PersonIdentity - object of type PersonIdentity that will store the unique id returned from database.
     */

    @Override
    PersonIdentity addPerson(String name) throws SQLException {

        //Check for the null and empty constraints
        if (name != null && !name.isEmpty()) {

            int person_id = 0;

            //Preparing the statement to execute the passed query as an argument.
            PreparedStatement add_person_statement = this.connection.prepareStatement(QueryCollector.insert_into_person, Statement.RETURN_GENERATED_KEYS);
            //setting the field in the query
            add_person_statement.setString(1, name.toLowerCase());
            //calling executeUpdate()
            add_person_statement.executeUpdate();
            ResultSet add_person_set = add_person_statement.getGeneratedKeys();
            //if success and result has some data then loop through the result
            //To get the person ID
            while (add_person_set.next()) {
                person_id = add_person_set.getInt(1);

            }
            System.out.println("Person added into database with given id " + person_id);
            add_person_set.close();
            return new PersonIdentity(person_id);
        }
        System.out.println("Please provide a name of a person");
        return null;

    }

    /**
     * This method will record attributes of the given person
     * Will update the result in the database as well
     * Will throw SQL Exception if operation is unsuccessful.
     *
     * @param person     - name of the given person
     * @param attributes - map that has all the attributes
     * @return Boolean - true if recorded otherwise false
     */
    @Override
    Boolean recordAttributes(PersonIdentity person, Map<String, String> attributes) throws SQLException {
        //checking the base constraints
        if (person.getPerson_id() > 0 && findName(person) != null) {
            //If person is already has its previous records just update that record
            //Else add new record in the table
            if (findIfExistInMetaDataTable(person.getPerson_id())) {
                PreparedStatement update_attribute_statement = connection.prepareStatement(QueryCollector.update_attr);

                update_attribute_statement.setString(1, attributes.get("birth_location").toLowerCase());
                update_attribute_statement.setString(2, attributes.get("death_location").toLowerCase());
                update_attribute_statement.setString(3, attributes.get("birth_day"));
                update_attribute_statement.setString(4, attributes.get("birth_month"));
                update_attribute_statement.setString(5, attributes.get("birth_year"));
                update_attribute_statement.setString(6, attributes.get("death_day"));
                update_attribute_statement.setString(7, attributes.get("death_month"));
                update_attribute_statement.setString(8, attributes.get("death_year"));
                update_attribute_statement.setString(9, attributes.get("gender").toLowerCase());
                update_attribute_statement.setString(10, attributes.get("occupation").toLowerCase());
                update_attribute_statement.setInt(11, person.getPerson_id());
                update_attribute_statement.addBatch();
                int result = update_attribute_statement.executeUpdate();
                update_attribute_statement.close();
                return result > 0;
            } else {
                PreparedStatement update_attribute_statement = connection.prepareStatement("INSERT INTO person_metadata ( person_id, birth_location, death_location, birth_day, birth_month, birth_year, death_day, death_month, death_year, gender, occupation) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                update_attribute_statement.setInt(1, person.getPerson_id());
                update_attribute_statement.setString(2, attributes.get("birth_location").toLowerCase());
                update_attribute_statement.setString(3, attributes.get("death_location").toLowerCase());
                update_attribute_statement.setString(4, attributes.get("birth_day"));
                update_attribute_statement.setString(5, attributes.get("birth_month"));
                update_attribute_statement.setString(6, attributes.get("birth_year"));
                update_attribute_statement.setString(7, attributes.get("death_day"));
                update_attribute_statement.setString(8, attributes.get("death_month"));
                update_attribute_statement.setString(9, attributes.get("death_year"));
                update_attribute_statement.setString(10, attributes.get("gender").toLowerCase());
                update_attribute_statement.setString(11, attributes.get("occupation").toLowerCase());
                int result = update_attribute_statement.executeUpdate();
                update_attribute_statement.close();
                return result > 0;

            }

        }
        return false;
    }


    /**
     * This method will store references of the given person passed as an argument
     * Will throw SQL Exception if operation is unsuccessful.
     *
     * @param person    - name of the given person
     * @param reference - reference of that person
     * @return Boolean - true if refernce added otherwise false
     */
    @Override
    Boolean recordReference(PersonIdentity person, String reference) throws SQLException {

        //checking base conditions
        if (reference != null && !reference.isEmpty()) {
            //checking if person exists or not in the database
            if (findName(person) != null) {
                PreparedStatement add_record_reference = null;

                int result = insertIntoNotesAndReference(add_record_reference, person, String.valueOf(type.REFERENCES), reference);

                if (result >= 1) {
                    return true;
                }
            }

        }

        return false;
    }


    /**
     * This method will store notes of the given person passed as an argument
     * Will throw SQL Exception if operation is unsuccessful.
     *
     * @param person - name of the given person
     * @param note   - note of that person
     * @return Boolean - true if note added otherwise false
     */
    @Override
    Boolean recordNote(PersonIdentity person, String note) throws SQLException {

        //checking base conditions
        if (note != null && !note.isEmpty()) {
            //checking if person exists or not in the database
            if (findName(person) != null) {
                PreparedStatement add_record_note = null;
                int result = insertIntoNotesAndReference(add_record_note, person, String.valueOf(type.NOTES), note);
                System.out.println(result);
                if (result >= 1) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * This method will store parent/child relation in the database
     * Will throw SQL Exception if operation is unsuccessful.
     *
     * @param parent - person as a parent
     * @param child  - person as a child
     * @return Boolean - true if note recorded otherwise false
     */
    @Override
    Boolean recordChild(PersonIdentity parent, PersonIdentity child) throws SQLException {

        //checking for base condition
        if ((parent != null && child != null && (parent.getPerson_id() != child.getPerson_id()) && parent.getPerson_id() > 0 && child.getPerson_id() > 0)) {

            if (findName(parent) == null || findName(child) == null || findSameChild(parent, child)) {
                System.out.println("parent or child not found in person database: ");
            } else {

                PreparedStatement recordChild_statement = connection.prepareStatement(QueryCollector.insert_into_relationship);
                recordChild_statement.setInt(1, child.getPerson_id());
                recordChild_statement.setInt(2, parent.getPerson_id());
                recordChild_statement.setInt(3, relation.parent_child.getValue());
                int result = recordChild_statement.executeUpdate();
                recordChild_statement.close();
                return result >= 1;
            }

        }
        return false;
    }


    /**
     * This method will store Marriage relation in the database
     * Will throw SQL Exception if operation is unsuccessful.
     *
     * @param partner1 - person as a partner1
     * @param partner2 - person as a partner2
     * @return Boolean - true if note recorded otherwise false
     */
    @Override
    Boolean recordPartnering(PersonIdentity partner1, PersonIdentity partner2) throws SQLException {


        //checking for base conditions
        if ((partner1 != null && partner2 != null && (partner1.getPerson_id() != partner2.getPerson_id()) && partner1.getPerson_id() > 0 && partner2.getPerson_id() > 0)) {
            if (findName(partner1) == null || findName(partner2) == null || findInRelationIfExist(partner1, partner2, relation.Dissolution.getValue())) {
                System.out.println("partner not found in person database: ");
            } else {

                PreparedStatement record_partner_statement = connection.prepareStatement(QueryCollector.insert_into_relationship);
                record_partner_statement.setInt(1, partner1.getPerson_id());
                record_partner_statement.setInt(2, partner2.getPerson_id());
                record_partner_statement.setInt(3, relation.Marriage.getValue());
                int result = record_partner_statement.executeUpdate();
                record_partner_statement.close();
                return result >= 1;
            }
        }

        return false;

    }


    /**
     * This method will store Dissolution relation in the database
     * Will throw SQL Exception if operation is unsuccessful.
     *
     * @param partner1 - person as a partner_1
     * @param partner2 - person as a partner_2
     * @return Boolean - true if note recorded otherwise false
     */
    @Override
    Boolean recordDissolution(PersonIdentity partner1, PersonIdentity partner2) throws SQLException {

        if ((partner1 != null && partner2 != null && (partner1.getPerson_id() != partner2.getPerson_id()) && partner1.getPerson_id() > 0 && partner2.getPerson_id() > 0)) {
            if (findName(partner1) == null || findName(partner2) == null || findInRelationIfExist(partner1, partner2, relation.Marriage.getValue()) || !findInMarriage(partner1, partner2)) {
                return false;
            } else {

                PreparedStatement record_dissolution_statement = connection.prepareStatement(QueryCollector.insert_into_relationship);
                record_dissolution_statement.setInt(1, partner1.getPerson_id());
                record_dissolution_statement.setInt(2, partner2.getPerson_id());
                record_dissolution_statement.setInt(3, relation.Dissolution.getValue());
                int result = record_dissolution_statement.executeUpdate();
                record_dissolution_statement.close();
                return result >= 1;
            }
        }
        return false;

    }

    //This method will check if person has previous attributes or not
    private boolean findIfExistInMetaDataTable(int person_id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from person_metadata where person_id = ?");
        statement.setInt(1, person_id);
        ResultSet results = statement.executeQuery();

        while (results.next()) {

            return true;
        }

        return false;
    }

    //This method will check if entries being added is already present in the database or not
    private boolean findInMarriage(PersonIdentity partner1, PersonIdentity partner2) throws SQLException {
        PreparedStatement find_in_marriage_statement = connection.prepareStatement(" select * from relationship where(( person_id_1=? and person_id_2=?) or (person_id_1=? and person_id_2=?)) and relationship_code = 2 ");
        find_in_marriage_statement.setInt(1, partner1.getPerson_id());
        find_in_marriage_statement.setInt(2, partner2.getPerson_id());
        find_in_marriage_statement.setInt(3, partner2.getPerson_id());
        find_in_marriage_statement.setInt(4, partner1.getPerson_id());
        ResultSet resultSet = find_in_marriage_statement.executeQuery();

        while (resultSet.next()) {

            return true;
        }

        return false;
    }

    //This method will check if this parent/child relation is already present in the database or not
    private boolean findSameChild(PersonIdentity parent, PersonIdentity child) throws SQLException {
        PreparedStatement check_statement = connection.prepareStatement(" select * from relationship where (person_id_1=? and person_id_2=?) or (person_id_1=? and person_id_2=?);");
        check_statement.setInt(1, parent.getPerson_id());
        check_statement.setInt(2, child.getPerson_id());
        check_statement.setInt(3, child.getPerson_id());
        check_statement.setInt(4, parent.getPerson_id());
        ResultSet resultSet = check_statement.executeQuery();

        while (resultSet.next()) {

            return true;
        }

        return false;
    }

    //This method will be used to check if person exists in the database or not
    private String findName(PersonIdentity personIdentity) throws SQLException {

        PreparedStatement check_if_exist_statement = this.connection.prepareStatement(QueryCollector.check_if_person_exist);
        check_if_exist_statement.setInt(1, personIdentity.getPerson_id());
        ResultSet check_if_exist_result = check_if_exist_statement.executeQuery();


        while (check_if_exist_result.next()) {
            return check_if_exist_result.getString(2);
        }
        return null;

    }

    //This method will check if entries being added is already present in the database or not
    //To avoid duplication as well as false data
    private boolean findInRelationIfExist(PersonIdentity person1, PersonIdentity person2, int code) throws SQLException {
        PreparedStatement checkRelationStatement = connection.prepareStatement(" select * from relationship where(( person_id_1=? and person_id_2=?) or (person_id_1=? and person_id_2=?))\n" +
                " and relationship_code not in (?);\n");
        checkRelationStatement.setInt(1, person1.getPerson_id());
        checkRelationStatement.setInt(2, person2.getPerson_id());
        checkRelationStatement.setInt(3, person2.getPerson_id());
        checkRelationStatement.setInt(4, person1.getPerson_id());
        checkRelationStatement.setInt(5, code);
        ResultSet resultSet = checkRelationStatement.executeQuery();
        while (resultSet.next()) {

            return true;
        }
        ;
        return false;
    }

    //Common method to insert notes and references into database
    private int insertIntoNotesAndReference(PreparedStatement statement, PersonIdentity person, String type, String value) throws SQLException {

        statement = this.connection.prepareStatement(QueryCollector.insert_into_notesandreference, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, null);
        statement.setInt(2, person.getPerson_id());
        statement.setString(3, type);
        statement.setString(4, value.toLowerCase());
        return statement.executeUpdate();
    }


}

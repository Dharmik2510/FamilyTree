import java.sql.SQLException;
import java.util.Map;

//Abstract class that will be extended by FamilyTreeManagementClass
//Contains all methods for FamilyTreeManagement
abstract public class AbstractFamilyTreeManagement
{
     abstract PersonIdentity addPerson(String name) throws SQLException;
     abstract Boolean recordAttributes(PersonIdentity person, Map<String,String> attributes) throws SQLException;
     abstract Boolean recordReference(PersonIdentity person, String reference) throws SQLException;
     abstract Boolean recordNote(PersonIdentity person,String note) throws SQLException;
     abstract Boolean recordChild(PersonIdentity parent, PersonIdentity child) throws SQLException;
     abstract Boolean recordPartnering(PersonIdentity partner1,PersonIdentity partner2) throws SQLException;
     abstract Boolean recordDissolution(PersonIdentity partner1,PersonIdentity partner2) throws SQLException;

}

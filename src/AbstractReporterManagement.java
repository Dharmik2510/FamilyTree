import java.sql.SQLException;
import java.util.List;
import java.util.Set;



//Abstract class that will be extended by ReporterManagement
//Contains all methods for ReporterManagement

abstract public class AbstractReporterManagement
{
    abstract PersonIdentity findPerson(String name) throws Exception;
    abstract FileIdentifier findMediaFile(String name) throws SQLException;
    abstract String findName(PersonIdentity id) throws SQLException;
    abstract String findMediaFile(FileIdentifier fileId) throws SQLException;
    abstract Set<PersonIdentity> ancestores(PersonIdentity person, Integer generations) throws SQLException;
    abstract Set<PersonIdentity> descendents (PersonIdentity person, Integer generations) throws SQLException;
    abstract BiologicalRelation findRelation (PersonIdentity person1,PersonIdentity person2) throws SQLException;
    abstract List<String> notesAndReferences(PersonIdentity person) throws SQLException;
    abstract Set<FileIdentifier> findMediaByTag(String tag, String startDate,String endDate) throws SQLException;
    abstract Set<FileIdentifier> findMediaByLocation(String location, String startDate,String endDate) throws SQLException;
    abstract List<FileIdentifier> findIndividualsMedia(Set<PersonIdentity> people, String startDate,String endDate) throws SQLException;
    abstract List<FileIdentifier> findBiologicalFamilyMedia(PersonIdentity person) throws SQLException;
}

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

//Abstract class that will be extended by MediaArchiveManagement
//Contains all methods for MediaArchiveManagement

abstract public class AbstractMediaArchiveManagement
{
    abstract  FileIdentifier addMediaFile(String fileLocation) throws SQLException;
    abstract  Boolean recordMediaAttributes(FileIdentifier fileIdentifier, Map<String,String> attributes) throws SQLException;
    abstract  Boolean peopleInMedia(FileIdentifier fileIdentifier, List<PersonIdentity> people) throws SQLException;
    abstract Boolean tagMedia( FileIdentifier fileIdentifier, String tag) throws SQLException;

}

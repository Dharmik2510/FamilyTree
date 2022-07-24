import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//This class will only going to execute one time whenever first time program runs
public class PopulateRelationshipTable
{
    //Hashmap that will map relationship_code to its relationship_type
    public static final HashMap<Integer,String> codeToRelation = new HashMap<>();
    private static String insert_into_relation_type = "INSERT INTO relation_type (relationship_code, relationship_type)" + "VALUES (?,?);";

    //This method will store each relation_code to its relation_type
    public static void insertIntoRelationType()
    {
        codeToRelation.put(1,"parent_child");
        codeToRelation.put(2,"Marriage");
        codeToRelation.put(3,"Dissolution");
        try {
            //Getting connection
            Connection connection =  DatabaseConnector.getConnection();

            //Preparing statement for the given passed sql query
            PreparedStatement relation_statement = connection.prepareStatement(insert_into_relation_type);
            for(Map.Entry<Integer,String > entry:codeToRelation.entrySet())
            {
               relation_statement.setInt(1,entry.getKey());
               relation_statement.setString(2,entry.getValue());
               //adding into batch these queries
               relation_statement.addBatch();
            }
            //executing batch queries
            relation_statement.executeBatch();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


//CLASS where almost all queries to fetch data from database is stored.

public class QueryCollector
{

    public static final String insert_into_person = "INSERT INTO person (name) VALUES (?);";
    public static final String check_if_person_exist = "SELECT * FROM person WHERE person_id = ?";
    public static final String check_if_person_name_exist = "SELECT * FROM person WHERE name = ?";
    public static final String insert_into_notesandreference = "INSERT INTO person_notesandreference VALUES (?,?,?,?);";
    public static final String insert_into_relationship = "INSERT INTO relationship VALUES(?,?,?) ";
    public static final String update_attr = "update person_metadata set birth_location=?, death_location=?,birth_day=?,birth_month=?,birth_year=?,death_day=?,death_month=?,death_year=?,gender=?,occupation=? where person_id =?";



    public static String add_media_file = "INSERT INTO media_data (media_file_location) values (?)";
    public static String find_media_file_using_name = "SELECT * FROM media_data WHERE media_file_name = ?";
    public static String find_media_file_using_id="SELECT * From media_data WHERE media_file_id = ?";




    public static final String find_notes_reference = "select type_content from person_notesandreference where person_id = ? order by nr_id";

    public static final String find_anscestors =
            "WITH recursive family_tree AS\n" +
                    "  ( SELECT parent_id, \n" +
                    "\t\t1 generation_away\n" +
                    "   FROM \n" +
                    "\t(select person_id_1 as \"child_id\", person_id_2 as \"parent_id\", relation_type.relationship_type from\n" +
                    "\trelationship inner join relation_type on relationship.relationship_code = relation_type.relationship_code\n" +
                    "\twhere relationship.relationship_code=1) as sub\n" +
                    "\twhere child_id =?\n" +
                    "\tunion all\n" +
                    "\tselect sub.parent_id,\n" +
                    "\t\t   generation_away+1\n" +
                    "\tfrom \n" +
                    "\t\t(select person_id_1 as \"child_id\", person_id_2 as \"parent_id\", relation_type.relationship_type from\n" +
                    "\t\trelationship inner join relation_type on relationship.relationship_code = relation_type.relationship_code\n" +
                    "\t\twhere relationship.relationship_code=1) as sub\n" +
                    "\t\tjoin family_tree on family_tree.parent_id = sub.child_id\n" +
                    "\t\t)\n" +
                    "\tselect * from family_tree where generation_away <=?;";



    public static final String find_descendent =
            "WITH recursive family_tree AS\n" +
                    "  ( SELECT child_id, \n" +
                    "\t\t1 generation_away\n" +
                    "   FROM \n" +
                    "\t(select person_id_1 as \"child_id\", person_id_2 as \"parent_id\", relation_type.relationship_type from\n" +
                    "\trelationship inner join relation_type on relationship.relationship_code = relation_type.relationship_code\n" +
                    "\twhere relationship.relationship_code=1) as sub\n" +
                    "\twhere parent_id =?\n" +
                    "\tunion all\n" +
                    "\tselect sub.child_id,\n" +
                    "\t\t   generation_away+1\n" +
                    "\tfrom \n" +
                    "\t\t(select person_id_1 as \"child_id\", person_id_2 as \"parent_id\", relation_type.relationship_type from\n" +
                    "\t\trelationship inner join relation_type on relationship.relationship_code = relation_type.relationship_code\n" +
                    "\t\twhere relationship.relationship_code=1) as sub\n" +
                    "\t\tjoin family_tree on family_tree.child_id = sub.parent_id\n" +
                    "\t\t)\n" +
                    "\tselect * from family_tree where generation_away <=?;";



    public static final String find_relation =
            "WITH RECURSIVE family_tree AS\n" +
                    "  ( SELECT person_id as \"parent_id\", person_id as \"relationship_with\",\n" +
                    "           0 generation\n" +
                    "   FROM person\n" +
                    "   WHERE person_id in (?,?)\n" +
                    "     UNION ALL\n" +
                    "\tSELECT sub.parent_id, relationship_with,\n" +
                    "\t\t   generation+1\n" +
                    "\t\tfrom\n" +
                    "        (select person_id_1 as \"child_id\", person_id_2 as \"parent_id\", relation_type.relationship_type from\n" +
                    "\t\trelationship inner join relation_type on relationship.relationship_code = relation_type.relationship_code\n" +
                    "\t\twhere relationship.relationship_code=1) as sub\n" +
                    "        inner join family_tree on family_tree.parent_id = sub.child_id\n" +
                    "\t)\n" +
                    "    SELECT min(generation) - 1 as \"degree_of_cousinship\",  max(generation) - min(generation) as \"degree_of_removal\" FROM family_tree \n" +
                    "    group by parent_id having count(parent_id) = 2 limit 1;";


    public static final String find_media_by_tag="select media_tags.media_file_id, media_tags.tag, media_data.date_last_updated  from \n" +
            "media_tags inner join media_data on media_tags.media_file_id = media_data.media_file_id\n" +
            "where  media_tags.tag =? \n" +
            "and (media_data.date_last_updated >= ? or ? is null)\n" +
            "and (media_data.date_last_updated <= ? or ? is null );";


    public static final String find_media_by_location="select media_data.media_file_id, media_data.media_location from \n" +
            "media_data where media_data.media_location = ? \n" +
            "and (media_data.date_last_updated >= ? or ? is null)\n" +
            "and (media_data.date_last_updated <= ? or ? is null );";


    public static final String find_biological_family_media = "SELECT media_data.media_file_id FROM media_data INNER JOIN person_in_media on media_data.media_file_id = person_in_media.media_file_id WHERE (person_in_media.person_id in (select person_id_1 from relationship where person_id_2 = ? and relationship_code = 1)) ORDER BY date_last_updated desc, media_data.media_file_name;";


}


//class whose object will identify each file uniquely by its id;
public class FileIdentifier
{
    //unique media_file_id
    private int media_file_id;


    public FileIdentifier(int media_file_id) {
        this.media_file_id = media_file_id;
    }

    //getting file identifier
    public int getMedia_file_id() {
        return media_file_id;
    }

    //setting file identifier
    public void setMedia_file_id(int media_file_id) {
        this.media_file_id = media_file_id;
    }
}

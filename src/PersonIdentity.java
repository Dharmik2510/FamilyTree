
//class whose object will identify each person uniquely by its id;
public class PersonIdentity
{
    //unique ID
    private int person_id;

    public PersonIdentity(int person_id) {

        this.person_id = person_id;
    }

    //getting person identity
    public int getPerson_id() {
        return person_id;
    }

    //setting person identity
    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }
}

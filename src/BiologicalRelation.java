
//This class will store cousinship and degree of removal of relationship
public class BiologicalRelation {

    //Cousinship
    private int degree_of_cousinship;
    //Degree/Level of removal
    private int degree_of_removal;

    public BiologicalRelation(int degree_of_cousinship, int degree_of_removal) {
        this.degree_of_cousinship = degree_of_cousinship;
        this.degree_of_removal = degree_of_removal;
    }


    public int getDegree_of_cousinship() {
        return degree_of_cousinship;
    }

    public void setDegree_of_cousinship(int degree_of_cousinship) {
        this.degree_of_cousinship = degree_of_cousinship;
    }

    public int getDegree_of_removal() {
        return degree_of_removal;
    }

    public void setDegree_of_removal(int degree_of_removal) {
        this.degree_of_removal = degree_of_removal;
    }
}

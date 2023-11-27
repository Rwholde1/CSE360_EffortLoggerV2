package application;

public class DefectName {
    //Basic DefectName info
    private String name;
    private int value;
    
    //Basic Constructor
    public DefectName(String name, int value) {
        this.name = name;
        this.value = value;
    }
    
    //Getters
    public String getName() {
        return this.name;
    }
    
    public int getValue() {
        return this.value;
    }
    
    //Setters
    public void setName(String name) {
        this.name = name;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    //toString override for format checking / initialization
    @Override
    public String toString() {
        String DefectInfo = this.name + " | " + this.value;
        return DefectInfo;
    }
}
package edu.jsu.mcis.tas_fa19;


public class Badge {
    private String id = null;
    private String description = null;;

    public Badge(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "#" + id + " (" + description + ")";
    }
    
    
    
}

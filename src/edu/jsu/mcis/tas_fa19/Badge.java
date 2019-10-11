package edu.jsu.mcis.tas_fa19;


public class Badge {
    private String id;
    private String description;

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
    
}

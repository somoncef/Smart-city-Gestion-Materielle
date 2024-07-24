package com.example.gestion_materielle_hackathon.model;

import java.util.List;

public class Demande {
    private String emplacement;
    private String dateDebut;
    private String dateFin;
    private String description;
    private List<Material> materials;
    private String justification;
    private String urgence;

    public Demande() {
        // Default constructor required for calls to DataSnapshot.getValue(Demande.class)
    }

    public Demande(String emplacement, String dateDebut, String dateFin, String description,
                   List<Material> materials, String justification, String urgence) {
        this.emplacement = emplacement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.materials = materials;
        this.justification = justification;
        this.urgence = urgence;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getUrgence() {
        return urgence;
    }

    public void setUrgence(String urgence) {
        this.urgence = urgence;
    }
}
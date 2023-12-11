package com.samar.delivery.models;

import java.util.List;

public class Task {
    private String id;
    private String libelle;
    private String commentaire;
    private String status;
    private String duree;
    private String heureDateDebutPrevu;
    private String heureDateFinPrevu;
    private String heureDateDebutReelle;
    private String heureDateFinReelle;
    private Double latitude;
    private Double longitude;
    private List<String> fileUrls;


    public Task() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getHeureDateDebutPrevu() {
        return heureDateDebutPrevu;
    }

    public void setHeureDateDebutPrevu(String heureDateDebutPrevu) {
        this.heureDateDebutPrevu = heureDateDebutPrevu;
    }

    public String getHeureDateFinPrevu() {
        return heureDateFinPrevu;
    }

    public void setHeureDateFinPrevu(String heureDateFinPrevu) {
        this.heureDateFinPrevu = heureDateFinPrevu;
    }

    public String getHeureDateDebutReelle() {
        return heureDateDebutReelle;
    }

    public void setHeureDateDebutReelle(String heureDateDebutReelle) {
        this.heureDateDebutReelle = heureDateDebutReelle;
    }

    public String getHeureDateFinReelle() {
        return heureDateFinReelle;
    }

    public void setHeureDateFinReelle(String heureDateFinReelle) {
        this.heureDateFinReelle = heureDateFinReelle;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }
}
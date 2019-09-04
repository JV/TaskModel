package com.example.taskmodel.element;

import java.io.Serializable;

public class ElementModel implements Serializable {

    private Long id; //added for differentiating elements with same @naziv, @pocetak, @kraj and @tag
    private String naziv;
    private Long pocetak;
    private Long kraj;
    private String tag;
//    private String creationTime; // for other sort options

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Long getPocetak() {
        return pocetak;
    }

    public void setPocetak(Long pocetak) {
        this.pocetak = pocetak;
    }

    public Long getKraj() {
        return kraj;
    }

    public void setKraj(Long kraj) {
        this.kraj = kraj;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "ElementModel{" +
                "id=" + id +
                ", naziv='" + naziv + '\'' +
                ", pocetak=" + pocetak +
                ", kraj=" + kraj +
                ", tag='" + tag + '\'' +
                '}';
    }
}

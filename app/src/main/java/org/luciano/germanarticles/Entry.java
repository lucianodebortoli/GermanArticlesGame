package org.luciano.germanarticles;



public class Entry {

    private String ID;
    private String article;
    private String noun;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getNoun() {
        return noun;
    }

    public void setNoun(String noun) {
        this.noun = noun;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "ID='" + ID + '\'' +
                ", article='" + article + '\'' +
                ", noun='" + noun + '\'' +
                '}';
    }
}

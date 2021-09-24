package com.game.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "player")
public class Player {

//  ID player (only numbers and whole numbers, > 0)
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//  Player name (until 12 symbols)
    @Column
    private String name;

//  Title (until 30 symbols)
    @Column
    private String title;

//  Race /Enum/
//  For Enum use annotation @Enumerated
    @Column
    @Enumerated(EnumType.STRING)
    private Race race;

//  Profession /Enum/
//  For Enum use annotation @Enumerated
    @Column
    @Enumerated(EnumType.STRING)
    private Profession profession;

//  Birthday (in milliseconds after 01.01.1970, >=0)
    @Column
    private Date birthday;

//  Banned? (boolean)
    @Column
    private Boolean banned;

//  Experience (Integer, within 0...10,000,000)
    @Column
    private Integer experience;

//  Level (Integer)
    @Column
    private Integer level;

//  UntilNextLevel (Integer)
    @Column
    private Integer untilNextLevel;

//  Entity Player. It has to be public or protected constructor w/o args
    public Player() {
    }

//  All Setters and Getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() { return profession; }

    public void setProfession(Profession profession) { this.profession = profession; }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean isBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) { this.level = level; }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

//  Additional method
    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", race=" + race +
                ", profession=" + profession +
                ", birthday=" + birthday +
                ", banned=" + banned +
                ", experience=" + experience +
                ", level=" + level +
                ", untilNextLevel=" + untilNextLevel +
                '}';
    }
}
package com.codefighters;

public class EnemyData {
    public String id;
    public String name;
    public String type;
    public String bio;
    public String weakness;
    public String realWorldFact;
    public int primaryColor;
    public int secondaryColor;

    public EnemyData(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public static EnemyData getWannaCry() {
        EnemyData e = new EnemyData("wannacry", "WannaCry", "Ransomware");
        e.bio = "A cryptoworm that targeted computers running Microsoft Windows. "
            + "It encrypted files and demanded ransom payments in Bitcoin. "
            + "In-game, it throws encrypted payloads and attempts to lock down your system.";
        e.weakness = "Strong defense and quick combos. Block its heavy attacks and counter.";
        e.realWorldFact = "On May 12, 2017, WannaCry infected over 200,000 computers across 150 countries in a single day, causing an estimated $4 billion in damages.";
        e.primaryColor = 0xff1744;
        e.secondaryColor = 0xd50000;
        return e;
    }
}

package com.codefighters;

import java.util.ArrayList;
import java.util.List;

public class CharacterData {
    public String id;
    public String name;
    public String subtitle;
    public String bio;
    public String specialName;
    public String specialDesc;
    public String superName;
    public String superDesc;
    public List<String> combos = new ArrayList<>();
    public int primaryColor;
    public int secondaryColor;
    public int accentColor;
    public boolean unlocked;

    public CharacterData(String id, String name, String subtitle) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.unlocked = true;
    }

    public static CharacterData getCat() {
        CharacterData c = new CharacterData("cat", "Cat", "Game Architect");
        c.bio = "The visionary behind the Code Fighters project. "
            + "Armed with design documents, rapid reflexes, and cyber defense protocols.";
        c.specialName = "Scope Lock";
        c.specialDesc = "Freezes the enemy in a bounding box of constraints, dealing damage over time.";
        c.superName = "Gold Master";
        c.superDesc = "A massive deploy wave that crashes over the enemy like a finished build.";
        c.combos.add("Light x3: Quick Punch Combo");
        c.combos.add("Light > Light > Heavy: Design Kick");
        c.combos.add("Forward + Special: GDD Throw");
        c.combos.add("Down > Forward + Attack: Scope Creep Slash");
        c.primaryColor = 0x4fc3f7;
        c.secondaryColor = 0x1565c0;
        c.accentColor = 0xffeb3b;
        return c;
    }

    public static CharacterData getTeamLead() {
        return getCat();
    }
}

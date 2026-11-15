package com.codefighters;

import java.util.HashSet;
import java.util.Set;

public class GameProgress {
    // Track which enemies have been defeated (for gallery unlock)
    public Set<String> defeatedEnemies = new HashSet<>();

    // Currently selected character and enemy
    public String selectedCharacter = "cat";
    public String selectedEnemy = "wannacry";

    public boolean isEnemyDefeated(String enemyId) {
        return defeatedEnemies.contains(enemyId);
    }

    public void markDefeated(String enemyId) {
        defeatedEnemies.add(enemyId);
    }
}

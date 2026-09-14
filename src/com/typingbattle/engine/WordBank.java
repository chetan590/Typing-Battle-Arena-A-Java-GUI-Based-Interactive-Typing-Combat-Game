package com.typingbattle.engine;

import com.typingbattle.model.Difficulty;
import java.util.*;

/**
 * Rich vocabulary repository partitioned by difficulty levels,
 * including special battle words and coding terminologies.
 */
public class WordBank {

    private static final Map<Difficulty, List<String>> WORDS_BY_DIFFICULTY = new EnumMap<>(Difficulty.class);
    private static final List<String> SPECIAL_WORDS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        // Easy Words (3-5 letters, common & punchy)
        WORDS_BY_DIFFICULTY.put(Difficulty.EASY, Arrays.asList(
            "ace", "arc", "axe", "aim", "arm", "air", "bat", "bit", "bow", "box", "byte",
            "cast", "code", "core", "dash", "dare", "dark", "dart", "deck", "dice", "dive",
            "drop", "echo", "epic", "fang", "fast", "fate", "fire", "fist", "flame", "flow",
            "fuse", "gear", "glow", "gold", "grid", "hack", "halo", "heal", "heat", "hero",
            "iron", "jolt", "jump", "kick", "king", "kite", "leap", "lock", "loop", "mage",
            "mana", "mask", "mind", "moon", "myth", "nova", "pack", "path", "peak", "play",
            "plot", "pure", "rage", "raid", "rank", "ray", "ring", "rise", "roar", "rune",
            "rush", "scan", "seal", "seed", "ship", "shot", "sign", "site", "slam", "slot",
            "snap", "soul", "spar", "spin", "star", "stun", "surf", "sync", "tact", "tank",
            "tech", "trap", "tune", "twin", "unit", "vault", "veil", "void", "volt", "warp",
            "wave", "wind", "wing", "wire", "wolf", "word", "zeal", "zone"
        ));

        // Medium Words (5-7 letters, action and fantasy vocabulary)
        WORDS_BY_DIFFICULTY.put(Difficulty.MEDIUM, Arrays.asList(
            "action", "agility", "anchor", "arcade", "archer", "armored", "avatar", "barrier",
            "battle", "blaster", "blessing", "bravado", "bullet", "castle", "charge", "chariot",
            "cipher", "clash", "clever", "combat", "comet", "courage", "crypto", "dagger",
            "damage", "danger", "defense", "destiny", "divine", "dragon", "dynamic", "eclipse",
            "element", "energy", "engine", "escape", "falcon", "fierce", "fighter", "flavor",
            "forest", "furious", "galaxy", "gamble", "glitch", "gravity", "guardian", "hammer",
            "hazard", "heroic", "hunter", "impact", "impulse", "inferno", "instant", "katana",
            "knight", "legend", "lethal", "matrix", "meteor", "miracle", "mission", "motion",
            "mystic", "nature", "nebula", "ninja", "orbital", "outlaw", "phantom", "phoenix",
            "plasma", "player", "portal", "power", "pulsar", "quantum", "radiant", "rebound",
            "reflex", "relic", "rocket", "samurai", "shadow", "shield", "sniper", "sorcery",
            "spark", "spirit", "strike", "swift", "tactics", "thunder", "trigger", "tsunami",
            "typhoon", "valiant", "vampire", "vector", "velocity", "vessel", "vortex", "warrior",
            "weapon", "wizard", "zenith"
        ));

        // Hard Words (7-10 letters, complex gaming & combat terms)
        WORDS_BY_DIFFICULTY.put(Difficulty.HARD, Arrays.asList(
            "accelerate", "acrobatic", "aggressive", "allegiance", "annihilate", "apocalypse",
            "arbitrary", "archbishop", "assassinate", "astronomy", "atmosphere", "barricade",
            "battlefield", "berserker", "blacksmith", "bloodhound", "boomerang", "calculator",
            "catastrophe", "championship", "chronology", "combustion", "commander", "conqueror",
            "constellation", "counterattack", "cybernetic", "decryption", "devastation", "dimension",
            "disciplined", "earthquake", "electrify", "enchantment", "equilibrium", "execution",
            "formidable", "gladiator", "guillotine", "headquarters", "hyperdrive", "immortality",
            "impervious", "incinerate", "infiltrator", "invincible", "juggernaut", "keyboard",
            "legendary", "lightning", "luminescence", "machinery", "masterpiece", "mechanized",
            "mercenary", "millennium", "monumental", "multithread", "mysterious", "nightfall",
            "oblivion", "omnipotent", "overclock", "paralyzing", "perseverance", "phenomenon",
            "predator", "proficiency", "punishment", "radioactive", "relentless", "resistance",
            "resurrection", "retribution", "revolution", "safeguard", "sanctuary", "scoreboard",
            "sentiment", "shadowblade", "spectacular", "spellcaster", "subroutine", "surveillance",
            "synchronize", "terminator", "trajectory", "transcend", "transmutation", "valkyrie",
            "vanquisher", "vulnerable", "whirlwind", "wilderness"
        ));

        // Intense Words (8-14 letters, computer science, high-level vocabulary)
        WORDS_BY_DIFFICULTY.put(Difficulty.INTENSE, Arrays.asList(
            "algorithmically", "architectural", "asynchronous", "authentication", "cryptography",
            "cybersecurity", "decentralized", "encapsulation", "extensibility", "infrastructure",
            "instantiation", "interoperable", "maintainability", "microservices", "multidimensional",
            "multithreading", "normalization", "orchestration", "parallelization", "parameterized",
            "polymorphism", "quantumcomputing", "reproducibility", "serialization", "synchronization",
            "telecommunication", "transformation", "transcendental", "troubleshooting", "unprecedented",
            "vulnerability", "weatherproofing", "intercontinental", "counteroffensive", "discombobulate",
            "supercalifragilistic", "incomprehensible", "indestructible", "hyperdimensional"
        ));

        // Special Attack Trigger Words
        SPECIAL_WORDS.addAll(Arrays.asList(
            "SUPERNOVA", "CATACLYSM", "VOIDCRUSH", "OVERDRIVE", "DRAGONSLASH",
            "HYPERBEAM", "ECLIPSE", "APOCALYPSE", "FINALSTRIKE", "OMNISLASH"
        ));
    }

    public static String getRandomWord(Difficulty difficulty) {
        List<String> list = WORDS_BY_DIFFICULTY.getOrDefault(difficulty, WORDS_BY_DIFFICULTY.get(Difficulty.MEDIUM));
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static String getSpecialWord() {
        return SPECIAL_WORDS.get(RANDOM.nextInt(SPECIAL_WORDS.size()));
    }

    public static List<String> getWordQueue(Difficulty difficulty, int count) {
        List<String> queue = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            queue.add(getRandomWord(difficulty));
        }
        return queue;
    }
}


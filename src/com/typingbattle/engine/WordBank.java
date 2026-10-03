package com.typingbattle.engine;

import com.typingbattle.model.Difficulty;
import com.typingbattle.model.GameSettings;
import java.util.*;

/**
 * Rich vocabulary repository partitioned by difficulty levels,
 * with strict length filtering support (3, 4, 5, 6 letters or Mixed),
 * special battle words, and arcade terminologies.
 */
public class WordBank {

    private static final Map<Difficulty, List<String>> WORDS_BY_DIFFICULTY = new EnumMap<>(Difficulty.class);
    private static final Map<Integer, List<String>> WORDS_BY_LENGTH = new HashMap<>();
    private static final List<String> SPECIAL_WORDS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        // Explicit 3-Letter Words (purely 3 letters)
        List<String> words3 = Arrays.asList(
            "ace", "aim", "air", "arc", "arm", "axe", "bat", "bit", "bow", "box",
            "cat", "cog", "cup", "dam", "day", "den", "dim", "dot", "dry", "duo",
            "elf", "elm", "end", "era", "eye", "fan", "far", "fit", "fix", "fog",
            "fox", "gem", "gap", "gun", "gut", "hit", "hop", "hot", "hub", "ice",
            "ink", "ion", "jar", "jaw", "jet", "jog", "joy", "key", "kin", "kit",
            "law", "leg", "lid", "lip", "log", "low", "map", "mat", "mix", "mud",
            "net", "nod", "oar", "oat", "oil", "one", "orb", "owl", "pan", "paw",
            "peg", "pen", "pet", "pin", "pit", "ply", "pod", "pot", "pro", "rag",
            "ram", "rap", "raw", "ray", "red", "rib", "rim", "rip", "rod", "row",
            "run", "rut", "rye", "sag", "sap", "saw", "sea", "set", "sew", "sin",
            "sip", "sir", "six", "sky", "sly", "son", "spy", "sub", "sun", "tag",
            "tap", "tar", "tea", "tie", "tin", "tip", "toe", "top", "tug", "urn",
            "van", "vat", "vet", "via", "vim", "vow", "war", "wax", "way", "web",
            "wig", "win", "wit", "woe", "yak", "yam", "yap", "yen", "yes", "zap"
        );
        WORDS_BY_LENGTH.put(3, words3);

        // Explicit 4-Letter Words (purely 4 letters)
        List<String> words4 = Arrays.asList(
            "acid", "apex", "arch", "army", "atom", "aura", "bark", "base", "beam", "bell",
            "bite", "blade", "blast", "blow", "bold", "bolt", "bomb", "bond", "bone", "boot",
            "burn", "byte", "cage", "calm", "camp", "cast", "cave", "claw", "clay", "clip",
            "club", "clue", "coal", "coat", "code", "coil", "coin", "cold", "core", "cove",
            "crab", "crew", "cure", "curl", "dare", "dark", "dart", "dawn", "daze", "deck",
            "deed", "dive", "dome", "dose", "down", "drag", "draw", "drop", "drum", "dusk",
            "dust", "duty", "echo", "edge", "epic", "fade", "fail", "fair", "fame", "fang",
            "fast", "fate", "fear", "feat", "feed", "file", "film", "fire", "firm", "fist",
            "flag", "flaw", "flee", "flux", "foam", "fold", "font", "form", "fort", "foul",
            "fume", "fuse", "game", "gang", "gaze", "gear", "gift", "glow", "gold", "grid",
            "grip", "gulf", "hack", "hail", "halo", "halt", "harm", "haze", "heal", "heat",
            "helm", "hero", "hold", "hook", "horn", "hunt", "hymn", "icon", "iron", "jade",
            "jolt", "jump", "keen", "keep", "kick", "king", "kite", "knot", "lava", "leap",
            "lens", "link", "lion", "lock", "loom", "loop", "lord", "lore", "luck", "lurk",
            "mage", "mail", "mana", "mark", "mask", "maze", "melt", "mesh", "mill", "mind",
            "mist", "mode", "mold", "monk", "moon", "moth", "myth", "nail", "nest", "node",
            "nova", "oath", "omen", "pack", "page", "path", "peak", "peel", "pelt", "pile",
            "pipe", "plan", "plot", "plug", "poet", "pole", "pond", "port", "post", "pour",
            "prey", "prop", "pulse", "pure", "rage", "raid", "rail", "rain", "ramp", "rank",
            "rare", "rash", "rave", "rays", "reap", "reed", "reef", "reign", "rest", "rift",
            "ring", "riot", "rise", "risk", "roar", "robe", "rock", "root", "rope", "ruin",
            "rule", "rune", "rush", "rust", "safe", "sail", "sand", "scan", "scar", "seal",
            "seed", "seek", "seep", "sham", "shed", "ship", "shoe", "shot", "sign", "silk",
            "sink", "site", "slab", "slam", "slap", "slit", "slot", "snap", "snow", "soar",
            "soil", "sole", "solo", "song", "soul", "soup", "sour", "span", "spar", "spin",
            "spit", "spur", "star", "stem", "step", "stew", "stir", "stop", "stun", "suit",
            "surf", "sway", "swim", "sync", "tact", "tail", "tank", "task", "team", "tech",
            "tide", "tile", "time", "tint", "toil", "toll", "tone", "tour", "trap", "trim",
            "trio", "trip", "true", "tube", "tune", "turf", "twin", "type", "unit", "vale",
            "vane", "vary", "vase", "vast", "vault", "veil", "vein", "vent", "verb", "vest",
            "view", "vine", "visa", "void", "volt", "vow", "wake", "walk", "wall", "wand",
            "ward", "warm", "warp", "wary", "wave", "wear", "weed", "weld", "well", "west",
            "wind", "wing", "wire", "wise", "wish", "wolf", "wool", "word", "work", "worm",
            "wrap", "yawn", "year", "yoke", "zeal", "zero", "zinc", "zone"
        );
        WORDS_BY_LENGTH.put(4, words4.stream().filter(w -> w.length() == 4).toList());

        // Explicit 5-Letter Words (purely 5 letters)
        List<String> words5 = Arrays.asList(
            "abyss", "adapt", "adept", "agent", "agile", "alarm", "align", "alloy", "alpha", "amber",
            "angel", "anvil", "apron", "arena", "armor", "arrow", "asset", "atlas", "audio", "aviso",
            "badge", "balmy", "baron", "basic", "basin", "batch", "beast", "berth", "beset", "birth",
            "black", "blade", "blame", "blank", "blast", "blaze", "blend", "bless", "blind", "blink",
            "bliss", "block", "blood", "bloom", "blunt", "board", "boast", "bonus", "boost", "booth",
            "bound", "brace", "braid", "brain", "brake", "brand", "brass", "brave", "bread", "break",
            "breed", "bribe", "brick", "bride", "brief", "brink", "brisk", "broad", "brood", "brook",
            "broom", "brush", "build", "burst", "cabin", "cable", "camel", "canal", "cargo", "cedar",
            "chain", "chair", "chalk", "champ", "chaos", "charm", "chart", "chase", "chasm", "cheer",
            "chief", "chill", "chime", "chord", "cider", "cigar", "clash", "clasp", "class", "clean",
            "clear", "cliff", "climb", "cloak", "clock", "clone", "cloud", "clove", "clown", "coach",
            "cobra", "comet", "coral", "couch", "cough", "count", "court", "cover", "crack", "craft",
            "crane", "crash", "crate", "crawl", "craze", "creak", "creed", "creek", "crest", "crick",
            "crime", "crisp", "cross", "crowd", "crown", "crude", "cruel", "crush", "crust", "crypt",
            "curse", "curve", "cyber", "cycle", "daily", "dairy", "datum", "daunt", "debit", "decay",
            "delta", "demon", "dense", "depth", "derby", "devil", "diary", "digit", "disco", "ditch",
            "diver", "dodge", "draft", "drain", "drake", "drama", "dream", "dress", "drift", "drill",
            "drink", "drive", "drone", "druid", "dryer", "dwarf", "eager", "eagle", "earth", "eerie",
            "elbow", "elder", "ember", "empty", "enemy", "entry", "epoch", "equal", "equip", "erode",
            "essay", "event", "exact", "exile", "fable", "facet", "faint", "faith", "false", "fancy",
            "fatal", "feast", "fence", "ferry", "fever", "fiber", "field", "fiery", "final", "finch",
            "flame", "flank", "flare", "flask", "fleet", "flesh", "flint", "float", "flock", "flood",
            "floor", "flora", "fluid", "flute", "focus", "force", "forge", "forth", "forum", "found",
            "frame", "frank", "fraud", "freed", "fresh", "front", "frost", "frown", "fruit", "gamma",
            "gauge", "ghost", "giant", "glade", "gland", "glass", "glide", "globe", "gloom", "glory",
            "gnome", "grace", "grade", "grain", "grand", "grant", "grape", "graph", "grasp", "grass",
            "grave", "gravy", "great", "greed", "green", "greet", "grief", "grill", "grind", "groan",
            "group", "grove", "guard", "guest", "guide", "guild", "guise", "habit", "haste", "haven",
            "havoc", "heart", "heavy", "hedge", "heist", "honor", "horde", "hound", "hover", "human",
            "humor", "hurry", "hydra", "hyper", "ideal", "image", "imply", "index", "inert", "infer",
            "ingot", "inlet", "inner", "input", "irony", "issue", "ivory", "joint", "judge", "juice",
            "karma", "kayak", "knack", "kneel", "knife", "knock", "label", "labor", "lance", "laser",
            "latch", "layer", "leafy", "lease", "lemon", "level", "lever", "light", "limit", "linen",
            "liner", "liver", "lobby", "logic", "lover", "loyal", "lucid", "lunar", "lunch", "magic",
            "major", "manor", "maple", "march", "marsh", "mason", "match", "maxim", "mayor", "medal",
            "melee", "mercy", "merit", "metal", "micro", "mimic", "miner", "minor", "mixer", "model",
            "modem", "moist", "money", "monte", "moral", "morph", "motor", "mount", "mouse", "mural",
            "music", "naked", "naval", "nerve", "ninja", "noble", "noise", "nomad", "north", "notch",
            "novel", "nudge", "nurse", "oasis", "ocean", "olive", "omega", "onset", "opera", "orbit",
            "order", "organ", "other", "outer", "oxide", "ozone", "paint", "panel", "panic", "parry",
            "parse", "patch", "pause", "peace", "pearl", "pedal", "peril", "phase", "pilot", "pinch",
            "pixel", "pivot", "place", "plain", "plane", "plant", "plate", "plaza", "plead", "plume",
            "point", "polar", "porch", "poten", "pound", "power", "press", "price", "pride", "prime",
            "prism", "prize", "probe", "pulse", "punch", "pupil", "quack", "quake", "quart", "queen",
            "quest", "quick", "quiet", "quill", "quilt", "radar", "radii", "radio", "rainy", "rally",
            "range", "rapid", "raven", "razor", "reach", "react", "realm", "rebel", "relic", "rhyme",
            "rider", "ridge", "rifle", "rigid", "rinse", "rival", "river", "rivet", "roast", "robot",
            "rocky", "rogue", "roman", "round", "route", "rover", "royal", "rupee", "rusty", "saber",
            "saint", "salad", "salon", "salsa", "scale", "scalp", "scare", "scarf", "scene", "scent",
            "scope", "score", "scout", "scrap", "screw", "scuba", "sedan", "shack", "shade", "shaft",
            "shake", "shall", "shame", "shape", "shard", "share", "shark", "sharp", "shawl", "sheen",
            "sheet", "shell", "shift", "shine", "shire", "shock", "shore", "shout", "shrew", "siege",
            "sight", "sigma", "silva", "since", "siren", "skate", "skull", "slate", "slave", "sleek",
            "sleep", "slice", "slick", "slide", "slime", "sling", "slope", "sloth", "smart", "smash",
            "smell", "smile", "smoke", "snail", "snake", "snare", "sneak", "solar", "solid", "sonar",
            "sonic", "sound", "south", "space", "spade", "spark", "spawn", "spear", "speed", "spell",
            "spice", "spike", "spine", "spire", "spite", "split", "spoke", "spoon", "sport", "spray",
            "squad", "staff", "stage", "stair", "stake", "stale", "stand", "stare", "steam", "steel",
            "steep", "steer", "stern", "stick", "stiff", "still", "sting", "stock", "stone", "stony",
            "storm", "story", "strap", "straw", "stray", "strip", "study", "surge", "swamp", "swarm",
            "sweep", "sweet", "swift", "sword", "talon", "tempo", "titan", "torch", "toxic", "track",
            "trail", "train", "trait", "tread", "tribe", "trove", "truck", "trust", "truth", "vapor",
            "vault", "venom", "vigor", "viper", "virus", "vital", "vocal", "vortex", "water", "witch",
            "wrath", "yield", "zenith"
        );
        WORDS_BY_LENGTH.put(5, words5.stream().filter(w -> w.length() == 5).toList());

        // Explicit 6-Letter Words (purely 6 letters)
        List<String> words6 = Arrays.asList(
            "action", "anchor", "arcade", "archer", "armory", "avatar", "battle", "beacon", "bishop",
            "blazer", "bullet", "cannon", "castle", "cavity", "chaser", "cipher", "citadel", "climax",
            "combat", "comet", "cosmos", "crater", "cyborg", "dagger", "damage", "danger", "deadly",
            "defense", "dragon", "dynamo", "empire", "energy", "engine", "escape", "falcon", "famine",
            "fighter", "flame", "forest", "frenzy", "furious", "fusion", "galaxy", "gamble", "glider",
            "glitch", "goblin", "granite", "gravel", "gravity", "groove", "hammer", "harbor", "hazard",
            "heroic", "hunter", "hybrid", "impact", "impulse", "inferno", "island", "jungle", "katana",
            "knight", "lagoon", "launch", "legacy", "legend", "legion", "lethal", "magnum", "marine",
            "matrix", "meteor", "mirage", "mirror", "missile", "mission", "monkey", "mortar", "motion",
            "mutant", "mystic", "nebula", "nectar", "nemesis", "ninja", "novice", "nucleus", "online",
            "oracle", "orbital", "outlaw", "palace", "parade", "patrol", "phantom", "phoenix", "pioneer",
            "pirate", "pistol", "planet", "plasma", "player", "poison", "portal", "potion", "powder",
            "proton", "pulsar", "pyramid", "quantum", "quiver", "radius", "raider", "ranger", "ravine",
            "rebound", "reflex", "relic", "rescue", "revolt", "riddle", "rocket", "rogue", "runway",
            "sabotage", "salvo", "samurai", "savage", "scarlet", "sector", "sensor", "serpent", "shadow",
            "shield", "shogun", "silver", "siphon", "sniper", "sorcery", "sparks", "spiral", "spirit",
            "stealth", "strike", "summon", "temple", "terror", "thunder", "timber", "titan", "tornado",
            "traitor", "trench", "trigger", "tsunami", "typhoon", "valiant", "vampire", "vector", "velocity",
            "venomous", "vessel", "victim", "viper", "virtue", "visage", "vortex", "walker", "warlock",
            "warrior", "weapon", "wizard", "wraith", "zenith"
        );
        WORDS_BY_LENGTH.put(6, words6.stream().filter(w -> w.length() == 6).toList());

        // Populate difficulty lists (Easy, Medium, Hard, Intense)
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

        // Special words for power strikes
        SPECIAL_WORDS.addAll(Arrays.asList(
            "SUPERNOVA", "CATACLYSM", "VOIDCRUSH", "OVERDRIVE", "DRAGONSLASH",
            "HYPERBEAM", "ECLIPSE", "APOCALYPSE", "FINALSTRIKE", "OMNISLASH"
        ));
    }

    /**
     * Gets a random word respecting the current GameSettings word length filter.
     * If a fixed length is selected (3, 4, 5, 6), guarantees ONLY words of that exact
     * length are returned.
     */
    public static String getRandomWord(Difficulty difficulty) {
        GameSettings.WordLengthFilter filter = GameSettings.getInstance().getWordLengthFilter();

        if (filter.isFixedLength()) {
            int len = filter.getMinLength();
            List<String> list = WORDS_BY_LENGTH.get(len);
            if (list != null && !list.isEmpty()) {
                return list.get(RANDOM.nextInt(list.size()));
            }
        }

        // Mixed mode: retrieve by difficulty level
        List<String> diffList = WORDS_BY_DIFFICULTY.getOrDefault(difficulty, WORDS_BY_DIFFICULTY.get(Difficulty.MEDIUM));
        return diffList.get(RANDOM.nextInt(diffList.size()));
    }

    /**
     * Gets a random word strictly matching a specific letter count.
     */
    public static String getWordOfExactLength(int length) {
        List<String> list = WORDS_BY_LENGTH.get(length);
        if (list != null && !list.isEmpty()) {
            return list.get(RANDOM.nextInt(list.size()));
        }
        return "arena";
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

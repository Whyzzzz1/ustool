package com.imnu.story.Enum;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 阿斯亚
 * @date 2024/3/20
 */
public enum VagueWord_Enum {
    THINGS("things"),
    STUFF("stuff"),
    VARIOUS("various"),
    SEVERAL("several"),
    A_LOT("a lot"),
    MANY("many"),
    FEW("few"),
    A_COUPLE_OF("a couple of"),
    OFTEN("often"),
    SOMETIMES("sometimes"),
    PROBABLY("probably"),
    MAYBE("maybe"),
    PERHAPS("perhaps"),
    KIND_OF("kind of"),
    SORT_OF("sort of"),
    ABOUT("about"),
    NEARLY("nearly"),
    ALMOST("almost"),
    AROUND("around"),
    APPROXIMATELY("approximately"),
    VIRTUALLY("virtually"),
    SOME("some"),
    ANY("any"),
    CERTAIN("certain"),
    QUITE("quite"),
    RATHER("rather"),
    REALLY("really"),
    PRETTY("pretty"),
    GENERALLY("generally"),
    MOSTLY("mostly"),
    LARGELY("largely"),
    A_BIT("a bit"),
    A_LITTLE("a little");

    private final String word;

    VagueWord_Enum(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public static Set<String> getWords() {
        Set<String> words = new HashSet<>();
        for (VagueWord_Enum vagueWord : VagueWord_Enum.values()) {
            words.add(vagueWord.word);
        }
        return words;
    }
}

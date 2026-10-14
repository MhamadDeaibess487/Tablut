package tablut.tests;

import tablut.model.Jeu;

public class TestAIHard {
    public static void main(String[] args) {
        TestAIUtils.executerTest(
            "TestAIHard",
            2,
            new Jeu(),
            2,
            false
        );
    }
}

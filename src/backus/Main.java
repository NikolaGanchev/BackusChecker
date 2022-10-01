package backus;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        BackusChecker backusChecker = BackusChecker.
                Builder("word", "A|BA|B<word>|AA<word>B")
                .build();

        System.out.println(backusChecker.check("A"));
        System.out.println(backusChecker.check("BA"));
        System.out.println(backusChecker.check("BBA"));
        System.out.println(backusChecker.check("AAAB"));
        System.out.println(backusChecker.check("AAAAB"));
        System.out.println();

        BackusChecker backusChecker1 = BackusChecker.
                Builder("word", "1|<word>12|21<word>")
                .build();

        System.out.println(backusChecker1.check("1"));
        System.out.println(backusChecker1.check("112"));
        System.out.println(backusChecker1.check("21112"));
        System.out.println(backusChecker1.check("2111"));
        System.out.println();

        BackusChecker backusChecker2 = BackusChecker.
                Builder("word", "00|10|1<word>0|<word>00")
                .build();

        System.out.println(backusChecker2.check("00"));
        System.out.println(backusChecker2.check("10"));
        System.out.println(backusChecker2.check("110000"));
        System.out.println(backusChecker2.check("11000000"));
        System.out.println(backusChecker2.check("11"));
        System.out.println();

        BackusChecker backusChecker3 = BackusChecker.
                Builder("word", "A|C|ABC|A<word>B|A<word>C|C<word>")
                .build();

        System.out.println(backusChecker3.check("A"));
        System.out.println(backusChecker3.check("C"));
        System.out.println(backusChecker3.check("ABC"));
        System.out.println(backusChecker3.check("AABCB"));
        System.out.println(backusChecker3.check("AAABCBC"));
        System.out.println(backusChecker3.check("CAAABCBC"));
        System.out.println(backusChecker3.check("ACAAABCBCC"));
        System.out.println(backusChecker3.check("ACAAABCCCC"));
        System.out.println(backusChecker3.check("ACAAABCCCCC"));
        System.out.println();


        //<word1>::=1|<word>1|<word2>0
        //<word2>::=<word1>0|<word2>1
        //<word>::=0|<word>0|<word1>1
        BackusChecker backusCheckerWord = BackusChecker
                .Builder("word", "0|<word>0|<word1>1")
                .expectDependency("word1")
                .build();
        BackusChecker backusCheckerWord1 = BackusChecker
                .Builder("word1", "1|<word>1|<word2>0")
                .expectDependency("word")
                .expectDependency("word2")
                .build();
        BackusChecker backusCheckerWord2 = BackusChecker
                .Builder("word2", "<word1>0|<word2>1")
                .expectDependency("word1")
                .build();

        backusCheckerWord.registerDependency(backusCheckerWord1);
        backusCheckerWord1.registerDependency(backusCheckerWord2);
        backusCheckerWord1.registerDependency(backusCheckerWord);
        backusCheckerWord2.registerDependency(backusCheckerWord1);

        System.out.println(backusCheckerWord.check("0"));
        System.out.println(backusCheckerWord.check("00"));
        System.out.println(backusCheckerWord.check("11"));
        System.out.println(backusCheckerWord.check("1111"));
        System.out.println(backusCheckerWord.check("10012"));
        System.out.println();

        BackusGenerator backusGenerator = BackusGenerator
                .Builder("word", "A|BA|B<word>|AA<word>B")
                .build();

        List<String> result = backusGenerator.generate(6).stream().toList();

        result.forEach(System.out::println);
    }
}

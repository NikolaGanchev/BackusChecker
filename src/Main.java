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
    }
}

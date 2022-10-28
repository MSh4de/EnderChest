package ww;

public class WW {

    public static void main(String[] args) {

        int max = 10 * 10;
        char[][] chars =
                {{'c', 'o', 't', 'd', 't', 'r', 's', 'n', 'e', 'c'},
                        {'r', 'e', 'e', 'o', 't', 'e', 'o', 'h', 'u', 'c'},
                        {'ê', 'u', 'h', 'h', 't', 'r', 'l', 'a', 'o', 'a'},
                        {'p', 'f', 'w', 'a', 'e', 'r', 'e', 'a', 'a', 'f'},
                        {'e', 's', 't', 'p', 'r', 't', 'a', 'e', 'r', 'é'},
                        {'s', 'a', 'p', 'y', 'u', 'i', 'o', 'e', 's', 'd'},
                        {'p', 'a', 'e', 'o', 'i', 't', 'c', 'd', 't', 'e'},
                        {'n', 'n', 'c', 'n', 'c', 'w', 'b', 'o', 'b', 'n'},
                        {'f', 'o', 'u', 'r', 'c', 'h', 'e', 't', 't', 'e'},
                        {'b', 'a', 'i', 'e', 's', 't', 'h', 'n', 'w', 's'}};

        char[] words = new char[max];

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                words[x * 10 + y] = chars[x][y];
            }
        }


        System.out.println(words);
        System.out.println(getYellowDiagonal(words, 1, 10, max));

/*        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                System.out.println(getGreenDiagonal(words, x, y, max));
            }
        }*/

/*        System.out.println(getGreenDiagonal(words, 0, 0, max));
        System.out.println(getGreenDiagonal(words, 1, 0, max));
        System.out.println(getGreenDiagonal(words, 0, 1, max));
        System.out.println(getGreenDiagonal(words, 0, 2, max));*/

    }

    private static StringBuilder getYellowDiagonal(char[] words, int x, int y, int max) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10 - x; i++) {
            int index = (max - (10 - x)) - ((10 - y) * 10) - (9 * i);
            if (index > max || index < 0) break;
            stringBuilder.append(words[index]);
        }
        return stringBuilder;
    }

    private static StringBuilder getGreenDiagonal(char[] words, int x, int y, int max) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10 - x; i++) {
            int index = x + (y * 10) + 11 * i;
            if (index > max) break;
            stringBuilder.append(words[index]);
        }
        return stringBuilder;
    }

}

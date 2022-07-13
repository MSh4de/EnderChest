package farm;

import java.util.*;

public class Farm {

    private String[][] strings;
    private List<FarmData> list;
    public Farm(int size) {
        this.list = new ArrayList<>();
        this.strings = createBoard(size);
    }

    private String[][] createBoard(int i) {
        String[][] strings = new String[i][i];
        for (int a = 0; a < i; a++) {
            for (int j = 0; j < i ; j++) {
                strings[a][j] = randomType();
            }
        }
        return strings;
    }

    public void printBoard(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            StringBuilder  builder = new StringBuilder();
            for (int j = 0; j < strings.length; j++) {
                builder.append(strings[i][j]);
            }
            stringBuilder.append(builder).append("\n");
        }
        System.out.println(stringBuilder.toString());
    }

    public int getCultivableBox(int r){
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings.length; j++) {
                String s = strings[i][j];
                if (s.equals("X")) {
                    this.list.addAll(this.getAround(r, i, j));
                }
            }
        }
        return this.list.size();
    }

    private List<FarmData> getAround(int r, int... ints) {
        List<FarmData> list = new ArrayList<>();
        int xMin = (strings.length <= r ? 0 : ints[1] - r);
        int yMin = (strings.length <= r ? 0: ints[0] - r);

        int xMax = (strings.length <= r ? strings.length : ints[1] + r + 1);
        int yMax = (strings.length <= r ? strings.length : ints[0] + r + 1);


        for (int fy = yMin; fy < yMax; fy++) {
            for (int fx = xMin; fx < xMax; fx++) {
                if (hasFarm(fy, fx) && !contains(fy, fx) && strings[fy][fx].equals("0")) {
                    this.strings[fy][fx] = "#";
                    list.add(new FarmData(fx, fy));
                }
            }
        }
        return list;
    }



    private boolean hasFarm(int... ints){
        return ints[0] < strings.length && ints[0] >= 0 && ints[1] < strings.length && ints[1] >= 0;
    }

    private boolean contains(int... ints){
        for (FarmData farmData : this.list) {
            if (farmData.getX() == ints[1] && farmData.getY() == ints[0]) {
                return true;
            }
        }
        return false;
    }

    private  String randomType(){
        Random random = new Random();
        int i = random.nextInt(100)+1;
        if (i < 10){
            return "X";
        }else {
            return "0";
        }
    }
}

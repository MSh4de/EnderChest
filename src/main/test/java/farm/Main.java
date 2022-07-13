package farm;

import java.util.*;

public class Main {


    public  static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int i = scanner.nextInt();
        if (i <= 0) {
            System.out.println("Tu as pas de terrain petit toutou");
            return;
        }
        Farm farm = new Farm(i);
        System.out.println(farm.getCultivableBox(1));
        //farm.printBoard();
    }


}

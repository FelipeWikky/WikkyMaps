package com.thewikky.wikkymaps.Model;

import java.util.Random;

public class UidGenerate {

   /*
    * UID Requer 28 Giros
    * Atualmente dispõe de 36 Opções
    * Podendo ser inseridas mais
    */

    private static String[] option = {"A","B","C","D","E","F","G","H","I","J",
                                      "K","L","M","N","O","P","Q","R","S","T",
                                      "U","V","W","X","Y","Z","a","b","c","d",
                                      "e","f","g","h","i","j","k","l","m","n",
                                      "o","p","q","r","s","t","u","v","w","x",
                                      "y","z","0","1","2","3", "4","5","6","7",
                                      "8","9"};//62 opções -> 61 index

    public static String generate(){
        StringBuilder uid = new StringBuilder();
        int min = 0;
        int max = 61;//index
        int giro = 28;
        Random r = new Random();

        for(int i = 0; i < giro; i++){
            int rGiro = r.nextInt(max - min + 1) + min;
            uid.append(option[rGiro]);
        }
        return uid.toString();
    }
}

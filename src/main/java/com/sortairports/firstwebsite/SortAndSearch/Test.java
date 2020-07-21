package com.sortairports.firstwebsite.SortAndSearch;

import java.util.*;

public class Test {

    static class TrieNode {
        Map<Character, TrieNode> children = new TreeMap<Character, TrieNode>();
        boolean leaf;
        List<String[]> listWithRef=new ArrayList<>();
    }

    public static List<String[]> list=new ArrayList<>();
    public static List<String[]> listWithResults=new ArrayList<>();
    static TrieNode root = new TrieNode();

    static class CharacterIterator implements Iterator<Character> {

        private final String str;
        private int pos = 0;

        public CharacterIterator(String str) {
            this.str = str;
        }

        public boolean hasNext() {
            return pos < str.length();
        }

        public Character next() {
            return str.charAt(pos++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static void putTree(String s, int indexInFirstList) {
        TrieNode v = root;
        CharacterIterator ci = new CharacterIterator(s);
        do{
            char ch = ci.next();
            if (!v.children.containsKey(ch)) {
                v.children.put(ch, new TrieNode());
                //System.out.println(ch);
            }
            v = v.children.get(ch);
        }while(ci.hasNext());
        v.listWithRef.add(list.get(indexInFirstList));
        //System.out.println(list.get(indexInFirstList)[1]);
        v.leaf = true;
    }

    public static void findInTree(String textForSearch, List<String[]> listWithResults) {
        TrieNode v = root;
        boolean yes = true;

        for (char ch : textForSearch.toLowerCase().toCharArray()) {
            if (!v.children.containsKey(ch)) {
                yes=false;
            } else {
                v = v.children.get(ch);
            }
        }
        if(yes){
            runByTree(v, listWithResults);

//                for(String[] mass : v.listWithRef){
//                    System.out.println(mass[0]);
//                    listWithResults.add(mass);
//                }
//            if(!v.children.isEmpty()){
//                //System.out.println("Нужно найти концы");
//                runByTree(v, listWithResults);
//            }else System.out.println("Это конец");
        }

    }

    public static void runByTree(TrieNode v, List<String[]> listWithResults){
        for(String[] mass : v.listWithRef){
            System.out.println(mass[0] + "!!!");
            listWithResults.add(mass);
        }
        if(!v.children.isEmpty()){
            Iterator<Map.Entry<Character, TrieNode>> iterator = v.children.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<Character, TrieNode> pair = iterator.next();
                TrieNode value = pair.getValue();
                runByTree(value, listWithResults);
            }
        }
//        if(v.children.isEmpty()){
//            for(String[] mass : v.listWithRef){
//                System.out.println(mass[0] + "!!!");
//                listWithResults.add(mass);
//            }
//        }else{
//            Iterator<Map.Entry<Character, TrieNode>> iterator = v.children.entrySet().iterator();
//            while (iterator.hasNext())
//            {
//                Map.Entry<Character, TrieNode> pair = iterator.next();
//                TrieNode value = pair.getValue();
//                runByTree(value, listWithResults);
//            }
//        }

    }

    private static String listParserToString(int columnNumber, List<String[]> list){
        String result="";
        String s="";
        //if(list.get(0)[0].equals("Данные не найдены")) return "Данные не найдены";
        if(list.isEmpty()) return "Данные не найдены";
        for(int i=0; i<list.size(); i++){
            s=list.get(i)[columnNumber];
            for(int j=0; j<columnNumber;j++){
                s=s + ", " + list.get(i)[j];
            }
            //s=s.substring(0, s.length()-2);
            for(int j=columnNumber+1; j<list.get(0).length; j++){
                s=s + ", " + list.get(i)[j];
            }
            //s=s.substring(0, s.length()-2);
            result=result + s + "\n";
        }
        return result;
    }

    public static void main(String[] args) {
        list.add(new String[]{"a", "1"});
        list.add(new String[]{"ab", "12"});
        //list.add(new String[]{"abc", "10"});
        //list.add(new String[]{"aca", "100"});
        //list.add(new String[]{"a", "11"});
        //list.add(new String[]{"Goroka Airport", "12"});
        list.add(new String[]{"abd", "122"});
        for(int i=0 ; i<list.size(); i++){
            putTree(list.get(i)[0], i);
        }
        findInTree("a", listWithResults);
        for (String[] mass : listWithResults){
            System.out.println(Arrays.toString(mass));
        }
        System.out.println(listParserToString(0, listWithResults));
        //runByTree(root, listWithResults);
    }
}

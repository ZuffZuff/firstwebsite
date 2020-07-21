package com.sortairports.firstwebsite.SortAndSearch;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Greeting {
    public String pathCsv;//путь до исходного файла
    public static List<String[]> list=new ArrayList<>();
    public static List<String[]> listWithResults=new ArrayList<>();

    public static int column = 0;

    static class TrieNode {
        Map<Character, TrieNode> children = new TreeMap<Character, TrieNode>();
        boolean leaf;
        List<String[]> listWithRef=new ArrayList<>();
    }

    static TrieNode root = new TrieNode();


    public void setPathCsv(String pathCsv) {
        this.pathCsv = pathCsv;
    }

//    public String searcherOfData(int columnNumber, String textForSearch){//метод принимает номер колонки, по которой осуществляется поиск, и элемент который мы хотим найти
//        //String result = "";
//        List<String[]> list2 =sortList(list, columnNumber);//сортировка слиянием возвращает отсортированный список
//        //List<String[]> list3 = allSearch(columnNumber,textForSearch, list2);
//        list2 = allSearch(columnNumber,textForSearch, list2);//поиск всех ,соответствующих критериям поиска , элементов и помещение результата в отдельный список
//        String result = listParserToString(columnNumber, list2);//преобразование списка с нужными элементами в стоку для вывода на экран
//
//        return result;
//    }

    public String searcherOfData(int columnNumber, String textForSearch){//метод принимает номер колонки, по которой осуществляется поиск, и элемент который мы хотим найти
        listWithResults=new ArrayList<>();
        if(column!=columnNumber) {
            column=columnNumber;
            root = new TrieNode();
            System.out.println("Выставили " + column);
            for(int i=0 ; i<list.size(); i++){
                putTree(list.get(i)[column], i);
            }
            findInTree(textForSearch, listWithResults);
        }else {
            //listWithResults=new ArrayList<>();
            findInTree(textForSearch, listWithResults);
        }
        String result = listParserToString(column, listWithResults);
        //listWithResults=new ArrayList<>();


        return result;
    }

    public void putTree(String s, int indexInFirstList) {
        TrieNode v = root;
        CharacterIterator ci = new CharacterIterator(s.toLowerCase());
        do{
            char ch = ci.next();
            if (!v.children.containsKey(ch)) {
                v.children.put(ch, new TrieNode());
            }
            v = v.children.get(ch);
        }while(ci.hasNext());
        v.listWithRef.add(list.get(indexInFirstList));
        v.leaf = true;
    }

    public void findInTree(String textForSearch, List<String[]> listWithResults) {
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

//            for(String[] mass : v.listWithRef){
//                //System.out.println(mass[1]);
//                listWithResults.add(mass);
//            }
            if(!v.children.isEmpty()){

                runByTree(v, listWithResults);
            }
        }

    }

    public void runByTree(TrieNode v, List<String[]> listWithResults){
        for(String[] mass : v.listWithRef){

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



    public static String[] parserOfLines(String line){
        String s = line.replaceAll("\\\"", " ");
        String[] mass =s.split(",");
        for(int i=0; i<mass.length; i++){
            mass[i]=mass[i].trim();
        }
        return mass;
    }

    private List<String[]> sortList(List<String[]> list, int column){
        List<String[]> tmp;
        List<String[]> currentListForResult = list;
        List<String[]> listBuffer = new ArrayList<>(list.size());
        for (int i=0; i<list.size(); i++){
            listBuffer.add(null);
        }
        int size = 1;
        while (size<list.size()){
            for(int i=0; i<list.size(); i+=2*size){
                mergeAndCompare(currentListForResult, i, currentListForResult, i+size, listBuffer, i, size, column);//метод отвечает за сравнение и смену элементов местами
            }
            tmp = currentListForResult;
            currentListForResult = listBuffer;
            listBuffer = tmp;
            size=size*2;
        }
        return currentListForResult;
    }

    private void mergeAndCompare(List<String[]> list1, int list1Start, List<String[]> list2, int list2Start, List<String[]> listBuffer, int listBufferStart, int size, int columnForSort){
        int index1 = list1Start;
        int index2 = list2Start;

        int list1End = Math.min(list1Start+size, list1.size());
        int list2End = Math.min(list2Start+size, list2.size());

        int iterationCount = list1End - list1Start + list2End - list2Start;

        for(int i=listBufferStart; i<listBufferStart+iterationCount; i++){
            if(index1<list1End && (index2>= list2End || (compare(list1.get(index1)[columnForSort],list2.get(index2)[columnForSort])<0))){
                listBuffer.set(i, list1.get(index1));
                index1++;
            }else{
                listBuffer.set(i, list2.get(index2));
                index2++;
            }
        }
    }

    private int compare(String s1, String s2){
        //int i = s1.compareTo(s2);
        return s1.compareTo(s2);
    }

    private boolean isNumeric(String s){
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private List<String[]> allSearch(int columnNumber, String pattern, List<String[]> list){
        List<String[]> result = new ArrayList<>();
        boolean status;
        int index = search(columnNumber, pattern, list);//поиск индекса с которого можно начать помещать нужные данные в итоговый список. Далее идет перебор от этого индекса вверх и вниз
        if(index==-1)result.add(new String[]{"Данные не найдены"});//результат на случай если ничего не найдем
        else result.add(list.get(index));
        if(index>0 && index<list.size()-1){
            int innerIndex=index+1;
            do{
                status=regExMather(list.get(innerIndex)[columnNumber], pattern);//проверка на соответствие критериям запроса на поиск
                if(status)result.add(list.get(innerIndex));
                innerIndex++;
            }while(status && innerIndex<list.size());
            innerIndex = index-1;
            do{
                status=regExMather(list.get(innerIndex)[columnNumber], pattern);
                if(status)result.add(0,list.get(innerIndex));
                innerIndex--;
            }while(status && innerIndex>-1);
        }

        return result;
    }

    private int search(int columnNumber, String pattern, List<String[]> list){
        int firstIndex = 0;
        int lastIndex = list.size()-1;

        while(firstIndex <= lastIndex) {
            int middleIndex = (firstIndex + lastIndex) / 2;

            if (regExMather(list.get(middleIndex)[columnNumber], pattern)) {
                return middleIndex;
            }
            else if (list.get(middleIndex)[columnNumber].compareTo(pattern)<0)
                firstIndex = middleIndex + 1;

            else if (list.get(middleIndex)[columnNumber].compareTo(pattern)>0)
                lastIndex = middleIndex - 1;
        }
        return -1;
    }

    private boolean regExMather(String stringForPattern, String pattern){
        String regEx = "^" + pattern + ".*";
        return Pattern.matches(regEx,stringForPattern);
    }

    private String listParserToString(int columnNumber, List<String[]> list){
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

    class CharacterIterator implements Iterator<Character> {

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
}

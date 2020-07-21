package com.sortairports.firstwebsite.controllers;

import com.sortairports.firstwebsite.SortAndSearch.Greeting;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class MainController {

    static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
     static Greeting greeting = context.getBean("greeting", Greeting.class);
     static{
         try {
             Files.lines(Paths.get(greeting.pathCsv)).forEach(line->Greeting.list.add(Greeting.parserOfLines(line)));//заполнение списка массивами строк(каждый массив - строка из файла)
         } catch (IOException e) {
             e.printStackTrace();
         }
         for(int i=0 ; i<Greeting.list.size(); i++){
             greeting.putTree(Greeting.list.get(i)[Greeting.column], i);
         }
     }

        //на сколько я  понял, основная магия Spring Boot происходит здесь
    @RequestMapping(value="/greeting", method=RequestMethod.GET)//GET запрос на главную страницу
    public String greetingForm(Model model) {
//        try {
//            Files.lines(Paths.get(greeting.pathCsv)).forEach(line->Greeting.list.add(Greeting.parserOfLines(line)));//заполнение списка массивами строк(каждый массив - строка из файла)
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "greeting";
    }

    @RequestMapping(value="/getting", method= RequestMethod.POST)//получение POST запроса со страницы /greeting , инициализация процесса обработки полученных данных параметров,
    @ResponseBody                                                //возврат результата обратно на страницу вызова
    public String gettingSubmit(@RequestParam(value="column") String column , @RequestParam(value="search") String search) {
        if(search.equals(""))return "пусто в строке";
        int columnNumber = Integer.parseInt(column);
        String textForSearch = search.toLowerCase();
        String result = greeting.searcherOfData(columnNumber, textForSearch);//Основной метод. Возвращает строку с сцепленными элементами из исходного  файла
        return result;
    }

}

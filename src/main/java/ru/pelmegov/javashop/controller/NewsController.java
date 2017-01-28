package ru.pelmegov.javashop.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.pelmegov.javashop.api.service.NewsService;

@Controller
public class NewsController {

    private String newsListView = "/news/news";

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @RequestMapping(value = {"/news"}, method = RequestMethod.GET)
    public ModelAndView newsList(){
        ModelAndView modelAndView = new ModelAndView(newsListView);
        modelAndView.addObject("news", newsService.getNews(12));
        return  modelAndView;
    }

    @RequestMapping(value = "/news/{id}", method = RequestMethod.GET)
    public ModelAndView getNewsById(@PathVariable Long id){
        ModelAndView modelAndView = new ModelAndView(newsPage);
        modelAndView.addObject("news", newsService.getNewsById(id));

        return modelAndView;
    }
}

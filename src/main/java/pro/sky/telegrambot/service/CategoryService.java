package pro.sky.telegrambot.service;

public interface CategoryService {

    void addRootCategory(String name);

    void addChildCategory(String parentName, String childName);

    void removeCategory(String name);

    String viewTree();
}

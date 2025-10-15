package pro.sky.telegrambot.service;

import pro.sky.telegrambot.exception.CategoryExistsException;

/**
 * Сервис для работы с иерархией категорий.
 */
public interface CategoryService {

    /**
     * Добавляет новую корневую категорию.
     *
     * @param name название создаваемой категории
     * @throws CategoryExistsException  если категория с таким именем уже существует
     * @throws IllegalArgumentException если имя категории пустое или null
     */
    void addRootCategory(String name);

    /**
     * Добавляет дочернюю категорию к указанной родительской.
     *
     * @param parentName название родительской категории
     * @param childName  название создаваемой дочерней категории
     */
    void addChildCategory(String parentName, String childName);

    /**
     * Удаляет категорию по имени.
     *
     * @param name название категории для удаления
     */
    void removeCategory(String name);

    /**
     * Генерирует текстовое представление дерева категорий.
     *
     * @return форматированное строковое представление иерархии категорий
     */
    String viewTree();
}
